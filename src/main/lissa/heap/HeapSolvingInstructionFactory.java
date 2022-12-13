
package lissa.heap;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.symbc.SymbolicInstructionFactory;
import gov.nasa.jpf.vm.Instruction;
import lissa.LISSAShell;
import lissa.bytecode.ALOAD;
import lissa.bytecode.GETFIELDHeapSolving;
import lissa.bytecode.GETSTATIC;
import lissa.config.SolvingStrategyEnum;

public class HeapSolvingInstructionFactory extends SymbolicInstructionFactory {

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

    public HeapSolvingInstructionFactory(Config conf) {
        super(conf);
    }

}
