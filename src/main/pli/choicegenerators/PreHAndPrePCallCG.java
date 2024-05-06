
package pli.choicegenerators;

public class PreHAndPrePCallCG extends LaunchSymbolicExecCG {

    public PreHAndPrePCallCG(String id, PLIChoiceGenerator curCG) {
        super(id, curCG);
    }

    @Override
    public boolean allRepOKPathsReturnedFalse() {
        if (pathReturningTrueFound) {
            if (isPathValidityCheck)
                strategy.countValidPath();
            else {
                // Cache Solution and repOK Path Condition
                curCG.setCurrentTestCode(testCode);
                curCG.setCurrentRepOKPathCondition(repOKPathCondition);
            }
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
