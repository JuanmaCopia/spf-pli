package pli.heap.solving.techniques;

import gov.nasa.jpf.vm.ClassInfo;
import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.MethodInfo;
import gov.nasa.jpf.vm.ThreadInfo;
import pli.bytecode.lazy.X2ProcedureCallInstruction;
import pli.choicegenerators.PCChoiceGeneratorLISSA;
import pli.choicegenerators.PLIChoiceGenerator;
import pli.choicegenerators.XCG;
import pli.heap.SymHeapHelper;
import pli.heap.SymbolicInputHeapLISSA;

public class X2 extends X {

    @Override
    public Instruction handlePrimitiveBranch(ThreadInfo ti, Instruction currentInstruction, Instruction nextInstruction,
            PCChoiceGeneratorLISSA currentCG) {
        assert (!isRepOKExecutionMode());

        SymbolicInputHeapLISSA symInputHeap = (SymbolicInputHeapLISSA) SymHeapHelper
                .getCurrentHeapChoiceGenerator(ti.getVM()).getCurrentSymInputHeap();

        if (isCacheHit(currentCG, symInputHeap))
            return nextInstruction;

        return createX2prePInstruction(ti, currentInstruction, nextInstruction, symInputHeap, currentCG);
    }

    Instruction createX2prePInstruction(ThreadInfo ti, Instruction current, Instruction next,
            SymbolicInputHeapLISSA symInputHeap, PLIChoiceGenerator curCG) {
        ClassInfo rootClassInfo = symInputHeap.getImplicitInputThis().getRootHeapNode().getType();
        MethodInfo staticMethod = rootClassInfo.getMethod("runPrePPartialHeap()V", false);
        SymHeapHelper.pushArguments(ti, null, null);
        XCG cg = new XCG("XCG", curCG, null);
        return new X2ProcedureCallInstruction(staticMethod, current, next, cg);
    }

}
