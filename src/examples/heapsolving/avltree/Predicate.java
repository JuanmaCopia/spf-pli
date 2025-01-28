package heapsolving.avltree;

import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.Set;

public class Predicate {
    private static AvlTree thisInstance;
    private static Set<AvlTree.AvlNode> visitedAvlNode;
    private static Set<Integer> visitedInteger;

    public static boolean repOkStructure_M_(AvlTree _this) {
        if ((_this.root != null) && (!traverse_worklist_0_M_(_this.root))) {
            return false;
        }
        return true;
    }

    public static boolean repOkPrimitive_M_(AvlTree _this) {
        return true;
    }

    public static boolean predicate(AvlTree _this) {
        thisInstance = _this;
        visitedAvlNode = Collections.newSetFromMap(new IdentityHashMap<>());
        visitedInteger = new HashSet<>();
        if (!repOkStructure_M_(_this)) {
            return false;
        }
        if (!repOkPrimitive_M_(_this)) {
            return false;
        }
        return true;
    }

    private static boolean traverse_worklist_0_M_(AvlTree.AvlNode subject) {
        if (subject == null) {
            return true;
        }
        AvlTree.AvlNode rootElement = subject;
        if (!visitedAvlNode.add(rootElement)) {
            return false;
        }
        LinkedList<AvlTree.AvlNode> worklist_ = new LinkedList<>();
        worklist_.add(rootElement);
        while (!worklist_.isEmpty()) {
            AvlTree.AvlNode current_ = worklist_.removeFirst();
            if ((current_ != null) && (current_.height < 0)) {
                return false;
            }
            if ((current_ != null) && (!visitedInteger.add(current_.element))) {
                return false;
            }
            if (((current_ != null) && (current_.right != null)) && (current_.height <= current_.right.height)) {
                return false;
            }
            if (((current_ != null) && (current_.left != null)) && (current_.element <= current_.left.element)) {
                return false;
            }
            if (((current_ != null) && (current_.left != null)) && (current_.height <= current_.left.height)) {
                return false;
            }
            if (((current_ != null) && (current_.right != null)) && (current_.element > current_.right.element)) {
                return false;
            }
            if (current_.left != null) {
                if (!visitedAvlNode.add(current_.left)) {
                    return false;
                }
                worklist_.add(current_.left);
            }
            if (current_.right != null) {
                if (!visitedAvlNode.add(current_.right)) {
                    return false;
                }
                worklist_.add(current_.right);
            }
        }
        return true;
    }
}