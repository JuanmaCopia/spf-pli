package lissa.heap.solving.techniques;

import gov.nasa.jpf.symbc.numeric.PathCondition;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.MJIEnv;
import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.ObjRef;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.Types;
import lissa.bytecode.lazy.StaticRepOKCallInstruction;
import lissa.choicegenerators.HeapChoiceGeneratorLISSA;
import lissa.choicegenerators.PCChoiceGeneratorLISSA;
import lissa.choicegenerators.RepOKCallCG;
import lissa.heap.SymHeapHelper;
import lissa.heap.SymbolicInputHeapLISSA;
import lissa.heap.SymbolicReferenceInput;
import lissa.heap.builder.CheckPathConditionVisitor;
import lissa.heap.builder.HeapSolutionBuilder;
import symsolve.candidates.traversals.BFSCandidateTraversal;
import symsolve.candidates.traversals.CandidateTraversal;
import symsolve.vector.SymSolveSolution;
import symsolve.vector.SymSolveVector;

public class NT extends LISSA implements PCCheckStrategy {

    StaticRepOKCallInstruction repOKCallInstruction;
    HeapSolutionBuilder builder;
    boolean executingRepOK = false;
    int prunedBranches = 0;
    long repokExecTime = 0;
    long repOKStartTime = 0;
    public int primitiveBranches = 0;

    public NT() {
        builder = new HeapSolutionBuilder(heapSolver.getFinitization().getStateSpace(), heapSolver);
    }

    @Override
    public Instruction handleLazyInitializationStep(ThreadInfo ti, Instruction currentInstruction,
            Instruction nextInstruction, HeapChoiceGeneratorLISSA heapCG) {
        SymbolicInputHeapLISSA symInputHeap = (SymbolicInputHeapLISSA) heapCG.getCurrentSymInputHeap();
        SymSolveVector vector = canonicalizer.createVector(symInputHeap);
        SymSolveSolution solution = heapSolver.solve(vector);

        PCChoiceGeneratorLISSA pcCG = SymHeapHelper.getCurrentPCChoiceGenerator(ti.getVM());
        if (pcCG != null) {
            while (solution != null) {
                if (isSatWithRespectToPathCondition(ti, solution, pcCG.getCurrentPC(), symInputHeap)) {
                    break;
                }
                solution = heapSolver.getNextSolution(solution);
            }
        }

        if (solution == null) {
            ti.getVM().getSystemState().setIgnored(true); // Backtrack
            return currentInstruction;
        }

        return createInvokeRepOKInstruction(ti, currentInstruction, nextInstruction, symInputHeap, solution, pcCG,
                heapCG);
    }

