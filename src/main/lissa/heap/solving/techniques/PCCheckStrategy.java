package lissa.heap.solving.techniques;

import gov.nasa.jpf.symbc.numeric.PathCondition;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.ThreadInfo;
import lissa.heap.SymbolicInputHeapLISSA;
import symsolve.vector.SymSolveSolution;

public interface PCCheckStrategy {

    Instruction getNextInstructionToGETFIELD(ThreadInfo ti, Instruction currentInstruction, Instruction nextInstruction,
            SymbolicInputHeapLISSA symInputHeap);

    Instruction getNextInstructionToPrimitiveBranching(ThreadInfo ti, Instruction currentInstruction,
            Instruction nextInstruction, PathCondition pc);

    boolean isRepOKExecutionMode();

    void startRepOKExecutionMode();

    void stopRepOKExecutionMode();

    long getRepOKSolvingTime();

    void countPrunedBranch();

    int getPrunedBranchCount();

    SymSolveSolution getNextSolution(ThreadInfo ti, SymSolveSolution previousSolution,
            SymbolicInputHeapLISSA symInputHeap);

}
