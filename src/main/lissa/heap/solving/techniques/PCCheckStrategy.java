package lissa.heap.solving.techniques;

import lissa.heap.cg.PCChoiceGeneratorLISSA;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.ThreadInfo;
import lissa.heap.SymbolicInputHeapLISSA;
import symsolve.vector.SymSolveSolution;

public interface PCCheckStrategy {

    boolean isRepOKExecutionMode();

    void startRepOKExecutionMode();

    void stopRepOKExecutionMode();

    long getRepOKSolvingTime();

    void countPrunedBranch();

    int getPrunedBranchCount();

    Instruction handlePrimitiveBranch(ThreadInfo ti, Instruction currentInstruction, Instruction nextInstruction,
            PCChoiceGeneratorLISSA cg);

    SymSolveSolution getNextSolution(ThreadInfo ti, SymSolveSolution previousSolution,
            SymbolicInputHeapLISSA symInputHeap);

    public boolean isSatWithRespectToPathCondition(ThreadInfo ti, SymSolveSolution candidateSolution,
            SymbolicInputHeapLISSA symInputHeap);

}
