package lissa.heap.solving.techniques;

import java.util.HashMap;

import gov.nasa.jpf.vm.ThreadInfo;
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
    public boolean checkHeapSatisfiability(ThreadInfo ti, SymbolicInputHeapLISSA symInputHeap) {
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

}
