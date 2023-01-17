package lissa.heap.solving.techniques;

import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.MJIEnv;
import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.ObjRef;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.Types;
import lissa.bytecode.StaticRepOKCallInstruction;
import lissa.heap.SymHeapHelper;
import lissa.heap.SymbolicInputHeapLISSA;
import lissa.heap.SymbolicReferenceInput;
import lissa.heap.builder.HeapSolutionBuilder;

public class LISSAPC extends LISSA {

    HeapSolutionBuilder builder;
    public int prunedPathsDueToPathCondition = 0;

    public LISSAPC() {
        builder = new HeapSolutionBuilder(heapSolver.getFinitization().getStateSpace());
    }

    public boolean hasNextSolution() {
        return heapSolver.searchNextSolution();
    }

    @Override
    public Instruction getNextInstructionToPrimitiveBranching(ThreadInfo ti) {
        SymbolicInputHeapLISSA symInputHeap = SymHeapHelper.getSymbolicInputHeap();
        if (symInputHeap == null)
            return ti.getPC().getNext();
        return createInvokeRepOKInstruction(ti, symInputHeap);
    }

    @Override
    public Instruction getNextInstructionToGETFIELD(ThreadInfo ti, SymbolicInputHeapLISSA symInputHeap) {
        return createInvokeRepOKInstruction(ti, symInputHeap);
    }

    Instruction createInvokeRepOKInstruction(ThreadInfo ti, SymbolicInputHeapLISSA symInputHeap) {
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
        Instruction currentInstruction = ti.getPC();
        realInvoke.setMethodInfo(currentInstruction.getMethodInfo());
        realInvoke.setLocation(currentInstruction.getInstructionIndex(), currentInstruction.getPosition());
        realInvoke.nextInstruction = ti.getPC().getNext();
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
