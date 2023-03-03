package lissa.heap.solving.techniques;

import gov.nasa.jpf.symbc.numeric.PCChoiceGenerator;
import gov.nasa.jpf.symbc.numeric.PathCondition;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.MJIEnv;
import gov.nasa.jpf.vm.ThreadInfo;
import lissa.bytecode.lazy.StaticRepOKCallInstruction;
import lissa.choicegenerators.HeapChoiceGeneratorLISSA;
import lissa.choicegenerators.RepOKCallCG;
import lissa.heap.SymHeapHelper;
import lissa.heap.SymbolicInputHeapLISSA;
import lissa.heap.builder.CheckPathConditionVisitor;
import lissa.heap.builder.HeapSolutionBuilder;
import symsolve.candidates.traversals.BFSCandidateTraversal;
import symsolve.candidates.traversals.CandidateTraversal;
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
        SymSolveVector vector = canonicalizer.createVector(symInputHeap);
        SymSolveSolution solution = heapSolver.solve(vector);

        PCChoiceGenerator pcCG = SymHeapHelper.getCurrentPCChoiceGenerator(ti.getVM());
        assert (pcCG != null);

        while (solution != null) {
            if (isSatWithRespectToPathCondition(ti, solution, pcCG.getCurrentPC(), symInputHeap)) {
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
            PCChoiceGenerator pcCG) {
        assert (!isRepOKExecutionMode());
        HeapChoiceGeneratorLISSA heapCG = SymHeapHelper.getCurrentHeapChoiceGenerator(ti.getVM());
        assert (heapCG != null);

        SymbolicInputHeapLISSA symInputHeap = (SymbolicInputHeapLISSA) heapCG.getCurrentSymInputHeap();
        assert (symInputHeap != null);

        primitiveBranches++;

        SymSolveVector vector = canonicalizer.createVector(symInputHeap);
        SymSolveSolution solution = heapSolver.solve(vector);
        while (solution != null) {
            if (isSatWithRespectToPathCondition(ti, solution, pcCG.getCurrentPC(), symInputHeap)) {
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
    public boolean isSatWithRespectToPathCondition(ThreadInfo ti, SymSolveSolution candidateSolution, PathCondition pc,
            SymbolicInputHeapLISSA symInputHeap) {
        assert (pc != null);
        if (pc.count() == 0)
            return true;

        CheckPathConditionVisitor visitor = new CheckPathConditionVisitor(ti, pc, symInputHeap, candidateSolution);
        CandidateTraversal traverser = new BFSCandidateTraversal(heapSolver.getFinitization().getStateSpace());
        traverser.traverse(candidateSolution.getSolutionVector(), visitor);
        return visitor.isSolutionSAT();
    }

    @Override
    public SymSolveSolution getNextSolution(ThreadInfo ti, SymSolveSolution previousSolution,
            SymbolicInputHeapLISSA symInputHeap) {
        assert (previousSolution != null);
        PCChoiceGenerator pcCG = SymHeapHelper.getCurrentPCChoiceGenerator(ti.getVM());
        assert isSatWithRespectToPathCondition(ti, previousSolution, pcCG.getCurrentPC(), symInputHeap);

        SymSolveSolution solution = heapSolver.getNextSolution(previousSolution);
        if (pcCG != null) {
            while (solution != null) {
                if (isSatWithRespectToPathCondition(ti, solution, pcCG.getCurrentPC(), symInputHeap)) {
                    return solution;
                }
                solution = heapSolver.getNextSolution(solution);
            }
        }
        return null;
    }

//    @Override
//    public void checkPathValidity(ThreadInfo ti, Instruction current, Instruction next) {
//        HeapChoiceGeneratorLISSA heapCG = SymHeapHelper.getCurrentHeapChoiceGenerator(ti.getVM());
//        assert (heapCG != null);
//        StaticRepOKCallInstruction ins = (StaticRepOKCallInstruction) handleLazyInitializationStep(ti, current, next,
//                heapCG);
//        ins.setAsPashValidityCheck();
//        ti.setNextPC(ins);
//    }

    Instruction createInvokeRepOKInstruction(ThreadInfo ti, Instruction current, Instruction next,
            SymbolicInputHeapLISSA symInputHeap, SymSolveSolution solution, PCChoiceGenerator pcCG,
            HeapChoiceGeneratorLISSA heapCG, boolean isLazyStep) {
        if (repOKCallInstruction == null)
            repOKCallInstruction = SymHeapHelper.createStaticRepOKCallInstruction(symInputHeap, "runRepOK()V");

        assert (pcCG != null && heapCG != null);
        RepOKCallCG rcg = new RepOKCallCG("repOKCG", symInputHeap, heapCG, solution, isLazyStep);
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
        PCChoiceGenerator pcCG = env.getSystemState().getLastChoiceGeneratorOfType(PCChoiceGenerator.class);
        assert (isSatWithRespectToPathCondition(env.getThreadInfo(), solution, pcCG.getCurrentPC(), symInputHeap));

        builder.buildSolution(env, objRef, symInputHeap, solution);
    }

    public void countPrunedBranch() {
        prunedBranches++;
    }

    public int getPrunedBranchCount() {
        return prunedBranches;
    }

}
