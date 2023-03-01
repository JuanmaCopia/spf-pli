package lissa.choicegenerators;

import gov.nasa.jpf.symbc.numeric.PathCondition;
import gov.nasa.jpf.vm.ChoiceGeneratorBase;
import lissa.LISSAShell;
import lissa.heap.solving.techniques.LIBasedStrategy;

public abstract class RepOKCallChoiceGenerator extends ChoiceGeneratorBase<Integer> {

    LIBasedStrategy strategy = (LIBasedStrategy) LISSAShell.solvingStrategy;
    int repOKExecutions = 0;
    boolean pathReturningTrueFound = false;
    PathCondition repOKPathCondition;

    public RepOKCallChoiceGenerator(String id) {
        super(id);
        strategy.startRepOKExecutionMode();
    }

    public boolean allRepOKPathsReturnedFalse() {
        if (pathReturningTrueFound) {
            setDone();
        }
        return !pathReturningTrueFound;
    }

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

    public void setRepOKPathCondition(PathCondition pc) {
        if (pc == null)
            pc = new PathCondition();
        repOKPathCondition = pc;
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

}
