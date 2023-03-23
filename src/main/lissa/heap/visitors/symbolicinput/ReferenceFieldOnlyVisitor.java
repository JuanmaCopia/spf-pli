package lissa.heap.visitors.symbolicinput;

import gov.nasa.jpf.symbc.numeric.Expression;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.FieldInfo;
import lissa.LISSAShell;
import lissa.heap.canonicalizer.VectorField;
import lissa.heap.canonicalizer.VectorStructure;
import lissa.heap.solving.techniques.LIBasedStrategy;

public class ReferenceFieldOnlyVisitor implements SymbolicInputHeapVisitor {

    String currentOwnerObjClassName;
    String currentFieldName;
    String currentFieldClassName;
    VectorStructure vector;
    LIBasedStrategy strategy;

    public ReferenceFieldOnlyVisitor(VectorStructure vector) {
        this.vector = vector;
        this.strategy = (LIBasedStrategy) LISSAShell.solvingStrategy;
    }

    @Override
    public void setCurrentOwner(int symbolicOwnerRef, ElementInfo ownerEI, ClassInfo ownerClass, int id) {
        this.currentOwnerObjClassName = ownerClass.getName();
    }

    @Override
    public void setCurrentField(FieldInfo field, ClassInfo fieldClass) {
        this.currentFieldName = field.getName();
        this.currentFieldClassName = fieldClass.getName();
    }

    @Override
    public boolean isIgnoredField() {
        return !strategy.isFieldTracked(currentOwnerObjClassName, currentFieldName);
    }

    @Override
    public void visitedNullReferenceField() {
        this.vector.setFieldAsConcrete(currentOwnerObjClassName, currentFieldName, VectorField.DEFAULT_VALUE);
    }

    @Override
    public void visitedNewReferenceField(int symbolicFieldRef, int id) {
        this.vector.setFieldAsConcrete(currentOwnerObjClassName, currentFieldName, id);
    }

    @Override
    public void visitedExistentReferenceField(int symbolicFieldRef, int id) {
        this.vector.setFieldAsConcrete(currentOwnerObjClassName, currentFieldName, id);
    }

    @Override
    public void visitedSymbolicReferenceField() {
        this.vector.setReferenceFieldAsSymbolic(currentOwnerObjClassName, currentFieldName);
    }

    @Override
    public void visitedSymbolicPrimitiveField(Expression symbolicVar) {
        this.vector.setPrimitiveFieldAsSymbolic(currentOwnerObjClassName, currentFieldName, symbolicVar);
    }

    @Override
    public void setRoot(int symbolicRootRef) {
    }

    @Override
    public boolean isAborted() {
        return false;
    }
}
