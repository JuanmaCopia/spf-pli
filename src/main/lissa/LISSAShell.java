package lissa;

import java.util.logging.Level;
import java.util.logging.Logger;

import gov.nasa.jpf.JPF;
import gov.nasa.jpf.JPFShell;

public class LISSAShell implements JPFShell {

    private static final Logger logger = JPF.getLogger(LISSAShell.class.getName());

    static {
        logger.setLevel(Level.ALL);
    }

    @Override
    public void start(String[] arg0) {
        // TODO Auto-generated method stub

    }

}
