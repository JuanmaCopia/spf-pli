package lissa;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.JPFConfigException;
import gov.nasa.jpf.JPFException;

public class Prueba {

//    public static void main(String[] args) {
//        try {
//            Config conf = JPF.createConfig(args);
//            conf.setProperty("target", "myexample.Example2");
//            JPF jpf = new JPF(conf);
//            jpf.run();
//        } catch (JPFConfigException cx) {
//            System.out.println(cx.getMessage());
//        } catch (JPFException jx) {
//            System.out.println(jx.getMessage());
//        }
//    }

    public static void secondRun() {
        try {
            String[] args = new String[10];
            Config conf = JPF.createConfig(args);
            conf.setProperty("target", "myexample.Example2");
            JPF jpf = new JPF(conf);
            jpf.run();
        } catch (JPFConfigException cx) {
            System.out.println(cx.getMessage());
        } catch (JPFException jx) {
            System.out.println(jx.getMessage());
        }
    }

}
