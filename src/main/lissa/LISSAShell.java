package lissa;

import java.util.logging.Level;
import java.util.logging.Logger;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.JPFShell;
import gov.nasa.jpf.PropertyListenerAdapter;
import lissa.heap.solving.config.ConfigParser;
import lissa.heap.solving.techniques.SolvingStrategy;
import lissa.listeners.HeapSolvingListener;

public class LISSAShell implements JPFShell {

    private static final Logger logger = JPF.getLogger(LISSAShell.class.getName());

    Config config;
    public static ConfigParser configParser;
    public static SolvingStrategy solvingStrategy;

    static {
        logger.setLevel(Level.ALL);
    }

    public LISSAShell(Config config) {
        this.config = config;
        configParser = new ConfigParser(config);
        solvingStrategy = SolvingStrategy.makeSymbolicHeapSolvingTechnique(configParser);
    }

    @Override
    public void start(String[] arg0) {

        JPF jpf = new JPF(config);
        PropertyListenerAdapter listener = new HeapSolvingListener(solvingStrategy, configParser);
        jpf.addListener(listener);

        logger.info("\n\nRuning spf-lissa...\n");

        runJPF(jpf);
    }

    private void runJPF(JPF jpf) {
        try {
            jpf.run();
        } catch (Exception e) {
            throw new SPFLISSAException("jpf-core threw exception", e);
        }
    }

}
