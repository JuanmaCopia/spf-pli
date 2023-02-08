package lissa.choicegenerators;

import gov.nasa.jpf.symbc.heap.HeapChoiceGenerator;
import symsolve.vector.SymSolveSolution;

public class HeapChoiceGeneratorLISSA extends HeapChoiceGenerator {

    SymSolveSolution[] solutionsCache;

    public HeapChoiceGeneratorLISSA(int size) {
        super(size);
        solutionsCache = new SymSolveSolution[size];
    }

    public void setCurrentSolution(SymSolveSolution solution) {
        solutionsCache[getNextChoice()] = solution;
    }

    public SymSolveSolution getCurrentSolution() {
        return solutionsCache[getNextChoice()];
    }

}
