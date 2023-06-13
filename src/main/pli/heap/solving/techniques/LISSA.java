package pli.heap.solving.techniques;

import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.ThreadInfo;
import pli.choicegenerators.HeapChoiceGeneratorLISSA;
import pli.heap.SymbolicInputHeapLISSA;
import pli.heap.solving.techniques.LIBasedStrategy;
import symsolve.vector.SymSolveVector;

public class LISSA extends LIBasedStrategy {

    @Override
    public Instruction handleLazyInitializationStep(ThreadInfo ti, Instruction currentInstruction,
            Instruction nextInstruction, HeapChoiceGeneratorLISSA heapCG) {
        solverCalls++;
        SymSolveVector vector = canonicalizer.createVector((SymbolicInputHeapLISSA) heapCG.getCurrentSymInputHeap());
        if (!heapSolver.isSatisfiable(vector)) {
            ti.getVM().getSystemState().setIgnored(true); // Backtrack
            return currentInstruction;
        }
        return nextInstruction;
    }

}
