
package lissa;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.symbc.SymbolicInstructionFactory;
import gov.nasa.jpf.vm.Instruction;
import lissa.bytecode.lazy.ALOAD;
import lissa.bytecode.lazy.GETFIELDHeapSolving;
import lissa.bytecode.lazy.GETSTATIC;
import lissa.config.SolvingStrategyEnum;

public class HeapSolvingInstructionFactory extends SymbolicInstructionFactory {

    // === Fully Overridden Instructions of jpf-symbc (depentent of jpf-core) ===

    @Override
    public Instruction getfield(String fieldName, String clsName, String fieldDescriptor) {
        if (LISSAShell.configParser.solvingStrategy == SolvingStrategyEnum.DRIVER)
            return super.getfield(fieldName, clsName, fieldDescriptor);
        return new GETFIELDHeapSolving(fieldName, clsName, fieldDescriptor);
    }

    @Override
    public Instruction getstatic(String fieldName, String clsName, String fieldDescriptor) {
        return new GETSTATIC(fieldName, clsName, fieldDescriptor);
    }

    @Override
    public Instruction aload(int localVarIndex) {
        return new ALOAD(localVarIndex);
    }

    @Override
    public Instruction aload_0() {
        return new ALOAD(0);
    }

    @Override
    public Instruction aload_1() {
        return new ALOAD(1);
    }

    @Override
    public Instruction aload_2() {
        return new ALOAD(2);
    }

    @Override
    public Instruction aload_3() {
        return new ALOAD(3);
    }

    // ============ Constructor ============ //

    public HeapSolvingInstructionFactory(Config conf) {
        super(conf);
    }

}
