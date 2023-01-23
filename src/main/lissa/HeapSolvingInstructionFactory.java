
package lissa;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.symbc.SymbolicInstructionFactory;
import gov.nasa.jpf.vm.Instruction;
import lissa.bytecode.DCMPG;
import lissa.bytecode.DCMPL;
import lissa.bytecode.FCMPG;
import lissa.bytecode.FCMPL;
import lissa.bytecode.IFEQ;
import lissa.bytecode.IFGE;
import lissa.bytecode.IFGT;
import lissa.bytecode.IFLE;
import lissa.bytecode.IFLT;
import lissa.bytecode.IFNE;
import lissa.bytecode.IF_ICMPEQ;
import lissa.bytecode.IF_ICMPGE;
import lissa.bytecode.IF_ICMPGT;
import lissa.bytecode.IF_ICMPLE;
import lissa.bytecode.IF_ICMPLT;
import lissa.bytecode.IF_ICMPNE;
import lissa.bytecode.lazy.ALOAD;
import lissa.bytecode.lazy.GETFIELDHeapSolving;
import lissa.bytecode.lazy.GETSTATIC;
import lissa.config.SolvingStrategyEnum;

public class HeapSolvingInstructionFactory extends SymbolicInstructionFactory {

    // === Fully Overridden Instructions of jpf-symbc (depentent of jpf-core) ===

    // --- Lazy initialization Instructions ---

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

    // --- Primitive type Path Condition Branching ---

    public Instruction ifle(int targetPc) {
        return new IFLE(targetPc);
    }

    public Instruction iflt(int targetPc) {
        return new IFLT(targetPc);
    }

    public Instruction ifge(int targetPc) {
        return new IFGE(targetPc);
    }

    public Instruction ifgt(int targetPc) {
        return new IFGT(targetPc);
    }

    public Instruction ifeq(int targetPc) {
        return new IFEQ(targetPc);
    }

    public Instruction ifne(int targetPc) {
        return new IFNE(targetPc);
    }

    public Instruction if_icmpge(int targetPc) {
        return new IF_ICMPGE(targetPc);
    }

    public Instruction if_icmpgt(int targetPc) {
        return new IF_ICMPGT(targetPc);
    }

    public Instruction if_icmple(int targetPc) {
        return new IF_ICMPLE(targetPc);
    }

    public Instruction if_icmplt(int targetPc) {
        return new IF_ICMPLT(targetPc);
    }

    public Instruction if_icmpeq(int targetPc) {
        return new IF_ICMPEQ(targetPc);
    }

    public Instruction if_icmpne(int targetPc) {
        return new IF_ICMPNE(targetPc);
    }

    public Instruction fcmpg() {
        return new FCMPG();
    }

    public Instruction fcmpl() {
        return new FCMPL();
    }

    public Instruction dcmpg() {
        return new DCMPG();
    }

    public Instruction dcmpl() {
        return new DCMPL();
    }

    // ============ Constructor ============ //

    public HeapSolvingInstructionFactory(Config conf) {
        super(conf);
    }

}
