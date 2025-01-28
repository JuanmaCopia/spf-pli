package heapsolving.avltree;

public class CombinedPredicate {

    public static boolean predicate(AvlTree _this) {
        if (!Predicate.predicate(_this))
            return false;
        if (!ChatGPTPredicate.predicate(_this))
            return false;
        return true;
    }

}
