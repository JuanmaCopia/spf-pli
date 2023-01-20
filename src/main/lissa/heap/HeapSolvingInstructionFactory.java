
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

    public static boolean isRepOKRun = false;

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

    // ===== Instructions dependent of jpf-symbc instructions =====

//      public Instruction ifle(int targetPc) {
//        return (filter.isPassing(ci) ? (pcChoiceOptimization) ? new gov.nasa.jpf.symbc.bytecode.optimization.IFLE(targetPc) : new IFLE(targetPc) : super.ifle(targetPc));
//      }
//
//
//      public Instruction iflt(int targetPc) {
//        return (filter.isPassing(ci) ? (pcChoiceOptimization) ? new gov.nasa.jpf.symbc.bytecode.optimization.IFLT(targetPc) : new IFLT(targetPc) : super.iflt(targetPc));
//      }
//
//
//      public Instruction ifge(int targetPc) {
//        return (filter.isPassing(ci) ? (pcChoiceOptimization) ? new gov.nasa.jpf.symbc.bytecode.optimization.IFGE(targetPc) : new IFGE(targetPc): super.ifge(targetPc));
//      }
//
//
//      public Instruction ifgt(int targetPc) {
//        return (filter.isPassing(ci) ? (pcChoiceOptimization) ? new gov.nasa.jpf.symbc.bytecode.optimization.IFGT(targetPc) : new IFGT(targetPc): super.ifgt(targetPc));
//      }
//
//
//      public Instruction ifeq(int targetPc) {
//        return (filter.isPassing(ci) ? (pcChoiceOptimization) ? new gov.nasa.jpf.symbc.bytecode.optimization.IFEQ(targetPc) : new IFEQ(targetPc): super.ifeq(targetPc));
//      }
//
//
//      public Instruction ifne(int targetPc) {
//        return (filter.isPassing(ci) ? (pcChoiceOptimization) ? new gov.nasa.jpf.symbc.bytecode.optimization.IFNE(targetPc) : new IFNE(targetPc): super.ifne(targetPc));
//      }
//
//      public Instruction if_icmpge(int targetPc) {
//            return (filter.isPassing(ci) ? (pcChoiceOptimization) ? new gov.nasa.jpf.symbc.bytecode.optimization.IF_ICMPGE(targetPc) : new IF_ICMPGE(targetPc): super.if_icmpge(targetPc));
//      }
//
//
//      public Instruction if_icmpgt(int targetPc) {
//            return (filter.isPassing(ci) ? (pcChoiceOptimization) ? new gov.nasa.jpf.symbc.bytecode.optimization.IF_ICMPGT(targetPc) : new IF_ICMPGT(targetPc): super.if_icmpgt(targetPc));
//      }
//
//
//      public Instruction if_icmple(int targetPc) {
//            return (filter.isPassing(ci) ? (pcChoiceOptimization) ? new gov.nasa.jpf.symbc.bytecode.optimization.IF_ICMPLE(targetPc) : new IF_ICMPLE(targetPc): super.if_icmple(targetPc));
//      }
//
//
//      public Instruction if_icmplt(int targetPc) {
//            return (filter.isPassing(ci) ? (pcChoiceOptimization) ? new gov.nasa.jpf.symbc.bytecode.optimization.IF_ICMPLT(targetPc) : new IF_ICMPLT(targetPc): super.if_icmplt(targetPc));
//      }
//
//
//      public Instruction idiv() {
//        return (filter.isPassing(ci) ? new IDIV(): super.idiv());
//      }

//      public Instruction irem() {
//        return (filter.isPassing(ci) ? new IREM(): super.irem());
//      }
//
//
//      public Instruction if_icmpeq(int targetPc) {
//            return (filter.isPassing(ci) ? (pcChoiceOptimization) ? new gov.nasa.jpf.symbc.bytecode.optimization.IF_ICMPEQ(targetPc) : new IF_ICMPEQ(targetPc): super.if_icmpeq(targetPc));
//      }
//
//
//      public Instruction if_icmpne(int targetPc) {
//            return (filter.isPassing(ci) ? (pcChoiceOptimization) ? new gov.nasa.jpf.symbc.bytecode.optimization.IF_ICMPNE(targetPc) : new IF_ICMPNE(targetPc): super.if_icmpne(targetPc));
//      }

