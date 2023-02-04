
package lissa;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.symbc.SymbolicInstructionFactory;
import gov.nasa.jpf.vm.Instruction;
import lissa.bytecode.D2I;
import lissa.bytecode.D2L;
import lissa.bytecode.DCMPG;
import lissa.bytecode.DCMPL;
import lissa.bytecode.DDIV;
import lissa.bytecode.F2I;
import lissa.bytecode.F2L;
import lissa.bytecode.FCMPG;
import lissa.bytecode.FCMPL;
import lissa.bytecode.FDIV;
import lissa.bytecode.I2D;
import lissa.bytecode.I2F;
import lissa.bytecode.IDIV;
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
import lissa.bytecode.IREM;
import lissa.bytecode.L2D;
import lissa.bytecode.L2F;
import lissa.bytecode.LCMP;
import lissa.bytecode.LDIV;
import lissa.bytecode.LREM;
import lissa.bytecode.TABLESWITCH;
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

    // PC optimization:

    @Override
    public Instruction ifle(int targetPc) {
        return new IFLE(targetPc);
    }

    @Override
    public Instruction iflt(int targetPc) {
        return new IFLT(targetPc);
    }

    @Override
    public Instruction ifge(int targetPc) {
        return new IFGE(targetPc);
    }

    @Override
    public Instruction ifgt(int targetPc) {
        return new IFGT(targetPc);
    }

    @Override
    public Instruction ifeq(int targetPc) {
        return new IFEQ(targetPc);
    }

    @Override
    public Instruction ifne(int targetPc) {
        return new IFNE(targetPc);
    }

    @Override
    public Instruction if_icmpge(int targetPc) {
        return new IF_ICMPGE(targetPc);
    }

    @Override
    public Instruction if_icmpgt(int targetPc) {
        return new IF_ICMPGT(targetPc);
    }

    @Override
    public Instruction if_icmple(int targetPc) {
        return new IF_ICMPLE(targetPc);
    }

    @Override
    public Instruction if_icmplt(int targetPc) {
        return new IF_ICMPLT(targetPc);
    }

    @Override
    public Instruction if_icmpeq(int targetPc) {
        return new IF_ICMPEQ(targetPc);
    }

    @Override
    public Instruction if_icmpne(int targetPc) {
        return new IF_ICMPNE(targetPc);
    }

    @Override
    public Instruction fcmpg() {
        return new FCMPG();
    }

    @Override
    public Instruction fcmpl() {
        return new FCMPL();
    }

    @Override
    public Instruction dcmpg() {
        return new DCMPG();
    }

    @Override
    public Instruction dcmpl() {
        return new DCMPL();
    }

    @Override
    public Instruction ddiv() {
        return new DDIV();
    }

    @Override
    public Instruction idiv() {
        return new IDIV();
    }

    @Override
    public Instruction irem() {
        return new IREM();
    }

    @Override
    public Instruction fdiv() {
        return new FDIV();
    }

    @Override
    public Instruction lcmp() {
        return new LCMP();
    }

    @Override
    public Instruction ldiv() {
        return new LDIV();
    }

    @Override
    public Instruction lrem() {
        return new LREM();
    }

    @Override
    public Instruction i2d() {
        return new I2D();
    }

    @Override
    public Instruction d2i() {
        return new D2I();
    }

    // ============ Constructor ============ //

    public HeapSolvingInstructionFactory(Config conf) {
        super(conf);
    }

    // others

    @Override
    public Instruction d2l() {
        return new D2L();
    }

    @Override
    public Instruction i2f() {
        return new I2F();
    }

    @Override
    public Instruction l2d() {
        return new L2D();
    }

    @Override
    public Instruction l2f() {
        return new L2F();
    }

    @Override
    public Instruction f2l() {
        return new F2L();
    }

    @Override
    public Instruction f2i() {
        return new F2I();
    }

    @Override
    public Instruction tableswitch(int defaultTargetPc, int low, int high) {
        return new TABLESWITCH(defaultTargetPc, low, high);
    }

}
