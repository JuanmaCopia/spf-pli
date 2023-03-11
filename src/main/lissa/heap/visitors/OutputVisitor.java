package lissa.heap.visitors;

import java.util.HashMap;

import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.FieldInfo;
import lissa.heap.canonicalizer.VectorField;
import lissa.heap.canonicalizer.VectorStructure;

public class OutputVisitor implements SymbolicOutputHeapVisitor {

    protected String currentOwnerObjClassName;
    protected String currentFieldName;
    protected String currentFieldClassName;
    protected VectorStructure vector;

    public OutputVisitor(VectorStructure vector) {
        this.vector = vector;
    }

    @Override
    public void setCurrentOwner(ClassInfo ownerObjectClass, int currentObjID) {
        this.currentOwnerObjClassName = ownerObjectClass.getName();
    }

    @Override
    public void setCurrentField(ClassInfo fieldClass, FieldInfo field) {
        this.currentFieldName = field.getName();
        this.currentFieldClassName = fieldClass.getName();
    }

    @Override
    public boolean isIgnoredField() {
        throw new RuntimeException("Unimplemented method");
    }

    @Override
    public void resetCurrentField() {
        this.currentFieldName = null;
    }

    @Override
    public void resetCurrentOwner() {
        this.currentOwnerObjClassName = null;
    }

    @Override
    public void visitedNullReferenceField() {
        this.vector.setFieldAsConcrete(currentOwnerObjClassName, currentFieldName, VectorField.DEFAULT_VALUE);
    }

    @Override
    public void visitedNewReferenceField(int id) {
        this.vector.setFieldAsConcrete(currentOwnerObjClassName, currentFieldName, id);
    }

    @Override
    public void visitedExistentReferenceField(int id) {
        this.vector.setFieldAsConcrete(currentOwnerObjClassName, currentFieldName, id);
    }

    @Override
    public void visitedSymbolicReferenceField() {
        this.vector.setReferenceFieldAsSymbolic(currentOwnerObjClassName, currentFieldName);
    }

    @Override
    public void setMaxIdMap(HashMap<ClassInfo, Integer> maxIdMap) {
    }

    @Override
    public void visitedSymbolicPrimitiveField(FieldInfo fi) {
        vector.setPrimitiveFieldAsSymbolic(currentOwnerObjClassName, currentFieldName, null);

    }

}
