package lissa.heap.builder;

import java.util.Map;

import gov.nasa.jpf.vm.MJIEnv;
import korat.finitization.impl.StateSpace;
import lissa.heap.SymbolicInputHeapLISSA;
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

    public void buildSolution(MJIEnv env, int objRef, SymbolicInputHeapLISSA symInputHeap, SymSolveSolution solution) {
        CandidateTraversal traverser = new BFSCandidateTraversal(stateSpace);

        Map<Object, Integer> symSolveToSymbolic = symInputHeap.getImplicitInputThis()
                .getSymSolveToSymbolicMap(stateSpace, solution);

        HeapSolutionVisitor visitor = new HeapSolutionVisitor(env, objRef, symInputHeap, solution, symSolveToSymbolic);
        traverser.traverse(solution.getSolutionVector(), visitor);
    }
}
