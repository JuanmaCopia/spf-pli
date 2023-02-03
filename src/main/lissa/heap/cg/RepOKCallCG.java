package lissa.heap.cg;

import gov.nasa.jpf.symbc.numeric.PathCondition;
import gov.nasa.jpf.vm.ChoiceGeneratorBase;
import lissa.LISSAShell;
import lissa.heap.SymbolicInputHeapLISSA;
import lissa.heap.solving.techniques.PCCheckStrategy;
import symsolve.vector.SymSolveSolution;

public class RepOKCallCG extends ChoiceGeneratorBase<Integer> {

    public boolean result = false;
    public int repOKExecutions = 0;

    SymSolveSolution candidateHeapSolution;
    PathCondition repOKPathCondition;

    SymbolicInputHeapLISSA symInputHeap;
    PCCheckStrategy strategy;

    public RepOKCallCG(String id, SymbolicInputHeapLISSA symInputHeap) {
        super(id);
        result = false;
        repOKExecutions = 0;
        candidateHeapSolution = symInputHeap.getHeapSolution();
        strategy = (PCCheckStrategy) LISSAShell.solvingStrategy;
        this.symInputHeap = symInputHeap;
    }

    public boolean hasNextSolution() {
        assert (candidateHeapSolution != null);
        if (repOKExecutions > 0) {
            candidateHeapSolution = strategy.getNextSolution(ti, candidateHeapSolution, symInputHeap);
            if (candidateHeapSolution == null) {
                strategy.countPrunedBranch();
                setDone();
                return false;
            }
        }

        repOKExecutions++;
        strategy.startRepOKExecutionMode();
        return true;
    }

    public boolean repOKReturnedTrue() {
        strategy.stopRepOKExecutionMode();
        if (result) {
            symInputHeap.setRepOKPC(repOKPathCondition);
            symInputHeap.setHeapSolution(candidateHeapSolution);
            setDone();
        }

        return result;
    }

    public void setCandidateHeapSolution(SymSolveSolution solution) {
        candidateHeapSolution = solution;
    }

    public SymSolveSolution getCandidateHeapSolution() {
        return candidateHeapSolution;
    }

    @Override
    public Integer getNextChoice() {
        return 1;
    }

    @Override
    public boolean hasMoreChoices() {
        return !isDone;
    }

    @Override
    public void advance() {
    }

    @Override
    public void reset() {
        repOKExecutions = 0;
    }

    @Override
    public int getTotalNumberOfChoices() {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getProcessedNumberOfChoices() {
        return repOKExecutions;
    }

    @Override
    public Class<Integer> getChoiceType() {
        return Integer.class;
    }

    public PathCondition getRepOKPathCondition() {
        return repOKPathCondition;
    }

    public void setRepOKPathCondition(PathCondition pc) {
        repOKPathCondition = pc;
    }

}
