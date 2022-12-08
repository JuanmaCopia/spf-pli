package lissa.heap.solving.techniques;

import gov.nasa.jpf.vm.ThreadInfo;
import lissa.heap.SymbolicInputHeapLISSA;
import lissa.heap.solving.config.ConfigParser;

public class DRIVER extends SolvingStrategy {

    public DRIVER(ConfigParser config) {
        this.config = config;
    }

    @Override
    public boolean isLazyInitializationBased() {
        return false;
    }

    @Override
    public boolean checkHeapSatisfiability(ThreadInfo ti, SymbolicInputHeapLISSA symInputHeap) {
        return true;
    }

}
