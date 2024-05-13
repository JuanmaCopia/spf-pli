package pli.heap.solving.techniques;

import gov.nasa.jpf.symbc.numeric.PathCondition;
import gov.nasa.jpf.vm.ChoiceGenerator;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.MJIEnv;
import gov.nasa.jpf.vm.ThreadInfo;
import pli.bytecode.lazy.PLIPrePCallInstruction;
import pli.choicegenerators.HeapChoiceGeneratorLISSA;
import pli.choicegenerators.LaunchSymbolicExecCG;
import pli.choicegenerators.PCChoiceGeneratorLISSA;
import pli.choicegenerators.PLIChoiceGenerator;
import pli.choicegenerators.prePCallCG;
import pli.heap.SymHeapHelper;
import pli.heap.SymbolicInputHeapLISSA;
import pli.heap.SymbolicReferenceInput;
import pli.heap.builder.HeapSolutionBuilder;
import pli.heap.pathcondition.PathConditionUtils;
import symsolve.vector.SymSolveSolution;
import symsolve.vector.SymSolveVector;

public class PLI extends LIBasedStrategy implements PCCheckStrategy {

    PLIPrePCallInstruction repOKCallInstruction;
    HeapSolutionBuilder builder;
    boolean executingRepOK = false;

    public PLI() {
        builder = new HeapSolutionBuilder(heapSolver.getFinitization().getStateSpace(), heapSolver);
    }

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
        SymSolveVector vector = canonicalizer.createVector(symInputHeap);

        return launchSolvingProcedure(ti, currentInstruction, nextInstruction, currentCG, symInputHeap, vector);
    }

    SymSolveSolution handleSatisfiabilityWithPathCondition(SymbolicInputHeapLISSA symInputHeap, PathCondition pc,
            SymSolveVector vector) {
        SymbolicReferenceInput symRefInput = symInputHeap.getImplicitInputThis();
        SymSolveSolution solution = heapSolver.solve(vector);
        return handleSatisfiabilityWithPathCondition(symRefInput, pc, solution);
    }

    SymSolveSolution handleSatisfiabilityWithPathCondition(SymbolicReferenceInput symRefInput, PathCondition pc,
            SymSolveSolution solution) {
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

    @Override
    public SymSolveSolution getNextSolution(ThreadInfo ti, SymSolveSolution previousSolution,
            SymbolicInputHeapLISSA symInputHeap) {
        PCChoiceGeneratorLISSA pcCG = SymHeapHelper.getCurrentPCChoiceGeneratorLISSA(ti.getVM());
        SymbolicReferenceInput symRefInput = symInputHeap.getImplicitInputThis();

        SymSolveSolution solution = heapSolver.getNextSolution(previousSolution);
        return handleSatisfiabilityWithPathCondition(symRefInput, pcCG.getCurrentPC(), solution);
    }

    Instruction createInvokePrePOnConcHeapInstruction(ThreadInfo ti, Instruction current, Instruction next,
            SymbolicInputHeapLISSA symInputHeap, SymSolveSolution solution, PLIChoiceGenerator curCG) {
        if (repOKCallInstruction == null)
            repOKCallInstruction = SymHeapHelper.createStaticRepOKCallInstruction(symInputHeap,
                    "runPrePConcreteHeap()V");

        assert (curCG != null);
        prePCallCG rcg = new prePCallCG("repOKCG", symInputHeap, solution, curCG);
        repOKCallInstruction.initialize(current, next, rcg);
        SymHeapHelper.pushArguments(ti, null, null);
        return repOKCallInstruction;
    }

    public void buildSolutionHeap(MJIEnv env, int objRef) {
        SymbolicInputHeapLISSA symInputHeap = SymHeapHelper.getSymbolicInputHeap(env.getVM());
        assert (symInputHeap != null);

        LaunchSymbolicExecCG repOKCG = env.getSystemState().getLastChoiceGeneratorOfType(LaunchSymbolicExecCG.class);
        SymSolveSolution solution = repOKCG.getCandidateHeapSolution();
        assert (solution != null);
        PCChoiceGeneratorLISSA pcCG = env.getSystemState().getLastChoiceGeneratorOfType(PCChoiceGeneratorLISSA.class);

        SymbolicReferenceInput symRefInput = symInputHeap.getImplicitInputThis();
        assert symRefInput.isSolutionSATWithPathCondition(stateSpace, solution, pcCG.getCurrentPC());

        builder.buildSolution(env, objRef, symInputHeap, solution);
    }

    int testID = 0;

    @Override
    void generateTestCase(ThreadInfo ti, Instruction current, Instruction next) {
        ChoiceGenerator<?> lastCG = ti.getVM().getSystemState().getChoiceGenerator();
        assert (lastCG != null);
        String code = getCachedTestCode(lastCG);
        assert code != null;
        code = code.replace("TESTID", Integer.toString(testID++));
        tests.add(code);
    }

    String getCachedTestCode(ChoiceGenerator<?> lastCG) {
        PLIChoiceGenerator cg = getParentBranchPoint(lastCG);
        if (cg == null)
            return null;
        return cg.getCurrentTestCode();
    }

    SymSolveSolution getCachedSolution(ChoiceGenerator<?> lastCG) {
        PLIChoiceGenerator cg = getParentBranchPoint(lastCG);
        if (cg == null)
            return null;
        return cg.getCurrentHeapSolution();
    }

    PathCondition getCachedPathCondition(ChoiceGenerator<?> lastCG) {
        PLIChoiceGenerator cg = getParentBranchPoint(lastCG);
        if (cg == null)
            return null;
        return cg.getCurrentRepOKPathCondition();
    }

    PLIChoiceGenerator getParentBranchPoint(ChoiceGenerator<?> lastCG) {
        assert (lastCG != null);
        ChoiceGenerator<?> currentCG = lastCG.getPreviousChoiceGenerator();
        for (ChoiceGenerator<?> cg = currentCG; cg != null; cg = cg.getPreviousChoiceGenerator()) {
            if (cg instanceof PLIChoiceGenerator)
                return (PLIChoiceGenerator) cg;
        }
        return null;
    }

}
