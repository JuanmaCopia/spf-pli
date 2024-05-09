
package pli.choicegenerators;

import pli.LISSAShell;
import symsolve.vector.SymSolveSolution;

public class XCG extends LaunchSymbolicExecCG {

    boolean prePWithPartialHeapExecuted = false;

    public XCG(String id, PLIChoiceGenerator curCG, SymSolveSolution solution) {
        super(id, curCG);
        this.curCG = curCG;
        this.candidateHeapSolution = solution;
    }

    public boolean allPathsOfPrePConcreteHReturnedFalse() {
        if (pathReturningTrueFound) {
            setDone();
            assert (candidateHeapSolution != null);
            assert (repOKPathCondition != null);
            // Cache Solution and repOK Path Condition
            if (LISSAShell.configParser.generateTests)
                curCG.setCurrentTestCode(testCode);
            curCG.setCurrentHeapSolution(candidateHeapSolution);
            curCG.setCurrentRepOKPathCondition(repOKPathCondition);
            return false;
        }
        return true;
    }

    public boolean allPathsOfPrePPartialHReturnedFalse() {
        if (pathReturningTrueFound) {
            setDone();
            assert (candidateHeapSolution != null);
            assert (repOKPathCondition != null);
            // Cache Solution and repOK Path Condition
            if (LISSAShell.configParser.generateTests)
                curCG.setCurrentTestCode(testCode);
            curCG.setCurrentPartialHeapSolution(partialHeap);
            curCG.setCurrentHeapSolution(candidateHeapSolution);
            curCG.setCurrentRepOKPathCondition(repOKPathCondition);
            return false;
        }
        return true;
    }

    @Override
    public boolean hasNextSolution() {
        throw new RuntimeException("This method should not be invoked");
    }

    public void setPrePWithPartialHeapExecuted() {
        prePWithPartialHeapExecuted = true;
    }

    public boolean isPrePWithPartialHeapExecuted() {
        return prePWithPartialHeapExecuted;
    }

    @Override
    public boolean allRepOKPathsReturnedFalse() {
        throw new RuntimeException(
                "This Choice Generator must use the specialized methods for PrePPartial and prePConcrete");
    }

}
