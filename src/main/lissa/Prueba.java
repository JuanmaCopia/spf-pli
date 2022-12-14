package lissa;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.JPFConfigException;
import gov.nasa.jpf.JPFException;

public class Prueba {

    public static void runSPF() {
        try {
            String[] args = new String[10];
            Config conf = JPF.createConfig(args);

            conf.setProperty("target", "myexample.Example2");
            conf.setProperty("symbolic.dp", "z3");
            conf.setProperty("search.depth_limit", "25");

            // ... explicitly create listeners (could be reused over multiple JPF runs)
            // APISearchListener myListener = new APISearchListener();

            JPF jpf = new JPF(conf);

            // ... set your listeners
            // jpf.addListener(myListener);

            jpf.run();
            if (jpf.foundErrors()) {
                // ... process property violations discovered by JPF
            }
        } catch (JPFConfigException cx) {
            System.out.println(cx.getMessage());
            // ... handle configuration exception
            // ... can happen before running JPF and indicates inconsistent configuration
            // data
        } catch (JPFException jx) {
            System.out.println(jx.getMessage());
            // ... handle exception while executing JPF, can be further differentiated into
            // ... JPFListenerException - occurred from within configured listener
            // ... JPFNativePeerException - occurred from within MJI method/native peer
            // ... all others indicate JPF internal errors
        }
    }

}
