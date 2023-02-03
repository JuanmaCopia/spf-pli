package lissa.heap.solving.techniques;

import gov.nasa.jpf.symbc.numeric.Constraint;
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
import lissa.heap.SymHeapHelper;
import lissa.heap.SymbolicInputHeapLISSA;
import lissa.heap.SymbolicReferenceInput;
import lissa.heap.builder.CheckPathConditionVisitor;
import lissa.heap.builder.HeapSolutionBuilder;
import symsolve.candidates.traversals.BFSCandidateTraversal;
import symsolve.candidates.traversals.CandidateTraversal;
import symsolve.vector.SymSolveSolution;
import symsolve.vector.SymSolveVector;

public class LISSAPC extends LISSA implements PCCheckStrategy {

    HeapSolutionBuilder builder;
    boolean executingRepOK = false;
    int prunedBranches = 0;
    long repokExecTime = 0;
    long repOKStartTime = 0;

    public int primitiveBranchingCacheHits = 0;
    public int primitiveBranches = 0;

    public LISSAPC() {
        builder = new HeapSolutionBuilder(heapSolver.getFinitization().getStateSpace(), heapSolver);
    }

    @Override
    public boolean checkHeapSatisfiability(ThreadInfo ti, SymbolicInputHeapLISSA symInputHeap) {
        SymSolveVector vector = canonicalizer.createVector(symInputHeap);
        SymSolveSolution solution = heapSolver.solve(vector);
        while (solution != null) {
            if (isSatWithRespectToPathCondition(ti, solution, symInputHeap)) {
                symInputHeap.setHeapSolution(solution);
                return true;
            }
            solution = heapSolver.getNextSolution(solution);
        }
        return false;
    }

    protected boolean isSatWithRespectToPathCondition(ThreadInfo ti, SymSolveSolution candidateSolution,
            SymbolicInputHeapLISSA symInputHeap) {
        PathCondition pc = PathCondition.getPC(ti.getVM());
        if (pc == null)
            return true;

        CheckPathConditionVisitor visitor = new CheckPathConditionVisitor(ti, pc.make_copy(), symInputHeap,
                candidateSolution);
        CandidateTraversal traverser = new BFSCandidateTraversal(heapSolver.getFinitization().getStateSpace());
        traverser.traverse(candidateSolution.getSolutionVector(), visitor);

        return visitor.isSolutionSAT();
    }

    @Override
    public SymSolveSolution getNextSolution(ThreadInfo ti, SymSolveSolution previousSolution,
            SymbolicInputHeapLISSA symInputHeap) {
        assert (previousSolution != null);

        SymSolveSolution solution = heapSolver.getNextSolution(previousSolution);
        while (solution != null) {
            if (isSatWithRespectToPathCondition(ti, solution, symInputHeap)) {
                return solution;
            }
            solution = heapSolver.getNextSolution(solution);
        }

        return null;
    }

    @Override
    public Instruction getNextInstructionToPrimitiveBranching(ThreadInfo ti, Instruction currentInstruction,
            Instruction nextInstruction, PathCondition pc) {
        SymbolicInputHeapLISSA symInputHeap = SymHeapHelper.getSymbolicInputHeap(ti.getVM());
        if (symInputHeap == null)
            return nextInstruction;

        SymSolveSolution previousSolution = symInputHeap.getHeapSolution();
        if (previousSolution == null) {
            if (!checkHeapSatisfiability(ti, symInputHeap))
                return nextInstruction;
        }

        primitiveBranches++;

        PathCondition repOKPC = symInputHeap.getRepOKPC();
        if (repOKPC != null) {
            Constraint lastConstraint = pc.header;
            repOKPC._addDet(lastConstraint.getComparator(), lastConstraint.getLeft(), lastConstraint.getRight());
            if (repOKPC.simplify()) {
                // Is Sat with cached repOK path condition
                primitiveBranchingCacheHits++;
                return nextInstruction;
            }
        }

        return createInvokeRepOKInstruction(ti, currentInstruction, nextInstruction, symInputHeap);
    }

    @Override
    public Instruction getNextInstructionToGETFIELD(ThreadInfo ti, Instruction currentInstruction,
            Instruction nextInstruction, SymbolicInputHeapLISSA symInputHeap) {
        return createInvokeRepOKInstruction(ti, currentInstruction, nextInstruction, symInputHeap);
    }

    Instruction createInvokeRepOKInstruction(ThreadInfo ti, Instruction currentInstruction, Instruction nextInstruction,
            SymbolicInputHeapLISSA symInputHeap) {
        assert (symInputHeap != null);
        SymbolicReferenceInput symRefInput = symInputHeap.getImplicitInputThis();
        assert (symRefInput != null);
        ClassInfo rootClassInfo = symRefInput.getRootHeapNode().getType();
        MethodInfo repokMI = rootClassInfo.getMethod("runRepOK()V", false);

        assert (repokMI != null);

        String clsName = repokMI.getClassInfo().getName();
        String mthName = repokMI.getName();
        String signature = repokMI.getSignature();

        StaticRepOKCallInstruction realInvoke = new StaticRepOKCallInstruction(clsName, mthName, signature);
        realInvoke.setMethodInfo(currentInstruction.getMethodInfo());
        realInvoke.setLocation(currentInstruction.getInstructionIndex(), currentInstruction.getPosition());
        realInvoke.nextInstruction = nextInstruction;
        realInvoke.currentSymInputHeap = symInputHeap;

        Object[] args = null;
        Object[] attrs = null;
        pushArguments(ti, args, attrs);

        return realInvoke;
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
        builder.buildSolution(env, objRef);
    }

    @Override
    public boolean isRepOKExecutionMode() {
        return executingRepOK;
    }

    @Override
    public void startRepOKExecutionMode() {
        if (!executingRepOK) {
            executingRepOK = true;
            repOKStartTime = System.currentTimeMillis();
        }
    }

    @Override
    public void stopRepOKExecutionMode() {
        if (executingRepOK) {
            executingRepOK = false;
            repokExecTime += System.currentTimeMillis() - repOKStartTime;
        }
    }

    @Override
    public long getRepOKSolvingTime() {
        return repokExecTime;
    }

    @Override
    public void countPrunedBranch() {
        prunedBranches++;
    }

    @Override
    public int getPrunedBranchCount() {
        return prunedBranches;
    }

}
