package lissa.heap.solving.techniques;

import gov.nasa.jpf.symbc.numeric.PathCondition;
import gov.nasa.jpf.vm.ChoiceGenerator;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.MJIEnv;
import gov.nasa.jpf.vm.ThreadInfo;
import lissa.bytecode.lazy.StaticRepOKCallInstruction;
import lissa.choicegenerators.HeapChoiceGeneratorLISSA;
import lissa.choicegenerators.PCChoiceGeneratorLISSA;
import lissa.choicegenerators.PLIChoiceGenerator;
import lissa.choicegenerators.RepOKCallCG;
import lissa.heap.SymHeapHelper;
import lissa.heap.SymbolicInputHeapLISSA;
import lissa.heap.SymbolicReferenceInput;
import lissa.heap.builder.HeapSolutionBuilder;
import symsolve.vector.SymSolveSolution;
import symsolve.vector.SymSolveVector;

public class PLI extends LIBasedStrategy implements PCCheckStrategy {

    StaticRepOKCallInstruction repOKCallInstruction;
    HeapSolutionBuilder builder;
    boolean executingRepOK = false;
    int prunedBranches = 0;

    public int primitiveBranches = 0;

    public PLI() {
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

        return createInvokeRepOKInstruction(ti, currentInstruction, nextInstruction, symInputHeap, solution, heapCG);
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

        return createInvokeRepOKInstruction(ti, currentInstruction, nextInstruction, symInputHeap, solution, pcCG);
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
            SymbolicInputHeapLISSA symInputHeap, SymSolveSolution solution, PLIChoiceGenerator curCG) {
        if (repOKCallInstruction == null)
            repOKCallInstruction = SymHeapHelper.createStaticRepOKCallInstruction(symInputHeap, "runRepOK()V");

        assert (curCG != null);
        RepOKCallCG rcg = new RepOKCallCG("repOKCG", symInputHeap, solution, curCG);
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

    int testID = 0;

    @Override
    void generateTestCase(ThreadInfo ti, Instruction current, Instruction next) {
        ChoiceGenerator<?> lastCG = ti.getVM().getSystemState().getChoiceGenerator();
        assert (lastCG != null);
        String code = getCachedTestCode(lastCG);
        assert code != null;
        code = code.replace("TESTID", Integer.toString(testID++));
        tests.add(code);
//        System.out.println("\n TEST: \n\n" + code);
    }

    String getCachedTestCode(ChoiceGenerator<?> lastCG) {
        ChoiceGenerator<?> cg = getLastBranchPoint(lastCG);
        if (cg == null)
            return null;
        if (cg instanceof HeapChoiceGeneratorLISSA)
            return ((HeapChoiceGeneratorLISSA) cg).getCurrentTestCode();

        return ((PCChoiceGeneratorLISSA) cg).getCurrentTestCode();
    }

    SymSolveSolution getCachedSolution(ChoiceGenerator<?> lastCG) {
        ChoiceGenerator<?> cg = getLastBranchPoint(lastCG);
        if (cg == null)
            return null;
        if (cg instanceof HeapChoiceGeneratorLISSA)
            return ((HeapChoiceGeneratorLISSA) cg).getCurrentSolution();

        return ((PCChoiceGeneratorLISSA) cg).getCurrentSolution();
    }

    PathCondition getCachedPathCondition(ChoiceGenerator<?> lastCG) {
        ChoiceGenerator<?> cg = getLastBranchPoint(lastCG);
        if (cg == null)
            return null;
        if (cg instanceof HeapChoiceGeneratorLISSA)
            return ((HeapChoiceGeneratorLISSA) cg).getCurrentRepOKPathCondition();

        return ((PCChoiceGeneratorLISSA) cg).getCurrentRepOKPathCondition();
    }

    ChoiceGenerator<?> getLastBranchPoint(ChoiceGenerator<?> lastCG) {
        assert (lastCG != null);
        ChoiceGenerator<?> currentCG = lastCG.getPreviousChoiceGenerator();
        for (ChoiceGenerator<?> cg = currentCG; cg != null; cg = cg.getPreviousChoiceGenerator()) {
            if (cg instanceof HeapChoiceGeneratorLISSA || cg instanceof PCChoiceGeneratorLISSA)
                return cg;
        }
        return null;
    }

    public void countPrunedBranch() {
        prunedBranches++;
    }

    public int getPrunedBranchCount() {
        return prunedBranches;
    }

}
