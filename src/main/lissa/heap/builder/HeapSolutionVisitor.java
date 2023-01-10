package lissa.heap.builder;

import symsolve.candidates.traversals.visitors.GenericCandidateVisitor;

public class HeapSolutionVisitor extends GenericCandidateVisitor {

    @Override
    public void accessedVisitedReferenceField(Object fieldObject, int fieldObjectID) {
    }

    @Override
    public void accessedNullReferenceField(int fieldObjectID) {
    }

    @Override
    public void accessedNewReferenceField(Object fieldObject, int fieldObjectID) {
    }

    @Override
    public void accessedPrimitiveField(int fieldObjectID) {
    }
}
