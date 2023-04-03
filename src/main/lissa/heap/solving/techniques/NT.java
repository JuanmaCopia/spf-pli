package lissa.heap.solving.techniques;

import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.MJIEnv;
import gov.nasa.jpf.vm.ThreadInfo;
import lissa.bytecode.lazy.StaticRepOKCallInstruction;
import lissa.choicegenerators.HeapChoiceGeneratorLISSA;
import lissa.choicegenerators.PCChoiceGeneratorLISSA;
import lissa.choicegenerators.RepOKCallCG;
import lissa.heap.SymHeapHelper;
import lissa.heap.SymbolicInputHeapLISSA;
import lissa.heap.SymbolicReferenceInput;
import lissa.heap.builder.HeapSolutionBuilder;
import symsolve.vector.SymSolveSolution;
import symsolve.vector.SymSolveVector;

public class NT extends LIBasedStrategy implements PCCheckStrategy {

    StaticRepOKCallInstruction repOKCallInstruction;
    HeapSolutionBuilder builder;
    boolean executingRepOK = false;
    int prunedBranches = 0;

    public int primitiveBranches = 0;

    public NT() {
        builder = new HeapSolutionBuilder(heapSolver.getFinitization().getStateSpace(), heapSolver);
    }

    @Override
    public Instruction handleLazyInitializationStep(ThreadInfo ti, Instruction currentInstruction,
            Instruction nextInstruction, HeapChoiceGeneratorLISSA heapCG) {
        assert (!isRepOKExecutionMode());
        SymbolicInputHeapLISSA symInputHeap = (SymbolicInputHeapLISSA) heapCG.getCurrentSymInputHeap();
        SymbolicReferenceInput symRefInput = symInputHeap.getImplicitInputThis();
        SymSolveVector vector = canonicalizer.createVector(symInputHeap);
        SymSolveSolution solution = heapSolver.solve(vector);

        PCChoiceGeneratorLISSA pcCG = SymHeapHelper.getCurrentPCChoiceGeneratorLISSA(ti.getVM());
        assert (pcCG != null);

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

        return createInvokeRepOKInstruction(ti, currentInstruction, nextInstruction, symInputHeap, solution, pcCG,
                heapCG, true);
    }

    @Override
    public Instruction handlePrimitiveBranch(ThreadInfo ti, Instruction currentInstruction, Instruction nextInstruction,
            PCChoiceGeneratorLISSA pcCG) {
        assert (!isRepOKExecutionMode());
        HeapChoiceGeneratorLISSA heapCG = SymHeapHelper.getCurrentHeapChoiceGenerator(ti.getVM());
        assert (heapCG != null);

        SymbolicInputHeapLISSA symInputHeap = (SymbolicInputHeapLISSA) heapCG.getCurrentSymInputHeap();
        assert (symInputHeap != null);
        SymbolicReferenceInput symRefInput = symInputHeap.getImplicitInputThis();

        primitiveBranches++;

        SymSolveVector vector = canonicalizer.createVector(symInputHeap);
        SymSolveSolution solution = heapSolver.solve(vector);
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

    @Override
    public SymSolveSolution getNextSolution(ThreadInfo ti, SymSolveSolution previousSolution,
            SymbolicInputHeapLISSA symInputHeap) {
        assert (previousSolution != null);
        PCChoiceGeneratorLISSA pcCG = SymHeapHelper.getCurrentPCChoiceGeneratorLISSA(ti.getVM());
        SymbolicReferenceInput symRefInput = symInputHeap.getImplicitInputThis();
        assert symRefInput.isSolutionSATWithPathCondition(stateSpace, previousSolution, pcCG.getCurrentPC());

        SymSolveSolution solution = heapSolver.getNextSolution(previousSolution);
        if (pcCG != null) {
            while (solution != null) {
                if (symRefInput.isSolutionSATWithPathCondition(stateSpace, solution, pcCG.getCurrentPC())) {
                    return solution;
                }
                solution = heapSolver.getNextSolution(solution);
            }
        }
        return null;
    }

    Instruction createInvokeRepOKInstruction(ThreadInfo ti, Instruction current, Instruction next,
            SymbolicInputHeapLISSA symInputHeap, SymSolveSolution solution, PCChoiceGeneratorLISSA pcCG,
            HeapChoiceGeneratorLISSA heapCG, boolean isLazyStep) {
        if (repOKCallInstruction == null)
            repOKCallInstruction = SymHeapHelper.createStaticRepOKCallInstruction(symInputHeap, "runRepOK()V");

        assert (pcCG != null && heapCG != null);
        RepOKCallCG rcg = new RepOKCallCG("repOKCG", symInputHeap, heapCG, pcCG, solution, isLazyStep);
        repOKCallInstruction.initialize(current, next, rcg);
        SymHeapHelper.pushArguments(ti, null, null);
        return repOKCallInstruction;
    }

    public void buildSolutionHeap(MJIEnv env, int objRef) {
        SymbolicInputHeapLISSA symInputHeap = SymHeapHelper.getSymbolicInputHeap(env.getVM());
        assert (symInputHeap != null);

        RepOKCallCG repOKCG = env.getSystemState().getLastChoiceGeneratorOfType(RepOKCallCG.class);
        SymSolveSolution solution = repOKCG.getCandidateHeapSolution();
        assert (solution != null);
        PCChoiceGeneratorLISSA pcCG = env.getSystemState().getLastChoiceGeneratorOfType(PCChoiceGeneratorLISSA.class);

        SymbolicReferenceInput symRefInput = symInputHeap.getImplicitInputThis();
        assert symRefInput.isSolutionSATWithPathCondition(stateSpace, solution, pcCG.getCurrentPC());

        builder.buildSolution(env, objRef, symInputHeap, solution);
    }

//    @Override
//    void generateTestCase(ThreadInfo ti, Instruction current, Instruction next) {
//        HeapChoiceGeneratorLISSA heapCG = SymHeapHelper.getCurrentHeapChoiceGenerator(ti.getVM());
//        SymSolveSolution heapSolution = heapCG.getCurrentSolution();
//        PathCondition solutionPC = heapCG.getCurrentRepOKPathCondition();
//    }

    public void countPrunedBranch() {
        prunedBranches++;
    }

    public int getPrunedBranchCount() {
        return prunedBranches;
    }

}
