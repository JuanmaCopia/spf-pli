
package lissa.choicegenerators;

import gov.nasa.jpf.vm.choice.IntIntervalGenerator;
import lissa.heap.solving.techniques.LIBasedStrategy;

public class PartialHeapBuildCG extends IntIntervalGenerator {

    LIBasedStrategy strategy;

    public PartialHeapBuildCG(String id, LIBasedStrategy strategy) {
        super(id, 0, 0);
        this.strategy = strategy;
        this.strategy.startRepOKExecutionMode();
    }

    @Override
    public boolean hasMoreChoices() {
        boolean hasMoreChoices = super.hasMoreChoices();
        if (!hasMoreChoices) {
            strategy.stopRepOKExecutionMode();
        }
        return hasMoreChoices;
    }

    @Override
    public void setDone() {
        super.setDone();
        strategy.stopRepOKExecutionMode();
    }

}
