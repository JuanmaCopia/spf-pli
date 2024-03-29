package pli.config;

import gov.nasa.jpf.Config;
import pli.config.InvalidConfigurationValueException;
import pli.config.MissingConfigurationValueException;
import pli.config.SolvingStrategyEnum;
import symsolve.explorers.impl.SymmetryBreakStrategy;

public class ConfigParser {

    private static final String TARGET_METHOD_CONFIG = "method";
    private static final String TARGET_CLASS_CONFIG = "target";
    private static final String SYMSOLVE_CLASS_CONFIG = "heapsolving.symsolve.finitization.class";
    private static final String SYMSOLVE_PREDICATE_CONFIG = "heapsolving.symsolve.predicate";
    private static final String SYMSOLVE_FINITIZATION_ARGS_CONFIG = "heapsolving.symsolve.finitization.args";
    private static final String HEAP_SOLVING_TECHNIQUE_CONFIG = "heapsolving.strategy";
    private static final String CHECK_PATH_VALIDITY_CONFIG = "heapsolving.checkPathValidity";
    private static final String GENERATE_TESTS_CONFIG = "heapsolving.generateTests";
    private static final String HEAP_GETFIELD_LIMIT_CONFIG = "heap.getFieldLimit";

    public static final String DEFAULT_PREDICATE_NAME = "repOK";

    private static final String RESULTS_FILE_POSFIX = "-results.csv";

    public static final String OUTPUT_DIR = "output";
    public static final String STATISTICS_DIR = String.format("%s/%s", OUTPUT_DIR, "results");
    public static final String TESTCASE_DIR = String.format("%s/%s", OUTPUT_DIR, "testcases");

    private static final int DEFAULT_GETFIELD_LIMIT = 200;

    public Config conf;

    public String targetMethodName;
    public String targetClassName;
    public String symSolveClassName;
    public String symSolveSimpleClassName;
    public String finitizationArgs;
    public String resultsFileName;
    public String testsFileName;
    public String predicateName;
    public SolvingStrategyEnum solvingStrategy;
    public SymmetryBreakStrategy symmetryBreakingStrategy = SymmetryBreakStrategy.SYMMETRY_BREAK;
    public int getFieldLimit;
    public boolean checkPathValidity;
    public boolean generateTests;

    public ConfigParser(Config conf) {
        this.conf = conf;
        this.targetMethodName = getConfigValueString(TARGET_METHOD_CONFIG, "METHOD-NOT-SET");
        this.targetClassName = getConfigValueString(TARGET_CLASS_CONFIG);
        this.symSolveClassName = getConfigValueString(SYMSOLVE_CLASS_CONFIG);
        this.symSolveSimpleClassName = calculateSimpleClassName(this.symSolveClassName);
        this.finitizationArgs = getConfigValueString(SYMSOLVE_FINITIZATION_ARGS_CONFIG);
        this.solvingStrategy = getSolvingHeapSolvingTechnique();
        this.getFieldLimit = conf.getInt(HEAP_GETFIELD_LIMIT_CONFIG, DEFAULT_GETFIELD_LIMIT);
        String resFileName = this.symSolveSimpleClassName + RESULTS_FILE_POSFIX;
        this.resultsFileName = String.format("%s/%s", STATISTICS_DIR, resFileName);
        this.testsFileName = String.format("%s/%s", TESTCASE_DIR, getTestFileName());
        this.predicateName = getConfigValueString(SYMSOLVE_PREDICATE_CONFIG, DEFAULT_PREDICATE_NAME);
        this.checkPathValidity = getConfigValueBoolean(CHECK_PATH_VALIDITY_CONFIG, "false");
        this.generateTests = getConfigValueBoolean(GENERATE_TESTS_CONFIG, "false");
    }

    public String getConfigValueString(String settingName) {
        String value = conf.getString(settingName, "");
        if (value.isEmpty())
            throw new MissingConfigurationValueException(settingName);
        return value.trim();
    }

    public String getConfigValueString(String settingName, String defaultValue) {
        return conf.getString(settingName, defaultValue).trim();
    }

    public boolean getConfigValueBoolean(String settingName, String defaultValue) {
        String value = conf.getString(settingName, defaultValue).trim();
        if (value.equals("true"))
            return true;
        return false;
    }

    public SolvingStrategyEnum getSolvingHeapSolvingTechnique() {
        String techniqueName = getConfigValueString(HEAP_SOLVING_TECHNIQUE_CONFIG);
        return SolvingStrategyEnum.valueOf(techniqueName.toUpperCase());
    }

    private String calculateSimpleClassName(String fullyQualifiedClassName) {
        String[] cnsplit = fullyQualifiedClassName.split("\\.");
        if (cnsplit.length == 0)
            throw new InvalidConfigurationValueException(
                    "the value of " + SYMSOLVE_CLASS_CONFIG + " is not well formed");
        return cnsplit[cnsplit.length - 1];
    }

    public String getTestFileName() {
        return getTestFileNameWithoutFormat() + ".java";
    }

    public String getTestFileNameWithoutFormat() {
        return symSolveSimpleClassName + targetMethodName + solvingStrategy.name() + finitizationArgs + "Test";
    }

}
