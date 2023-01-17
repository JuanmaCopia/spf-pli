package lissa.heap.solving.techniques;

import java.util.HashMap;

import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.ThreadInfo;
import lissa.heap.SymbolicInputHeapLISSA;
import lissa.heap.SymbolicReferenceInput;
import lissa.heap.canonicalizer.Canonicalizer;
import lissa.heap.solving.solver.SymSolveHeapSolver;
import symsolve.vector.SymSolveVector;

public class LISSA extends LIBasedStrategy {

    protected SymSolveHeapSolver heapSolver;
    protected Canonicalizer canonicalizer;
    protected SymbolicReferenceInput currentSymbolicInput;

    public LISSA() {
        heapSolver = new SymSolveHeapSolver();
        canonicalizer = new Canonicalizer(heapSolver.getStructureList());
    }

    @Override
    public boolean checkHeapSatisfiability(ThreadInfo ti, SymbolicInputHeapLISSA symInputHeap) {
        currentSymbolicInput = symInputHeap.getImplicitInputThis();
        SymSolveVector vector = canonicalizer.createVector(symInputHeap);
        return heapSolver.isSatisfiable(vector);
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

    @Override
    public Instruction getNextInstructionToGETFIELD(ThreadInfo ti, SymbolicInputHeapLISSA symInputHeap) {
        return ti.getPC().getNext();
    }

    @Override
    public Instruction getNextInstructionToPrimitiveBranching(ThreadInfo ti) {
        return ti.getPC().getNext();
    }

}
