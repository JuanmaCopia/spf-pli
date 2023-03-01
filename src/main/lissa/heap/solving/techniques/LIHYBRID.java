package lissa.heap.solving.techniques;

import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.ThreadInfo;
import lissa.choicegenerators.HeapChoiceGeneratorLISSA;
import lissa.heap.SymbolicInputHeapLISSA;
import symsolve.vector.SymSolveVector;

public class LIHYBRID extends PLAINLAZY {

    @Override
    public Instruction handleLazyInitializationStep(ThreadInfo ti, Instruction currentInstruction,
            Instruction nextInstruction, HeapChoiceGeneratorLISSA heapCG) {
        fieldGetCount.clear();
        SymSolveVector vector = canonicalizer.createVector((SymbolicInputHeapLISSA) heapCG.getCurrentSymInputHeap());
        if (!heapSolver.isSatisfiableAutoHybridRepOK(vector)) {
            ti.getVM().getSystemState().setIgnored(true); // Backtrack
            return currentInstruction;
        }
        return nextInstruction;
    }

}
