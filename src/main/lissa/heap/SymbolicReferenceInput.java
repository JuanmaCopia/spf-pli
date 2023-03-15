package lissa.heap;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.apache.commons.lang3.tuple.ImmutablePair;

import gov.nasa.jpf.symbc.heap.HeapNode;
import gov.nasa.jpf.symbc.numeric.Expression;
import gov.nasa.jpf.symbc.numeric.PathCondition;
import gov.nasa.jpf.symbc.numeric.SymbolicInteger;
import gov.nasa.jpf.symbc.string.StringSymbolic;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.FieldInfo;
import gov.nasa.jpf.vm.MJIEnv;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.VM;
import korat.finitization.impl.StateSpace;
import lissa.choicegenerators.HeapChoiceGeneratorLISSA;
import lissa.heap.visitors.CheckPCVisitor;
import lissa.heap.visitors.PartialHeapBuilderVisitor;
import lissa.heap.visitors.SymbolicInputHeapVisitor;
import lissa.heap.visitors.SymbolicInputHeapVisitor2;
import symsolve.vector.SymSolveSolution;

public class SymbolicReferenceInput {

    public static final int NULL = 0;
    public static final int SYMBOLIC = -1;

    private HeapNode rootHeapNode;

    private HashMap<ImmutablePair<Integer, FieldInfo>, Integer> fieldToReferenceIndex;

    private HashMap<ImmutablePair<Integer, FieldInfo>, Expression> fieldToPrimitiveTypeExpression;

    public SymbolicReferenceInput() {
        this.fieldToReferenceIndex = new HashMap<>();
        this.fieldToPrimitiveTypeExpression = new HashMap<>();
    }

    public SymbolicReferenceInput(HashMap<ImmutablePair<Integer, FieldInfo>, Integer> fieldToRefIndex,
            HashMap<ImmutablePair<Integer, FieldInfo>, Expression> fieldToPrimExpr, HeapNode rootHeapNode) {
        this.fieldToReferenceIndex = new HashMap<>(fieldToRefIndex);
        this.fieldToPrimitiveTypeExpression = new HashMap<>(fieldToPrimExpr);
        this.rootHeapNode = rootHeapNode;
    }

    public SymbolicReferenceInput makeShallowCopy() {
        return new SymbolicReferenceInput(this.fieldToReferenceIndex, this.fieldToPrimitiveTypeExpression,
                this.rootHeapNode);
    }

    public Integer getReferenceField(Integer ownerIndex, FieldInfo field) {
        ImmutablePair<Integer, FieldInfo> fieldDescriptor = new ImmutablePair<>(ownerIndex, field);
        return fieldToReferenceIndex.get(fieldDescriptor);
    }

    public void addReferenceField(Integer ownerIndex, FieldInfo field, Integer valueIndex) {
        ImmutablePair<Integer, FieldInfo> fieldDescriptor = new ImmutablePair<>(ownerIndex, field);
        fieldToReferenceIndex.put(fieldDescriptor, valueIndex);
    }

    public Expression getPrimitiveSymbolicField(Integer ObjIndex, FieldInfo field) {
        ImmutablePair<Integer, FieldInfo> fieldDescriptor = new ImmutablePair<>(ObjIndex, field);
        return fieldToPrimitiveTypeExpression.get(fieldDescriptor);
    }

    public void addPrimitiveSymbolicField(Integer ObjIndex, FieldInfo field, Expression symbolicVar) {
        ImmutablePair<Integer, FieldInfo> fieldDescriptor = new ImmutablePair<>(ObjIndex, field);
        fieldToPrimitiveTypeExpression.put(fieldDescriptor, symbolicVar);
    }

    public void setRootHeapNode(HeapNode rootHeapNode) {
        this.rootHeapNode = rootHeapNode;
    }

    public HeapNode getRootHeapNode() {
        return this.rootHeapNode;
    }

