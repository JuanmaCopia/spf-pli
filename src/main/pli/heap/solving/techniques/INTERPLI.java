package pli.heap.solving.techniques;

import gov.nasa.jpf.symbc.numeric.Comparator;
import gov.nasa.jpf.symbc.numeric.Constraint;
import gov.nasa.jpf.symbc.numeric.Expression;
import gov.nasa.jpf.symbc.numeric.PathCondition;
import gov.nasa.jpf.symbc.numeric.interp.SpecialSolverQueries;
import gov.nasa.jpf.symbc.string.StringComparator;
import gov.nasa.jpf.symbc.string.StringConstraint;
import gov.nasa.jpf.symbc.string.StringExpression;
import gov.nasa.jpf.symbc.string.StringPathCondition;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.ThreadInfo;
import korat.utils.IntListAI;
import pli.choicegenerators.HeapChoiceGeneratorLISSA;
import pli.choicegenerators.PCChoiceGeneratorLISSA;
import pli.choicegenerators.PLIChoiceGenerator;
import pli.heap.SymHeapHelper;
import pli.heap.SymbolicInputHeapLISSA;
import pli.heap.SymbolicReferenceInput;
import symsolve.vector.SymSolveSolution;
import symsolve.vector.SymSolveVector;

public class INTERPLI extends PLI {

    @Override
    public Instruction handleLazyInitializationStep(ThreadInfo ti, Instruction currentInstruction,
            Instruction nextInstruction, HeapChoiceGeneratorLISSA currentCG) {
        assert (!isRepOKExecutionMode());

        SymbolicInputHeapLISSA symInputHeap = (SymbolicInputHeapLISSA) currentCG.getCurrentSymInputHeap();
        PCChoiceGeneratorLISSA pcCG = SymHeapHelper.getCurrentPCChoiceGeneratorLISSA(ti.getVM());
        // System.out.println("checkign lazy init, current pc is: " +
        // pcCG.getCurrentPC());
        SymSolveVector vector = canonicalizer.createVector(symInputHeap, pcCG.getCurrentPC());

        // Optimization that avoid some solver calls
        PLIChoiceGenerator parent = getParentBranchPoint(currentCG);
        SymSolveSolution cachedHeapSolution = parent.getCurrentHeapSolution();
        if (cachedHeapSolution != null) {
            if (fixedFieldsMatch(vector, cachedHeapSolution)) {
                SymSolveSolution newSolution = getNewSolution(vector, cachedHeapSolution);
                // heapCG.setCurrentRepOKPathCondition(parent.getCurrentRepOKPathCondition());
                // // I cannot set the previous pc because with the current implementation I
                // dont have the symbolic value correspondence
                currentCG.setCurrentHeapSolution(newSolution);
                return nextInstruction;
            }
        }

        solverCalls++;

        SymbolicReferenceInput symRefInput = symInputHeap.getImplicitInputThis();

        SymSolveSolution solution = heapSolver.solve(vector);
        while (solution != null) {
            // if (symRefInput.isSolutionSATWithPathCondition(stateSpace, solution,
            // pcCG.getCurrentPC())) {
            // break;
            // }
            PathCondition accessedPC = symRefInput.getAccessedFieldsPathCondition(stateSpace, solution);
            if (isConjunctionSAT(accessedPC, pcCG.getCurrentPC()))
                break;

            System.err.println("\n--------------------------------------\n");
            System.err.println("\nconcretized primitive types ussat");
            System.err.println("conc heap pc: " + accessedPC);
            System.err.println("\n program pc: " + pcCG.getCurrentPC());
            SpecialSolverQueries.calculateInterpolant(accessedPC, pcCG.getCurrentPC());
            getNextHeapCalls++;
            solution = heapSolver.getNextSolution(solution);
        }

        if (solution == null) {
            ti.getVM().getSystemState().setIgnored(true); // Backtrack
            return currentInstruction;
        }

        return createInvokeRepOKInstruction(ti, currentInstruction, nextInstruction, symInputHeap, solution, currentCG);
    }

    private boolean fixedFieldsMatch(SymSolveVector vector, SymSolveSolution cachedSolution) {
        IntListAI fixedIndices = vector.getFixedIndices();
        int[] vect = vector.getConcreteVector();
        int[] candidateSolution = cachedSolution.getSolutionVector();
        for (int i : fixedIndices.toArray()) {
            if (vect[i] != candidateSolution[i])
                return false;
        }
        return true;
    }

