package lissa.heap.solving.techniques;

import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.MJIEnv;
import gov.nasa.jpf.vm.ThreadInfo;
import lissa.choicegenerators.HeapChoiceGeneratorLISSA;
import lissa.heap.SymHeapHelper;
import lissa.heap.SymbolicInputHeapLISSA;

public abstract class LIBasedStrategy extends SolvingStrategy {

    public boolean pathCheckEnabled = false;
    public int validPaths = 0;
    private boolean pathCheckingMode;

    public abstract long getSolvingTime();

    public abstract boolean isClassInBounds(String fieldSimpleClassName);

    public abstract Integer getBoundForClass(String fieldSimpleClassName);

    public Instruction handleLazyInitializationStep(ThreadInfo ti, Instruction currentInstruction,
            Instruction nextInstruction, HeapChoiceGeneratorLISSA heapCG) {
        return nextInstruction;
    }

    public void buildPartialHeap(MJIEnv env) {
        SymbolicInputHeapLISSA symInputHeap = SymHeapHelper.getSymbolicInputHeap(env.getVM());
        assert (symInputHeap != null);
        symInputHeap.getImplicitInputThis().buildPartialHeap(env);
    }

    public void countValidPath() {
        validPaths++;
    }

    public boolean isPathCheckingMode() {
        return pathCheckingMode;
    }

    public void startPathCheckingMode() {
        pathCheckEnabled = true;
        pathCheckingMode = true;
    }

    public void stopPathCheckingMode() {
        pathCheckingMode = false;
    }

}
