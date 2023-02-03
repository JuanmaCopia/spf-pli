package lissa.heap.cg;

import gov.nasa.jpf.symbc.numeric.PathCondition;
import gov.nasa.jpf.vm.ChoiceGeneratorBase;
import symsolve.vector.SymSolveSolution;

public class RepOKCallCG extends ChoiceGeneratorBase<Integer> {

    public boolean result = false;
    public int repOKExecutions = 0;
    public int pccount = 0;

    SymSolveSolution candidateHeapSolution;
    PathCondition repOKPathCondition;

    public RepOKCallCG(String id) {
        super(id);
        result = false;
        repOKExecutions = 0;
        pccount = 0;
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
