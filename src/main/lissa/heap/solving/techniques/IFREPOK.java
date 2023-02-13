package lissa.heap.solving.techniques;

import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.ThreadInfo;
import lissa.choicegenerators.HeapChoiceGeneratorLISSA;

public class IFREPOK extends LISSA {

    @Override
    public Instruction handleLazyInitializationStep(ThreadInfo ti, Instruction currentInstruction,
            Instruction nextInstruction, HeapChoiceGeneratorLISSA heapCG) {
        return nextInstruction;
    }

}
