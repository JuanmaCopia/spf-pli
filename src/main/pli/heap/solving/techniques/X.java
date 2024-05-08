package pli.heap.solving.techniques;

import gov.nasa.jpf.symbc.numeric.PathCondition;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.ThreadInfo;
import pli.bytecode.lazy.XProcedureCallInstruction;
import pli.choicegenerators.HeapChoiceGeneratorLISSA;
import pli.choicegenerators.PCChoiceGeneratorLISSA;
import pli.choicegenerators.PLIChoiceGenerator;
import pli.choicegenerators.XCG;
import pli.heap.SymHeapHelper;
import pli.heap.SymbolicInputHeapLISSA;
import pli.heap.SymbolicReferenceInput;
import pli.heap.pathcondition.PathConditionUtils;
import symsolve.vector.SymSolveSolution;
import symsolve.vector.SymSolveVector;

public class X extends PLIOPT {

    @Override
    public Instruction handleLazyInitializationStep(ThreadInfo ti, Instruction currentInstruction,
            Instruction nextInstruction, HeapChoiceGeneratorLISSA currentCG) {
        SymbolicInputHeapLISSA symInputHeap = (SymbolicInputHeapLISSA) currentCG.getCurrentSymInputHeap();
        SymbolicReferenceInput symRefInput = symInputHeap.getImplicitInputThis();
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

        SymSolveSolution solution = heapSolver.solve(vector);
        PCChoiceGeneratorLISSA pcCG = SymHeapHelper.getCurrentPCChoiceGeneratorLISSA(ti.getVM());

        while (solution != null) {
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

        if (isRepOKExecutionMode())
            return nextInstruction;

        return createXprePInstruction(ti, currentInstruction, nextInstruction, symInputHeap, solution, currentCG);
    }

    Instruction createXprePInstruction(ThreadInfo ti, Instruction current, Instruction next,
            SymbolicInputHeapLISSA symInputHeap, SymSolveSolution solution, PLIChoiceGenerator curCG) {
        ClassInfo rootClassInfo = symInputHeap.getImplicitInputThis().getRootHeapNode().getType();
        MethodInfo staticMethod = rootClassInfo.getMethod("runPrePConcreteHeap()V", false);
        SymHeapHelper.pushArguments(ti, null, null);
        XCG cg = new XCG("XCG", curCG, solution);
        return new XProcedureCallInstruction(staticMethod, current, next, cg);
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
        assert (heapCG != null);

        SymbolicInputHeapLISSA symInputHeap = (SymbolicInputHeapLISSA) heapCG.getCurrentSymInputHeap();
        SymbolicReferenceInput symRefInput = symInputHeap.getImplicitInputThis();
        SymSolveVector vector = canonicalizer.createVector(symInputHeap);
        SymSolveSolution solution = heapSolver.solve(vector);
        PCChoiceGeneratorLISSA pcCG = SymHeapHelper.getCurrentPCChoiceGeneratorLISSA(ti.getVM());

        while (solution != null) {
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

        return createXprePInstruction(ti, currentInstruction, nextInstruction, symInputHeap, solution, currentCG);
    }

}
