package pli.heap.visitors;

import gov.nasa.jpf.symbc.numeric.Expression;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.FieldInfo;
import pli.heap.SymbolicReferenceInput.ObjectData;

public interface HeapVisitor {

    public void visitedSymbolicReferenceField();

    public void visitedNullReferenceField();

    public void visitedNewReferenceField(ObjectData ownerData);

    public void visitedExistentReferenceField(ObjectData ownerData);

    public void visitedSymbolicPrimitiveField(Expression symbolicPrimitive);

    public void visitedConcretePrimitiveField();

    public void setCurrentOwner(ObjectData ownerData);

    public void setCurrentField(FieldInfo field, ClassInfo fieldClass);

    public boolean isIgnoredField();

    public void setRoot(int symbolicRootRef);

    public boolean isAborted();

    public void visitFinished();
}
