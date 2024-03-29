package pli.heap.visitors.symbolicinput;

import java.util.HashMap;
import java.util.Map;

import gov.nasa.jpf.symbc.heap.HeapNode;
import gov.nasa.jpf.symbc.numeric.Comparator;
import gov.nasa.jpf.symbc.numeric.Expression;
import gov.nasa.jpf.symbc.numeric.IntegerConstant;
import gov.nasa.jpf.symbc.numeric.PathCondition;
import gov.nasa.jpf.symbc.numeric.SymbolicInteger;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.FieldInfo;
import gov.nasa.jpf.vm.Heap;
import gov.nasa.jpf.vm.MJIEnv;
import pli.LISSAShell;
import pli.heap.SymHeapHelper;
import pli.heap.SymbolicInputHeapLISSA;
import pli.heap.SymbolicReferenceInput;
import pli.heap.SymbolicReferenceInput.ObjectData;
import pli.heap.solving.techniques.LIBasedStrategy;
import pli.heap.visitors.HeapVisitor;

public class PartialHeapBuilderVisitor implements HeapVisitor {

    LIBasedStrategy strategy;

    MJIEnv env;
    Heap JPFHeap;

    int newRootRef;

    // Owner:
    int currentOwnerRef;
    ElementInfo currentOwner;

    // Field:
    FieldInfo currentField;
    ClassInfo currentFieldType;
    Object fieldAttr;

    HeapNode[] prevSymRefs;
    int numPrevSymRefs;

    SymbolicInputHeapLISSA newSymbolicHeap;
    SymbolicReferenceInput newSymRefInput;
    PathCondition pcHeap;

    Map<Integer, Integer> objectMap = new HashMap<>();

    public PartialHeapBuilderVisitor(MJIEnv env, int newRootRef) {
        this.env = env;
        this.JPFHeap = env.getVM().getHeap();
        this.newRootRef = newRootRef;
        this.newSymbolicHeap = new SymbolicInputHeapLISSA();
        this.newSymRefInput = this.newSymbolicHeap.getImplicitInputThis();
        this.pcHeap = new PathCondition();
        this.strategy = (LIBasedStrategy) LISSAShell.solvingStrategy;
    }

    public void setRoot(int rootRef) {
        objectMap.put(rootRef, newRootRef);
        initializeSymbolicRoot();
    }

    public void setCurrentOwner(ObjectData symbolicOwnerData) {
        currentOwnerRef = objectMap.get(symbolicOwnerData.objRef);
        currentOwner = JPFHeap.getModifiable(currentOwnerRef);
        assert (currentOwner != null);
    }

    public void setCurrentField(FieldInfo field, ClassInfo fieldClass) {
        currentField = field;
        currentFieldType = fieldClass;
        fieldAttr = currentOwner.getFieldAttr(currentField);

        prevSymRefs = newSymbolicHeap.getNodesOfType(currentFieldType);
        numPrevSymRefs = prevSymRefs.length;
    }

    public void visitedSymbolicReferenceField() {
    }

    public void visitedNullReferenceField() {
        pcHeap._addDet(Comparator.EQ, (SymbolicInteger) fieldAttr, new IntegerConstant(-1));
        newSymRefInput.addReferenceField(currentOwnerRef, currentField, MJIEnv.NULL);
        currentOwner.setReferenceField(currentField, MJIEnv.NULL);
        currentOwner.setFieldAttr(currentField, null);
    }

    public void visitedExistentReferenceField(ObjectData ownerData) {
        int fieldRef = ownerData.objRef;
        Integer objRef = objectMap.get(fieldRef);
        pcHeap._addDet(Comparator.EQ, (SymbolicInteger) fieldAttr, newSymbolicHeap.getNode(fieldRef));
        newSymRefInput.addReferenceField(currentOwnerRef, currentField, objRef);
        currentOwner.setReferenceField(currentField, objRef);
        currentOwner.setFieldAttr(currentField, null);
    }

    public void visitedNewReferenceField(ObjectData ownerData) {
        int fieldRef = ownerData.objRef;
        int objRef = SymHeapHelper.addNewHeapNode(currentFieldType, env.getVM().getCurrentThread(), fieldAttr, pcHeap,
                newSymbolicHeap, numPrevSymRefs, prevSymRefs, currentOwner.isShared());

        objectMap.put(fieldRef, objRef);
        newSymRefInput.addReferenceField(currentOwnerRef, currentField, objRef);
        currentOwner.setReferenceField(currentField, objRef);
        currentOwner.setFieldAttr(currentField, null);
    }

    public void visitedSymbolicPrimitiveField(Expression symbolicPrimitive) {
        newSymRefInput.addPrimitiveSymbolicField(currentOwnerRef, currentField, symbolicPrimitive);
        currentOwner.setFieldAttr(currentField, symbolicPrimitive);
    }

    private void initializeSymbolicRoot() {
        ClassInfo ci = env.getClassInfo(newRootRef);
        ElementInfo eiRef = JPFHeap.getModifiable(newRootRef);
        FieldInfo[] fields = ci.getDeclaredInstanceFields();
        String refChain = "SYMBOLIC_ROOT" + "[" + newRootRef + "]";
        SymHeapHelper.initializeInstanceFields(env, fields, eiRef, refChain, newSymbolicHeap);

        ClassInfo typeClassInfo = eiRef.getClassInfo();
        SymbolicInteger newSymRef = new SymbolicInteger(refChain);
        HeapNode rootHeapNode = new HeapNode(newRootRef, typeClassInfo, newSymRef);

        newSymbolicHeap._add(rootHeapNode);
        newSymRefInput.setRootHeapNode(rootHeapNode);
        pcHeap._addDet(Comparator.NE, newSymRef, new IntegerConstant(-1));
    }

    public SymbolicInputHeapLISSA getNewSymbolicInputHeap() {
        return newSymbolicHeap;
    }

    public PathCondition getHeapPathCondition() {
        return pcHeap;
    }

    public boolean isIgnoredField() {
        String currentOwnerClassName = currentOwner.getClassInfo().getName();
        return !strategy.isFieldTracked(currentOwnerClassName, currentField.getName());
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
