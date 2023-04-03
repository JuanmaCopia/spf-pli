package lissa.heap.testgen;

import java.util.Map;

import gov.nasa.jpf.vm.MJIEnv;
import korat.finitization.impl.StateSpace;
import lissa.heap.SymbolicInputHeapLISSA;
import lissa.heap.solving.solver.SymSolveHeapSolver;
import symsolve.candidates.traversals.BFSCandidateTraversal;
import symsolve.candidates.traversals.CandidateTraversal;
import symsolve.vector.SymSolveSolution;

public class HeapSolutionTestGen {

    StateSpace stateSpace;
    SymSolveHeapSolver heapSolver;

    public HeapSolutionTestGen(StateSpace statespace, SymSolveHeapSolver heapSolver) {
        this.stateSpace = statespace;
        this.heapSolver = heapSolver;
    }

    public void generateTestCase(MJIEnv env, int objRef, SymbolicInputHeapLISSA symInputHeap,
            SymSolveSolution solution) {
        CandidateTraversal traverser = new BFSCandidateTraversal(stateSpace);

        Map<Object, Integer> symSolveToSymbolic = symInputHeap.getImplicitInputThis()
                .getSymSolveToSymbolicMap(stateSpace, solution);

        TestGenVisitor visitor = new TestGenVisitor(env, objRef, symInputHeap, solution, symSolveToSymbolic);
        traverser.traverse(solution.getSolutionVector(), visitor);
    }
}
