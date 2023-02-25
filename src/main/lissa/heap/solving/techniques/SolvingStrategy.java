package lissa.heap.solving.techniques;

import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.VM;
import lissa.config.ConfigParser;
import symsolve.explorers.impl.SymmetryBreakStrategy;

public class SolvingStrategy {

    public int exploredPaths = 0;
    public int exceptionsThrown = 0;
    static ConfigParser config;

    public static SolvingStrategy makeSymbolicHeapSolvingTechnique(ConfigParser config) {
        SolvingStrategy.config = config;
        switch (config.solvingStrategy) {
        case LISSA:
            return new LISSA();
        case LISSAM:
            return new LISSAM();
        case NT:
            return new NT();
        case NTOPT:
            return new NTOPT();
        case LISSANOSB:
            config.symmetryBreakingStrategy = SymmetryBreakStrategy.NO_SYMMETRY_BREAK;
            return new LISSA();
        case LIHYBRID:
            return new LIHYBRID();
        case DRIVER:
            return new SolvingStrategy();
        case IFREPOK:
            return new IFREPOK();
        case PLAINLAZY:
            return new PLAINLAZY();
        default:
            throw new IllegalArgumentException("Invalid symbolic heap solving technique");
        }
    }

    public void pathFinished(VM vm, ThreadInfo terminatedThread) {
        exploredPaths++;
    }

    public void countException() {
        exceptionsThrown++;
    }

}
