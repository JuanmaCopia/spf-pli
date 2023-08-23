package pli.bytecode.lazy;

import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.ThreadInfo;

public class GETSTATIC extends gov.nasa.jpf.jvm.bytecode.GETSTATIC {
    public GETSTATIC(String fieldName, String clsName, String fieldDescriptor) {
        super(fieldName, clsName, fieldDescriptor);
    }

    @Override
    public Instruction execute(ThreadInfo ti) {
        return super.execute(ti);
    }

}
