package lissa.heap.solving.techniques;

import java.util.HashMap;

import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.ThreadInfo;
import lissa.choicegenerators.HeapChoiceGeneratorLISSA;
import lissa.heap.SymbolicInputHeapLISSA;
import lissa.heap.canonicalizer.Canonicalizer;
import lissa.heap.solving.solver.SymSolveHeapSolver;
import symsolve.vector.SymSolveVector;

public class LISSA extends LIBasedStrategy {

    protected SymSolveHeapSolver heapSolver;
    protected Canonicalizer canonicalizer;

    public LISSA() {
        heapSolver = new SymSolveHeapSolver();
        canonicalizer = new Canonicalizer(heapSolver.getStructureList());
    }

    @Override
    public Instruction handleLazyInitializationStep(ThreadInfo ti, Instruction currentInstruction,
            Instruction nextInstruction, HeapChoiceGeneratorLISSA heapCG) {
        SymSolveVector vector = canonicalizer.createVector((SymbolicInputHeapLISSA) heapCG.getCurrentSymInputHeap());
        if (!heapSolver.isSatisfiable(vector)) {
            ti.getVM().getSystemState().setIgnored(true); // Backtrack
            return currentInstruction;
        }
        return nextInstruction;
    }

    @Override
    public Integer getBoundForClass(String simpleClassName) {
        HashMap<String, Integer> dataBounds = heapSolver.getDataBounds();
        return dataBounds.get(simpleClassName);
    }

    @Override
    public boolean isClassInBounds(String simpleClassName) {
        return getBoundForClass(simpleClassName) != null;
    }

    @Override
    public long getSolvingTime() {
        return heapSolver.getSolvingTime();
    }

    public Canonicalizer getCanonicalizer() {
        return canonicalizer;
    }

    public SymSolveHeapSolver getSolver() {
        return heapSolver;
    }

}