    private SymSolveSolution getNewSolution(SymSolveVector vector, SymSolveSolution cachedSolution) {
        int[] candidateSolution = cachedSolution.getSolutionVector();
        return new SymSolveSolution(vector, candidateSolution, cachedSolution.getAccessedIndices(),
                cachedSolution.getBuildedSolution());
    }

    @Override
    public Instruction handlePrimitiveBranch(ThreadInfo ti, Instruction currentInstruction, Instruction nextInstruction,
            PCChoiceGeneratorLISSA currentCG) {
        assert (!isRepOKExecutionMode());
        // System.out.println("checkign branch, current pc is: " +
        // currentCG.getCurrentPC());

        // Optimization that avoid some solver calls
        PLIChoiceGenerator parent = getParentBranchPoint(currentCG);
        PathCondition cachedRepOKPC = parent.getCurrentRepOKPathCondition();
        if (cachedRepOKPC != null) {
            PathCondition conjunction = getConjunction(currentCG.getCurrentPC(), cachedRepOKPC);
            if (conjunction.simplify()) {
                currentCG.setCurrentRepOKPathCondition(conjunction);
                currentCG.setCurrentHeapSolution(parent.getCurrentHeapSolution());
                return nextInstruction;
            }
        }

        solverCalls++;

        HeapChoiceGeneratorLISSA heapCG = SymHeapHelper.getCurrentHeapChoiceGenerator(ti.getVM());
        SymbolicInputHeapLISSA symInputHeap = (SymbolicInputHeapLISSA) heapCG.getCurrentSymInputHeap();
        SymbolicReferenceInput symRefInput = symInputHeap.getImplicitInputThis();

//        SymSolveSolution solution = getCachedSolution(currentCG);
//        if (solution == null) {
//            SymSolveVector vector = canonicalizer.createVector(symInputHeap, currentCG.getCurrentPC());
//            solution = heapSolver.solve(vector);
//        }

        SymSolveVector vector = canonicalizer.createVector(symInputHeap, currentCG.getCurrentPC());
        SymSolveSolution solution = heapSolver.solve(vector);

        while (solution != null) {
            PathCondition accessedPC = symRefInput.getAccessedFieldsPathCondition(stateSpace, solution);
            if (isConjunctionSAT(accessedPC, currentCG.getCurrentPC()))
                break;
            System.err.println("\n--------------------------------------\n");
            System.err.println("\nconcretized primitive types ussat");
            System.err.println("conc heap pc: " + accessedPC);
            System.err.println("\n program pc: " + currentCG.getCurrentPC());
            SpecialSolverQueries.calculateInterpolant(accessedPC, currentCG.getCurrentPC());
            getNextHeapCalls++;
            solution = heapSolver.getNextSolution(solution);
        }

        if (solution == null) {
            ti.getVM().getSystemState().setIgnored(true); // Backtrack
            return currentInstruction;
        }

        return createInvokeRepOKInstruction(ti, currentInstruction, nextInstruction, symInputHeap, solution, currentCG);
    }

    PathCondition getConjunction(PathCondition pc1, PathCondition pc2) {
        PathCondition conjunction = pc1.make_copy();

        Constraint current = pc2.header;
        while (current != null) {
            Expression left = current.getLeft();
            Expression right = current.getRight();
            Comparator comp = current.getComparator();
            conjunction._addDet(comp, left, right);

            current = current.and;
        }

        // check conjunctions of string path conditions
        StringPathCondition spc1 = pc1.spc;
        StringPathCondition spc2 = pc2.spc;

        if (spc1 == null && spc2 == null)
            return conjunction;
        if (spc1 == null) {
            conjunction.spc = spc2.make_copy(conjunction);
            return conjunction;
        }
        if (spc2 == null) {
            conjunction.spc = spc1.make_copy(conjunction);
            return conjunction;
        }

        StringPathCondition spc_conjunction = spc1.make_copy(pc1);

        StringConstraint scurrent = spc2.header;
        while (scurrent != null) {
            StringExpression left = scurrent.getLeft();
            StringExpression right = scurrent.getRight();
            StringComparator comp = scurrent.getComparator();
            spc_conjunction._addDet(comp, left, right);

            scurrent = scurrent.and();
        }

        conjunction.spc = spc_conjunction.make_copy(conjunction);
        return conjunction;
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
