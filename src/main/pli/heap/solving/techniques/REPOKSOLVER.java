package pli.heap.solving.techniques;

import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.ThreadInfo;
import pli.bytecode.lazy.StaticRepOKCallInstruction;
import pli.choicegenerators.HeapChoiceGeneratorLISSA;
import pli.choicegenerators.PCChoiceGeneratorLISSA;
import pli.choicegenerators.PLIChoiceGenerator;
import pli.choicegenerators.PreHAndPrePCallCG;
import pli.heap.SymHeapHelper;
import pli.heap.solving.techniques.PLI;

public class REPOKSOLVER extends PLI {

    @Override
    public Instruction handleLazyInitializationStep(ThreadInfo ti, Instruction currentInstruction,
            Instruction nextInstruction, HeapChoiceGeneratorLISSA heapCG) {
        assert (!isRepOKExecutionMode());
        solverCalls++;
        return createInvokeCompleteRepOKInstruction(ti, currentInstruction, nextInstruction, heapCG);
    }

    @Override
    public Instruction handlePrimitiveBranch(ThreadInfo ti, Instruction currentInstruction, Instruction nextInstruction,
            PCChoiceGeneratorLISSA pcCG) {
        assert (!isRepOKExecutionMode());
        solverCalls++;
        return createInvokeCompleteRepOKInstruction(ti, currentInstruction, nextInstruction, pcCG);
    }

    Instruction createInvokeCompleteRepOKInstruction(ThreadInfo ti, Instruction current, Instruction next,
            PLIChoiceGenerator curCG) {

        StaticRepOKCallInstruction repOKCallInstruction = SymHeapHelper
                .createStaticRepOKCallInstruction("runRepOKComplete()V");

        PreHAndPrePCallCG rcg = new PreHAndPrePCallCG("runRepOKComplete", curCG);
        repOKCallInstruction.initialize(current, next, rcg);
        SymHeapHelper.pushArguments(ti, null, null);
        return repOKCallInstruction;
    }

}
