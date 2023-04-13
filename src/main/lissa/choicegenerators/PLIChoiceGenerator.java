package lissa.choicegenerators;

import gov.nasa.jpf.symbc.numeric.PathCondition;
import symsolve.vector.SymSolveSolution;

public interface PLIChoiceGenerator {

    void setCurrentSolution(SymSolveSolution solution);

    SymSolveSolution getCurrentSolution();

    void setCurrentRepOKPathCondition(PathCondition repOKPC);

    PathCondition getCurrentRepOKPathCondition();

    void setCurrentTestCode(String code);

    String getCurrentTestCode();
}
