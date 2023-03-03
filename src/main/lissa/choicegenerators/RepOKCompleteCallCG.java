
package lissa.choicegenerators;

public class RepOKCompleteCallCG extends RepOKCallChoiceGenerator {

    public RepOKCompleteCallCG(String id) {
        super(id);
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
