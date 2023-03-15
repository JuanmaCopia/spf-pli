package lissa.heap.visitors;

import gov.nasa.jpf.symbc.numeric.Expression;
import gov.nasa.jpf.vm.FieldInfo;

public interface SymbolicInputHeapVisitor2 {

    public void visitedSymbolicReferenceField();

    public void visitedNullReferenceField();

    public void visitedNewReferenceField(int symbolicFieldRef);

    public void visitedExistentReferenceField(int symbolicFieldRef);

    public void visitedSymbolicPrimitiveField(Expression symbolicPrimitive);

    public void setCurrentOwner(int symbolicOwnerRef);

    public void setCurrentField(FieldInfo field);

    public boolean isIgnoredField();

    public void setRoot(int symbolicRootRef);

    public boolean isAborted();
}
