package heapsolving.schedule;

public class CombinedPredicate {

    public static boolean predicate(Schedule _this) {
        if (!Predicate.predicate(_this))
            return false;
        if (!ChatGPTPredicate.predicate(_this))
            return false;
        return true;
    }

}
