
package pli.choicegenerators;

import symsolve.vector.SymSolveSolution;

public class XCG extends LaunchSymbolicExecCG {

    boolean prePWithPartialHeapExecuted = false;

    public XCG(String id, PLIChoiceGenerator curCG, SymSolveSolution solution) {
        super(id, curCG);
        this.curCG = curCG;
        this.candidateHeapSolution = solution;
    }

    @Override
    public boolean allRepOKPathsReturnedFalse() {
        if (pathReturningTrueFound) {
            setDone();
            return false;
        }
        return true;
    }

    @Override
    public boolean hasNextSolution() {
        return false;
    }

    public void setPrePWithPartialHeapExecuted() {
        prePWithPartialHeapExecuted = true;
    }

    public boolean isPrePWithPartialHeapExecuted() {
        return prePWithPartialHeapExecuted;
    }

}
