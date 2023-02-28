package lissa.choicegenerators;

import gov.nasa.jpf.vm.ChoiceGeneratorBase;
import lissa.LISSAShell;
import lissa.heap.solving.techniques.REPOKSOLVER;

public class RepokCallWithLazyInitCG extends ChoiceGeneratorBase<Integer> {

    REPOKSOLVER strategy = (REPOKSOLVER) LISSAShell.solvingStrategy;;

    boolean executed = false;
    boolean pathReturningTrueFound = false;

    public RepokCallWithLazyInitCG(String id) {
        super(id);
        strategy.startRepOKExecutionMode();
    }

    public boolean allRepOKPathsReturnedFalse() {
        if (pathReturningTrueFound) {
            setDone();
        }

        return !pathReturningTrueFound;
    }

    public void pathReturningTrueFound() {
        pathReturningTrueFound = true;
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
        executed = false;
    }

    @Override
    public int getTotalNumberOfChoices() {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getProcessedNumberOfChoices() {
        if (executed)
            return 1;
        return 0;
    }

    @Override
    public Class<Integer> getChoiceType() {
        return Integer.class;
    }

    public boolean executed() {
        return executed;
    }

}
