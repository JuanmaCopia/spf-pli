package lissa.heap.builder;

import java.util.HashMap;

import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.MJIEnv;
import gov.nasa.jpf.vm.VM;
import symsolve.candidates.traversals.visitors.GenericCandidateVisitor;

public class HeapSolutionVisitor extends GenericCandidateVisitor {

    MJIEnv env;

    ElementInfo rootElementInfo;
    ElementInfo currentObjectElementInfo;

    HashMap<Object, ElementInfo> mirrorMap = new HashMap<>();

    public HeapSolutionVisitor(MJIEnv env, int rootRef) {
        this.env = env;
        this.rootElementInfo = VM.getVM().getHeap().getModifiable(rootRef);
    }

    @Override
    public void setRoot(Object rootObject, int rootID) {
        super.setRoot(rootObject, rootID);
        mirrorMap.put(rootObject, rootElementInfo);
    }

    @Override
    public void setCurrentOwner(Object currentOwnerObject, int currentOwnerID) {
        super.setCurrentOwner(currentOwnerObject, currentOwnerID);
        currentObjectElementInfo = mirrorMap.get(currentOwnerObject);
    }

    @Override
    public void accessedVisitedReferenceField(Object fieldObject, int fieldObjectID) {
        ElementInfo mirrorFieldObject = mirrorMap.get(fieldObject);
        currentObjectElementInfo.setReferenceField(currentFieldName, mirrorFieldObject.getObjectRef());
    }

    @Override
    public void accessedNullReferenceField(int fieldObjectID) {
        currentObjectElementInfo.setReferenceField(currentFieldName, MJIEnv.NULL);
    }

    @Override
    public void accessedNewReferenceField(Object fieldObject, int fieldObjectID) {
        ElementInfo newObjectElementInfo = env.newElementInfo(currentFieldClassName);
        currentObjectElementInfo.setReferenceField(currentFieldName, newObjectElementInfo.getObjectRef());
        mirrorMap.put(fieldObject, newObjectElementInfo);

    }

    @Override
    public void accessedPrimitiveField(int fieldObjectID) {
    }
}
