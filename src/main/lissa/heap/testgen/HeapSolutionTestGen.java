package lissa.heap.testgen;

import gov.nasa.jpf.symbc.numeric.PathCondition;
import gov.nasa.jpf.vm.MJIEnv;
import korat.finitization.impl.StateSpace;
import lissa.heap.SymbolicInputHeapLISSA;
import lissa.heap.solving.solver.SymSolveHeapSolver;
import symsolve.vector.SymSolveSolution;

public class HeapSolutionTestGen {

    StateSpace stateSpace;
    SymSolveHeapSolver heapSolver;

    int testCaseId = 0;

    public HeapSolutionTestGen(StateSpace statespace, SymSolveHeapSolver heapSolver) {
        this.stateSpace = statespace;
        this.heapSolver = heapSolver;
    }

    public void generateTestCase(MJIEnv env, SymbolicInputHeapLISSA symInputHeap, SymSolveSolution solution,
            PathCondition repOKPathCondition) {

    }
}
