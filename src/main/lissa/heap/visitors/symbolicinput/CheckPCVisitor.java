package lissa.heap.visitors.symbolicinput;

import java.util.HashMap;
import java.util.Map;

import gov.nasa.jpf.symbc.numeric.Comparator;
import gov.nasa.jpf.symbc.numeric.Expression;
import gov.nasa.jpf.symbc.numeric.IntegerConstant;
import gov.nasa.jpf.symbc.numeric.PathCondition;
import gov.nasa.jpf.symbc.numeric.SymbolicInteger;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.FieldInfo;
import gov.nasa.jpf.vm.Heap;
import gov.nasa.jpf.vm.VM;
import korat.finitization.impl.BooleanSet;
import korat.finitization.impl.FieldDomain;
import korat.finitization.impl.IntSet;
import korat.finitization.impl.ObjSet;
import korat.finitization.impl.StateSpace;
import korat.utils.IIntList;
import lissa.LISSAShell;
import lissa.heap.solving.techniques.LIBasedStrategy;
import symsolve.vector.SymSolveSolution;

public class CheckPCVisitor implements SymbolicInputHeapVisitor {

    VM vm;
    Heap JPFHeap;

    LIBasedStrategy strategy;
    StateSpace stateSpace;

    int[] solutionVector;
    IIntList accessedIndices;
    Object solutionRoot;

    PathCondition pc;

    // Owner:
    int currentSymbolicOwnerRef;
    ElementInfo symbolicOwnerEI;
    ClassInfo currentOwnerClass;

    Object currentSolutionOwner;

    // Field:
    FieldInfo currentField;
    ClassInfo currentFieldType;
    String currentFieldName;

    Object fieldAttr;

    boolean isAborted = false;

    Map<Integer, Object> symbolicToConcrete = new HashMap<>();

    public CheckPCVisitor(StateSpace stateSpace, SymSolveSolution solution, PathCondition pc) {
        vm = VM.getVM();
        JPFHeap = vm.getHeap();
        strategy = (LIBasedStrategy) LISSAShell.solvingStrategy;
        this.stateSpace = stateSpace;
        solutionVector = solution.getSolutionVector();
        accessedIndices = solution.getAccessedIndices();
        solutionRoot = solution.getBuildedSolution();
        this.pc = pc;
    }

    public void setRoot(int symbolicRootRef) {
        symbolicToConcrete.put(symbolicRootRef, solutionRoot);
    }

    public void setCurrentOwner(int symbolicOwnerRef, ElementInfo ownerEI, ClassInfo ownerClass, int id) {
        symbolicOwnerEI = ownerEI;
        currentSolutionOwner = symbolicToConcrete.get(symbolicOwnerRef);
        assert (currentSolutionOwner != null);
        currentOwnerClass = ownerClass;
    }

    public void setCurrentField(FieldInfo field, ClassInfo fieldClass) {
        currentField = field;
        currentFieldType = fieldClass;
        currentFieldName = field.getName();
        fieldAttr = symbolicOwnerEI.getFieldAttr(currentField);
    }

    public void visitedNewReferenceField(int symbolicFieldRef, int id) {
        Object solutionFieldValue = getSolutionFieldValue();
        assert (solutionFieldValue != null);
        symbolicToConcrete.put(symbolicFieldRef, solutionFieldValue);
    }

    private Object getSolutionFieldValue() {
        int indexInVector = stateSpace.getIndexInCandidateVector(currentSolutionOwner, currentFieldName);
        FieldDomain fieldDomain = stateSpace.getFieldDomain(indexInVector);
        int indexInFieldDomain = solutionVector[indexInVector];
        Object solutionFieldValue = ((ObjSet) fieldDomain).getObject(indexInFieldDomain);
        return solutionFieldValue;
    }

    public void visitedSymbolicPrimitiveField(Expression symbolicPrimitive) {
        assert (currentField != null);
        assert (symbolicPrimitive != null);

        int indexInVector = stateSpace.getIndexInCandidateVector(currentSolutionOwner, currentFieldName);
        FieldDomain fieldDomain = stateSpace.getFieldDomain(indexInVector);
        int indexInFieldDomain = solutionVector[indexInVector];

        if (accessedIndices.contains(indexInVector)) {

            Class<?> clsOfField = fieldDomain.getClassOfField();
            if (clsOfField == int.class) {
                assert (currentField.isIntField());
                assert (symbolicPrimitive instanceof SymbolicInteger);
                int value = ((IntSet) fieldDomain).getInt(indexInFieldDomain);
                IntegerConstant constant = new IntegerConstant(value);
                pc._addDet(Comparator.EQ, symbolicPrimitive, constant);

            } else if (clsOfField == boolean.class) {
                assert (currentField.isBooleanField());
                assert (symbolicPrimitive instanceof SymbolicInteger);
                boolean boolValue = ((BooleanSet) fieldDomain).getBoolean(indexInFieldDomain);
                int value = 0;
                if (boolValue)
                    value = 1;
                IntegerConstant constant = new IntegerConstant(value);
                pc._addDet(Comparator.EQ, symbolicPrimitive, constant);
            } else {
                assert (false); // TODO: add support for other types, String, Long, etc.
            }

            if (!pc.simplify()) {
                // UNSAT
                isAborted = true;
            }
        }
    }

    public boolean isIgnoredField() {
        String currentOwnerClassName = currentOwnerClass.getName();
        return !strategy.isFieldTracked(currentOwnerClassName, currentFieldName);
    }

    @Override
    public void visitedSymbolicReferenceField() {
    }

    @Override
    public void visitedNullReferenceField() {
    }

    @Override
    public void visitedExistentReferenceField(int symbolicFieldRef, int id) {
    }

    @Override
    public boolean isAborted() {
        return isAborted;
    }
}
