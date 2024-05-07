package pli.heap.solving.techniques;

import gov.nasa.jpf.symbc.numeric.PathCondition;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.ThreadInfo;
import pli.bytecode.lazy.StaticRepOKCallInstruction;
import pli.choicegenerators.HeapChoiceGeneratorLISSA;
import pli.choicegenerators.PCChoiceGeneratorLISSA;
import pli.choicegenerators.PLIChoiceGenerator;
import pli.choicegenerators.prePCallCG;
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

    Instruction createXprePInstruction(ThreadInfo ti, Instruction current, Instruction next,
            SymbolicInputHeapLISSA symInputHeap, SymSolveSolution solution, PLIChoiceGenerator curCG) {

        StaticRepOKCallInstruction repOKCallInstruction = SymHeapHelper.createStaticRepOKCallInstruction(symInputHeap,
                "runPrePConcreteHeap()V");

        assert (curCG != null);
        PathCondition currentPC = SymHeapHelper.getCurrentPCChoiceGeneratorLISSA(ti.getVM()).getCurrentPC();
        prePCallCG rcg = new prePCallCG("repOKCG", symInputHeap, solution, curCG, currentPC);
        repOKCallInstruction.initialize(current, next, rcg);
        SymHeapHelper.pushArguments(ti, null, null);
        return repOKCallInstruction;
    }

    @Override
    public Instruction handlePrimitiveBranch(ThreadInfo ti, Instruction currentInstruction, Instruction nextInstruction,
            PCChoiceGeneratorLISSA currentCG) {
        assert (!isRepOKExecutionMode());
        return nextInstruction;
    }

}
