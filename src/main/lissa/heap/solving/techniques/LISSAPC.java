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
import lissa.heap.SymHeapHelper;
import lissa.heap.SymbolicInputHeapLISSA;
import lissa.heap.SymbolicReferenceInput;
import lissa.heap.builder.CheckPathConditionVisitor;
import lissa.heap.builder.HeapSolutionBuilder;
import symsolve.candidates.traversals.BFSCandidateTraversal;
import symsolve.candidates.traversals.CandidateTraversal;
import symsolve.vector.SymSolveVector;

public class LISSAPC extends LISSA implements PCCheckStrategy {

    HeapSolutionBuilder builder;
    public boolean executingRepOK = false;
    public int prunedPathsDueToPathCondition = 0;
    public long repokExecTime = 0;

    public LISSAPC() {
        builder = new HeapSolutionBuilder(heapSolver.getFinitization().getStateSpace(), heapSolver);
    }

    @Override
    public boolean checkHeapSatisfiability(ThreadInfo ti, SymbolicInputHeapLISSA symInputHeap) {
        currentSymbolicInput = symInputHeap.getImplicitInputThis();
        SymSolveVector vector = canonicalizer.createVector(symInputHeap);
        boolean hasSolution = heapSolver.isSatisfiable(vector);
        while (hasSolution) {
            if (isSatWithRespectToPathCondition(ti))
                return true;
            hasSolution = heapSolver.searchNextSolution();
            if (!hasSolution)
                prunedPathsDueToPathCondition++;
        }

        return false;
    }

    protected boolean isSatWithRespectToPathCondition(ThreadInfo ti) {
        PathCondition pc = PathCondition.getPC(ti.getVM());
        if (pc == null)
            return true;

        CheckPathConditionVisitor visitor = new CheckPathConditionVisitor(ti, pc.make_copy(), currentSymbolicInput,
                heapSolver.getAccessedIndices());
        CandidateTraversal traverser = new BFSCandidateTraversal(heapSolver.getFinitization().getStateSpace());
        traverser.traverse(heapSolver.getCurrentSolutionVector(), visitor);

        return visitor.isSolutionSAT();
    }

    public boolean hasNextSolution(ThreadInfo ti) {
        while (heapSolver.searchNextSolution()) {
            if (isSatWithRespectToPathCondition(ti))
                return true;
        }
        return false;
    }

    @Override
    public Instruction getNextInstructionToPrimitiveBranching(ThreadInfo ti, Instruction currentInstruction,
            Instruction nextInstruction) {
        SymbolicInputHeapLISSA symInputHeap = SymHeapHelper.getSymbolicInputHeap(ti.getVM());
        if (symInputHeap == null)
            return nextInstruction;
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
        builder.buildSolution(env, objRef, currentSymbolicInput, heapSolver.getCurrentSolutionVector());
    }

}
