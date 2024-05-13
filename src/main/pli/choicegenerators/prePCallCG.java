package pli.choicegenerators;

import pli.LISSAShell;
import pli.heap.SymbolicInputHeapLISSA;
import pli.heap.solving.techniques.PLI;
import symsolve.vector.SymSolveSolution;

public class prePCallCG extends LaunchSymbolicExecCG {

    SymbolicInputHeapLISSA symInputHeap;
    PLI stg;

    public prePCallCG(String id, SymbolicInputHeapLISSA symInputHeap, SymSolveSolution solution,
            PLIChoiceGenerator curCG) {
        super(id, curCG);
        this.stg = (PLI) LISSAShell.solvingStrategy;
        this.symInputHeap = symInputHeap;
        this.candidateHeapSolution = solution;
        this.curCG = curCG;
    }

    @Override
    public boolean hasNextSolution() {
        assert (candidateHeapSolution != null);
        candidateHeapSolution = stg.getNextSolution(ti, candidateHeapSolution, symInputHeap);
        if (candidateHeapSolution == null) {
            // The branch is pruned
            setDone();
            return false;
        }
        repOKExecutions++;
        return true;
    }

    @Override
    public boolean allRepOKPathsReturnedFalse() {
        if (pathReturningTrueFound) {
            setDone();
            assert (candidateHeapSolution != null);
            assert (repOKPathCondition != null);
            // Cache Solution and repOK Path Condition
            if (LISSAShell.configParser.generateTests)
                curCG.setCurrentTestCode(testCode);
            curCG.setCurrentHeapSolution(candidateHeapSolution);
            curCG.setCurrentRepOKPathCondition(repOKPathCondition);
            return false;
        }

        return true;
    }

}
