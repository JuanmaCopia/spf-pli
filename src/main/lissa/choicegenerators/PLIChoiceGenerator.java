package lissa.choicegenerators;

import gov.nasa.jpf.symbc.numeric.PathCondition;
import symsolve.vector.SymSolveSolution;

public interface PLIChoiceGenerator {

    void setCurrentHeapSolution(SymSolveSolution solution);

    SymSolveSolution getCurrentHeapSolution();

    void setCurrentRepOKPathCondition(PathCondition repOKPC);

    PathCondition getCurrentRepOKPathCondition();

    void setCurrentTestCode(String code);

    String getCurrentTestCode();
}
