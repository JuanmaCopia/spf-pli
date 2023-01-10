package lissa.heap.builder;

import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.VM;
import symsolve.candidates.traversals.visitors.GenericCandidateVisitor;

public class HeapSolutionVisitor extends GenericCandidateVisitor {

    int objRef;
    ElementInfo eiRef;

    public HeapSolutionVisitor(int objRef) {
        eiRef = VM.getVM().getHeap().getModifiable(objRef);

        this.objRef = objRef;
    }

    @Override
    public void accessedVisitedReferenceField(Object fieldObject, int fieldObjectID) {
    }

    @Override
    public void accessedNullReferenceField(int fieldObjectID) {
    }

    @Override
    public void accessedNewReferenceField(Object fieldObject, int fieldObjectID) {
        //eiRef.setReferenceField("", 0);
    }

    @Override
    public void accessedPrimitiveField(int fieldObjectID) {
    }
}
