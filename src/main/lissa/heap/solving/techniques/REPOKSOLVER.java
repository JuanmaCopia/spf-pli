package lissa.heap.solving.techniques;

import lissa.choicegenerators.PCChoiceGeneratorLISSA;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.ThreadInfo;
import lissa.bytecode.lazy.StaticRepOKCallInstruction;
import lissa.choicegenerators.HeapChoiceGeneratorLISSA;
import lissa.choicegenerators.RepOKCompleteCallCG;
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
            PCChoiceGeneratorLISSA pcCG) {
        assert (!isRepOKExecutionMode());

        return createInvokeCompleteRepOKInstruction(ti, currentInstruction, nextInstruction);
    }

    Instruction createInvokeCompleteRepOKInstruction(ThreadInfo ti, Instruction current, Instruction next) {

        StaticRepOKCallInstruction repOKCallInstruction = SymHeapHelper
                .createStaticRepOKCallInstruction("runRepOKComplete()V");

        RepOKCompleteCallCG rcg = new RepOKCompleteCallCG("runRepOKComplete");
        repOKCallInstruction.initialize(current, next, rcg);
        SymHeapHelper.pushArguments(ti, null, null);
        return repOKCallInstruction;
    }

}
