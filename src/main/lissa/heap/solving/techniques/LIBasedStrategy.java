package lissa.heap.solving.techniques;

import java.util.HashMap;

import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.MJIEnv;
import gov.nasa.jpf.vm.ThreadInfo;
import lissa.choicegenerators.HeapChoiceGeneratorLISSA;
import lissa.heap.SymHeapHelper;
import lissa.heap.SymbolicInputHeapLISSA;
import lissa.heap.canonicalizer.Canonicalizer;
import lissa.heap.solving.solver.SymSolveHeapSolver;

public abstract class LIBasedStrategy extends SolvingStrategy {

    SymSolveHeapSolver heapSolver = new SymSolveHeapSolver();;
    Canonicalizer canonicalizer = new Canonicalizer(heapSolver.getStructureList());

    public boolean pathCheckEnabled = false;
    public int validPaths = 0;
    boolean pathCheckingMode;

    public Integer getBoundForClass(String simpleClassName) {
        HashMap<String, Integer> dataBounds = heapSolver.getDataBounds();
        return dataBounds.get(simpleClassName);
    }

    public boolean isClassInBounds(String simpleClassName) {
        return getBoundForClass(simpleClassName) != null;
    }

    public long getSolvingTime() {
        return heapSolver.getSolvingTime();
    }

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
