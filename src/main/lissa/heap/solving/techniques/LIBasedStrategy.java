package lissa.heap.solving.techniques;

import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.ThreadInfo;
import lissa.heap.SymbolicInputHeapLISSA;

public abstract class LIBasedStrategy extends SolvingStrategy {

    public abstract long getSolvingTime();

    public abstract boolean isClassInBounds(String fieldSimpleClassName);

    public abstract Integer getBoundForClass(String fieldSimpleClassName);

    public Instruction handleLazyInitializationStep(ThreadInfo ti, Instruction currentInstruction,
            Instruction nextInstruction, SymbolicInputHeapLISSA symInputHeap) {
        return nextInstruction;
    }

}
