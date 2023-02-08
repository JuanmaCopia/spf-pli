package lissa.heap.solving.techniques;

import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.ThreadInfo;
import lissa.choicegenerators.HeapChoiceGeneratorLISSA;
import lissa.choicegenerators.PCChoiceGeneratorLISSA;
import lissa.heap.SymHeapHelper;
import lissa.heap.SymbolicInputHeapLISSA;
import symsolve.vector.SymSolveSolution;
import symsolve.vector.SymSolveVector;

public class NTOPT extends NT {

    public int primitiveBranchingCacheHits = 0;

    @Override
    public Instruction handlePrimitiveBranch(ThreadInfo ti, Instruction currentInstruction, Instruction nextInstruction,
            PCChoiceGeneratorLISSA pcCG) {
        HeapChoiceGeneratorLISSA heapCG = SymHeapHelper.getCurrentHeapChoiceGenerator(ti.getVM());
        if (heapCG == null)
            return nextInstruction;
        SymbolicInputHeapLISSA symInputHeap = (SymbolicInputHeapLISSA) heapCG.getCurrentSymInputHeap();
        assert (symInputHeap != null);

        primitiveBranches++;

        SymSolveVector vector = canonicalizer.createVector(symInputHeap);
        SymSolveSolution solution = heapSolver.solve(vector);
        while (solution != null) {
            if (isSatWithRespectToPathCondition(ti, solution, pcCG.getCurrentPC(), symInputHeap)) {
                break;
            }
            solution = heapSolver.getNextSolution(solution);
        }

        if (solution == null) {
            ti.getVM().getSystemState().setIgnored(true); // Backtrack
            prunedBranches++;
            return currentInstruction;
        }

        return createInvokeRepOKInstruction(ti, currentInstruction, nextInstruction, symInputHeap, solution, pcCG,
                heapCG, false);
    }

}
