
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
            if (isPathValidityCheck)
                strategy.countValidPath();
        }
        setDone();
        return !pathReturningTrueFound;
    }

    @Override
    public boolean hasNextSolution() {
        return false;
    }

}
