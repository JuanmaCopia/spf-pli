package lissa.heap.solving.techniques;

import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.ThreadInfo;
import lissa.heap.SymbolicInputHeapLISSA;

public interface PCCheckStrategy {

    boolean hasNextSolution(ThreadInfo ti);

    Instruction getNextInstructionToGETFIELD(ThreadInfo ti, Instruction currentInstruction, Instruction nextInstruction,
            SymbolicInputHeapLISSA symInputHeap);

    Instruction getNextInstructionToPrimitiveBranching(ThreadInfo ti, Instruction currentInstruction,
            Instruction nextInstruction);

    boolean isRepOKExecutionMode();

    void startRepOKExecutionMode();

    void stopRepOKExecutionMode();

    long getRepOKSolvingTime();

    void countPrunedBranch();

    int getPrunedBranchCount();

}
