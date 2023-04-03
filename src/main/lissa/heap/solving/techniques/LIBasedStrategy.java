package lissa.heap.solving.techniques;

import java.util.HashMap;

import gov.nasa.jpf.vm.Instruction;
import gov.nasa.jpf.vm.ThreadInfo;
import korat.finitization.impl.Finitization;
import korat.finitization.impl.StateSpace;
import lissa.bytecode.lazy.StaticRepOKCallInstruction;
import lissa.choicegenerators.HeapChoiceGeneratorLISSA;
import lissa.choicegenerators.RepOKCompleteCallCG;
import lissa.heap.SymHeapHelper;
import lissa.heap.canonicalizer.Canonicalizer;
import lissa.heap.solving.solver.SymSolveHeapSolver;

public abstract class LIBasedStrategy extends SolvingStrategy {

    SymSolveHeapSolver heapSolver;
    Finitization finitization;
    StateSpace stateSpace;
    Canonicalizer canonicalizer;

    public int validPaths = 0;

    public int lazyChoices = 0;
    public int primitiveBranchChoices = 0;
    public int primitiveBranchCacheHits = 0;

    boolean executingRepOK = false;
    long repokExecTime = 0;
    long repOKStartTime = 0;

    public LIBasedStrategy() {
        heapSolver = new SymSolveHeapSolver();
        finitization = heapSolver.getFinitization();
        stateSpace = finitization.getStateSpace();
        canonicalizer = new Canonicalizer(heapSolver.getStructureList());
    }

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

    public void countValidPath() {
        validPaths++;
    }

    public boolean isRepOKExecutionMode() {
        return executingRepOK;
    }

    public void startRepOKExecutionMode() {
        if (!executingRepOK) {
            executingRepOK = true;
            repOKStartTime = System.currentTimeMillis();
        }
    }

    public void stopRepOKExecutionMode() {
        if (executingRepOK) {
            executingRepOK = false;
            repokExecTime += System.currentTimeMillis() - repOKStartTime;
        }
    }

    public long getRepOKSolvingTime() {
        return repokExecTime;
    }

    @Override
    void checkPathValidity(ThreadInfo ti, Instruction current, Instruction next) {
        StaticRepOKCallInstruction repOKCallInstruction = SymHeapHelper
                .createStaticRepOKCallInstruction("runRepOKComplete()V");

        RepOKCompleteCallCG rcg = new RepOKCompleteCallCG("checkPathValidity");
        rcg.markAsPathValidityCheck();
        repOKCallInstruction.initialize(current, next, rcg);
        SymHeapHelper.pushArguments(ti, null, null);
        ti.setNextPC(repOKCallInstruction);
    }

    public boolean isFieldTracked(String ownerClassName, String fieldName) {
        return finitization.isFieldTracked(ownerClassName, fieldName);
    }

}
