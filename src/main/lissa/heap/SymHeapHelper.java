package lissa.heap;

import java.util.HashMap;
import java.util.LinkedList;

import gov.nasa.jpf.symbc.arrays.ArrayExpression;
import gov.nasa.jpf.symbc.heap.HeapNode;
import gov.nasa.jpf.symbc.numeric.Comparator;
import gov.nasa.jpf.symbc.numeric.Expression;
import gov.nasa.jpf.symbc.numeric.IntegerConstant;
import gov.nasa.jpf.symbc.numeric.PCChoiceGenerator;
import gov.nasa.jpf.symbc.numeric.PathCondition;
import gov.nasa.jpf.symbc.numeric.SymbolicInteger;
import gov.nasa.jpf.symbc.numeric.SymbolicReal;
import gov.nasa.jpf.symbc.string.StringSymbolic;
import gov.nasa.jpf.vm.BooleanFieldInfo;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.DoubleFieldInfo;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.FieldInfo;
import gov.nasa.jpf.vm.FloatFieldInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.IntegerFieldInfo;
import gov.nasa.jpf.vm.LongFieldInfo;
import gov.nasa.jpf.vm.MJIEnv;
import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.ObjRef;
import gov.nasa.jpf.vm.ReferenceFieldInfo;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.Types;
import gov.nasa.jpf.vm.VM;
import lissa.LISSAShell;
import lissa.bytecode.lazy.StaticRepOKCallInstruction;
import lissa.choicegenerators.HeapChoiceGeneratorLISSA;
import lissa.heap.solving.techniques.LIBasedStrategy;
import lissa.heap.solving.techniques.PCCheckStrategy;
import lissa.heap.solving.techniques.SolvingStrategy;
import lissa.heap.visitors.SymbolicOutputHeapVisitor;

public class SymHeapHelper {

    public static StaticRepOKCallInstruction createStaticRepOKCallInstruction(String staticMethodSignature) {
        HeapChoiceGeneratorLISSA heapCG = VM.getVM().getLastChoiceGeneratorOfType(HeapChoiceGeneratorLISSA.class);
        return createStaticRepOKCallInstruction(heapCG.getCurrentSymInputHeap(), staticMethodSignature);
    }

    public static StaticRepOKCallInstruction createStaticRepOKCallInstruction(SymbolicInputHeapLISSA symInputHeap,
            String staticMethodSignature) {
        SymbolicReferenceInput symRefInput = symInputHeap.getImplicitInputThis();

        ClassInfo rootClassInfo = symRefInput.getRootHeapNode().getType();
        MethodInfo repokMI = rootClassInfo.getMethod(staticMethodSignature, false);

        String clsName = repokMI.getClassInfo().getName();
        String mthName = repokMI.getName();
        String signature = repokMI.getSignature();

        return new StaticRepOKCallInstruction(clsName, mthName, signature);
    }

    public static Instruction checkIfPathConditionAndHeapAreSAT(ThreadInfo ti, Instruction current, Instruction next,
            PCChoiceGenerator cg) {
        SolvingStrategy solvingStrategy = LISSAShell.solvingStrategy;
        if (solvingStrategy instanceof LIBasedStrategy && !((LIBasedStrategy) solvingStrategy).isRepOKExecutionMode()) {
            if (solvingStrategy instanceof PCCheckStrategy && !ti.getVM().getSystemState().isIgnored()) {
                PCCheckStrategy strategy = (PCCheckStrategy) solvingStrategy;
                return strategy.handlePrimitiveBranch(ti, current, next, cg);
            }
        }
        return next;
    }

