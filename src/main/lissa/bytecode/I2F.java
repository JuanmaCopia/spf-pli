/*
 * Copyright (C) 2014, United States Government, as represented by the
 * Administrator of the National Aeronautics and Space Administration.
 * All rights reserved.
 *
 * Symbolic Pathfinder (jpf-symbc) is licensed under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
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

import gov.nasa.jpf.symbc.numeric.Comparator;
import gov.nasa.jpf.symbc.numeric.IntegerExpression;
import gov.nasa.jpf.symbc.numeric.PathCondition;
import gov.nasa.jpf.symbc.numeric.SymbolicReal;
import gov.nasa.jpf.vm.ChoiceGenerator;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;
import lissa.choicegenerators.PCChoiceGeneratorLISSA;
import lissa.heap.SymHeapHelper;

/**
 * Convert int to float ..., value =>..., result
 */
public class I2F extends gov.nasa.jpf.jvm.bytecode.I2F {

    public Instruction execute(ThreadInfo th) {

        IntegerExpression sym_ival = (IntegerExpression) th.getModifiableTopFrame().getOperandAttr();

        if (sym_ival == null) {
            // System.out.println("Execute concrete I2F");
            return super.execute(th);
        } else {
            // System.out.println("Execute symbolic I2F");
            // here we get a hold of the current path condition and
            // add an extra mixed constraint sym_dval==sym_ival

            ChoiceGenerator<?> cg;
            if (!th.isFirstStepInsn()) { // first time around
                cg = new PCChoiceGeneratorLISSA(1); // only one choice
                th.getVM().getSystemState().setNextChoiceGenerator(cg);
                return this;
            } else { // this is what really returns results
                cg = th.getVM().getSystemState().getChoiceGenerator();
                assert (cg instanceof PCChoiceGeneratorLISSA) : "expected PCChoiceGeneratorLISSA, got: " + cg;
            }

            // get the path condition from the
            // previous choice generator of the same type

            PathCondition pc;
            ChoiceGenerator<?> prev_cg = cg.getPreviousChoiceGeneratorOfType(PCChoiceGeneratorLISSA.class);

            if (prev_cg == null)
                pc = new PathCondition(); // TODO: handling of preconditions needs to be changed
            else
                pc = ((PCChoiceGeneratorLISSA) prev_cg).getCurrentPC();
            assert pc != null;

            StackFrame sf = th.getModifiableTopFrame();
            int ival = sf.pop();
            sf.pushFloat(ival);
            SymbolicReal sym_dval = new SymbolicReal();
            sf.setOperandAttr(sym_dval);

            pc._addDet(Comparator.EQ, sym_dval, sym_ival);

            if (!pc.simplify()) { // not satisfiable
                th.getVM().getSystemState().setIgnored(true);
            } else {
                ((PCChoiceGeneratorLISSA) cg).setCurrentPC(pc);
            }

            Instruction nextInstruction = getNext(th);
            return SymHeapHelper.checkIfPathConditionAndHeapAreSAT(th, this, nextInstruction, (PCChoiceGeneratorLISSA) cg);
        }
    }
}
