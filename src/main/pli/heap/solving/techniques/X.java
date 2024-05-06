package pli.heap.solving.techniques;

import gov.nasa.jpf.symbc.numeric.PathCondition;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.ThreadInfo;
import korat.utils.IntListAI;
import pli.choicegenerators.HeapChoiceGeneratorLISSA;
import pli.choicegenerators.PCChoiceGeneratorLISSA;
import pli.choicegenerators.PLIChoiceGenerator;
import pli.heap.SymHeapHelper;
import pli.heap.SymbolicInputHeapLISSA;
import pli.heap.SymbolicReferenceInput;
import pli.heap.pathcondition.PathConditionUtils;
import symsolve.vector.SymSolveSolution;
import symsolve.vector.SymSolveVector;

public class X extends PLI {

    @Override
    public Instruction handleLazyInitializationStep(ThreadInfo ti, Instruction currentInstruction,
            Instruction nextInstruction, HeapChoiceGeneratorLISSA currentCG) {
        assert (!isRepOKExecutionMode());

        SymbolicInputHeapLISSA symInputHeap = (SymbolicInputHeapLISSA) currentCG.getCurrentSymInputHeap();
        PCChoiceGeneratorLISSA pcCG = SymHeapHelper.getCurrentPCChoiceGeneratorLISSA(ti.getVM());

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
            if (PathConditionUtils.isConjunctionSAT(accessedPC, pcCG.getCurrentPC()))
                break;

//            System.err.println("\n\n-------------------------------  Lazy init   -------------------------------\n");
//            System.err.println("\nProgram pc: " + pcCG.getCurrentPC());
//            System.err.println("\nConcrete heap pc: " + accessedPC);
//            SpecialSolverQueries.calculateInterpolant(pcCG.getCurrentPC(), accessedPC);

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

        // Optimization that avoid some solver calls
        PLIChoiceGenerator parent = getParentBranchPoint(currentCG);
        PathCondition cachedRepOKPC = parent.getCurrentRepOKPathCondition();
        if (cachedRepOKPC != null) {
            PathCondition conjunction = PathConditionUtils.getConjunction(currentCG.getCurrentPC(), cachedRepOKPC);
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
            if (PathConditionUtils.isConjunctionSAT(accessedPC, currentCG.getCurrentPC()))
                break;

//            System.err.println("\n\n-----------------------------  Primitive Branch   ----------------------------\n");
//            System.err.println("\nProgram pc: " + currentCG.getCurrentPC());
//            System.err.println("\nConcrete heap pc: " + accessedPC);
//            SpecialSolverQueries.calculateInterpolant(currentCG.getCurrentPC(), accessedPC);

            solution = heapSolver.getNextSolution(solution);
        }

        if (solution == null) {
            ti.getVM().getSystemState().setIgnored(true); // Backtrack
            return currentInstruction;
        }

        return createInvokeRepOKInstruction(ti, currentInstruction, nextInstruction, symInputHeap, solution, currentCG);
    }

}
