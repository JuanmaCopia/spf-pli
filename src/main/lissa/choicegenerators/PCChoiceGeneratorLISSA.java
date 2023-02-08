package lissa.choicegenerators;

import java.util.HashMap;

import gov.nasa.jpf.symbc.numeric.PCChoiceGenerator;
import gov.nasa.jpf.symbc.numeric.PathCondition;

public class PCChoiceGeneratorLISSA extends PCChoiceGenerator {

    protected HashMap<Integer, PathCondition> repOKPCMap = new HashMap<>();

    public PCChoiceGeneratorLISSA(int size) {
        super(size);
    }

    public PCChoiceGeneratorLISSA(int min, int max) {
        super(min, max);
    }

    public PCChoiceGeneratorLISSA(int min, int max, int delta) {
        super(min, max, delta);
    }

    public void setCurrentRepOKPC(PathCondition repOKPC) {
        repOKPCMap.put(getNextChoice(), repOKPC);
    }

    public PathCondition getCurrentRepOKPC() {
        PathCondition repOKPC;

        repOKPC = repOKPCMap.get(getNextChoice());
        if (repOKPC != null) {
            return repOKPC.make_copy();
        } else {
            return null;
        }
    }

}
