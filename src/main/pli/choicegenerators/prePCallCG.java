package pli.choicegenerators;

import gov.nasa.jpf.symbc.numeric.PathCondition;
import pli.LISSAShell;
import pli.heap.SymbolicInputHeapLISSA;
import pli.heap.solving.techniques.PLI;
import symsolve.vector.SymSolveSolution;

public class prePCallCG extends LaunchSymbolicExecCG {

    SymSolveSolution candidateHeapSolution;
    SymbolicInputHeapLISSA symInputHeap;

    PathCondition currentPC;

    int buildedObjectRef;

    PLI stg;

    public prePCallCG(String id, SymbolicInputHeapLISSA symInputHeap, SymSolveSolution solution,
            PLIChoiceGenerator curCG, PathCondition currentPC) {
        super(id, curCG);
        this.stg = (PLI) LISSAShell.solvingStrategy;
        this.symInputHeap = symInputHeap;
        this.candidateHeapSolution = solution;
        this.curCG = curCG;
        this.currentPC = currentPC;
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
        } else {
//            System.err.println("\n----------------------------------------------------\n");
//            System.err.println("Initial PC: " + currentPC);
//            System.err.println("\nRepok pc returning false: " + repOKPathCondition);
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
