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
        if (isRepOKExecutionMode())
            return nextInstruction;

        SymbolicInputHeapLISSA symInputHeap = (SymbolicInputHeapLISSA) currentCG.getCurrentSymInputHeap();
        SymSolveVector vector = canonicalizer.createVector(symInputHeap);

        if (lazyCacheHit(currentCG, vector))
            return nextInstruction;

        return launchSolvingProcedure(ti, currentInstruction, nextInstruction, currentCG, symInputHeap, vector);
    }

    @Override
    public Instruction handlePrimitiveBranch(ThreadInfo ti, Instruction currentInstruction, Instruction nextInstruction,
            PCChoiceGeneratorLISSA currentCG) {
        assert (!isRepOKExecutionMode());
        if (pcBranchCacheHit(currentCG))
            return nextInstruction;

        SymbolicInputHeapLISSA symInputHeap = (SymbolicInputHeapLISSA) SymHeapHelper
                .getCurrentHeapChoiceGenerator(ti.getVM()).getCurrentSymInputHeap();
        SymSolveVector vector = canonicalizer.createVector(symInputHeap);

        return launchSolvingProcedure(ti, currentInstruction, nextInstruction, currentCG, symInputHeap, vector);
    }

    boolean fixedFieldsMatch(SymSolveVector vector, SymSolveSolution cachedSolution) {
        IntListAI fixedIndices = vector.getFixedIndices();
        int[] vect = vector.getConcreteVector();
        int[] candidateSolution = cachedSolution.getSolutionVector();
        for (int i : fixedIndices.toArray()) {
            if (vect[i] != candidateSolution[i])
                return false;
        }
        return true;
    }

    SymSolveSolution getNewSolution(SymSolveVector vector, SymSolveSolution cachedSolution) {
        int[] candidateSolution = cachedSolution.getSolutionVector();
        return new SymSolveSolution(vector, candidateSolution, cachedSolution.getAccessedIndices(),
                cachedSolution.getBuildedSolution());
    }

    boolean lazyCacheHit(HeapChoiceGeneratorLISSA currentCG, SymSolveVector vector) {
        // Optimization that avoid some solver calls
        PLIChoiceGenerator parent = getParentBranchPoint(currentCG);
        SymSolveSolution cachedHeapSolution = parent.getCurrentHeapSolution();
        if (cachedHeapSolution != null && fixedFieldsMatch(vector, cachedHeapSolution)) {
            // heapCG.setCurrentRepOKPathCondition(parent.getCurrentRepOKPathCondition());
            // // I cannot set the previous pc because with the current implementation I
            // dont have the symbolic value correspondence
            currentCG.setCurrentHeapSolution(getNewSolution(vector, cachedHeapSolution));
            return true;

        }
        return false;
    }

    boolean pcBranchCacheHit(PCChoiceGeneratorLISSA currentCG) {
        PLIChoiceGenerator parent = getParentBranchPoint(currentCG);
        PathCondition cachedRepOKPC = parent.getCurrentRepOKPathCondition();
        if (cachedRepOKPC != null) {
            PathCondition conjunction = PathConditionUtils.getConjunction(currentCG.getCurrentPC(), cachedRepOKPC);
            if (conjunction.simplify()) {
                currentCG.setCurrentRepOKPathCondition(conjunction);
                currentCG.setCurrentHeapSolution(parent.getCurrentHeapSolution());
                return true;
            }
        }
        return false;
    }

    SymSolveSolution handleSatisfiabilityWithPathCondition(SymbolicInputHeapLISSA symInputHeap, PathCondition pc,
            SymSolveVector vector) {
        SymbolicReferenceInput symRefInput = symInputHeap.getImplicitInputThis();
        SymSolveSolution solution = heapSolver.solve(vector);

        while (solution != null) {
            PathCondition accessedPC = symRefInput.getAccessedFieldsPathCondition(stateSpace, solution);
            if (PathConditionUtils.isConjunctionSAT(accessedPC, pc))
                return solution;

            solution = heapSolver.getNextSolution(solution);
        }
        return solution;
    }

    Instruction launchSolvingProcedure(ThreadInfo ti, Instruction currentInstruction, Instruction nextInstruction,
            PLIChoiceGenerator currentCG, SymbolicInputHeapLISSA symInputHeap, SymSolveVector vector) {

        solverCalls++;

        PathCondition pc = SymHeapHelper.getCurrentPCChoiceGeneratorLISSA(ti.getVM()).getCurrentPC();
        SymSolveSolution solution = handleSatisfiabilityWithPathCondition(symInputHeap, pc, vector);
        if (solution == null) {
            ti.getVM().getSystemState().setIgnored(true); // Backtrack
            return currentInstruction;
        }

        return createInvokePrePOnConcHeapInstruction(ti, currentInstruction, nextInstruction, symInputHeap, solution,
                currentCG);
    }

}
