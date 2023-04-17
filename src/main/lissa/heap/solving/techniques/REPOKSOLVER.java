package lissa.heap.solving.techniques;

import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.ThreadInfo;
import lissa.bytecode.lazy.StaticRepOKCallInstruction;
import lissa.choicegenerators.HeapChoiceGeneratorLISSA;
import lissa.choicegenerators.PCChoiceGeneratorLISSA;
import lissa.choicegenerators.PLIChoiceGenerator;
import lissa.choicegenerators.RepOKCompleteCallCG;
import lissa.heap.SymHeapHelper;

public class REPOKSOLVER extends PLI {

    @Override
    public Instruction handleLazyInitializationStep(ThreadInfo ti, Instruction currentInstruction,
            Instruction nextInstruction, HeapChoiceGeneratorLISSA heapCG) {
        assert (!isRepOKExecutionMode());

        return createInvokeCompleteRepOKInstruction(ti, currentInstruction, nextInstruction, heapCG);
    }

    @Override
    public Instruction handlePrimitiveBranch(ThreadInfo ti, Instruction currentInstruction, Instruction nextInstruction,
            PCChoiceGeneratorLISSA pcCG) {
        assert (!isRepOKExecutionMode());

        return createInvokeCompleteRepOKInstruction(ti, currentInstruction, nextInstruction, pcCG);
    }

    Instruction createInvokeCompleteRepOKInstruction(ThreadInfo ti, Instruction current, Instruction next,
            PLIChoiceGenerator curCG) {

        StaticRepOKCallInstruction repOKCallInstruction = SymHeapHelper
                .createStaticRepOKCallInstruction("runRepOKComplete()V");

        RepOKCompleteCallCG rcg = new RepOKCompleteCallCG("runRepOKComplete", curCG);
        repOKCallInstruction.initialize(current, next, rcg);
        SymHeapHelper.pushArguments(ti, null, null);
        return repOKCallInstruction;
    }

}