//      public Instruction fdiv() {
//        return (filter.isPassing(ci) ? new FDIV(): super.fdiv());
//      }

//
//      public Instruction fcmpg() {
//        return (filter.isPassing(ci) ? (pcChoiceOptimization) ? new gov.nasa.jpf.symbc.bytecode.optimization.FCMPG() : new FCMPG(): super.fcmpg());
//      }
//
//
//      public Instruction fcmpl() {
//            return (filter.isPassing(ci) ? (pcChoiceOptimization) ? new gov.nasa.jpf.symbc.bytecode.optimization.FCMPL() : new FCMPL(): super.fcmpl());
//          }

//
//
//      public Instruction dcmpg() {
//            return (filter.isPassing(ci) ? (pcChoiceOptimization) ? new gov.nasa.jpf.symbc.bytecode.optimization.DCMPG() : new DCMPG(): super.dcmpg());
//          }
//
//
//      public Instruction dcmpl() {
//            return (filter.isPassing(ci) ? (pcChoiceOptimization) ? new gov.nasa.jpf.symbc.bytecode.optimization.DCMPL() : new DCMPL(): super.dcmpl());
//          }

//      public Instruction ddiv() {
//            return (filter.isPassing(ci) ? new DDIV(): super.ddiv());
//          }

//
//
//      public Instruction lcmp() {
//            return (filter.isPassing(ci) ? new LCMP(): super.lcmp());
//          }

//      public Instruction ldiv() {
//            return (filter.isPassing(ci) ? new LDIV(): super.ldiv());
//          }

//
//      public Instruction lrem() {
//          return (filter.isPassing(ci) ? new LREM(): super.lrem());
//          }

//    public Instruction i2d() {
//        return (filter.isPassing(ci) ? new I2D() : super.i2d());
//    }

//    public Instruction d2i() {
//        return (filter.isPassing(ci) ? new D2I() : super.d2i());
//    }

//    public Instruction d2l() {
//        return (filter.isPassing(ci) ? new D2L() : super.d2l());
//    }

//    public Instruction i2f() {
//        return (filter.isPassing(ci) ? new I2F() : super.i2f());
//    }

//    public Instruction l2d() {
//        return (filter.isPassing(ci) ? new L2D() : super.l2d());
//    }

//    public Instruction l2f() {
//        return (filter.isPassing(ci) ? new L2F() : super.l2f());
//    }

//    public Instruction f2l() {
//        return (filter.isPassing(ci) ? new F2L() : super.f2l());
//    }

//    public Instruction f2i() {
//        return (filter.isPassing(ci) ? new F2I() : super.f2i());
//    }

//    public Instruction tableswitch(int defaultTargetPc, int low, int high) {
//        return (filter.isPassing(ci) ? new TABLESWITCH(defaultTargetPc, low, high)
//                : super.tableswitch(defaultTargetPc, low, high));
//    }

    // array ops
