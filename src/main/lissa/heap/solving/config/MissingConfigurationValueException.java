package lissa.heap.solving.config;

public class MissingConfigurationValueException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = 1215779849582413144L;

    public MissingConfigurationValueException(String configValueName) {
        super(configValueName + " not set in configuration file");
    }

}
