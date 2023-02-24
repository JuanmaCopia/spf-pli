package lissa.heap.visitors;

import gov.nasa.jpf.symbc.numeric.Expression;
import gov.nasa.jpf.symbc.numeric.SymbolicInteger;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.FieldInfo;
import gov.nasa.jpf.vm.Heap;
import gov.nasa.jpf.vm.MJIEnv;

public class PartialHeapBuilderVisitor {

    Heap JPFHeap;
    ElementInfo currentOwner;
    String currentRefChain;
    FieldInfo currentField;

    public PartialHeapBuilderVisitor(MJIEnv env) {
        this.JPFHeap = env.getVM().getHeap();
    }

    public void setCurrentOwner(int ownerRef, String refChain) {
        currentOwner = JPFHeap.getModifiable(ownerRef);
        currentRefChain = refChain;
        assert (currentOwner != null);
    }

    public void setCurrentField(FieldInfo field) {
        currentField = field;
    }

    public void visitedSymbolicReferenceField() {
        currentOwner.setFieldAttr(currentField, new SymbolicInteger(currentRefChain + "." + currentField.getName()));
    }

    public void visitedNullReferenceField() {
        currentOwner.setReferenceField(currentField, MJIEnv.NULL);
    }

    public void visitedExistentReferenceField(int fieldRef) {
        currentOwner.setReferenceField(currentField, fieldRef);
    }

    public void visitedNewReferenceField(int fieldRef) {
        currentOwner.setReferenceField(currentField, fieldRef);
    }

    public void visitedSymbolicPrimitiveField(Expression symbolicPrimitive) {
        currentOwner.setFieldAttr(currentField, symbolicPrimitive);
    }

}
