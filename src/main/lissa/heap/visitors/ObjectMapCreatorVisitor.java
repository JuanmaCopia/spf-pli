package lissa.heap.visitors;

import java.util.HashMap;
import java.util.Map;

import gov.nasa.jpf.symbc.numeric.Expression;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.FieldInfo;
import gov.nasa.jpf.vm.Heap;
import gov.nasa.jpf.vm.VM;
import korat.finitization.impl.FieldDomain;
import korat.finitization.impl.ObjSet;
import korat.finitization.impl.StateSpace;
import korat.utils.IIntList;
import lissa.LISSAShell;
import lissa.heap.solving.techniques.LIBasedStrategy;
import symsolve.vector.SymSolveSolution;

public class ObjectMapCreatorVisitor implements SymbolicInputHeapVisitor2 {

    VM vm;
    Heap JPFHeap;

    LIBasedStrategy strategy;
    StateSpace stateSpace;

    int[] solutionVector;
    IIntList accessedIndices;
    Object solutionRoot;

    // Owner:
    int currentSymbolicOwnerRef;
    ElementInfo symbolicOwnerEI;
    ClassInfo currentOwnerClass;

    Object currentSolutionOwner;
    // int equivalentOwnerInSymHeap;

    // Field:
    FieldInfo currentField;
    ClassInfo currentFieldType;
    String currentFieldName;

    Object fieldAttr;

    boolean isAborted = false;

    Map<Integer, Object> symbolicToConcrete = new HashMap<>();
    Map<Object, Integer> concreteToSymbolic = new HashMap<>();

    public ObjectMapCreatorVisitor(StateSpace stateSpace, SymSolveSolution solution) {
        vm = VM.getVM();
        JPFHeap = vm.getHeap();
        strategy = (LIBasedStrategy) LISSAShell.solvingStrategy;
        this.stateSpace = stateSpace;
        solutionVector = solution.getSolutionVector();
        accessedIndices = solution.getAccessedIndices();
        solutionRoot = solution.getBuildedSolution();
    }

    public void setRoot(int symbolicRootRef) {
        symbolicToConcrete.put(symbolicRootRef, solutionRoot);
        concreteToSymbolic.put(solutionRoot, symbolicRootRef);
    }

    public void setCurrentOwner(int symbolicOwnerRef) {
        symbolicOwnerEI = JPFHeap.getModifiable(symbolicOwnerRef);
        currentSolutionOwner = symbolicToConcrete.get(symbolicOwnerRef);
        // equivalentOwnerInSymHeap = concreteToSymbolic.get(symbolicOwnerRef);
        assert (currentSolutionOwner != null);
        currentOwnerClass = symbolicOwnerEI.getClassInfo();
    }

    public void setCurrentField(FieldInfo field) {
        currentField = field;
        currentFieldType = field.getTypeClassInfo();
        currentFieldName = field.getName();
        fieldAttr = symbolicOwnerEI.getFieldAttr(currentField);
    }

    public void visitedNewReferenceField(int symbolicFieldRef) {
        Object solutionFieldValue = getSolutionFieldValue();
        assert (solutionFieldValue != null);
        symbolicToConcrete.put(symbolicFieldRef, solutionFieldValue);
        concreteToSymbolic.put(solutionFieldValue, symbolicFieldRef);
    }

    private Object getSolutionFieldValue() {
        int indexInVector = stateSpace.getIndexInCandidateVector(currentSolutionOwner, currentFieldName);
        FieldDomain fieldDomain = stateSpace.getFieldDomain(indexInVector);
        int indexInFieldDomain = solutionVector[indexInVector];
        Object solutionFieldValue = ((ObjSet) fieldDomain).getObject(indexInFieldDomain);
        return solutionFieldValue;
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
    public void visitedExistentReferenceField(int symbolicFieldRef) {
    }

    @Override
    public boolean isAborted() {
        return false;
    }

    @Override
    public void visitedSymbolicPrimitiveField(Expression symbolicPrimitive) {
    }

    public Map<Object, Integer> getConcreteToSymbolicMap() {
        return concreteToSymbolic;
    }
}
