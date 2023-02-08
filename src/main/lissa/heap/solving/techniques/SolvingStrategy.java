package lissa.heap.solving.techniques;

import gov.nasa.jpf.vm.ThreadInfo;
import gov.nasa.jpf.vm.VM;
import lissa.config.ConfigParser;
import symsolve.explorers.impl.SymmetryBreakStrategy;

public class SolvingStrategy {

    public int exploredPaths = 0;

    public static SolvingStrategy makeSymbolicHeapSolvingTechnique(ConfigParser configParser) {
        switch (configParser.solvingStrategy) {
        case LISSA:
            return new LISSA();
        case LISSAM:
            return new LISSAM();
        case NT:
            return new NT();
        case NTOPT:
            return new NTOPT();
        case LISSANOSB:
            configParser.symmetryBreakingStrategy = SymmetryBreakStrategy.NO_SYMMETRY_BREAK;
            return new LISSA();
        case LIHYBRID:
            return new LIHYBRID(configParser.getFieldLimit);
        case DRIVER:
            return new SolvingStrategy();
        case IFREPOK:
            return new IFREPOK();
        default:
            throw new IllegalArgumentException("Invalid symbolic heap solving technique");
        }
    }

    public void pathFinished(VM vm, ThreadInfo terminatedThread) {
        exploredPaths++;
    }

}
