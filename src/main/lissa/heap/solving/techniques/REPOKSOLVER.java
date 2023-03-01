package lissa.heap.solving.techniques;

import gov.nasa.jpf.symbc.numeric.PCChoiceGenerator;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.ThreadInfo;
import lissa.bytecode.lazy.StaticCompleteRepOKCallInstruction;
import lissa.choicegenerators.HeapChoiceGeneratorLISSA;
import lissa.choicegenerators.RepokCallWithLazyInitCG;
import lissa.heap.SymHeapHelper;

public class REPOKSOLVER extends NT {

    @Override
    public Instruction handleLazyInitializationStep(ThreadInfo ti, Instruction currentInstruction,
            Instruction nextInstruction, HeapChoiceGeneratorLISSA heapCG) {
        assert (!isRepOKExecutionMode());

        return createInvokeCompleteRepOKInstruction(ti, currentInstruction, nextInstruction);
    }

    @Override
    public Instruction handlePrimitiveBranch(ThreadInfo ti, Instruction currentInstruction, Instruction nextInstruction,
            PCChoiceGenerator pcCG) {
        assert (!isRepOKExecutionMode());

        return createInvokeCompleteRepOKInstruction(ti, currentInstruction, nextInstruction);
    }

    Instruction createInvokeCompleteRepOKInstruction(ThreadInfo ti, Instruction current, Instruction next) {

        StaticCompleteRepOKCallInstruction repOKCallInstruction = SymHeapHelper
                .createStaticCompleteRepOKCallInstruction("runRepOK()V");

        RepokCallWithLazyInitCG rcg = new RepokCallWithLazyInitCG("repOKCompleteCG");
        repOKCallInstruction.initialize(current, next, rcg);
        SymHeapHelper.pushArguments(ti, null, null);
        return repOKCallInstruction;
    }

}
