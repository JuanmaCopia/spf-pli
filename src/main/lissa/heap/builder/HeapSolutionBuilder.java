package lissa.heap.builder;

import gov.nasa.jpf.vm.MJIEnv;
import korat.finitization.impl.StateSpace;
import lissa.heap.SymHeapHelper;
import lissa.heap.SymbolicInputHeapLISSA;
import lissa.heap.cg.RepOKCallCG;
import lissa.heap.solving.solver.SymSolveHeapSolver;
import symsolve.candidates.traversals.BFSCandidateTraversal;
import symsolve.candidates.traversals.CandidateTraversal;
import symsolve.vector.SymSolveSolution;

public class HeapSolutionBuilder {

    StateSpace stateSpace;
    SymSolveHeapSolver heapSolver;

    public HeapSolutionBuilder(StateSpace statespace, SymSolveHeapSolver heapSolver) {
        this.stateSpace = statespace;
        this.heapSolver = heapSolver;
    }

    public void buildSolution(MJIEnv env, int objRef) {
        SymbolicInputHeapLISSA symInputHeap = SymHeapHelper.getSymbolicInputHeap(env.getVM());
        assert (symInputHeap != null);

        CandidateTraversal traverser = new BFSCandidateTraversal(stateSpace);

        String cgID = "repOKCG";
        RepOKCallCG repOKCG = env.getSystemState().getCurrentChoiceGenerator(cgID, RepOKCallCG.class);
        SymSolveSolution solution = repOKCG.getCandidateHeapSolution();
        assert (solution != null);

        HeapSolutionVisitor visitor = new HeapSolutionVisitor(env, objRef, symInputHeap, solution);
        traverser.traverse(solution.getSolutionVector(), visitor);
    }
}
