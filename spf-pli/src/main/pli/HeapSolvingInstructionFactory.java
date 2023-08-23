
package pli;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.symbc.SymbolicInstructionFactory;
import gov.nasa.jpf.vm.Instruction;
import pli.LISSAShell;
import pli.bytecode.AALOAD;
import pli.bytecode.AASTORE;
import pli.bytecode.BALOAD;
import pli.bytecode.BASTORE;
import pli.bytecode.CALOAD;
import pli.bytecode.CASTORE;
import pli.bytecode.D2I;
import pli.bytecode.D2L;
import pli.bytecode.DALOAD;
import pli.bytecode.DASTORE;
import pli.bytecode.DCMPG;
import pli.bytecode.DCMPL;
import pli.bytecode.DDIV;
import pli.bytecode.F2I;
import pli.bytecode.F2L;
import pli.bytecode.FALOAD;
import pli.bytecode.FASTORE;
import pli.bytecode.FCMPG;
import pli.bytecode.FCMPL;
import pli.bytecode.FDIV;
import pli.bytecode.I2D;
import pli.bytecode.I2F;
import pli.bytecode.IALOAD;
import pli.bytecode.IASTORE;
import pli.bytecode.IDIV;
import pli.bytecode.IFEQ;
import pli.bytecode.IFGE;
import pli.bytecode.IFGT;
import pli.bytecode.IFLE;
import pli.bytecode.IFLT;
import pli.bytecode.IFNE;
import pli.bytecode.IF_ICMPEQ;
import pli.bytecode.IF_ICMPGE;
import pli.bytecode.IF_ICMPGT;
import pli.bytecode.IF_ICMPLE;
import pli.bytecode.IF_ICMPLT;
import pli.bytecode.IF_ICMPNE;
import pli.bytecode.IREM;
import pli.bytecode.L2D;
import pli.bytecode.L2F;
import pli.bytecode.LALOAD;
import pli.bytecode.LASTORE;
import pli.bytecode.LCMP;
import pli.bytecode.LDIV;
import pli.bytecode.LREM;
import pli.bytecode.SALOAD;
import pli.bytecode.SASTORE;
import pli.bytecode.TABLESWITCH;
import pli.bytecode.lazy.ALOAD;
import pli.bytecode.lazy.GETFIELDHeapSolving;
import pli.bytecode.lazy.GETSTATIC;
import pli.config.SolvingStrategyEnum;

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

    // others

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

    // array ops

    public Instruction aaload() {
        return new AALOAD();
    }

    public Instruction aastore() {
        return new AASTORE();
    }

    public Instruction baload() {
        return new BALOAD();
    }

    public Instruction bastore() {
        return new BASTORE();
    }

    public Instruction caload() {
        return new CALOAD();
    }

    public Instruction castore() {
        return new CASTORE();
    }

    public Instruction daload() {
        return new DALOAD();
    }

    public Instruction dastore() {
        return new DASTORE();
    }

    public Instruction faload() {
        return new FALOAD();
    }

    public Instruction fastore() {
        return new FASTORE();
    }

    public Instruction iaload() {
        return new IALOAD();
    }

    public Instruction iastore() {
        return new IASTORE();
    }

    public Instruction laload() {
        return new LALOAD();
    }

    public Instruction lastore() {
        return new LASTORE();
    }

    public Instruction saload() {
        return new SALOAD();
    }

    public Instruction sastore() {
        return new SASTORE();
    }

    // TODO: to review
    // From Fujitsu:

//    public Instruction new_(String clsName) {
//        return (filter.isPassing(ci) ? new NEW(clsName) : super.new_(clsName));
//    }
//
//    public Instruction ifnonnull(int targetPc) {
//        return (filter.isPassing(ci) ? new IFNONNULL(targetPc) : super.ifnonnull(targetPc));
//    }
//
//    public Instruction ifnull(int targetPc) {
//        return (filter.isPassing(ci) ? new IFNULL(targetPc) : super.ifnull(targetPc));
//    }
//
//    public Instruction newarray(int typeCode) {
//        return (filter.isPassing(ci)
//                ? (symArrays) ? new gov.nasa.jpf.symbc.bytecode.symarrays.NEWARRAY(typeCode) : new NEWARRAY(typeCode)
//                : super.newarray(typeCode));
//    }
//
//    public Instruction anewarray(String typeDescriptor) {
//        return (filter.isPassing(ci) && (symArrays)
//                ? new gov.nasa.jpf.symbc.bytecode.symarrays.ANEWARRAY(typeDescriptor)
//                : super.anewarray(typeDescriptor));
//    }
//
//    public Instruction multianewarray(String clsName, int dimensions) {
//        return (filter.isPassing(ci) ? new MULTIANEWARRAY(clsName, dimensions)
//                : super.multianewarray(clsName, dimensions));
//    }

    // ============ Constructor ============ //

    public HeapSolvingInstructionFactory(Config conf) {
        super(conf);
    }

}