    public void acceptBFS(SymbolicInputHeapVisitor visitor) {
        HashMap<Integer, Integer> idMap = new HashMap<Integer, Integer>();
        HashMap<ClassInfo, Integer> maxIdMap = new HashMap<ClassInfo, Integer>();

        ThreadInfo ti = VM.getVM().getCurrentThread();
        ClassInfo rootClass = this.rootHeapNode.getType();
        Integer rootIndex = this.rootHeapNode.getIndex();

        idMap.put(rootIndex, 0);
        maxIdMap.put(rootClass, 0);

        LinkedList<Integer> worklist = new LinkedList<Integer>();
        worklist.add(rootIndex);

        while (!worklist.isEmpty()) {
            int currentObjRef = worklist.removeFirst();
            int currentObjID = idMap.get(currentObjRef);
            ElementInfo eiRef = ti.getElementInfo(currentObjRef);
            ClassInfo ownerObjectClass = eiRef.getClassInfo();

            visitor.setCurrentOwner(ownerObjectClass, currentObjID);

            FieldInfo[] instanceFields = ownerObjectClass.getDeclaredInstanceFields();
            for (int i = 0; i < instanceFields.length; i++) {
                FieldInfo field = instanceFields[i];
                ClassInfo fieldClass = field.getTypeClassInfo();

                visitor.setCurrentField(fieldClass, field);

                // System.out.println("BFS: class of current obj: " +
                // ownerObjectClass.getSimpleName());
                // System.out.println("BFS: current field: " + field.getName());
                // System.out.println("BFS: current type: " + fieldClass.getSimpleName());
                // System.out.println("\n");

                if (visitor.isIgnoredField()) {
                    // System.out.println("Ignored field: " + field.getName());
                    // System.out.println("type: " + fieldClass.getSimpleName());
                    continue;
                }

                if (field.isReference() && !field.getType().equals("java.lang.String")) {

                    Integer fieldIndex = getReferenceField(currentObjRef, field);
                    if (fieldIndex == SYMBOLIC) {
                        visitor.visitedSymbolicReferenceField();
                    } else if (fieldIndex == NULL) {
                        visitor.visitedNullReferenceField();
                    } else if (idMap.containsKey(fieldIndex)) { // previously visited object
                        visitor.visitedExistentReferenceField(idMap.get(fieldIndex) + 1);
                    } else { // first time visited
                        int id = 0;
                        if (maxIdMap.containsKey(fieldClass))
                            id = maxIdMap.get(fieldClass) + 1;

                        idMap.put(fieldIndex, id);
                        maxIdMap.put(fieldClass, id);
                        visitor.visitedNewReferenceField(id + 1);
                        worklist.add(fieldIndex);
                    }
                } else {
                    Expression symbolicPrimitive = getPrimitiveSymbolicField(currentObjRef, field);
                    if (symbolicPrimitive instanceof SymbolicInteger) {
                        SymbolicInteger symbolicInteger = (SymbolicInteger) symbolicPrimitive;
                        if (field.isBooleanField()) {
                            visitor.visitedSymbolicBooleanField(field, symbolicInteger);
                        } else if (field.isLongField()) {
                            visitor.visitedSymbolicLongField(field, symbolicInteger);
                        } else if (field.isIntField()) {
                            visitor.visitedSymbolicIntegerField(field, symbolicInteger);
                        } else {
                            assert (false); // ERROR!
                        }
                    } else if (symbolicPrimitive instanceof StringSymbolic) {
                        visitor.visitedSymbolicStringField(field, (StringSymbolic) symbolicPrimitive);
                    } else {
                        assert (false); // ERROR!
                    }
                }
            }
        }
    }

    public void buildPartialHeap(MJIEnv env, int newRootRef, HeapChoiceGeneratorLISSA heapCG) {
        PartialHeapBuilderVisitor visitor = new PartialHeapBuilderVisitor(env, newRootRef);
        acceptBFS2(visitor);

        heapCG.setCurrentSymInputHeap(visitor.getNewSymbolicInputHeap());
        heapCG.setCurrentPCheap(visitor.getHeapPathCondition());
    }

    public boolean isSolutionSATWithPathCondition(StateSpace stateSpace, SymSolveSolution solution, PathCondition pc) {
        assert (pc != null);
        if (pc.count() == 0)
            return true;

        CheckPCVisitor visitor = new CheckPCVisitor(stateSpace, solution, pc);
        acceptBFS2(visitor);

        return !visitor.isAborted();
    }

