package lissa.choicegenerators;

import gov.nasa.jpf.symbc.numeric.PathCondition;
import gov.nasa.jpf.vm.ChoiceGeneratorBase;
import lissa.LISSAShell;
import lissa.heap.SymbolicInputHeapLISSA;
import lissa.heap.solving.techniques.NT;
import symsolve.vector.SymSolveSolution;

public class RepOKCallCG extends ChoiceGeneratorBase<Integer> {

    HeapChoiceGeneratorLISSA curHeapCG;
    public PCChoiceGeneratorLISSA currPCCG;
    public PathCondition programPC;

    boolean pathReturningTrueFound = false;
    int repOKExecutions = 0;

    SymSolveSolution candidateHeapSolution;
    PathCondition repOKPathCondition;

    SymbolicInputHeapLISSA symInputHeap;
    NT strategy;

    boolean isLazyStep;

    public RepOKCallCG(String id, SymbolicInputHeapLISSA symInputHeap, PCChoiceGeneratorLISSA currPCCG,
            HeapChoiceGeneratorLISSA curHeapCG, SymSolveSolution solution, boolean isLazyStep) {
        super(id);
        repOKExecutions = 0;
        strategy = (NT) LISSAShell.solvingStrategy;
        this.symInputHeap = symInputHeap;
        candidateHeapSolution = solution;
        this.curHeapCG = curHeapCG;
        this.isLazyStep = isLazyStep;
        if (currPCCG != null) {
            this.currPCCG = currPCCG;
            programPC = currPCCG.getCurrentPC();
        } else {
            this.programPC = new PathCondition();
        }
    }

    public boolean hasNextSolution() {
        assert (candidateHeapSolution != null);
        if (currPCCG != null)
            assert (strategy.isSatWithRespectToPathCondition(ti, candidateHeapSolution, currPCCG.getCurrentPC(),
                    symInputHeap));
        if (repOKExecutions > 0) {
            candidateHeapSolution = strategy.getNextSolution(ti, candidateHeapSolution, symInputHeap);
            if (candidateHeapSolution == null) {
                strategy.countPrunedBranch();
                setDone();
                return false;
            }
        }

        repOKExecutions++;
        strategy.startRepOKExecutionMode();
        return true;
    }

    public boolean allRepOKPathsReturnedFalse() {
        strategy.stopRepOKExecutionMode();

        if (pathReturningTrueFound) {
            setDone();
            if (isLazyStep) {
                // Cache Solution and repOK Path Condition
                assert (candidateHeapSolution != null);
                assert (repOKPathCondition != null);
                curHeapCG.setCurrentSolution(candidateHeapSolution);
                curHeapCG.setCurrentRepOKPathCondition(repOKPathCondition);
            }
        }

        resetProgramPathCondition();

        return !pathReturningTrueFound;
    }

    private void resetProgramPathCondition() {
        if (currPCCG != null)
            currPCCG.setCurrentPC(programPC);
    }

    public void pathReturningTrueFound() {
        pathReturningTrueFound = true;
    }

    public void setCandidateHeapSolution(SymSolveSolution solution) {
        candidateHeapSolution = solution;
    }

    public SymSolveSolution getCandidateHeapSolution() {
        return candidateHeapSolution;
    }

    @Override
    public Integer getNextChoice() {
        return 1;
    }

    @Override
    public boolean hasMoreChoices() {
        return !isDone;
    }

    @Override
    public void advance() {
    }

    @Override
    public void reset() {
        repOKExecutions = 0;
    }

    @Override
    public int getTotalNumberOfChoices() {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getProcessedNumberOfChoices() {
        return repOKExecutions;
    }

    @Override
    public Class<Integer> getChoiceType() {
        return Integer.class;
    }

    public PathCondition getRepOKPathCondition() {
        return repOKPathCondition;
    }

    public void setRepOKPathCondition(PathCondition pc) {
        if (pc == null)
            pc = new PathCondition();
        repOKPathCondition = pc;
    }

    public PCChoiceGeneratorLISSA getPCChoiceGenerator() {
        return currPCCG;
    }

}
