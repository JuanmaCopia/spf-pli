package pli.bytecode.lazy;

import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.ThreadInfo;

public class ALOAD extends gov.nasa.jpf.jvm.bytecode.ALOAD {

    public ALOAD(int localVarIndex) {
        super(localVarIndex);
    }

    @Override
    public Instruction execute(ThreadInfo ti) {
        return super.execute(ti);
    }

}