//      public Instruction arraylength() {
//          return (symArrays ? new gov.nasa.jpf.symbc.bytecode.symarrays.ARRAYLENGTH() : super.arraylength());
//      }
//
//      public Instruction aaload() {
//            return (filter.isPassing(ci) ? (symArrays) ? new gov.nasa.jpf.symbc.bytecode.symarrays.AALOAD() : new AALOAD(): super.aaload());
//          }
//
//      public Instruction aastore() {
//            return (filter.isPassing(ci) ? (symArrays) ? new gov.nasa.jpf.symbc.bytecode.symarrays.AASTORE() : new AASTORE(): super.aastore());
//          }
//          
//      public Instruction baload() {
//            return (filter.isPassing(ci) ? (symArrays) ? new gov.nasa.jpf.symbc.bytecode.symarrays.BALOAD() : new BALOAD(): super.baload());
//          }
//
//      public Instruction bastore() {
//            return (filter.isPassing(ci) ? (symArrays) ? new gov.nasa.jpf.symbc.bytecode.symarrays.BASTORE() : new BASTORE(): super.bastore());
//          }
//      
//      public Instruction caload() {
//            return (filter.isPassing(ci) ? (symArrays) ? new gov.nasa.jpf.symbc.bytecode.symarrays.CALOAD() : new CALOAD(): super.caload());
//          }
//
//      public Instruction castore() {
//            return (filter.isPassing(ci) ? (symArrays) ? new gov.nasa.jpf.symbc.bytecode.symarrays.CASTORE() : new CASTORE(): super.castore());
//          }
//      
//      public Instruction daload() {
//            return (filter.isPassing(ci) ? (symArrays) ? new gov.nasa.jpf.symbc.bytecode.symarrays.DALOAD() : new DALOAD(): super.daload());
//          }
//
//      public Instruction dastore() {
//            return (filter.isPassing(ci) ? (symArrays) ? new gov.nasa.jpf.symbc.bytecode.symarrays.DASTORE() : new DASTORE(): super.dastore());
//          }
//      
//      public Instruction faload() {
//            return (filter.isPassing(ci) ? (symArrays) ? new gov.nasa.jpf.symbc.bytecode.symarrays.FALOAD() : new FALOAD(): super.faload());
//          }
//
//      public Instruction fastore() {
//            return (filter.isPassing(ci) ? (symArrays) ? new gov.nasa.jpf.symbc.bytecode.symarrays.FASTORE() : new FASTORE(): super.fastore());
//          }
//      
//      public Instruction iaload() {
//            return (filter.isPassing(ci) ? (symArrays) ? new gov.nasa.jpf.symbc.bytecode.symarrays.IALOAD() : new IALOAD(): super.iaload());
//          }
//
//      public Instruction iastore() {
//            return (filter.isPassing(ci) ? (symArrays) ? new gov.nasa.jpf.symbc.bytecode.symarrays.IASTORE() : new IASTORE(): super.iastore());
//          }
//      
//      public Instruction laload() {
//            return (filter.isPassing(ci) ? (symArrays) ? new gov.nasa.jpf.symbc.bytecode.symarrays.LALOAD() : new LALOAD(): super.laload());
//          }
//
//      public Instruction lastore() {
//            return (filter.isPassing(ci) ? (symArrays) ? new gov.nasa.jpf.symbc.bytecode.symarrays.LASTORE() : new LASTORE(): super.lastore());
//          }
//      
//      public Instruction saload() {
//            return (filter.isPassing(ci) ? (symArrays) ? new gov.nasa.jpf.symbc.bytecode.symarrays.SALOAD() : new SALOAD(): super.saload());
//      }
//
//      public Instruction sastore() {
//            return (filter.isPassing(ci) ? (symArrays) ? new gov.nasa.jpf.symbc.bytecode.symarrays.SASTORE() : new SASTORE(): super.sastore());
//      }

//    public Instruction newarray(int typeCode) {
//        return (filter.isPassing(ci)
//                ? (symArrays) ? new gov.nasa.jpf.symbc.bytecode.symarrays.NEWARRAY(typeCode) : new NEWARRAY(typeCode)
//                : super.newarray(typeCode));
//    }

//    public Instruction anewarray(String typeDescriptor) {
//        return (filter.isPassing(ci) && (symArrays)
//                ? new gov.nasa.jpf.symbc.bytecode.symarrays.ANEWARRAY(typeDescriptor)
//                : super.anewarray(typeDescriptor));
//    }

    // ============ Constructor ============ //

    public HeapSolvingInstructionFactory(Config conf) {
        super(conf);
    }

}
