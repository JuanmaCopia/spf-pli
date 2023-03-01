
package lissa.choicegenerators;

public class RepOKCompleteCallCG extends RepOKCallChoiceGenerator {

    boolean isPathValidityCheck = false;;

    public RepOKCompleteCallCG(String id, boolean isPathValidityCheck) {
        super(id);
        this.isPathValidityCheck = isPathValidityCheck;
    }

    @Override
    public boolean allRepOKPathsReturnedFalse() {
        if (pathReturningTrueFound) {
            setDone();
        }
        return !pathReturningTrueFound;
    }

    @Override
    public boolean hasMoreChoices() {
        if (executed()) {
            strategy.stopRepOKExecutionMode();
            return false;
        }
        return !isDone;
    }

    @Override
    public void pathReturningTrueFound() {
        super.pathReturningTrueFound();
        if (isPathValidityCheck)
            strategy.countValidPath();
    }

    @Override
    public boolean hasNextSolution() {
        return false;
    }

}
