package lissa.heap.builder;

import java.util.HashMap;
import java.util.Map;

import gov.nasa.jpf.symbc.numeric.Comparator;
import gov.nasa.jpf.symbc.numeric.Expression;
import gov.nasa.jpf.symbc.numeric.IntegerConstant;
import gov.nasa.jpf.symbc.numeric.PCChoiceGenerator;
import gov.nasa.jpf.symbc.numeric.PathCondition;
import gov.nasa.jpf.symbc.numeric.SymbolicInteger;
import gov.nasa.jpf.symbc.numeric.SymbolicReal;
import gov.nasa.jpf.symbc.string.StringExpression;
import gov.nasa.jpf.symbc.string.StringSymbolic;
import gov.nasa.jpf.vm.BooleanFieldInfo;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.DoubleFieldInfo;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.FieldInfo;
import gov.nasa.jpf.vm.FloatFieldInfo;
import gov.nasa.jpf.vm.IntegerFieldInfo;
import gov.nasa.jpf.vm.LongFieldInfo;
import gov.nasa.jpf.vm.MJIEnv;
import gov.nasa.jpf.vm.ReferenceFieldInfo;
import gov.nasa.jpf.vm.VM;
import korat.finitization.impl.BooleanSet;
import korat.finitization.impl.FieldDomain;
import korat.finitization.impl.IntSet;
import korat.utils.IntListAI;
import lissa.heap.SymHeapHelper;
import lissa.heap.SymbolicInputHeapLISSA;
import lissa.heap.SymbolicReferenceInput;
import symsolve.candidates.traversals.visitors.GenericCandidateVisitor;
import symsolve.vector.SymSolveSolution;

public class HeapSolutionVisitor extends GenericCandidateVisitor {

    MJIEnv env;
    SymbolicReferenceInput symRefInput;
    IntListAI accessedIndices;

    ElementInfo newObjectRootElementInfo;
    Integer rootSymbolicInputIndex;

    // Owner
    ElementInfo currentOwnerEI;
    ClassInfo currentOwnerClass;

    Integer equivalentOwnerInSymHeap;

    // Field
    FieldInfo currentField;

    Map<Object, ElementInfo> symSolveToNewJPFObjects = new HashMap<>();
    Map<Object, Integer> symSolveToSymbolic;

    int symbolicID = 0;

    public HeapSolutionVisitor(MJIEnv env, int newObjectRootRef, SymbolicInputHeapLISSA symInputHeap,
            SymSolveSolution solution, Map<Object, Integer> symSolveToSymbolic) {
        this.env = env;
        this.newObjectRootElementInfo = VM.getVM().getHeap().getModifiable(newObjectRootRef);
        this.symRefInput = symInputHeap.getImplicitInputThis();
        this.rootSymbolicInputIndex = symRefInput.getRootHeapNode().getIndex();
        this.accessedIndices = solution.getAccessedIndices();
        this.symSolveToSymbolic = symSolveToSymbolic;
    }

    @Override
    public void setRoot(Object rootObject, int rootID) {
        super.setRoot(rootObject, rootID);
        symSolveToNewJPFObjects.put(rootObject, newObjectRootElementInfo);
    }

    @Override
    public void setCurrentOwner(Object currentOwnerObject, int currentOwnerID) {
        super.setCurrentOwner(currentOwnerObject, currentOwnerID);
        currentOwnerEI = symSolveToNewJPFObjects.get(currentOwnerObject);
        currentOwnerClass = currentOwnerEI.getClassInfo();
        equivalentOwnerInSymHeap = symSolveToSymbolic.get(currentOwnerObject);
    }

    @Override
    public void setCurrentField(FieldDomain fieldDomain, String fieldName, int fieldIndexInVector,
            int fieldIndexInFieldDomain) {
        super.setCurrentField(fieldDomain, fieldName, fieldIndexInVector, fieldIndexInFieldDomain);
        currentField = currentOwnerClass.getInstanceField(currentFieldName);
    }

    @Override
    public void accessedNullReferenceField(int fieldObjectID) {
        currentOwnerEI.setReferenceField(currentField, MJIEnv.NULL);
        currentOwnerEI.setFieldAttr(currentField, null);
    }

