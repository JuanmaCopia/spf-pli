package lissa.choicegenerators;

import lissa.LISSAShell;
import lissa.heap.SymbolicInputHeapLISSA;
import lissa.heap.solving.techniques.PLI;
import symsolve.vector.SymSolveSolution;

public class RepOKCallCG extends RepOKCallChoiceGenerator {

    SymSolveSolution candidateHeapSolution;
    SymbolicInputHeapLISSA symInputHeap;

    int buildedObjectRef;

    PLI stg;

    public RepOKCallCG(String id, SymbolicInputHeapLISSA symInputHeap, SymSolveSolution solution,
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
            curCG.setCurrentTestCode(testCode);
            curCG.setCurrentHeapSolution(candidateHeapSolution);
            curCG.setCurrentRepOKPathCondition(repOKPathCondition);
        }

        return !pathReturningTrueFound;
    }

    public void setCandidateHeapSolution(SymSolveSolution solution) {
        candidateHeapSolution = solution;
    }

    public SymSolveSolution getCandidateHeapSolution() {
        return candidateHeapSolution;
    }

    public void setBuildedObjectRef(int objvRef) {
        buildedObjectRef = objvRef;
    }

}
