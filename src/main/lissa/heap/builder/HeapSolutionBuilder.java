package lissa.heap.builder;

import korat.finitization.impl.StateSpace;
import symsolve.candidates.traversals.BFSCandidateTraversal;
import symsolve.candidates.traversals.CandidateTraversal;

public class HeapSolutionBuilder {

    StateSpace stateSpace;

    public HeapSolutionBuilder(StateSpace statespace) {
        this.stateSpace = statespace;
    }

    public void buildSolution(int[] solutionVector) {
        CandidateTraversal traverser = new BFSCandidateTraversal(stateSpace);
        HeapSolutionVisitor visitor = new HeapSolutionVisitor();
        traverser.traverse(solutionVector, visitor);
    }
}