    @Override
    public void accessedNewReferenceField(Object fieldObject, int fieldObjectID) {
        ElementInfo newObjectElementInfo = env.newElementInfo(currentField.getType());
        currentOwnerEI.setReferenceField(currentField, newObjectElementInfo.getObjectRef());
        currentOwnerEI.setFieldAttr(currentField, null);
        symSolveToNewJPFObjects.put(fieldObject, newObjectElementInfo);
    }

    @Override
    public void accessedVisitedReferenceField(Object fieldObject, int fieldObjectID) {
        ElementInfo mirrorFieldObject = symSolveToNewJPFObjects.get(fieldObject);
        currentOwnerEI.setReferenceField(currentField, mirrorFieldObject.getObjectRef());
        currentOwnerEI.setFieldAttr(currentField, null);
    }

    @Override
    public void accessedPrimitiveField(int fieldObjectID) {
        if (equivalentOwnerInSymHeap != null) {
            setValueForExistingPrimitiveField();
        } else {
            setValueForNonExistingPrimitiveField();
        }
    }

    void setValueForExistingPrimitiveField() {
        assert (currentFieldDomain.isPrimitiveType());
        if (accessedIndices.contains(currentFieldIndexInVector)) {
            int value = 0;
            Class<?> clsOfField = currentFieldDomain.getClassOfField();
            if (clsOfField == int.class) {
                value = ((IntSet) currentFieldDomain).getInt(currentFieldIndexInFieldDomain);
                currentOwnerEI.setIntField(currentField, value);
            } else if (clsOfField == boolean.class) {
                boolean boolValue = ((BooleanSet) currentFieldDomain).getBoolean(currentFieldIndexInFieldDomain);
                currentOwnerEI.setBooleanField(currentField, boolValue);
                if (boolValue)
                    value = 1;
            } else {
                assert (false); // TODO: add support for other types, String, Long, etc.
            }

            currentOwnerEI.setFieldAttr(currentField, null);

            Expression symbolicVar = symRefInput.getPrimitiveSymbolicField(equivalentOwnerInSymHeap, currentField);
            assert (symbolicVar != null);

            IntegerConstant constant = new IntegerConstant(value);

            PCChoiceGenerator currPCCG = SymHeapHelper.getCurrentPCChoiceGenerator(env.getVM());
            assert (currPCCG != null);

            PathCondition pc = currPCCG.getCurrentPC();
            assert (pc != null);
            pc._addDet(Comparator.EQ, symbolicVar, constant);

            assert (pc.simplify());
            currPCCG.setCurrentPC(pc);
        } else {
            Expression symbolicVar = symRefInput.getPrimitiveSymbolicField(equivalentOwnerInSymHeap, currentField);
            assert (symbolicVar != null);
            if (symbolicVar instanceof StringExpression) {
                int val = env.newString("WWWWW's Birthday is 12-17-77");
                currentOwnerEI.set1SlotField(currentField, val);
            }

            currentOwnerEI.setFieldAttr(currentField, symbolicVar);
        }
    }

    void setValueForNonExistingPrimitiveField() {
        Expression symbolicValue = null;
        String name = currentField.getName() + "(sym)_" + symbolicID;
        symbolicID++;
        if (currentField instanceof IntegerFieldInfo || currentField instanceof LongFieldInfo) {
            symbolicValue = new SymbolicInteger(name);
        } else if (currentField instanceof FloatFieldInfo || currentField instanceof DoubleFieldInfo) {
            symbolicValue = new SymbolicReal(name);
        } else if (currentField instanceof ReferenceFieldInfo) {
            if (currentField.getType().equals("java.lang.String")) {
                symbolicValue = new StringSymbolic(name);
                int val = env.newString("WWWWW's Birthday is 12-17-77");
                currentOwnerEI.set1SlotField(currentField, val);
            } else {
                symbolicValue = new SymbolicInteger(name);
            }
        } else if (currentField instanceof BooleanFieldInfo) {
            // treat boolean as an integer with range [0,1]
            symbolicValue = new SymbolicInteger(name, 0, 1);
        } else {
            throw new RuntimeException("symbolicValue is null !!!!");
        }
        currentOwnerEI.setFieldAttr(currentField, symbolicValue);
    }
}
