
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

        if (!pathReturningTrueFound) {
//            String str = SymHeapHelper.getSymbolicInputHeap(VM.getVM()).getImplicitInputThis().toString();
//            System.out.println(" ==============  Invalid Path!!!!!!!  =============");
//            System.out.println(" Symbolic Heap:\n" + str);
        }
        return !pathReturningTrueFound;
    }

    @Override
    public boolean hasNextSolution() {
        return false;
    }

}
