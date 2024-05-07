
package pli.choicegenerators;

import symsolve.vector.SymSolveSolution;

public class XCG extends LaunchSymbolicExecCG {

    public XCG(String id, PLIChoiceGenerator curCG, SymSolveSolution solution) {
        super(id, curCG);
        this.curCG = curCG;
        this.candidateHeapSolution = solution;
    }

    @Override
    public boolean allRepOKPathsReturnedFalse() {
        setDone();
        return !pathReturningTrueFound;
    }

    @Override
    public boolean hasNextSolution() {
        return false;
    }

}
