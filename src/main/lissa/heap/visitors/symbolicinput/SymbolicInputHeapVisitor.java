package lissa.heap.visitors.symbolicinput;

import gov.nasa.jpf.symbc.numeric.Expression;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.FieldInfo;

public interface SymbolicInputHeapVisitor {

    public void visitedSymbolicReferenceField();

    public void visitedNullReferenceField();

    public void visitedNewReferenceField(int symbolicFieldRef, int id);

    public void visitedExistentReferenceField(int symbolicFieldRef, int id);

    public void visitedSymbolicPrimitiveField(Expression symbolicPrimitive);

    public void setCurrentOwner(int symbolicOwnerRef, ElementInfo ownerEI, ClassInfo ownerClass, int id);

    public void setCurrentField(FieldInfo field, ClassInfo fieldClass);

    public boolean isIgnoredField();

    public void setRoot(int symbolicRootRef);

    public boolean isAborted();
}
