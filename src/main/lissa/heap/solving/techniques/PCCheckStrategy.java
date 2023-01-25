package lissa.heap.solving.techniques;

import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.ThreadInfo;
import lissa.heap.SymbolicInputHeapLISSA;

public interface PCCheckStrategy {

    public Instruction getNextInstructionToGETFIELD(ThreadInfo ti, Instruction currentInstruction,
            Instruction nextInstruction, SymbolicInputHeapLISSA symInputHeap);

    public Instruction getNextInstructionToPrimitiveBranching(ThreadInfo ti, Instruction currentInstruction,
            Instruction nextInstruction);

}
