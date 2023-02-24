package lissa.heap.solving.techniques;

import java.util.HashMap;

import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.ThreadInfo;
import lissa.choicegenerators.HeapChoiceGeneratorLISSA;
import lissa.heap.SymbolicInputHeapLISSA;
import symsolve.vector.SymSolveVector;

public class LIHYBRID extends LISSA {

    HashMap<Integer, Integer> fieldGetCount = new HashMap<Integer, Integer>();
    int getFieldLimit;

    public LIHYBRID(int getFieldLimit) {
        this.getFieldLimit = getFieldLimit;
    }

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

    public boolean reachedGETFIELDLimit(int objRef) {
        if (!fieldGetCount.containsKey(objRef))
            fieldGetCount.put(objRef, 0);
        Integer count = fieldGetCount.get(objRef);

        if (count >= getFieldLimit) {
            fieldGetCount.clear();
            return true;
        }
        fieldGetCount.put(objRef, count + 1);
        return false;
    }

    public void resetGetFieldCount() {
        fieldGetCount.clear();
    }

}