    @Override
    public Instruction handlePrimitiveBranch(ThreadInfo ti, Instruction currentInstruction, Instruction nextInstruction,
            PCChoiceGeneratorLISSA pcCG) {
        HeapChoiceGeneratorLISSA heapCG = SymHeapHelper.getCurrentHeapChoiceGenerator(ti.getVM());
        if (heapCG == null)
            return nextInstruction;
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
                heapCG);
    }

    @Override
    public boolean isSatWithRespectToPathCondition(ThreadInfo ti, SymSolveSolution candidateSolution, PathCondition pc,
            SymbolicInputHeapLISSA symInputHeap) {
        assert (pc != null);

        CheckPathConditionVisitor visitor = new CheckPathConditionVisitor(ti, pc, symInputHeap, candidateSolution);
        CandidateTraversal traverser = new BFSCandidateTraversal(heapSolver.getFinitization().getStateSpace());
        traverser.traverse(candidateSolution.getSolutionVector(), visitor);
        return visitor.isSolutionSAT();
    }

    @Override
    public SymSolveSolution getNextSolution(ThreadInfo ti, SymSolveSolution previousSolution,
            SymbolicInputHeapLISSA symInputHeap) {
        assert (previousSolution != null);
        SymSolveSolution solution = heapSolver.getNextSolution(previousSolution);

        PCChoiceGeneratorLISSA pcCG = SymHeapHelper.getCurrentPCChoiceGenerator(ti.getVM());
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

    Instruction createInvokeRepOKInstruction(ThreadInfo ti, Instruction current, Instruction next,
            SymbolicInputHeapLISSA symInputHeap, SymSolveSolution solution, PCChoiceGeneratorLISSA pcCG,
            HeapChoiceGeneratorLISSA heapCG) {
        if (repOKCallInstruction == null)
            initializeRepOKCallInstruction(symInputHeap);

        repOKCallInstruction.initialize(current, next, symInputHeap, solution, pcCG, heapCG);
        pushArguments(ti, null, null);
        return repOKCallInstruction;
    }

    private void initializeRepOKCallInstruction(SymbolicInputHeapLISSA symInputHeap) {
        SymbolicReferenceInput symRefInput = symInputHeap.getImplicitInputThis();

        ClassInfo rootClassInfo = symRefInput.getRootHeapNode().getType();
        MethodInfo repokMI = rootClassInfo.getMethod("runRepOK()V", false);

        String clsName = repokMI.getClassInfo().getName();
        String mthName = repokMI.getName();
        String signature = repokMI.getSignature();

        repOKCallInstruction = new StaticRepOKCallInstruction(clsName, mthName, signature);
    }

    void pushArguments(ThreadInfo ti, Object[] args, Object[] attrs) {
        StackFrame frame = ti.getModifiableTopFrame();

        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                Object a = args[i];
                boolean isLong = false;

                if (a != null) {
                    if (a instanceof ObjRef) {
                        frame.pushRef(((ObjRef) a).getReference());
                    } else if (a instanceof Boolean) {
                        frame.push((Boolean) a ? 1 : 0, false);
                    } else if (a instanceof Integer) {
                        frame.push((Integer) a, false);
                    } else if (a instanceof Long) {
                        frame.pushLong((Long) a);
                        isLong = true;
                    } else if (a instanceof Double) {
                        frame.pushLong(Types.doubleToLong((Double) a));
                        isLong = true;
                    } else if (a instanceof Byte) {
                        frame.push((Byte) a, false);
                    } else if (a instanceof Short) {
                        frame.push((Short) a, false);
                    } else if (a instanceof Float) {
                        frame.push(Types.floatToInt((Float) a), false);
                    }
                }

                if (attrs != null && attrs[i] != null) {
                    if (isLong) {
                        frame.setLongOperandAttr(attrs[i]);
                    } else {
                        frame.setOperandAttr(attrs[i]);
                    }
                }
            }
        }
    }

    public void buildSolutionHeap(MJIEnv env, int objRef) {
        SymbolicInputHeapLISSA symInputHeap = SymHeapHelper.getSymbolicInputHeap(env.getVM());
        assert (symInputHeap != null);

        String cgID = "repOKCG";
        RepOKCallCG repOKCG = env.getSystemState().getCurrentChoiceGenerator(cgID, RepOKCallCG.class);
        SymSolveSolution solution = repOKCG.getCandidateHeapSolution();
        assert (solution != null);
        PCChoiceGeneratorLISSA pcCG = repOKCG.getPCChoiceGenerator();
        if (pcCG != null)
            assert (isSatWithRespectToPathCondition(env.getThreadInfo(), solution, pcCG.getCurrentPC(), symInputHeap));

        builder.buildSolution(env, objRef, symInputHeap, solution);
    }

    @Override
    public boolean isRepOKExecutionMode() {
        return executingRepOK;
    }

    public void startRepOKExecutionMode() {
        if (!executingRepOK) {
            executingRepOK = true;
            repOKStartTime = System.currentTimeMillis();
        }
    }

    public void stopRepOKExecutionMode() {
        if (executingRepOK) {
            executingRepOK = false;
            repokExecTime += System.currentTimeMillis() - repOKStartTime;
        }
    }

    public long getRepOKSolvingTime() {
        return repokExecTime;
    }

    public void countPrunedBranch() {
        prunedBranches++;
    }

    public int getPrunedBranchCount() {
        return prunedBranches;
    }

}