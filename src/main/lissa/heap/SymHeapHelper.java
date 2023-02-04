package lissa.heap;

import java.util.HashMap;
import java.util.LinkedList;

import gov.nasa.jpf.symbc.arrays.ArrayExpression;
import gov.nasa.jpf.symbc.heap.HeapChoiceGenerator;
import gov.nasa.jpf.symbc.heap.HeapNode;
import gov.nasa.jpf.symbc.heap.SymbolicInputHeap;
import gov.nasa.jpf.symbc.numeric.Comparator;
import gov.nasa.jpf.symbc.numeric.Expression;
import gov.nasa.jpf.symbc.numeric.IntegerConstant;
import gov.nasa.jpf.symbc.numeric.PCChoiceGenerator;
import gov.nasa.jpf.symbc.numeric.PathCondition;
import gov.nasa.jpf.symbc.numeric.SymbolicInteger;
import gov.nasa.jpf.symbc.numeric.SymbolicReal;
import gov.nasa.jpf.symbc.string.StringSymbolic;
import gov.nasa.jpf.vm.BooleanFieldInfo;
import gov.nasa.jpf.vm.ChoiceGenerator;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.DoubleFieldInfo;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.FieldInfo;
import gov.nasa.jpf.vm.FloatFieldInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.IntegerFieldInfo;
import gov.nasa.jpf.vm.LongFieldInfo;
import gov.nasa.jpf.vm.MJIEnv;
import gov.nasa.jpf.vm.ReferenceFieldInfo;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.VM;
import lissa.LISSAShell;
import lissa.heap.solving.techniques.PCCheckStrategy;
import lissa.heap.solving.techniques.SolvingStrategy;
import lissa.heap.visitors.SymbolicOutputHeapVisitor;

public class SymHeapHelper {

    public static Instruction checkIfPathConditionAndHeapAreSAT(ThreadInfo ti, Instruction current, Instruction next,
            PathCondition pc) {
        SolvingStrategy solvingStrategy = LISSAShell.solvingStrategy;
        if (solvingStrategy instanceof PCCheckStrategy && !ti.getVM().getSystemState().isIgnored()) {
            PCCheckStrategy strategy = (PCCheckStrategy) solvingStrategy;
            if (!strategy.isRepOKExecutionMode()) {
                return strategy.getNextInstructionToPrimitiveBranching(ti, current, next, pc);
            }
        }
        return next;
    }

    public static Expression initializeInstanceField(FieldInfo field, ElementInfo eiRef, String refChain, String suffix,
            SymbolicInputHeap symInputHeap) {
        Expression sym_v = null;
        String name = "";

        name = field.getName();
        String fullName = refChain + "." + name + suffix;
        if (field instanceof IntegerFieldInfo || field instanceof LongFieldInfo) {
            sym_v = new SymbolicInteger(fullName);
        } else if (field instanceof FloatFieldInfo || field instanceof DoubleFieldInfo) {
            sym_v = new SymbolicReal(fullName);
        } else if (field instanceof ReferenceFieldInfo) {
            if (field.getType().equals("java.lang.String"))
                sym_v = new StringSymbolic(fullName);
            else
                sym_v = new SymbolicInteger(fullName);
        } else if (field instanceof BooleanFieldInfo) {
            // treat boolean as an integer with range [0,1]
            sym_v = new SymbolicInteger(fullName, 0, 1);
        }
        eiRef.setFieldAttr(field, sym_v);

        // ==== ADDED:

        SymbolicReferenceInput symRefInput = ((SymbolicInputHeapLISSA) symInputHeap).getImplicitInputThis();
        if (!(field instanceof ReferenceFieldInfo) || field.getType().equals("java.lang.String")) {
            symRefInput.addPrimitiveSymbolicField(eiRef.getObjectRef(), field, sym_v);
        } else {
            symRefInput.addReferenceField(eiRef.getObjectRef(), field, SymbolicReferenceInput.SYMBOLIC);
        }
        return sym_v;
    }

    public static void initializeInstanceFields(FieldInfo[] fields, ElementInfo eiRef, String refChain,
            SymbolicInputHeap symInputHeap) {
        for (int i = 0; i < fields.length; i++)
            initializeInstanceField(fields[i], eiRef, refChain, "", symInputHeap);
    }

