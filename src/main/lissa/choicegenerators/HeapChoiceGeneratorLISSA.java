package lissa.choicegenerators;

import gov.nasa.jpf.symbc.numeric.PathCondition;
import gov.nasa.jpf.vm.choice.IntIntervalGenerator;
import lissa.heap.SymbolicInputHeapLISSA;
import symsolve.vector.SymSolveSolution;

public class HeapChoiceGeneratorLISSA extends IntIntervalGenerator implements PLIChoiceGenerator {

    protected PathCondition[] PCheap; // maintains constraints on the heap: one PC per choice
    protected SymbolicInputHeapLISSA[] symInputHeap; // maintains list of input symbolic nodes; one list per choice

    SymSolveSolution[] solutionsCache;
    PathCondition[] repOKPathConditionCache;
    String[] testCodes;

    public HeapChoiceGeneratorLISSA(String id, int size) {
        super(id, 0, size - 1);
        PCheap = new PathCondition[size];
        symInputHeap = new SymbolicInputHeapLISSA[size];
        solutionsCache = new SymSolveSolution[size];
        repOKPathConditionCache = new PathCondition[size];
        testCodes = new String[size];
    }

    // sets the heap constraints for the current choice
    public void setCurrentPCheap(PathCondition pc) {
        PCheap[getNextChoice()] = pc;

    }

    // returns the heap constraints for the current choice
    public PathCondition getCurrentPCheap() {
        PathCondition pc;

        pc = PCheap[getNextChoice()];
        if (pc != null) {
            return pc.make_copy();
        } else {
            return null;
        }
    }

    // sets the heap constraints for the current choice
    public void setCurrentSymInputHeap(SymbolicInputHeapLISSA ih) {
        symInputHeap[getNextChoice()] = ih;

    }

    // returns the heap constraints for the current choice
    public SymbolicInputHeapLISSA getCurrentSymInputHeap() {
        SymbolicInputHeapLISSA ih;

        ih = symInputHeap[getNextChoice()];
        if (ih != null) {
            return ih.make_copy();
        } else {
            return null;
        }
    }

    public void setCurrentHeapSolution(SymSolveSolution solution) {
        solutionsCache[getNextChoice()] = solution;
    }

    public SymSolveSolution getCurrentHeapSolution() {
        return solutionsCache[getNextChoice()];
    }

    public void setCurrentRepOKPathCondition(PathCondition repOKPC) {
        repOKPathConditionCache[getNextChoice()] = repOKPC.make_copy();
    }

    public PathCondition getCurrentRepOKPathCondition() {
        PathCondition pc = repOKPathConditionCache[getNextChoice()];
        if (pc != null)
            return pc.make_copy();
        return null;
    }

    public void setCurrentTestCode(String code) {
        testCodes[getNextChoice()] = code;
    }

    public String getCurrentTestCode() {
        return testCodes[getNextChoice()];
    }

}
