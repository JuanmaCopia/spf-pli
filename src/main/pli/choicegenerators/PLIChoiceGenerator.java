package pli.choicegenerators;

import gov.nasa.jpf.symbc.numeric.PathCondition;
import pli.heap.SymbolicInputHeapLISSA;
import symsolve.vector.SymSolveSolution;

public interface PLIChoiceGenerator {

    void setCurrentPartialHeapSolution(SymbolicInputHeapLISSA patialHeap);

    SymbolicInputHeapLISSA getCurrentPartialHeapSolution();

    void setCurrentHeapSolution(SymSolveSolution solution);

    SymSolveSolution getCurrentHeapSolution();

    void setCurrentRepOKPathCondition(PathCondition repOKPC);

    PathCondition getCurrentRepOKPathCondition();

    void setCurrentTestCode(String code);

    String getCurrentTestCode();
}
