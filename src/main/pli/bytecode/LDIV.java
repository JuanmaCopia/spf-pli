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
package pli.bytecode;

import gov.nasa.jpf.symbc.SymbolicInstructionFactory;
import gov.nasa.jpf.symbc.numeric.Comparator;
import gov.nasa.jpf.symbc.numeric.IntegerExpression;
import gov.nasa.jpf.symbc.numeric.PathCondition;
import gov.nasa.jpf.vm.ChoiceGenerator;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.StackFrame;
import gov.nasa.jpf.vm.ThreadInfo;
import pli.choicegenerators.PCChoiceGeneratorLISSA;
import pli.heap.SymHeapHelper;

/**
 * Divide long ..., value1, value2 => ..., result
 *
 * YN: fixed choice selection in symcrete support (Yannic Noller
 * <nolleryc@gmail.com>)
 */
public class LDIV extends gov.nasa.jpf.jvm.bytecode.LDIV {

    @Override
    public Instruction execute(ThreadInfo th) {
        StackFrame sf = th.getModifiableTopFrame();
        IntegerExpression sym_v1 = (IntegerExpression) sf.getOperandAttr(1);
        long v1 = sf.peekLong();
        IntegerExpression sym_v2 = (IntegerExpression) sf.getOperandAttr(3);
        long v2 = sf.peekLong(2);
        // super.execute takes care of if(v1==0) return th.createAndThrowException ...
        if (sym_v1 == null) {
            Instruction next_insn = super.execute(th);
            if (sym_v2 != null) // result is symbolic expression
                sf.setLongOperandAttr(sym_v2._div(v1));
            return next_insn;
        }

        // div by zero check affects path condition
        // sym_v1 is non-null and should be checked against zero

        ChoiceGenerator<?> cg;
        boolean condition;

        if (!th.isFirstStepInsn()) { // first time around
            cg = new PCChoiceGeneratorLISSA(SymbolicInstructionFactory.collect_constraints ? 1 : 2);
            ((PCChoiceGeneratorLISSA) cg).setOffset(this.position);
            ((PCChoiceGeneratorLISSA) cg).setMethodName(this.getMethodInfo().getFullName());
            th.getVM().setNextChoiceGenerator(cg);
            return this;
        } else { // this is what really returns results
            cg = th.getVM().getChoiceGenerator();
            assert (cg instanceof PCChoiceGeneratorLISSA) : "expected PCChoiceGeneratorLISSA, got: " + cg;

            if (SymbolicInstructionFactory.collect_constraints) {
                condition = v1 == 0; // i.e. false
                ((PCChoiceGeneratorLISSA) cg).select(condition ? 1 : 0); // YN: set the choice correctly
            } else {
                condition = (Integer) cg.getNextChoice() == 0 ? false : true;
            }
        }

        super.execute(th); // pops v1, v2 and pushes r = v2 / v1;

        PathCondition pc;
        ChoiceGenerator<?> prev_cg = cg.getPreviousChoiceGeneratorOfType(PCChoiceGeneratorLISSA.class);

        if (prev_cg == null)
            pc = new PathCondition();
        else
            pc = ((PCChoiceGeneratorLISSA) prev_cg).getCurrentPC();

        assert pc != null;

        if (condition) { // check div by zero
            pc._addDet(Comparator.EQ, sym_v1, 0);
            if (pc.simplify()) { // satisfiable
                ((PCChoiceGeneratorLISSA) cg).setCurrentPC(pc);

                Instruction nextInstruction = th.createAndThrowException("java.lang.ArithmeticException", "div by 0");
                return SymHeapHelper.checkIfPathConditionAndHeapAreSAT(th, this, nextInstruction, (PCChoiceGeneratorLISSA) cg);
            } else {
                th.getVM().getSystemState().setIgnored(true);
                return getNext(th);
            }
        } else {
            pc._addDet(Comparator.NE, sym_v1, 0);
            if (pc.simplify()) { // satisfiable
                ((PCChoiceGeneratorLISSA) cg).setCurrentPC(pc);

                // set the result
                IntegerExpression result;
                if (sym_v2 != null)
                    result = sym_v2._div(sym_v1);
                else
                    result = sym_v1._div_reverse(v2);

                sf = th.getModifiableTopFrame();
                sf.setLongOperandAttr(result);

                Instruction nextInstruction = getNext(th);
                return SymHeapHelper.checkIfPathConditionAndHeapAreSAT(th, this, nextInstruction, (PCChoiceGeneratorLISSA) cg);

            } else {
                th.getVM().getSystemState().setIgnored(true);
                return getNext(th);
            }
        }

    }

}
