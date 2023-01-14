package lissa.heap.solving.techniques;

import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.MJIEnv;
import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.ObjRef;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.Types;
import lissa.bytecode.STATICREPOK2;
import lissa.heap.SymbolicReferenceInput;
import lissa.heap.builder.HeapSolutionBuilder;

public class LISSAPC extends LISSA {

    HeapSolutionBuilder builder;
    public int prunedPathsDueToPathCondition = 0;

    public LISSAPC() {
        builder = new HeapSolutionBuilder(heapSolver.getFinitization().getStateSpace());
    }

    @Override
    public Instruction getNextInstructionForGETFIELD(ThreadInfo ti, Instruction getfield,
            SymbolicReferenceInput symRefInput) {
        return createInvokeRepOKInstruction(ti, getfield, symRefInput);
    }

    Instruction createInvokeRepOKInstruction(ThreadInfo ti, Instruction getfield, SymbolicReferenceInput symRefInput) {
        ClassInfo rootClassInfo = symRefInput.getRootHeapNode().getType();
        MethodInfo repokMI = rootClassInfo.getMethod("runRepOK()V", false);

        assert (repokMI != null);

        String clsName = repokMI.getClassInfo().getName();
        String mthName = repokMI.getName();
        String signature = repokMI.getSignature();

        STATICREPOK2 realInvoke = new STATICREPOK2(clsName, mthName, signature);
        realInvoke.setMethodInfo(getfield.getMethodInfo());
        realInvoke.setLocation(getfield.getInstructionIndex(), getfield.getPosition());
        realInvoke.nextOfGETFIELD = ti.getPC().getNext();

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
