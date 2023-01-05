/*
 * Copyright (C) 2014, United States Government, as represented by the
 * Administrator of the National Aeronautics and Space Administration.
 * All rights reserved.
 *
 * The Java Pathfinder core (jpf-core) platform is licensed under the
 * Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0. 
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */

package lissa.bytecode;

import java.util.LinkedList;
import java.util.List;

import gov.nasa.jpf.jvm.JVMInstructionFactory;
import gov.nasa.jpf.jvm.bytecode.JVMInstruction;
import gov.nasa.jpf.jvm.bytecode.JVMInstructionVisitor;
import gov.nasa.jpf.util.Invocation;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.ObjRef;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.Types;
import gov.nasa.jpf.vm.choice.InvocationCG;
import lissa.heap.SymHeapHelper;
import lissa.heap.SymbolicInputHeapLISSA;
import lissa.heap.SymbolicReferenceInput;

/**
 * a sytnthetic INVOKE instruction that gets it's parameters from an
 * InvocationCG. Whoever uses this better makes sure the frame this executes in
 * has enough operand space (e.g. a DirectCallStackFrame).
 * 
 */
public class INVREPOK extends Instruction implements JVMInstruction {

    public INVREPOK() {
    }

    @Override
    public Instruction execute(ThreadInfo ti) {

        List<Invocation> invokes;

        SymbolicInputHeapLISSA symInputHeap = SymHeapHelper.getSymbolicInputHeap();
        SymbolicReferenceInput symRefInput = symInputHeap.getImplicitInputThis();
        int rootIndex = symRefInput.getRootHeapNode().getIndex();
        ElementInfo ei = ti.getModifiableElementInfo(rootIndex);
        ClassInfo rootClassInfo = ei.getClassInfo();

        MethodInfo repokMI = rootClassInfo.getMethod("emptyMethod()V", false);

        Object[] args = new Object[1];
        args[0] = rootIndex;
        Object[] attrs = null;
        Invocation repokInvocation = new Invocation(repokMI, args, attrs);

        invokes = new LinkedList<Invocation>();
        invokes.add(repokInvocation);

        if (!ti.isFirstStepInsn()) {
            InvocationCG cg = new InvocationCG("INVOKECG", invokes);
            if (ti.getVM().setNextChoiceGenerator(cg)) {
                return this;
            }

        } else {
            InvocationCG cg = ti.getVM().getCurrentChoiceGenerator("INVOKECG", InvocationCG.class);
            assert (cg != null) : "no current InvocationCG";

            Invocation call = cg.getNextChoice();
            MethodInfo callee = call.getMethodInfo();
            JVMInstructionFactory insnFactory = JVMInstructionFactory.getFactory();

            String clsName = callee.getClassInfo().getName();
            String mthName = callee.getName();
            String signature = callee.getSignature();

            Instruction realInvoke;
            if (callee.isStatic()) {
                realInvoke = insnFactory.invokestatic(clsName, mthName, signature);
            } else {
                realInvoke = insnFactory.invokevirtual(clsName, mthName, signature);
            }

            StackFrame frame = ti.getModifiableTopFrame();
            // frame.pushLocal(rootIndex);
            frame.push(rootIndex);

            // pushArguments(ti, call.getArguments(), call.getAttrs());

            return realInvoke;
        }

        return getNext();
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

    @Override
    public boolean isExtendedInstruction() {
        return true;
    }

    public static final int OPCODE = 258;

    @Override
    public int getByteCode() {
        return OPCODE;
    }

    @Override
    public void accept(JVMInstructionVisitor insVisitor) {
        insVisitor.visit(this);
    }

}
