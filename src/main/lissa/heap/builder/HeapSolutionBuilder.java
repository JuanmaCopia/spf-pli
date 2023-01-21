package lissa.heap.builder;

import gov.nasa.jpf.vm.MJIEnv;
import korat.finitization.impl.StateSpace;
import lissa.heap.SymbolicReferenceInput;
import lissa.heap.solving.solver.SymSolveHeapSolver;
import symsolve.candidates.traversals.BFSCandidateTraversal;
import symsolve.candidates.traversals.CandidateTraversal;

public class HeapSolutionBuilder {

    StateSpace stateSpace;
    SymSolveHeapSolver heapSolver;

    public HeapSolutionBuilder(StateSpace statespace, SymSolveHeapSolver heapSolver) {
        this.stateSpace = statespace;
        this.heapSolver = heapSolver;
    }

    public void buildSolution(MJIEnv env, int objRef, SymbolicReferenceInput symInput, int[] solutionVector) {
        CandidateTraversal traverser = new BFSCandidateTraversal(stateSpace);
        HeapSolutionVisitor visitor = new HeapSolutionVisitor(env, objRef, symInput, heapSolver.getAccessedIndices());
        traverser.traverse(solutionVector, visitor);
    }
}
