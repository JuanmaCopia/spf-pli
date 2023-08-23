package pli.heap.visitors.symbolicinput;

import gov.nasa.jpf.symbc.numeric.Expression;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.FieldInfo;
import pli.LISSAShell;
import pli.heap.SymbolicReferenceInput.ObjectData;
import pli.heap.canonicalizer.VectorField;
import pli.heap.canonicalizer.VectorStructure;
import pli.heap.solving.techniques.LIBasedStrategy;
import pli.heap.visitors.HeapVisitor;

public class ReferenceFieldOnlyVisitor implements HeapVisitor {

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
    public void setCurrentOwner(ObjectData ownerData) {
        this.currentOwnerObjClassName = ownerData.type.getName();
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
    public void visitedNewReferenceField(ObjectData ownerData) {
        this.vector.setFieldAsConcrete(currentOwnerObjClassName, currentFieldName, ownerData.id + 1);
    }

    @Override
    public void visitedExistentReferenceField(ObjectData ownerData) {
        this.vector.setFieldAsConcrete(currentOwnerObjClassName, currentFieldName, ownerData.id + 1);
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

    @Override
    public void visitedConcretePrimitiveField() {
    }

    @Override
    public void visitFinished() {
    }

}
