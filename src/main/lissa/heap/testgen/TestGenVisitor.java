package lissa.heap.testgen;

import java.util.Map;

import gov.nasa.jpf.symbc.numeric.PathCondition;
import gov.nasa.jpf.vm.MJIEnv;
import korat.finitization.impl.FieldDomain;
import lissa.heap.SymbolicInputHeapLISSA;
import lissa.heap.SymbolicReferenceInput;
import symsolve.candidates.traversals.BFSCandidateTraversal;
import symsolve.candidates.traversals.visitors.GenericCandidateVisitor;
import symsolve.vector.SymSolveSolution;

public class TestGenVisitor extends GenericCandidateVisitor {

    MJIEnv env;
    SymbolicReferenceInput symRefInput;

//    // Field
//    FieldInfo currentField;

    Integer equivalentOwnerInSymHeap;

    Map<Object, Integer> symSolveToSymbolic;

    BFSCandidateTraversal.ObjectInfo currentOwnerInfo;
    String currentRefChain;

    StringBuilder testCase = new StringBuilder();
    int testID;

    public TestGenVisitor(MJIEnv env, SymbolicInputHeapLISSA symInputHeap, SymSolveSolution solution,
            PathCondition repOKPathCondition, Map<Object, Integer> symSolveToSymbolic, int testID) {
        this.env = env;
        this.symRefInput = symInputHeap.getImplicitInputThis();
        this.symSolveToSymbolic = symSolveToSymbolic;
        this.testID = testID;

    }

    @Override
    public void setRoot(Object rootObject, BFSCandidateTraversal.ObjectInfo rootInfo) {
        super.setRoot(rootObject, rootInfo);
        appendTestSignature(rootClass.getSimpleName(), testID);
        appendCreateRoot(rootInfo.chainRef);

    }

    @Override
    public void setCurrentOwner(Object currentOwnerObject, BFSCandidateTraversal.ObjectInfo currentOwnerInfo) {
        super.setCurrentOwner(currentOwnerObject, currentOwnerInfo);
        this.currentOwnerInfo = currentOwnerInfo;
        equivalentOwnerInSymHeap = symSolveToSymbolic.get(currentOwnerObject);
    }

    @Override
    public void setCurrentField(FieldDomain fieldDomain, String fieldName, int fieldIndexInVector,
            int fieldIndexInFieldDomain) {
        super.setCurrentField(fieldDomain, fieldName, fieldIndexInVector, fieldIndexInFieldDomain);
        currentRefChain = currentOwnerInfo.chainRef + "." + fieldName;
    }

    @Override
    public void accessedNullReferenceField() {
        appendLine(makeAssing(currentRefChain, "null"));
    }

    @Override
    public void accessedNewReferenceField(Object fieldObject, BFSCandidateTraversal.ObjectInfo fieldObjectInfo) {
        appendLine(makeAssing(currentRefChain, fieldObjectInfo.chainRef));
    }

    @Override
    public void accessedVisitedReferenceField(Object fieldObject, BFSCandidateTraversal.ObjectInfo fieldObjectInfo) {
        appendLine(makeAssing(currentRefChain, fieldObjectInfo.chainRef));
    }

    @Override
    public void accessedPrimitiveField(int fieldObjectID) {
//        if (equivalentOwnerInSymHeap != null) {
//            setValueForExistingPrimitiveField();
//        } else {
//            setValueForNonExistingPrimitiveField();
//        }
    }

//    void setValueForExistingPrimitiveField() {
//        assert (currentFieldDomain.isPrimitiveType());
//        if (accessedIndices.contains(currentFieldIndexInVector)) {
//            int value = 0;
//            Class<?> clsOfField = currentFieldDomain.getClassOfField();
//            if (clsOfField == int.class) {
//                value = ((IntSet) currentFieldDomain).getInt(currentFieldIndexInFieldDomain);
//                currentOwnerEI.setIntField(currentField, value);
//            } else if (clsOfField == boolean.class) {
//                boolean boolValue = ((BooleanSet) currentFieldDomain).getBoolean(currentFieldIndexInFieldDomain);
//                currentOwnerEI.setBooleanField(currentField, boolValue);
//                if (boolValue)
//                    value = 1;
//            } else {
//                assert (false); // TODO: add support for other types, String, Long, etc.
//            }
//
//            currentOwnerEI.setFieldAttr(currentField, null);
//
//            Expression symbolicVar = symRefInput.getPrimitiveSymbolicField(equivalentOwnerInSymHeap, currentField);
//            assert (symbolicVar != null);
//
//            IntegerConstant constant = new IntegerConstant(value);
//
//            PCChoiceGeneratorLISSA currPCCG = SymHeapHelper.getCurrentPCChoiceGeneratorLISSA(env.getVM());
//            assert (currPCCG != null);
//
//            PathCondition pc = currPCCG.getCurrentPC();
//            assert (pc != null);
//            pc._addDet(Comparator.EQ, symbolicVar, constant);
//
//            assert (pc.simplify());
//            currPCCG.setCurrentPC(pc);
//        } else {
//            Expression symbolicVar = symRefInput.getPrimitiveSymbolicField(equivalentOwnerInSymHeap, currentField);
//            assert (symbolicVar != null);
//            if (symbolicVar instanceof StringExpression) {
//                int val = env.newString("WWWWW's Birthday is 12-17-77");
//                currentOwnerEI.set1SlotField(currentField, val);
//            }
//
//            currentOwnerEI.setFieldAttr(currentField, symbolicVar);
//        }
//    }
//
//    void setValueForNonExistingPrimitiveField() {
//        Expression symbolicValue = null;
//        String name = currentField.getName() + "(sym)_" + symbolicID;
//        symbolicID++;
//        if (currentField instanceof IntegerFieldInfo || currentField instanceof LongFieldInfo) {
//            symbolicValue = new SymbolicInteger(name);
//        } else if (currentField instanceof FloatFieldInfo || currentField instanceof DoubleFieldInfo) {
//            symbolicValue = new SymbolicReal(name);
//        } else if (currentField instanceof ReferenceFieldInfo) {
//            if (currentField.getType().equals("java.lang.String")) {
//                symbolicValue = new StringSymbolic(name);
//                int val = env.newString("WWWWW's Birthday is 12-17-77");
//                currentOwnerEI.set1SlotField(currentField, val);
//            } else {
//                symbolicValue = new SymbolicInteger(name);
//            }
//        } else if (currentField instanceof BooleanFieldInfo) {
//            // treat boolean as an integer with range [0,1]
//            symbolicValue = new SymbolicInteger(name, 0, 1);
//        } else {
//            throw new RuntimeException("symbolicValue is null !!!!");
//        }
//        currentOwnerEI.setFieldAttr(currentField, symbolicValue);
//    }

    String makeConstructorCall(String className) {
        return String.format("new %s()", className);
    }

    String makeAssing(String left, String right) {
        return String.format("%s = %s;", left, right);
    }

    void appendTestSignature(String className, int testId) {
        testCase.append(String.format("@Test\npublic void %sTest%d {\n", className.toLowerCase(), testId));
    }

    void appendCreateRoot(String rootIdentifier) {
        testCase.append(String.format("\t%s = %s;\n", rootIdentifier, makeConstructorCall(rootClass.getSimpleName())));
    }

    void appendLine(String line) {
        testCase.append(line + "\n");
    }

}
