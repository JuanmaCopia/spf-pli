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

// author corina pasareanu corina.pasareanu@sv.cmu.edu

package pli.bytecode;

import gov.nasa.jpf.symbc.SymbolicInstructionFactory;
import gov.nasa.jpf.symbc.numeric.Comparator;
import gov.nasa.jpf.symbc.numeric.IntegerExpression;
import gov.nasa.jpf.symbc.numeric.PathCondition;
import gov.nasa.jpf.vm.ArrayIndexOutOfBoundsExecutiveException;
import gov.nasa.jpf.vm.ElementInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.MJIEnv;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;
import pli.choicegenerators.PCChoiceGeneratorLISSA;
import pli.heap.SymHeapHelper;

/**
 * Load double from array ..., arrayref, index => ..., value
 *
 * YN: added symcrete support (Yannic Noller <nolleryc@gmail.com>)
 */
public class DALOAD extends gov.nasa.jpf.jvm.bytecode.DALOAD {

    public static int lastLength = -1; // YN: helper variable for last known length

    @Override
    public Instruction execute(ThreadInfo ti) {
        if (peekIndexAttr(ti) == null || !(peekIndexAttr(ti) instanceof IntegerExpression))
            return super.execute(ti);
        StackFrame frame = ti.getModifiableTopFrame();
        arrayRef = frame.peek(1); // ..,arrayRef,idx
        if (arrayRef == MJIEnv.NULL) {
            return ti.createAndThrowException("java.lang.NullPointerException");
        }

        ElementInfo eiArray = ti.getElementInfo(arrayRef);
        int len = (eiArray.getArrayFields()).arrayLength(); // assumed concrete
        lastLength = len; // YN: store last length

        if (!ti.isFirstStepInsn()) {
            PCChoiceGeneratorLISSA arrayCG;

            if (SymbolicInstructionFactory.collect_constraints) {
                arrayCG = new PCChoiceGeneratorLISSA(1); // YN: symcrete mode
            } else {
                arrayCG = new PCChoiceGeneratorLISSA(0, len + 1); // add 2 error cases: <0, >=len
            }

            arrayCG.setOffset(this.position);
            arrayCG.setMethodName(this.getMethodInfo().getFullName());

            ti.getVM().getSystemState().setNextChoiceGenerator(arrayCG);

            if (SymbolicInstructionFactory.debugMode)
                System.out.println("# array cg registered: " + arrayCG);
            return this;

        } else { // this is what really returns results

            // index = frame.peek();
            PCChoiceGeneratorLISSA lastCG = ti.getVM().getSystemState()
                    .getLastChoiceGeneratorOfType(PCChoiceGeneratorLISSA.class);
            assert (lastCG != null);
            PCChoiceGeneratorLISSA prevCG = lastCG.getPreviousChoiceGeneratorOfType(PCChoiceGeneratorLISSA.class);

            if (SymbolicInstructionFactory.collect_constraints) {
                // YN: reuse index written from concrete exec + set choice correctly
                index = peekIndex(ti);
                ((PCChoiceGeneratorLISSA) lastCG).select(index);
            } else {
                index = lastCG.getNextChoice();
            }

            // System.out.println("array index "+index);
            IntegerExpression sym_index = (IntegerExpression) peekIndexAttr(ti);
            // check the constraint

            PathCondition pc;

            if (prevCG == null)
                pc = new PathCondition();
            else
                pc = ((PCChoiceGeneratorLISSA) prevCG).getCurrentPC();

            assert pc != null;

            if (index < len) {
                pc._addDet(Comparator.EQ, index, sym_index);
                if (pc.simplify()) { // satisfiable
                    ((PCChoiceGeneratorLISSA) lastCG).setCurrentPC(pc);
                } else {
                    ti.getVM().getSystemState().setIgnored(true);// backtrack
                    return getNext(ti);
                }
            }
            // now check for out of bounds exceptions
            else if (index == len) {
                pc._addDet(Comparator.LT, sym_index, 0);
                if (pc.simplify()) { // satisfiable
                    ((PCChoiceGeneratorLISSA) lastCG).setCurrentPC(pc);
                    Instruction nextInstruction = ti
                            .createAndThrowException("java.lang.ArrayIndexOutOfBoundsException");
                    return SymHeapHelper.checkIfPathConditionAndHeapAreSAT(ti, this, nextInstruction,
                            (PCChoiceGeneratorLISSA) lastCG);
                } else {
                    ti.getVM().getSystemState().setIgnored(true);// backtrack
                    return getNext(ti);
                }
            } else if (index == len + 1) {
                pc._addDet(Comparator.GE, sym_index, len);
                if (pc.simplify()) { // satisfiable
                    ((PCChoiceGeneratorLISSA) lastCG).setCurrentPC(pc);
                    Instruction nextInstruction = ti
                            .createAndThrowException("java.lang.ArrayIndexOutOfBoundsException");
                    return SymHeapHelper.checkIfPathConditionAndHeapAreSAT(ti, this, nextInstruction,
                            (PCChoiceGeneratorLISSA) lastCG);
                } else {
                    ti.getVM().getSystemState().setIgnored(true);// backtrack
                    return getNext(ti);
                }
            }

            // original code for concrete execution
            arrayOperandAttr = peekArrayAttr(ti);
            indexOperandAttr = peekIndexAttr(ti);

            // corina: Ignore POR for now
            /*
             * Scheduler scheduler = ti.getScheduler(); if (scheduler.canHaveSharedArrayCG(
             * ti, this, eiArray, index)){ // don't modify the frame before this eiArray =
             * scheduler.updateArraySharedness(ti, eiArray, index); if
             * (scheduler.setsSharedArrayCG( ti, this, eiArray, index)){ return this; } }
             */

            frame.pop(2); // now we can pop index and array reference
            // assign to index any value between 0 and array length

            try {
                push(frame, eiArray, index);

                Object elementAttr = eiArray.getElementAttr(index);
                if (elementAttr != null) {
                    if (getElementSize() == 1) {
                        frame.setOperandAttr(elementAttr);
                    } else {
                        frame.setLongOperandAttr(elementAttr);
                    }
                }

                return SymHeapHelper.checkIfPathConditionAndHeapAreSAT(ti, this, getNext(ti),
                        (PCChoiceGeneratorLISSA) lastCG);
            } catch (ArrayIndexOutOfBoundsExecutiveException ex) {
                return SymHeapHelper.checkIfPathConditionAndHeapAreSAT(ti, this, ex.getInstruction(),
                        (PCChoiceGeneratorLISSA) lastCG);
            }
        }
    }

}
