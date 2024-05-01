package pli.heap.canonicalizer;

import gov.nasa.jpf.symbc.numeric.PathCondition;
import korat.finitization.impl.CVElem;
import pli.heap.SymbolicInputHeapLISSA;
import pli.heap.SymbolicReferenceInput;
import pli.heap.visitors.symbolicinput.ConstantPropagationVisitor;
import pli.heap.visitors.symbolicinput.ReferenceFieldOnlyVisitor;
import symsolve.vector.SymSolveVector;

public class Canonicalizer {

    VectorStructure vector;

    public Canonicalizer(CVElem[] structureList) {
        vector = new VectorStructure(structureList);
    }

    public SymSolveVector createVector(SymbolicInputHeapLISSA symInputHeap) {
        vector.resetVector();
        SymbolicReferenceInput symRefInput = symInputHeap.getImplicitInputThis();
        ReferenceFieldOnlyVisitor visitor = new ReferenceFieldOnlyVisitor(vector);
        symRefInput.acceptBFS(visitor);
        return new SymSolveVector(vector.getVectorAsIntArray(), vector.getFixedIndices());
    }

    public SymSolveVector createVector(SymbolicInputHeapLISSA symInputHeap, PathCondition pc) {
        vector.resetVector();
        SymbolicReferenceInput symRefInput = symInputHeap.getImplicitInputThis();
        ConstantPropagationVisitor visitor = new ConstantPropagationVisitor(vector, pc);
        symRefInput.acceptBFS(visitor);
        return new SymSolveVector(vector.getVectorAsIntArray(), vector.getFixedIndices());
    }

    public VectorStructure getVector() {
        return vector;
    }

}
