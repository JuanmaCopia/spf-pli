package expr;

import gov.nasa.jpf.PropertyListenerAdapter;
import gov.nasa.jpf.report.PublisherExtension;
import gov.nasa.jpf.search.Search;

public class ExprListener extends PropertyListenerAdapter implements PublisherExtension {

    public static int truePositives = 0;
    public static int trueNegatives = 0;
    public static int falsePositives = 0;
    public static int falseNegatives = 0;

    @Override
    public void searchStarted(Search search) {
        super.searchStarted(search);
        System.out.println("==================== Expr Evaluation ================ ");
    }

    @Override
    public void searchFinished(Search search) {
        System.out.println("\n==================== Expr Evaluation Results ================\n");
        System.out.println("True Positives: " + truePositives);
        System.out.println("True Negatives: " + trueNegatives);
        System.out.println("False Positives: " + falsePositives);
        System.out.println("False Negatives: " + falseNegatives);
        System.out.println("\n=============================================================\n");
    }

}
