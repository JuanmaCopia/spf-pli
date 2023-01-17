package lissa.bytecode;

import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.ThreadInfo;

public class ALOAD extends gov.nasa.jpf.jvm.bytecode.ALOAD {

    public ALOAD(int localVarIndex) {
        super(localVarIndex);
    }

    @Override
    public Instruction execute(ThreadInfo ti) {
        Instruction returnedIns = super.execute(ti);

//        SolvingStrategy solvingStrategy = LISSAShell.solvingStrategy;
//        if (solvingStrategy instanceof LIBasedStrategy) {
//            LIBasedStrategy heapSolvingStrategy = (LIBasedStrategy) solvingStrategy;
//            return heapSolvingStrategy.getNextInstructionToPrimitiveBranching(ti, this);
//        }
        return returnedIns;
    }

}