package lissa.choicegenerators;

import gov.nasa.jpf.symbc.heap.HeapChoiceGenerator;
import gov.nasa.jpf.symbc.numeric.PathCondition;
import symsolve.vector.SymSolveSolution;

public class HeapChoiceGeneratorLISSA extends HeapChoiceGenerator {

    SymSolveSolution[] solutionsCache;
    PathCondition[] repOKPathConditionCache;

    public HeapChoiceGeneratorLISSA(int size) {
        super(size);
        solutionsCache = new SymSolveSolution[size];
        repOKPathConditionCache = new PathCondition[size];
    }

    public void setCurrentSolution(SymSolveSolution solution) {
        solutionsCache[getNextChoice()] = solution;
    }

    public SymSolveSolution getCurrentSolution() {
        return solutionsCache[getNextChoice()];
    }

    public void setCurrentRepOKPathCondition(PathCondition repOKPC) {
        repOKPathConditionCache[getNextChoice()] = repOKPC;
    }

    public PathCondition getCurrentRepOKPathCondition() {
        return repOKPathConditionCache[getNextChoice()];
    }

}