    public void acceptBFS2(SymbolicInputHeapVisitor2 visitor) {
        ThreadInfo ti = VM.getVM().getCurrentThread();
        Set<Integer> visited = new HashSet<>();
        LinkedList<Integer> worklist = new LinkedList<Integer>();
        Integer rootIndex = this.rootHeapNode.getIndex();
        visited.add(rootIndex);
        worklist.add(rootIndex);
        visitor.setRoot(rootIndex);

        while (!worklist.isEmpty()) {
            int currentOwnerRef = worklist.removeFirst();
            ElementInfo owner = ti.getElementInfo(currentOwnerRef);
            visitor.setCurrentOwner(currentOwnerRef);

            ClassInfo ownerObjectClass = owner.getClassInfo();
            FieldInfo[] instanceFields = ownerObjectClass.getDeclaredInstanceFields();

            for (int i = 0; i < instanceFields.length; i++) {

                if (visitor.isAborted())
                    return;

                FieldInfo field = instanceFields[i];
                visitor.setCurrentField(field);

                if (visitor.isIgnoredField()) {
                    // System.out.println("Ignored field: " + field.getName());
                    // System.out.println("type: " + fieldClass.getSimpleName());
                    continue;
                }

                if (field.isReference() && !field.getType().equals("java.lang.String")) {

                    Integer fieldRef = getReferenceField(currentOwnerRef, field);
                    if (fieldRef == SYMBOLIC) {
                        visitor.visitedSymbolicReferenceField();
                    } else if (fieldRef == NULL) {
                        visitor.visitedNullReferenceField();
                    } else if (!visited.add(fieldRef)) { // previously visited object
                        visitor.visitedExistentReferenceField(fieldRef);
                    } else { // first time visited
                        visitor.visitedNewReferenceField(fieldRef);
                        worklist.add(fieldRef);
                    }
                } else {
                    Expression symbolicPrimitive = getPrimitiveSymbolicField(currentOwnerRef, field);
                    visitor.visitedSymbolicPrimitiveField(symbolicPrimitive);
                }
            }
        }
    }

    @Override
    public String toString() {
        ThreadInfo ti = VM.getVM().getCurrentThread();
        Set<Integer> visited = new HashSet<>();
        LinkedList<Integer> worklist = new LinkedList<Integer>();
        Integer rootIndex = this.rootHeapNode.getIndex();
        visited.add(rootIndex);
        worklist.add(rootIndex);

        StringBuilder sb = new StringBuilder();
        String indent = "";

        while (!worklist.isEmpty()) {
            int currentOwnerRef = worklist.removeFirst();
            ElementInfo owner = ti.getElementInfo(currentOwnerRef);

            ClassInfo ownerObjectClass = owner.getClassInfo();
            FieldInfo[] instanceFields = ownerObjectClass.getDeclaredInstanceFields();

            String ownerString = "[" + owner.getObjectRef() + "]";

            for (int i = 0; i < instanceFields.length; i++) {

                FieldInfo field = instanceFields[i];

                String fieldString = indent + ownerString + "." + field.getName();

                if (field.isReference() && !field.getType().equals("java.lang.String")) {

                    Integer fieldRef = getReferenceField(currentOwnerRef, field);
                    if (fieldRef == SYMBOLIC) {
                        sb.append(String.format("%s -> SYMBOLIC\n", fieldString));
                    } else if (fieldRef == NULL) {
                        sb.append(String.format("%s -> null\n", fieldString));
                    } else if (!visited.add(fieldRef)) { // previously visited object
                        sb.append(String.format("%s -> *%d*\n", fieldString, fieldRef));
                    } else { // first time visited
                        sb.append(String.format("%s -> %d\n", fieldString, fieldRef));
                        worklist.add(fieldRef);
                    }
                } else {
                    Expression symbolicPrimitive = getPrimitiveSymbolicField(currentOwnerRef, field);
                    if (symbolicPrimitive instanceof SymbolicInteger) {
                        SymbolicInteger symbolicInteger = (SymbolicInteger) symbolicPrimitive;
                        sb.append(String.format("%s -> %s\n", fieldString, symbolicInteger.toString()));
                    } else if (symbolicPrimitive instanceof StringSymbolic) {
                        StringSymbolic symbolicString = (StringSymbolic) symbolicPrimitive;
                        sb.append(String.format("%s -> %s\n", fieldString, symbolicString.toString()));
                    } else {
                        assert (false); // ERROR!
                    }
                }
            }
            indent = indent + "  ";
        }
        PathCondition pc = PathCondition.getPC(ti.getVM());
        String pcString = pc.toString();
        sb.append("\n" + pcString);
        return sb.toString();
    }

}
