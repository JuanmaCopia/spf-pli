package lissa.heap.builder;

import gov.nasa.jpf.vm.MJIEnv;
import korat.finitization.impl.StateSpace;
import lissa.heap.SymbolicReferenceInput;
import symsolve.candidates.traversals.BFSCandidateTraversal;
import symsolve.candidates.traversals.CandidateTraversal;

public class HeapSolutionBuilder {

    StateSpace stateSpace;

    public HeapSolutionBuilder(StateSpace statespace) {
        this.stateSpace = statespace;
    }

    public void buildSolution(MJIEnv env, int objRef, SymbolicReferenceInput symInput, int[] solutionVector) {
        CandidateTraversal traverser = new BFSCandidateTraversal(stateSpace);
        HeapSolutionVisitor visitor = new HeapSolutionVisitor(env, objRef, symInput);
        traverser.traverse(solutionVector, visitor);
    }
}
