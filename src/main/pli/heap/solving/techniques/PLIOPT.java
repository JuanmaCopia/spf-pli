package pli.heap.solving.techniques;

import gov.nasa.jpf.symbc.numeric.PathCondition;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.ThreadInfo;
import pli.choicegenerators.HeapChoiceGeneratorLISSA;
import pli.choicegenerators.PCChoiceGeneratorLISSA;
import pli.choicegenerators.PLIChoiceGenerator;
import pli.heap.SymHeapHelper;
import pli.heap.SymbolicInputHeapLISSA;
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

        return launchSolvingProcedure(ti, currentInstruction, nextInstruction, currentCG, symInputHeap, vector);
    }

    @Override
    public Instruction handlePrimitiveBranch(ThreadInfo ti, Instruction currentInstruction, Instruction nextInstruction,
            PCChoiceGeneratorLISSA currentCG) {
        assert (!isRepOKExecutionMode());

        SymbolicInputHeapLISSA symInputHeap = (SymbolicInputHeapLISSA) SymHeapHelper
                .getCurrentHeapChoiceGenerator(ti.getVM()).getCurrentSymInputHeap();

        if (isCacheHit(currentCG, symInputHeap))
            return nextInstruction;

        SymSolveVector vector = canonicalizer.createVector(symInputHeap);

        return launchSolvingProcedure(ti, currentInstruction, nextInstruction, currentCG, symInputHeap, vector);
    }

    boolean isCacheHit(PCChoiceGeneratorLISSA currentCG, SymbolicInputHeapLISSA symInputHeap) {
        PLIChoiceGenerator parent = getParentBranchPoint(currentCG);
        PathCondition cachedRepOKPC = parent.getCurrentRepOKPathCondition();
        SymSolveSolution cachedSymSolveSolution = parent.getCurrentHeapSolution();
        if (cachedRepOKPC == null || cachedSymSolveSolution == null)
            return false;

        PathCondition accessedPC = symInputHeap.getImplicitInputThis().getAccessedFieldsPathCondition(stateSpace,
                cachedSymSolveSolution);

        PathCondition c1 = PathConditionUtils.getConjunction(currentCG.getCurrentPC(), cachedRepOKPC);
        PathCondition conjunction = PathConditionUtils.getConjunction(c1, accessedPC);
        if (!conjunction.simplify()) {
            currentCG.setCurrentRepOKPathCondition(conjunction);
            currentCG.setCurrentHeapSolution(cachedSymSolveSolution);
            return false;
        }

        return true;
    }

}
