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

import gov.nasa.jpf.jvm.bytecode.JVMInstructionVisitor;
import gov.nasa.jpf.symbc.bytecode.INVOKEVIRTUAL;
import gov.nasa.jpf.vm.ChoiceGenerator;
import gov.nasa.jpf.vm.ClassChangeException;
import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.MJIEnv;
import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.choice.IntIntervalGenerator;

/**
 * Invoke instance method; dispatch based on class ..., objectref, [arg1, [arg2
 * ...]] => ...
 */
public class INVOKEREPOK extends INVOKEVIRTUAL {

    public INVOKEREPOK(String clsDescriptor, String methodName, String signature) {
        super(clsDescriptor, methodName, signature);
    }

    Instruction next;
    int rootIndex;

    @Override
    public Instruction execute(ThreadInfo ti) {
        System.out.println("INVOKE REPOK!! ");
        int objRef = ti.getCalleeThis(getArgSize());
        MethodInfo callee;

        if (rootIndex == MJIEnv.NULL) {
            System.out.println("INVOKE repok: null object!! ");
            lastObj = MJIEnv.NULL;
            return ti.createAndThrowException("java.lang.NullPointerException",
                    "Calling '" + mname + "' on null object");
        }

        try {
            callee = getInvokedMethod(ti, rootIndex);
        } catch (ClassChangeException ccx) {
            return ti.createAndThrowException("java.lang.IncompatibleClassChangeError", ccx.getMessage());
        }

        ElementInfo ei = ti.getElementInfo(rootIndex);

        if (callee == null) {
            String clsName = ti.getClassInfo(rootIndex).getName();
            return ti.createAndThrowException("java.lang.NoSuchMethodError", clsName + '.' + mname);
        } else {
            if (callee.isAbstract()) {
                return ti.createAndThrowException("java.lang.AbstractMethodError",
                        callee.getFullName() + ", object: " + ei);
            }
        }

        if (callee.isSynchronized()) {
            ei = ti.getScheduler().updateObjectSharedness(ti, ei, null); // locks most likely belong to shared objects
            if (reschedulesLockAcquisition(ti, ei)) {
                return this;
            }
        }

        String choiceGeneratorID = "repokCall";
        ChoiceGenerator<?> cg;
        int choice;

        if (!ti.isFirstStepInsn()) { // first time around
            cg = new IntIntervalGenerator(choiceGeneratorID, 0, 1);
            ti.getVM().getSystemState().setNextChoiceGenerator(cg);
            return this;
        } else { // this is what really returns results
            cg = ti.getVM().getSystemState().getChoiceGenerator();
            choice = (Integer) cg.getNextChoice();
        }

        System.out.println("choice is: " + choice);

        if (choice == 0) {

            ClassInfo ciCaller = callee.getClassInfo();
            System.out.println("Caller is: " + ciCaller.toString());
            System.out.println("Calleee is: " + callee.toString());
            StackFrame frame = ciCaller.createStackFrame(ti, callee);
            ti.pushFrame(frame);
            ti.enter();
            return ti.getPC();
//            return super.execute(ti);
        } else {
            System.out.println("CONTINUING ");
            return next;
        }

    }

    @Override
    public int getByteCode() {
        return 0xB6;
    }

    @Override
    public String toString() {
        // methodInfo not set outside real call context (requires target object)
        return "invoke repok:  " + cname + '.' + mname;
    }

    @Override
    public void accept(JVMInstructionVisitor insVisitor) {
        insVisitor.visit(this);
    }

//    @Override
//    public Instruction execute (ThreadInfo ti) {
//      int objRef = ti.getCalleeThis(getArgSize());
//      MethodInfo callee;
//
//      if (objRef == MJIEnv.NULL) {
//        lastObj = MJIEnv.NULL;
//        return ti.createAndThrowException("java.lang.NullPointerException", "Calling '" + mname + "' on null object");
//      }
//
//      try {
//        callee = getInvokedMethod(ti, objRef);
//      } catch (ClassChangeException ccx){
//        return ti.createAndThrowException("java.lang.IncompatibleClassChangeError", ccx.getMessage());
//      }
//      
//      ElementInfo ei = ti.getElementInfo(objRef);
//      
//      if (callee == null) {
//        String clsName = ti.getClassInfo(objRef).getName();
//        return ti.createAndThrowException("java.lang.NoSuchMethodError", clsName + '.' + mname);
//      } else {
//        if (callee.isAbstract()){
//          return ti.createAndThrowException("java.lang.AbstractMethodError", callee.getFullName() + ", object: " + ei);
//        }
//      }
//
//      if (callee.isSynchronized()) {
//        ei = ti.getScheduler().updateObjectSharedness(ti, ei, null); // locks most likely belong to shared objects
//        if (reschedulesLockAcquisition(ti, ei)){
//          return this;
//        }
//      }
//
//      setupCallee( ti, callee); // this creates, initializes and pushes the callee StackFrame
//
//      return ti.getPC(); // we can't just return the first callee insn if a listener throws an exception
//    }
}
