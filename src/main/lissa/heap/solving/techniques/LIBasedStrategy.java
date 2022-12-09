package lissa.heap.solving.techniques;

import gov.nasa.jpf.vm.ThreadInfo;
import lissa.heap.SymbolicInputHeapLISSA;

public abstract class LIBasedStrategy extends SolvingStrategy {

    public abstract boolean checkHeapSatisfiability(ThreadInfo ti, SymbolicInputHeapLISSA symInputHeap);

    public abstract long getSolvingTime();

    public abstract boolean isClassInBounds(String fieldSimpleClassName);

    public abstract Integer getBoundForClass(String fieldSimpleClassName);

}