    public static int addNewHeapNode(ClassInfo typeClassInfo, ThreadInfo ti, Object attr, PathCondition pcHeap,
            SymbolicInputHeap symInputHeap, int numSymRefs, HeapNode[] prevSymRefs, boolean setShared) {
        int daIndex = ti.getHeap().newObject(typeClassInfo, ti).getObjectRef();
        ti.getHeap().registerPinDown(daIndex);
        String refChain = ((SymbolicInteger) attr).getName(); // + "[" + daIndex + "]"; // do we really need to add
                                                              // daIndex here?
        SymbolicInteger newSymRef = new SymbolicInteger(refChain);
        ElementInfo eiRef = ti.getModifiableElementInfo(daIndex);// ti.getElementInfo(daIndex); // TODO to review!
        if (setShared) {
            eiRef.setShared(ti, true);// ??
        }
        // daIndex.getObjectRef() -> number

        // neha: this change allows all the fields in the class hierarchy of the
        // object to be initialized as symbolic and not just its instance fields

        int numOfFields = eiRef.getNumberOfFields();
        FieldInfo[] fields = new FieldInfo[numOfFields];
        for (int fieldIndex = 0; fieldIndex < numOfFields; fieldIndex++) {
            fields[fieldIndex] = eiRef.getFieldInfo(fieldIndex);
        }

        initializeInstanceFields(fields, eiRef, refChain, symInputHeap);

        // Put symbolic array in PC if we create a new array.
        if (typeClassInfo.isArray()) {
            String typeClass = typeClassInfo.getType();
            ArrayExpression arrayAttr = null;
            if (typeClass.charAt(1) != 'L') {
                arrayAttr = new ArrayExpression(eiRef.toString());
            } else {
                arrayAttr = new ArrayExpression(eiRef.toString(), typeClass.substring(2, typeClass.length() - 1));
            }
            ti.getVM().getLastChoiceGeneratorOfType(PCChoiceGenerator.class).getCurrentPC().arrayExpressions
                    .put(eiRef.toString(), arrayAttr);
        }

        // create new HeapNode based on above info
        // update associated symbolic input heap
        HeapNode n = new HeapNode(daIndex, typeClassInfo, newSymRef);
        symInputHeap._add(n);
        pcHeap._addDet(Comparator.NE, newSymRef, new IntegerConstant(-1));
        pcHeap._addDet(Comparator.EQ, newSymRef, new IntegerConstant(numSymRefs));
        for (int i = 0; i < numSymRefs; i++)
            pcHeap._addDet(Comparator.NE, n.getSymbolic(), prevSymRefs[i].getSymbolic());
        return daIndex;
    }

    public static SymbolicInputHeapLISSA getSymbolicInputHeap() {
        return getSymbolicInputHeap(VM.getVM());
    }

    public static SymbolicInputHeapLISSA getSymbolicInputHeap(VM vm) {
        HeapChoiceGenerator heapCG = vm.getLastChoiceGeneratorOfType(HeapChoiceGenerator.class);
        if (heapCG != null)
            return (SymbolicInputHeapLISSA) heapCG.getCurrentSymInputHeap();
        return null;
    }

    public static ThreadInfo getCurrentThread() {
        return ThreadInfo.getCurrentThread();
    }

    public static PathCondition getPathCondition() {
        return PathCondition.getPC(VM.getVM());
    }

    public static PathCondition getPathCondition(VM vm) {
        return PathCondition.getPC(vm);
    }

    public static PCChoiceGenerator getCurrentPCChoiceGenerator(VM vm) {
        ChoiceGenerator<?> cg = vm.getChoiceGenerator();
        if (cg != null && !(cg instanceof PCChoiceGenerator)) {
            cg = cg.getPreviousChoiceGeneratorOfType(PCChoiceGenerator.class);
        }

        if (cg instanceof PCChoiceGenerator) {
            return ((PCChoiceGenerator) cg);
        } else {
            return null;
        }
    }

