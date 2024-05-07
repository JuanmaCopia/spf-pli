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

public class PLIOPT extends PLI {

    @Override
    public Instruction handleLazyInitializationStep(ThreadInfo ti, Instruction currentInstruction,
            Instruction nextInstruction, HeapChoiceGeneratorLISSA currentCG) {
        if (isRepOKExecutionMode()) {
            return nextInstruction;
        }
        SymbolicInputHeapLISSA symInputHeap = (SymbolicInputHeapLISSA) currentCG.getCurrentSymInputHeap();
        SymSolveVector vector = canonicalizer.createVector(symInputHeap);

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
        PCChoiceGeneratorLISSA pcCG = SymHeapHelper.getCurrentPCChoiceGeneratorLISSA(ti.getVM());

        SymSolveSolution solution = heapSolver.solve(vector);
        while (solution != null) {
            if (symRefInput.isSolutionSATWithPathCondition(stateSpace, solution, pcCG.getCurrentPC())) {
                break;
            }
            solution = heapSolver.getNextSolution(solution);
        }

        if (solution == null) {
            ti.getVM().getSystemState().setIgnored(true); // Backtrack
            return currentInstruction;
        }

        return createInvokePrePOnConcHeapInstruction(ti, currentInstruction, nextInstruction, symInputHeap, solution,
                currentCG);
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

        SymSolveSolution solution = getCachedSolution(currentCG);
        if (solution == null) {
            SymSolveVector vector = canonicalizer.createVector(symInputHeap);
            solution = heapSolver.solve(vector);
        }

        while (solution != null) {
            if (symRefInput.isSolutionSATWithPathCondition(stateSpace, solution, currentCG.getCurrentPC())) {
                break;
            }
            solution = heapSolver.getNextSolution(solution);
        }

        if (solution == null) {
            ti.getVM().getSystemState().setIgnored(true); // Backtrack
            return currentInstruction;
        }

        return createInvokePrePOnConcHeapInstruction(ti, currentInstruction, nextInstruction, symInputHeap, solution,
                currentCG);
    }

}
