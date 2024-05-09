package pli.heap.solving.techniques;

import gov.nasa.jpf.symbc.numeric.PathCondition;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.ThreadInfo;
import pli.bytecode.lazy.X2ProcedureCallInstruction;
import pli.bytecode.lazy.XProcedureCallInstruction;
import pli.choicegenerators.HeapChoiceGeneratorLISSA;
import pli.choicegenerators.PCChoiceGeneratorLISSA;
import pli.choicegenerators.PLIChoiceGenerator;
import pli.choicegenerators.XCG;
import pli.heap.SymHeapHelper;
import pli.heap.SymbolicInputHeapLISSA;
import symsolve.vector.SymSolveSolution;
import symsolve.vector.SymSolveVector;

public class X2 extends PLIOPT {

    @Override
    public Instruction handleLazyInitializationStep(ThreadInfo ti, Instruction currentInstruction,
            Instruction nextInstruction, HeapChoiceGeneratorLISSA currentCG) {

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

        return createX2prePInstruction(ti, currentInstruction, nextInstruction, symInputHeap, currentCG);
    }

    @Override
    Instruction launchSolvingProcedure(ThreadInfo ti, Instruction currentInstruction, Instruction nextInstruction,
            PLIChoiceGenerator currentCG, SymbolicInputHeapLISSA symInputHeap, SymSolveVector vector) {

        if (!isRepOKExecutionMode())
            solverCalls++;

        PathCondition pc = SymHeapHelper.getCurrentPCChoiceGeneratorLISSA(ti.getVM()).getCurrentPC();
        SymSolveSolution solution = handleSatisfiabilityWithPathCondition(symInputHeap, pc, vector);
        if (solution == null) {
            ti.getVM().getSystemState().setIgnored(true); // Backtrack
            return currentInstruction;
        }

        currentCG.setCurrentHeapSolution(solution);

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

    Instruction createX2prePInstruction(ThreadInfo ti, Instruction current, Instruction next,
            SymbolicInputHeapLISSA symInputHeap, PLIChoiceGenerator curCG) {
        ClassInfo rootClassInfo = symInputHeap.getImplicitInputThis().getRootHeapNode().getType();
        MethodInfo staticMethod = rootClassInfo.getMethod("runPrePPartialHeap()V", false);
        SymHeapHelper.pushArguments(ti, null, null);
        XCG cg = new XCG("XCG", curCG, null);
        return new X2ProcedureCallInstruction(staticMethod, current, next, cg);
    }

}
