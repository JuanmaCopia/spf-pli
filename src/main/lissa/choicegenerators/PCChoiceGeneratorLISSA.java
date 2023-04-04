package lissa.choicegenerators;

import java.util.HashMap;

import gov.nasa.jpf.symbc.numeric.PCChoiceGenerator;
import gov.nasa.jpf.symbc.numeric.PathCondition;
import symsolve.vector.SymSolveSolution;

public class PCChoiceGeneratorLISSA extends PCChoiceGenerator {

    HashMap<Integer, PathCondition> repOKPathConditionCache = new HashMap<>();
    HashMap<Integer, SymSolveSolution> solutionsCache = new HashMap<>();
    HashMap<Integer, Integer> buildedObjectsCache = new HashMap<>();

    public PCChoiceGeneratorLISSA(String id, int size) {
        super(id, size);
    }

    public PCChoiceGeneratorLISSA(int size) {
        super(size);
    }

    public PCChoiceGeneratorLISSA(String id, int min, int max) {
        super(id, min, max);
    }

    public PCChoiceGeneratorLISSA(int min, int max) {
        super(min, max);
    }

    public PCChoiceGeneratorLISSA(String id, int min, int max, int delta) {
        super(id, min, max, delta);
    }

    public PCChoiceGeneratorLISSA(int min, int max, int delta) {
        super(min, max, delta);
    }

    public void setCurrentRepOKPathCondition(PathCondition pc) {
        repOKPathConditionCache.put(getNextChoice(), pc.make_copy());
    }

    public PathCondition getCurrentRepOKPathCondition() {
        PathCondition pc;

        pc = repOKPathConditionCache.get(getNextChoice());
        if (pc != null) {
            return pc.make_copy();
        } else {
            return null;
        }
    }

    public void setCurrentSolution(SymSolveSolution candidateHeapSolution) {
        solutionsCache.put(getNextChoice(), candidateHeapSolution);
    }

    public SymSolveSolution getCurrentSolution() {
        return solutionsCache.get(getNextChoice());
    }

    public void setCurrentBuildedObject(int buildedObjectRef) {
        buildedObjectsCache.put(getNextChoice(), buildedObjectRef);
    }

    public int getCurrentBuildedObject() {
        return buildedObjectsCache.get(getNextChoice());
    }

}
