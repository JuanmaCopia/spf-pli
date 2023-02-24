package lissa.heap.solving.techniques;

import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.ThreadInfo;
import lissa.choicegenerators.HeapChoiceGeneratorLISSA;

public class PLAINLAZY extends LIHYBRID {

    public PLAINLAZY(int getFieldLimit) {
        super(getFieldLimit);
    }

    @Override
    public Instruction handleLazyInitializationStep(ThreadInfo ti, Instruction currentInstruction,
            Instruction nextInstruction, HeapChoiceGeneratorLISSA heapCG) {
        fieldGetCount.clear();
        return nextInstruction;
    }

}
