package lissa.heap.builder;

import java.util.HashMap;

import gov.nasa.jpf.symbc.numeric.Comparator;
import gov.nasa.jpf.symbc.numeric.Expression;
import gov.nasa.jpf.symbc.numeric.IntegerConstant;
import gov.nasa.jpf.symbc.numeric.PathCondition;
import gov.nasa.jpf.symbc.numeric.SymbolicInteger;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.FieldInfo;
import gov.nasa.jpf.vm.ThreadInfo;
import korat.finitization.impl.BooleanSet;
import korat.finitization.impl.FieldDomain;
import korat.finitization.impl.IntSet;
import korat.utils.IIntList;
import lissa.heap.SymbolicInputHeapLISSA;
import lissa.heap.SymbolicReferenceInput;
import symsolve.candidates.traversals.visitors.GenericCandidateVisitor;
import symsolve.vector.SymSolveSolution;

public class CheckPathConditionVisitor extends GenericCandidateVisitor {

    ThreadInfo ti;

    PathCondition pc;

    SymbolicReferenceInput symRefInput;

    Integer currentObjectInSymRefInput;
    ClassInfo currentObjectClassInfo;

    FieldInfo currentField;

    HashMap<Object, Integer> symSolveToSymbolicInputObjects = new HashMap<>();

    IIntList accessedIndices;

    boolean isAborted = false;

    public CheckPathConditionVisitor(ThreadInfo ti, PathCondition pc, SymbolicInputHeapLISSA symInputHeap,
            SymSolveSolution solution) {
        this.ti = ti;
        this.pc = pc;
        this.symRefInput = symInputHeap.getImplicitInputThis();
        this.accessedIndices = solution.getAccessedIndices();
    }

    @Override
    public void setRoot(Object rootObject, int rootID) {
        super.setRoot(rootObject, rootID);
        symSolveToSymbolicInputObjects.put(rootObject, symRefInput.getRootHeapNode().getIndex());
    }

    @Override
    public void setCurrentOwner(Object currentOwnerObject, int currentOwnerID) {
        super.setCurrentOwner(currentOwnerObject, currentOwnerID);
        currentObjectInSymRefInput = symSolveToSymbolicInputObjects.get(currentOwnerObject);
        assert (currentObjectInSymRefInput != null);
        assert (currentObjectInSymRefInput != SymbolicReferenceInput.NULL);

        if (currentObjectInSymRefInput != SymbolicReferenceInput.SYMBOLIC) {
            ElementInfo currentObjectElementInfo = ti.getModifiableElementInfo(currentObjectInSymRefInput);
            assert (currentObjectElementInfo != null);
            currentObjectClassInfo = currentObjectElementInfo.getClassInfo();
            assert (currentObjectInSymRefInput != null);
        } else {
            currentObjectClassInfo = null;
        }
    }

    @Override
    public void setCurrentField(FieldDomain fieldDomain, String fieldName, int fieldIndexInVector,
            int fieldIndexInFieldDomain) {
        super.setCurrentField(fieldDomain, fieldName, fieldIndexInVector, fieldIndexInFieldDomain);
        if (currentObjectClassInfo != null)
            currentField = currentObjectClassInfo.getInstanceField(fieldName);
        else
            currentField = null;
    }

    @Override
    public void accessedNewReferenceField(Object fieldObject, int fieldObjectID) {
        Integer equivalentObjectInSymInput;
        if (currentObjectInSymRefInput == SymbolicReferenceInput.SYMBOLIC) {
            equivalentObjectInSymInput = SymbolicReferenceInput.SYMBOLIC;
        } else {
            assert (currentField != null);
            equivalentObjectInSymInput = symRefInput.getReferenceField(currentObjectInSymRefInput, currentField);
            assert (equivalentObjectInSymInput != null);
            assert (equivalentObjectInSymInput != SymbolicReferenceInput.NULL);
        }
        symSolveToSymbolicInputObjects.put(fieldObject, equivalentObjectInSymInput);
    }

    @Override
    public void accessedPrimitiveField(int fieldObjectID) {
        assert (currentObjectInSymRefInput != SymbolicReferenceInput.NULL);
        if (currentObjectInSymRefInput != SymbolicReferenceInput.SYMBOLIC)
            checkSatisfiabilityWithPathCondition();
        // If the current object in the symbolic heap is symbolic, we don't care.
    }

    void checkSatisfiabilityWithPathCondition() {
        assert (currentFieldDomain.isPrimitiveType());

        if (accessedIndices.contains(currentFieldIndexInVector)) {
            assert (currentField != null);
            Expression symbolicVar = symRefInput.getPrimitiveSymbolicField(currentObjectInSymRefInput, currentField);
            assert (symbolicVar != null);

            Class<?> clsOfField = currentFieldDomain.getClassOfField();
            if (clsOfField == int.class) {
                assert (currentField.isIntField());
                assert (symbolicVar instanceof SymbolicInteger);
                int value = ((IntSet) currentFieldDomain).getInt(currentFieldIndexInFieldDomain);
                IntegerConstant constant = new IntegerConstant(value);
                pc._addDet(Comparator.EQ, symbolicVar, constant);

            } else if (clsOfField == boolean.class) {
                assert (currentField.isBooleanField());
                assert (symbolicVar instanceof SymbolicInteger);
                boolean boolValue = ((BooleanSet) currentFieldDomain).getBoolean(currentFieldIndexInFieldDomain);
                int value = 0;
                if (boolValue)
                    value = 1;
                IntegerConstant constant = new IntegerConstant(value);
                pc._addDet(Comparator.EQ, symbolicVar, constant);
            } else {
                assert (false); // TODO: add support for other types, String, Long, etc.
            }

            if (!pc.simplify()) {
                // UNSAT
                isAborted = true;
            }
        }
    }

    @Override
    public boolean isTraversalAborted() {
        return isAborted;
    }

    public boolean isSolutionSAT() {
        return !isAborted;
    }

}
