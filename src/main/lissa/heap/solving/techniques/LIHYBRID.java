package lissa.heap.solving.techniques;

import java.util.HashMap;

import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.VM;
import lissa.choicegenerators.HeapChoiceGeneratorLISSA;
import lissa.heap.SymbolicInputHeapLISSA;
import symsolve.vector.SymSolveVector;

public class LIHYBRID extends LISSA {

    HashMap<Integer, Integer> fieldGetCount = new HashMap<Integer, Integer>();
    public int invalidPaths = 0;

    int getFieldLimit;

    public LIHYBRID(int getFieldLimit) {
        this.getFieldLimit = getFieldLimit;
    }

    @Override
    public Instruction handleLazyInitializationStep(ThreadInfo ti, Instruction currentInstruction,
            Instruction nextInstruction, SymbolicInputHeapLISSA symInputHeap) {
        resetGetFieldCount();
        SymSolveVector vector = canonicalizer.createVector(symInputHeap);
        if (!heapSolver.isSatisfiableAutoHybridRepOK(vector)) {
            ti.getVM().getSystemState().setIgnored(true); // Backtrack
            return currentInstruction;
        }
        return nextInstruction;
    }

    @Override
    public void pathFinished(VM vm, ThreadInfo terminatedThread) {
        super.pathFinished(vm, terminatedThread);
        checkPathValidity(vm, terminatedThread);
    }

    private void checkPathValidity(VM vm, ThreadInfo terminatedThread) {
        HeapChoiceGeneratorLISSA heapCG = vm.getLastChoiceGeneratorOfType(HeapChoiceGeneratorLISSA.class);
        SymbolicInputHeapLISSA symInputHeap = (SymbolicInputHeapLISSA) heapCG.getCurrentSymInputHeap();
        SymSolveVector vector = canonicalizer.createVector(symInputHeap);
        if (symInputHeap != null && !heapSolver.isSatisfiable(vector))
            invalidPaths++;
    }

    public boolean reachedGETFIELDLimit(int objRef) {
        if (!fieldGetCount.containsKey(objRef))
            fieldGetCount.put(objRef, 0);
        Integer count = fieldGetCount.get(objRef);

        if (count >= getFieldLimit) {
            resetGetFieldCount();
            return true;
        }
        fieldGetCount.put(objRef, count + 1);
        return false;
    }

    private void resetGetFieldCount() {
        fieldGetCount.clear();
    }

}
