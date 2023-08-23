package pli.heap.solving.techniques;

import java.util.Arrays;
import java.util.HashMap;

import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.ThreadInfo;
import pli.choicegenerators.HeapChoiceGeneratorLISSA;
import pli.heap.SymbolicInputHeapLISSA;
import pli.heap.solving.techniques.LIBasedStrategy;
import symsolve.vector.SymSolveVector;

public class LISSAM extends LIBasedStrategy {

    HashMap<String, Boolean> isSatCache = new HashMap<>();
    public int cacheHits = 0;

    @Override
    public Instruction handleLazyInitializationStep(ThreadInfo ti, Instruction currentInstruction,
            Instruction nextInstruction, HeapChoiceGeneratorLISSA heapCG) {

        SymSolveVector vector = canonicalizer.createVector((SymbolicInputHeapLISSA) heapCG.getCurrentSymInputHeap());
        String query = createIsSatQueryString(vector);

        Boolean isSAT = isSatCache.get(query);
        if (isSAT != null) {
            cacheHits++;
            if (isSAT)
                return nextInstruction;
            else {
                ti.getVM().getSystemState().setIgnored(true); // Backtrack
                return currentInstruction;
            }
        }

        isSAT = heapSolver.isSatisfiable(vector);
        isSatCache.put(query, isSAT);

        if (!isSAT) {
            ti.getVM().getSystemState().setIgnored(true); // Backtrack
            return currentInstruction;
        }
        return nextInstruction;
    }

    private String createIsSatQueryString(SymSolveVector vector) {
        return Arrays.toString(vector.createPartialVector());
    }

}
