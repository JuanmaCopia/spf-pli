package lissa.heap.solving.techniques;

import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.ThreadInfo;
import lissa.heap.SymbolicInputHeapLISSA;
import lissa.heap.SymbolicReferenceInput;

public abstract class LIBasedStrategy extends SolvingStrategy {

    public abstract boolean checkHeapSatisfiability(ThreadInfo ti, SymbolicInputHeapLISSA symInputHeap);

    public abstract long getSolvingTime();

    public abstract boolean isClassInBounds(String fieldSimpleClassName);

    public abstract Integer getBoundForClass(String fieldSimpleClassName);

    public abstract Instruction getNextInstructionToGETFIELD(ThreadInfo ti, Instruction getfield,
            SymbolicReferenceInput symRefInput);

    public abstract Instruction getNextInstructionToPrimitiveBranching(ThreadInfo ti, Instruction ins,
            SymbolicReferenceInput symRefInput);

}