    public static Integer getSolution(SymbolicInteger symbolicInteger, PathCondition pathCondition) {
        int solution = 0;
        if (pathCondition != null) {
            if (!PathCondition.flagSolved)
                pathCondition.solveOld();
            long val = symbolicInteger.solution();
            if (val != SymbolicInteger.UNDEFINED)
                solution = (int) val;
        }
        return solution;
    }

    public static void acceptBFS(int rootIndex, SymbolicOutputHeapVisitor visitor) {
        HashMap<Integer, Integer> idMap = new HashMap<Integer, Integer>();
        HashMap<ClassInfo, Integer> maxIdMap = new HashMap<ClassInfo, Integer>();

        ThreadInfo ti = VM.getVM().getCurrentThread();
        ElementInfo rootElementInfo = ti.getElementInfo(rootIndex);
        ClassInfo rootClass = rootElementInfo.getClassInfo();

        idMap.put(rootIndex, 0);
        maxIdMap.put(rootClass, 0);

        LinkedList<Integer> worklist = new LinkedList<Integer>();
        worklist.add(rootIndex);

        while (!worklist.isEmpty()) {
            int currentObjRef = worklist.removeFirst();
            int currentObjID = idMap.get(currentObjRef);
            ElementInfo elementInfo = ti.getElementInfo(currentObjRef);
            ClassInfo ownerObjectClass = elementInfo.getClassInfo();

//            visitor.setCurrentOwner(ownerObjectClass, currentObjID);

            if (currentObjRef != rootIndex)
                visitor.setCurrentOwner(ownerObjectClass, currentObjID + 1);
            else
                visitor.setCurrentOwner(ownerObjectClass, currentObjID);

            FieldInfo[] instanceFields = ownerObjectClass.getDeclaredInstanceFields();
            for (int i = 0; i < instanceFields.length; i++) {
                FieldInfo field = instanceFields[i];
                ClassInfo fieldClass = field.getTypeClassInfo();

                visitor.setCurrentField(fieldClass, field);

                if (visitor.isIgnoredField()) {
                    // System.out.println("Ignored field: " + field.getName());
                    // System.out.println("type: " + fieldClass.getSimpleName());
                    continue;
                }

                if (field.isReference() && !field.getType().equals("java.lang.String")) {
                    Object attr = elementInfo.getFieldAttr(field);
                    int fieldIndex = elementInfo.getReferenceField(field);
                    // Integer fieldIndex = getReferenceField(currentObjRef, field);
                    if (attr != null) {
                        visitor.visitedSymbolicReferenceField();
                    } else if (fieldIndex == MJIEnv.NULL) {
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
                    visitor.visitedSymbolicPrimitiveField(field);
                }
                visitor.resetCurrentField();
            }
            visitor.resetCurrentOwner();
        }
    }

//    
//    public boolean isStateMatching(SymbolicInputHeapVisitor visitor, int candidate) {
//        HashMap<Integer, Integer> idMap = new HashMap<Integer, Integer>();
//        HashMap<ClassInfo, Integer> maxIdMap = new HashMap<ClassInfo, Integer>();
//
//        HashMap<Integer, Integer> idMap2 = new HashMap<Integer, Integer>();
//
//        ThreadInfo ti = VM.getVM().getCurrentThread();
//        ClassInfo rootClass = this.rootHeapNode.getType();
//        Integer rootIndex = this.rootHeapNode.getIndex();
//
//        ElementInfo candElementInfo = ti.getElementInfo(candidate);
//        ClassInfo candClass = candElementInfo.getClassInfo();
//
//        if (candClass != rootClass)
//            return false;
//
//        idMap.put(rootIndex, 0);
//        idMap2.put(candidate, 0);
//        maxIdMap.put(rootClass, 0);
//
//        LinkedList<Integer> worklist = new LinkedList<Integer>();
//        worklist.add(rootIndex);
//
//        LinkedList<Integer> worklist2 = new LinkedList<Integer>();
//        worklist2.add(candidate);
//
//        while (!worklist.isEmpty()) {
//
//            if (worklist2.isEmpty())
//                return false;
//
//            int currentObjRef = worklist.removeFirst();
//            int currentObjID = idMap.get(currentObjRef);
//            ElementInfo elementInfo = ti.getElementInfo(currentObjRef);
//            ClassInfo ownerObjectClass = elementInfo.getClassInfo();
//
//            int currentObjRef2 = worklist2.removeFirst();
//            int currentObjID2 = idMap2.get(currentObjRef2);
//            ElementInfo elementInfo2 = ti.getElementInfo(currentObjRef2);
//
//            if (currentObjID != currentObjID2)
//                return false;
//
//            visitor.setCurrentOwner(ownerObjectClass, currentObjID + 1);
//
//            FieldInfo[] instanceFields = ownerObjectClass.getDeclaredInstanceFields();
//            for (int i = 0; i < instanceFields.length; i++) {
//                FieldInfo field = instanceFields[i];
//                ClassInfo fieldClass = field.getTypeClassInfo();
//
//                visitor.setCurrentField(fieldClass, field);
//
//                if (visitor.isIgnoredField()) {
//                    // System.out.println("Ignored field: " + field.getName());
//                    // System.out.println("type: " + fieldClass.getSimpleName());
//                    continue;
//                }
//
//                if (field.isReference() && !field.getType().equals("java.lang.String")) {
//                    Integer fieldIndex = getReferenceField(currentObjRef, field);
//
//                    int fieldIndex2 = elementInfo2.getReferenceField(field);
//
//
//                    if (fieldIndex == SYMBOLIC) {
//                        visitor.visitedSymbolicReferenceField();
//                    } else if (fieldIndex == NULL) {
//                        visitor.visitedNullReferenceField();
//                        if (fieldIndex2 != MJIEnv.NULL)
//                            return false;
//                    } else if (idMap.containsKey(fieldIndex)) { // previously visited object
//                        visitor.visitedExistentReferenceField(idMap.get(fieldIndex) + 1);
//                        if (fieldIndex2 == MJIEnv.NULL || !idMap2.containsKey(fieldIndex2) || idMap.get(fieldIndex) != idMap2.get(fieldIndex2))
//                            return false;
//
//                    } else { // first time visited
//                        if (fieldIndex2 == MJIEnv.NULL || idMap2.containsKey(fieldIndex2))
//                            return false;
//
//                        int id = 0;
//                        if (maxIdMap.containsKey(fieldClass))
//                            id = maxIdMap.get(fieldClass) + 1;
//
//                        idMap.put(fieldIndex, id);
//                        idMap2.put(fieldIndex2, id);
//
//                        maxIdMap.put(fieldClass, id);
//                        visitor.visitedNewReferenceField(id + 1);
//                        worklist.add(fieldIndex);
//                        worklist2.add(fieldIndex2);
//                    }
//                } else {
//                    Expression symbolicPrimitive = getPrimitiveSymbolicField(currentObjRef, field);
//                    if (symbolicPrimitive instanceof SymbolicInteger) {
//                        SymbolicInteger symbolicInteger = (SymbolicInteger) symbolicPrimitive;
//                        if (field.isBooleanField()) {
//                            visitor.visitedSymbolicBooleanField(field, symbolicInteger);
//                        } else if (field.isLongField()) {
//                            visitor.visitedSymbolicLongField(field, symbolicInteger);
//                        } else if (field.isIntField()) {
//                            visitor.visitedSymbolicIntegerField(field, symbolicInteger);
//                        } else {
//                            assert (false); // ERROR!
//                        }
//                    } else if (symbolicPrimitive instanceof StringSymbolic) {
//                        visitor.visitedSymbolicStringField(field, (StringSymbolic) symbolicPrimitive);
//                    } else {
//                        assert (false); // ERROR!
//                    }
//                }
//                visitor.resetCurrentField();
//            }
//            visitor.resetCurrentOwner();
//        }
//        visitor.setMaxIdMap(maxIdMap);
//        return true;
//    }
//
//
//    public boolean doesItMatch(int candidate) {
//        HashMap<Integer, Integer> idMap = new HashMap<Integer, Integer>();
//        HashMap<ClassInfo, Integer> maxIdMap = new HashMap<ClassInfo, Integer>();
//
//        HashMap<Integer, Integer> idMap2 = new HashMap<Integer, Integer>();
//        HashMap<ClassInfo, Integer> maxIdMap2 = new HashMap<ClassInfo, Integer>();
//
//        ThreadInfo ti = VM.getVM().getCurrentThread();
//        ClassInfo rootClass = this.rootHeapNode.getType();
//        Integer rootIndex = this.rootHeapNode.getIndex();
//
//        ElementInfo candElementInfo = ti.getElementInfo(candidate);
//        ClassInfo candClass = candElementInfo.getClassInfo();
//
//        if (candClass != rootClass)
//            return false;
//
//        idMap.put(rootIndex, 0);
//        idMap2.put(candidate, 0);
//        maxIdMap.put(rootClass, 0);
//        maxIdMap2.put(candClass, 0);
//
//        LinkedList<Integer> worklist = new LinkedList<Integer>();
//        worklist.add(rootIndex);
//
//        LinkedList<Integer> worklist2 = new LinkedList<Integer>();
//        worklist2.add(candidate);
//
//        while (!worklist.isEmpty()) {
//            int currentObjRef = worklist.removeFirst();
//            int currentObjID = idMap.get(currentObjRef);
//            ElementInfo elementInfo = ti.getElementInfo(currentObjRef);
//            ClassInfo ownerObjectClass = elementInfo.getClassInfo();
//
//            int currentObjRef2 = worklist2.removeFirst();
//            int currentObjID2 = idMap2.get(currentObjRef2);
//            ElementInfo elementInfo2 = ti.getElementInfo(currentObjRef2);
//
//            if (currentObjID != currentObjID2)
//                return false;
//
//            //visitor.setCurrentOwner(ownerObjectClass, currentObjID + 1);
//
//            FieldInfo[] instanceFields = ownerObjectClass.getDeclaredInstanceFields();
//            for (int i = 0; i < instanceFields.length; i++) {
//                FieldInfo field = instanceFields[i];
//                ClassInfo fieldClass = field.getTypeClassInfo();
//
////                visitor.setCurrentField(fieldClass, field);
////
////                if (visitor.isIgnoredField()) {
////                    // System.out.println("Ignored field: " + field.getName());
////                    // System.out.println("type: " + fieldClass.getSimpleName());
////                    continue;
////                }
//
//                if (field.isReference() && !field.getType().equals("java.lang.String")) {
//                    Integer fieldIndex = getReferenceField(currentObjRef, field);
//
//                    Object attr2 = elementInfo2.getFieldAttr(field);
//                    int fieldIndex2 = elementInfo2.getReferenceField(field);
//
//
//
//
//                    if (fieldIndex == SYMBOLIC) {
//                        //visitor.visitedSymbolicReferenceField();
//                    } else if (fieldIndex == NULL && fieldIndex2 != MJIEnv.NULL) {
//                        return false;
//                        //visitor.visitedNullReferenceField();
//                    } else if (idMap.containsKey(fieldIndex)) { // previously visited object
//                        if (!idMap2.containsKey(fieldIndex2) || idMap.get(fieldIndex) != idMap2.get(fieldIndex2))
//                            return false;
//                        //visitor.visitedExistentReferenceField(idMap.get(fieldIndex) + 1);
//                    } else { // first time visited
//                        int id = 0;
//                        if (maxIdMap.containsKey(fieldClass))
//                            id = maxIdMap.get(fieldClass) + 1;
//
//                        int id2 = 0;
//                        if (maxIdMap2.containsKey(fieldClass))
//                            id2 = maxIdMap2.get(fieldClass) + 1;
//
//                        if (id != id2)
//                            return false;
//
//                        idMap.put(fieldIndex, id);
//                        maxIdMap.put(fieldClass, id);
//
//                        idMap2.put(fieldIndex2, id2);
//                        maxIdMap2.put(fieldClass, id2);
//
//                        //visitor.visitedNewReferenceField(id + 1);
//                        worklist.add(fieldIndex);
//                        worklist2.add(fieldIndex2);
//                    }
//                } else {
//                    //visitor.visitedSymbolicPrimitiveField(field);
//                }
//                //visitor.resetCurrentField();
//            }
//            //visitor.resetCurrentOwner();
//        }
//        return true;
//    }

}