    public static Expression initializeInstanceField(MJIEnv env, FieldInfo field, ElementInfo eiRef, String refChain,
            String suffix, SymbolicInputHeapLISSA symInputHeap) {
        Expression sym_v = null;
        String name = "";

        name = field.getName();
        String fullName = refChain + "." + name + suffix;
        if (field instanceof IntegerFieldInfo || field instanceof LongFieldInfo) {
            sym_v = new SymbolicInteger(fullName);
        } else if (field instanceof FloatFieldInfo || field instanceof DoubleFieldInfo) {
            sym_v = new SymbolicReal(fullName);
        } else if (field instanceof ReferenceFieldInfo) {
            if (field.getType().equals("java.lang.String")) {
                sym_v = new StringSymbolic(fullName);
                int val = env.newString("WWWWW's Birthday is 12-17-77");
                eiRef.set1SlotField(field, val);
            } else {
                sym_v = new SymbolicInteger(fullName);
            }
        } else if (field instanceof BooleanFieldInfo) {
            // treat boolean as an integer with range [0,1]
            sym_v = new SymbolicInteger(fullName, 0, 1);
        }
        eiRef.setFieldAttr(field, sym_v);

        // ==== ADDED:

        SymbolicReferenceInput symRefInput = symInputHeap.getImplicitInputThis();
        if (!(field instanceof ReferenceFieldInfo) || field.getType().equals("java.lang.String")) {
            symRefInput.addPrimitiveSymbolicField(eiRef.getObjectRef(), field, sym_v);
        } else {
            symRefInput.addReferenceField(eiRef.getObjectRef(), field, SymbolicReferenceInput.SYMBOLIC);
        }
        return sym_v;
    }

    public static void initializeInstanceFields(MJIEnv env, FieldInfo[] fields, ElementInfo eiRef, String refChain,
            SymbolicInputHeapLISSA symInputHeap) {
        LIBasedStrategy stg = (LIBasedStrategy) LISSAShell.solvingStrategy;
        for (int i = 0; i < fields.length; i++) {
            FieldInfo field = fields[i];
            String fieldName = field.getName();
            String ownerClassName = eiRef.getClassInfo().getName();
            if (stg.isFieldTracked(ownerClassName, fieldName)) {
                initializeInstanceField(env, fields[i], eiRef, refChain, "", symInputHeap);
            }
        }
    }

    public static int addNewHeapNode(ClassInfo typeClassInfo, ThreadInfo ti, Object attr, PathCondition pcHeap,
            SymbolicInputHeapLISSA symInputHeap, int numSymRefs, HeapNode[] prevSymRefs, boolean setShared) {
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

        initializeInstanceFields(ti.getEnv(), fields, eiRef, refChain, symInputHeap);

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

    public static SymbolicInputHeapLISSA getSymbolicInputHeap(VM vm) {
        HeapChoiceGeneratorLISSA heapCG = vm.getLastChoiceGeneratorOfType(HeapChoiceGeneratorLISSA.class);
        if (heapCG != null)
            return (SymbolicInputHeapLISSA) heapCG.getCurrentSymInputHeap();
        return null;
    }

    public static PCChoiceGenerator getCurrentPCChoiceGenerator(VM vm) {
        return vm.getLastChoiceGeneratorOfType(PCChoiceGenerator.class);
    }

    public static HeapChoiceGeneratorLISSA getCurrentHeapChoiceGenerator(VM vm) {
        return vm.getLastChoiceGeneratorOfType(HeapChoiceGeneratorLISSA.class);
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

    public static void pushArguments(ThreadInfo ti, Object[] args, Object[] attrs) {
        StackFrame frame = ti.getModifiableTopFrame();

        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                Object a = args[i];
                boolean isLong = false;

                if (a != null) {
                    if (a instanceof ObjRef) {
                        frame.pushRef(((ObjRef) a).getReference());
                    } else if (a instanceof Boolean) {
                        frame.push((Boolean) a ? 1 : 0, false);
                    } else if (a instanceof Integer) {
                        frame.push((Integer) a, false);
                    } else if (a instanceof Long) {
                        frame.pushLong((Long) a);
                        isLong = true;
                    } else if (a instanceof Double) {
                        frame.pushLong(Types.doubleToLong((Double) a));
                        isLong = true;
                    } else if (a instanceof Byte) {
                        frame.push((Byte) a, false);
                    } else if (a instanceof Short) {
                        frame.push((Short) a, false);
                    } else if (a instanceof Float) {
                        frame.push(Types.floatToInt((Float) a), false);
                    }
                }

                if (attrs != null && attrs[i] != null) {
                    if (isLong) {
                        frame.setLongOperandAttr(attrs[i]);
                    } else {
                        frame.setOperandAttr(attrs[i]);
                    }
                }
            }
        }
    }

    // public static Integer getSolution(SymbolicInteger symbolicInteger,
    // PathCondition pathCondition) {
    // int solution = 0;
    // if (pathCondition != null) {
    // if (!PathCondition.flagSolved)
    // pathCondition.solveOld();
    // long val = symbolicInteger.solution();
    // if (val != SymbolicInteger.UNDEFINED)
    // solution = (int) val;
    // }
    // return solution;
    // }

}
