package pli.heap.solving.solver;

import java.util.HashMap;

import korat.finitization.impl.CVElem;
import korat.finitization.impl.Finitization;
import pli.LISSAShell;
import pli.config.ConfigParser;
import symsolve.SymSolve;
import symsolve.config.SolverConfig;
import symsolve.vector.SymSolveSolution;
import symsolve.vector.SymSolveVector;

public class SymSolveHeapSolver {

    SymSolve solver;
    Finitization finitization;
    long solvingTime = 0;

    public SymSolveHeapSolver() {
        solver = createSymSolveInstance();
        finitization = solver.getFinitization();
        assert (finitization != null);
    }

    private SymSolve createSymSolveInstance() {
        ConfigParser conf = LISSAShell.configParser;
        SolverConfig symSolveConfig = new SolverConfig(conf.symSolveClassName, conf.finitizationArgs,
                conf.symmetryBreakingStrategy, conf.predicateName);
        SymSolve solver = new SymSolve(symSolveConfig);
        return solver;
    }

    public boolean isSatisfiable(SymSolveVector vector) {
        long time = System.currentTimeMillis();
        boolean result = solver.isSatisfiable(vector);
        solvingTime += (System.currentTimeMillis() - time);
        return result;
    }

    public boolean isSatisfiableAutoHybridRepOK(SymSolveVector vector) {
        long time = System.currentTimeMillis();
        boolean result = solver.isSatAutoHybridRepOK(vector);
        solvingTime += (System.currentTimeMillis() - time);
        return result;
    }

    public SymSolveSolution solve(SymSolveVector vector) {
        long time = System.currentTimeMillis();
        SymSolveSolution result = solver.solve(vector);
        solvingTime += (System.currentTimeMillis() - time);
        return result;
    }

    public SymSolveSolution getNextSolution(SymSolveSolution previousSolution) {
        long time = System.currentTimeMillis();
        SymSolveSolution result = solver.getNextSolution(previousSolution);
        solvingTime += (System.currentTimeMillis() - time);
        return result;
    }

    public long getSolvingTime() {
        return solvingTime;
    }

    public Finitization getFinitization() {
        return finitization;
    }

    public CVElem[] getStructureList() {
        return finitization.getStateSpace().getStructureList();
    }

    public HashMap<String, Integer> getDataBounds() {
        return finitization.getDataBounds();
    }

}
