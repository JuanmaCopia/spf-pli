package lissa.heap.solving.techniques;

import gov.nasa.jpf.symbc.numeric.Comparator;
import gov.nasa.jpf.symbc.numeric.Constraint;
import gov.nasa.jpf.symbc.numeric.Expression;
import gov.nasa.jpf.symbc.numeric.PCChoiceGenerator;
import gov.nasa.jpf.symbc.numeric.PathCondition;
import gov.nasa.jpf.symbc.string.StringComparator;
import gov.nasa.jpf.symbc.string.StringConstraint;
import gov.nasa.jpf.symbc.string.StringExpression;
import gov.nasa.jpf.symbc.string.StringPathCondition;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.ThreadInfo;
import lissa.choicegenerators.HeapChoiceGeneratorLISSA;
import lissa.heap.SymHeapHelper;
import lissa.heap.SymbolicInputHeapLISSA;
import lissa.heap.SymbolicReferenceInput;
import symsolve.vector.SymSolveSolution;
import symsolve.vector.SymSolveVector;

public class NTOPT extends NT {

    @Override
    public Instruction handlePrimitiveBranch(ThreadInfo ti, Instruction currentInstruction, Instruction nextInstruction,
            PCChoiceGenerator pcCG) {
        assert (!isRepOKExecutionMode());
        HeapChoiceGeneratorLISSA heapCG = SymHeapHelper.getCurrentHeapChoiceGenerator(ti.getVM());
        assert (heapCG != null);
        SymbolicInputHeapLISSA symInputHeap = (SymbolicInputHeapLISSA) heapCG.getCurrentSymInputHeap();
        assert (symInputHeap != null);
        SymbolicReferenceInput symRefInput = symInputHeap.getImplicitInputThis();

        primitiveBranches++;

        PathCondition currentProgramPC = pcCG.getCurrentPC();
        assert (currentProgramPC != null);
        PathCondition cachedRepOKPC = heapCG.getCurrentRepOKPathCondition();
        if (cachedRepOKPC != null) {
            if (isConjunctionSAT(currentProgramPC, cachedRepOKPC)) {
                primitiveBranchCacheHits++;
                return nextInstruction;
            }
        }

        SymSolveSolution solution = heapCG.getCurrentSolution();
        if (solution == null) {
            SymSolveVector vector = canonicalizer.createVector(symInputHeap);
            solution = heapSolver.solve(vector);
        }

        while (solution != null) {
            if (symRefInput.isSolutionSATWithPathCondition(stateSpace, solution, pcCG.getCurrentPC())) {
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

    boolean isConjunctionSAT(PathCondition pc1, PathCondition pc2) {
        assert (pc1 != null && pc2 != null);

        PathCondition conjunction = pc1.make_copy();

        Constraint current = pc2.header;
        while (current != null) {
            Expression left = current.getLeft();
            Expression right = current.getRight();
            Comparator comp = current.getComparator();
            conjunction._addDet(comp, left, right);

            current = current.and;
        }

        if (!conjunction.simplify())
            return false;

        // check conjunctions of string path conditions
        StringPathCondition spc1 = pc1.spc;
        StringPathCondition spc2 = pc2.spc;

        if (spc1 == null && spc2 == null)
            return true;
        if (spc1 == null)
            return spc2.simplify();
        if (spc2 == null)
            return spc1.simplify();

        StringPathCondition spc_conjunction = spc1.make_copy(pc1);

        StringConstraint scurrent = spc2.header;
        while (scurrent != null) {
            StringExpression left = scurrent.getLeft();
            StringExpression right = scurrent.getRight();
            StringComparator comp = scurrent.getComparator();
            spc_conjunction._addDet(comp, left, right);

            scurrent = scurrent.and();
        }

        return spc_conjunction.simplify();
    }

}
