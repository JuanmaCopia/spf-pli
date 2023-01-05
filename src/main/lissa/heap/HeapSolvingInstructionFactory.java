
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

//    public static boolean executingRepOK = false;
//
//    public static Instruction createInvokeVirtualIns(String clsName, String methodName, String methodSignature) {
//        return new INVOKEREPOK(clsName, methodName, methodSignature);
//    }

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

//    @Override
//    public Instruction return_() {
//        return new RETURN();
//    }
//
//    public static INVOKEREPOK2 invokerepok2(String clsName, String methodName, String methodSignature) {
//        return new INVOKEREPOK2(clsName, methodName, methodSignature);
//    }

    public HeapSolvingInstructionFactory(Config conf) {
        super(conf);
    }

}
