package lissa.heap.solving.techniques;

import gov.nasa.jpf.vm.ThreadInfo;
import lissa.heap.SymbolicInputHeapLISSA;

public class IFREPOK extends LISSA {

    @Override
    public boolean checkHeapSatisfiability(ThreadInfo ti, SymbolicInputHeapLISSA symInputHeap) {
        return true;
    }

}
