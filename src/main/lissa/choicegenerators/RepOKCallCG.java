package lissa.choicegenerators;

import gov.nasa.jpf.symbc.numeric.PathCondition;
import gov.nasa.jpf.vm.ChoiceGeneratorBase;
import lissa.LISSAShell;
import lissa.heap.SymbolicInputHeapLISSA;
import lissa.heap.solving.techniques.NT;
import symsolve.vector.SymSolveSolution;

public class RepOKCallCG extends ChoiceGeneratorBase<Integer> {

    HeapChoiceGeneratorLISSA curHeapCG;

    boolean pathReturningTrueFound = false;
    int repOKExecutions = 0;

    SymSolveSolution candidateHeapSolution;
    PathCondition repOKPathCondition;

    SymbolicInputHeapLISSA symInputHeap;
    NT strategy;

    boolean isLazyStep;

    public RepOKCallCG(String id, SymbolicInputHeapLISSA symInputHeap, HeapChoiceGeneratorLISSA curHeapCG,
            SymSolveSolution solution, boolean isLazyStep) {
        super(id);
        repOKExecutions = 0;
        strategy = (NT) LISSAShell.solvingStrategy;
        this.symInputHeap = symInputHeap;
        candidateHeapSolution = solution;
        this.curHeapCG = curHeapCG;
        this.isLazyStep = isLazyStep;
        strategy.startRepOKExecutionMode();
    }

    public boolean hasNextSolution() {
        assert (candidateHeapSolution != null);
        if (repOKExecutions > 0) {
            candidateHeapSolution = strategy.getNextSolution(ti, candidateHeapSolution, symInputHeap);
            if (candidateHeapSolution == null) {
                strategy.countPrunedBranch();
                setDone();
                return false;
            }
        }

        repOKExecutions++;
        return true;
    }

    public boolean allRepOKPathsReturnedFalse() {
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

        return !pathReturningTrueFound;
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
        if (isDone) {
            strategy.stopRepOKExecutionMode();
        }
        return !isDone;
    }

    @Override
    public void setDone() {
        super.setDone();
        strategy.stopRepOKExecutionMode();
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

}
