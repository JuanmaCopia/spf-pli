package pli.heap.solving.techniques;

import gov.nasa.jpf.symbc.numeric.PathCondition;
import gov.nasa.jpf.symbc.numeric.interp.SpecialSolverQueries;
import pli.heap.SymbolicReferenceInput;
import pli.heap.pathcondition.PathConditionUtils;
import symsolve.vector.SymSolveSolution;

public class INTERX extends X {

    SpecialSolverQueries interpolantSolver;

    public INTERX() {
        super();
        interpolantSolver = new SpecialSolverQueries();
    }

    @Override
    SymSolveSolution handleSatisfiabilityWithPathCondition(SymbolicReferenceInput symRefInput, PathCondition pc,
            SymSolveSolution solution) {
        while (solution != null) {
            PathCondition accessedPC = symRefInput.getAccessedFieldsPathCondition(stateSpace, solution);
            if (PathConditionUtils.isConjunctionSAT(accessedPC, pc))
                return solution;

            solution = null;
        }
        return solution;
    }

//    SymSolveSolution calculateSolutionUsingInterpolant(PathCondition programPC, PathCondition accessedPC) {
//        
//    }

}
