package lissa.heap.solving.techniques;

import gov.nasa.jpf.symbc.numeric.Comparator;
import gov.nasa.jpf.symbc.numeric.Constraint;
import gov.nasa.jpf.symbc.numeric.Expression;
import gov.nasa.jpf.symbc.numeric.PathCondition;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.ThreadInfo;
import lissa.choicegenerators.HeapChoiceGeneratorLISSA;
import gov.nasa.jpf.symbc.numeric.PCChoiceGenerator;
import lissa.heap.SymHeapHelper;
import lissa.heap.SymbolicInputHeapLISSA;
import symsolve.vector.SymSolveSolution;

public class NTOPT extends NT {

    public int primitiveBranchingCacheHits = 0;

    @Override
    public Instruction handlePrimitiveBranch(ThreadInfo ti, Instruction currentInstruction, Instruction nextInstruction,
            PCChoiceGenerator pcCG) {
        HeapChoiceGeneratorLISSA heapCG = SymHeapHelper.getCurrentHeapChoiceGenerator(ti.getVM());
        if (heapCG == null)
            return nextInstruction;
        SymbolicInputHeapLISSA symInputHeap = (SymbolicInputHeapLISSA) heapCG.getCurrentSymInputHeap();
        assert (symInputHeap != null);

        primitiveBranches++;

        // ========== cache check

        PathCondition currentProgramPC = pcCG.getCurrentPC();
        assert (currentProgramPC != null);
        PathCondition repOKPC = heapCG.getCurrentRepOKPathCondition();
        if (isConjuntionSAT(currentProgramPC.make_copy(), repOKPC.make_copy())) {
            primitiveBranchingCacheHits++;
            return nextInstruction;
        }

        SymSolveSolution solution = heapCG.getCurrentSolution();

        // ==========

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

    boolean isConjuntionSAT(PathCondition pc1, PathCondition pc2) {
        PathCondition conjuntion = pc1.make_copy();

        Constraint current = pc2.header;
        while (current != null) {
            Expression left = current.getLeft();
            Expression right = current.getRight();
            Comparator comp = current.getComparator();
            conjuntion._addDet(comp, left, right);

            current = current.and;
        }

        return conjuntion.simplify();
    }

}
