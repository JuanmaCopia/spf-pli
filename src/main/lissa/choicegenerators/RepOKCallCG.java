package lissa.choicegenerators;

import lissa.LISSAShell;
import lissa.heap.SymbolicInputHeapLISSA;
import lissa.heap.solving.techniques.NT;
import symsolve.vector.SymSolveSolution;

public class RepOKCallCG extends RepOKCallChoiceGenerator {

    HeapChoiceGeneratorLISSA curHeapCG;
    PCChoiceGeneratorLISSA curPCCG;
    SymSolveSolution candidateHeapSolution;
    SymbolicInputHeapLISSA symInputHeap;
    boolean isLazyStep;

    int buildedObjectRef;

    NT stg;

    public RepOKCallCG(String id, SymbolicInputHeapLISSA symInputHeap, HeapChoiceGeneratorLISSA curHeapCG,
            PCChoiceGeneratorLISSA pcCG, SymSolveSolution solution, boolean isLazyStep) {
        super(id);
        this.stg = (NT) LISSAShell.solvingStrategy;
        this.symInputHeap = symInputHeap;
        this.candidateHeapSolution = solution;
        this.curHeapCG = curHeapCG;
        this.curPCCG = pcCG;
        this.isLazyStep = isLazyStep;
    }

    @Override
    public boolean hasNextSolution() {
        assert (candidateHeapSolution != null);
        candidateHeapSolution = stg.getNextSolution(ti, candidateHeapSolution, symInputHeap);
        if (candidateHeapSolution == null) {
            stg.countPrunedBranch();
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
            if (isLazyStep) {
                // Cache Solution and repOK Path Condition
                curHeapCG.setCurrentBuildedObject(buildedObjectRef);
                curHeapCG.setCurrentSolution(candidateHeapSolution);
                curHeapCG.setCurrentRepOKPathCondition(repOKPathCondition);
            } else {
                curPCCG.setCurrentBuildedObject(buildedObjectRef);
                curPCCG.setCurrentSolution(candidateHeapSolution);
                curPCCG.setCurrentRepOKPathCondition(repOKPathCondition);
            }
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
