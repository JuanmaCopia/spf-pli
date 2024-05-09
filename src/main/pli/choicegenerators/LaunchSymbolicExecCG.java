package pli.choicegenerators;

import gov.nasa.jpf.symbc.numeric.PathCondition;
import gov.nasa.jpf.vm.ChoiceGeneratorBase;
import pli.LISSAShell;
import pli.heap.SymbolicInputHeapLISSA;
import pli.heap.solving.techniques.LIBasedStrategy;
import symsolve.vector.SymSolveSolution;

public abstract class LaunchSymbolicExecCG extends ChoiceGeneratorBase<Integer> {

    LIBasedStrategy strategy = (LIBasedStrategy) LISSAShell.solvingStrategy;
    PLIChoiceGenerator curCG;
    // boolean isLazyStep;
    int repOKExecutions = 0;
    boolean pathReturningTrueFound = false;
    boolean isPathValidityCheck = false;
    PathCondition repOKPathCondition;
    String testCode = null;
    int buildedObjectRef;

    SymSolveSolution candidateHeapSolution;
    SymbolicInputHeapLISSA partialHeap;

    public LaunchSymbolicExecCG(String id, PLIChoiceGenerator curCG) {
        super(id);
        this.curCG = curCG;
        if (curCG == null)
            isPathValidityCheck = true;

        strategy.startRepOKExecutionMode();
    }

    public abstract boolean allRepOKPathsReturnedFalse();

    public abstract boolean hasNextSolution();

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

    public boolean executed() {
        return repOKExecutions > 0;
    }

    public void setExecuted() {
        repOKExecutions++;
    }

    public void pathReturningTrueFound() {
        pathReturningTrueFound = true;
    }

    public PathCondition getRepOKPathCondition() {
        return repOKPathCondition;
    }

    public void markAsPathValidityCheck() {
        isPathValidityCheck = true;
    }

    public void setRepOKPathCondition(PathCondition pc) {
        if (pc == null)
            pc = new PathCondition();
        repOKPathCondition = pc;
    }

    public void setTestCode(String code) {
        testCode = code;
    }

    @Override
    public Integer getNextChoice() {
        return 0;
    }

    @Override
    public Class<Integer> getChoiceType() {
        return Integer.class;
    }

    @Override
    public void advance() {
    }

    @Override
    public void reset() {
    }

    @Override
    public int getTotalNumberOfChoices() {
        return 0;
    }

    @Override
    public int getProcessedNumberOfChoices() {
        return 0;
    }

    public void setBuildedObjectRef(int objvRef) {
        buildedObjectRef = objvRef;
    }

    public void setCandidateHeapSolution(SymSolveSolution solution) {
        candidateHeapSolution = solution;
    }

    public SymSolveSolution getCandidateHeapSolution() {
        return candidateHeapSolution;
    }

    public void setSymbolicInputHeap(SymbolicInputHeapLISSA partiallySymbolicHeap) {
        partialHeap = partiallySymbolicHeap;
    }

    public SymbolicInputHeapLISSA getSymbolicInputHeap() {
        return partialHeap;
    }

}
