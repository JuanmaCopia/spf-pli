package heapsolving.avltree;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.Set;

import heapsolving.avltree.AvlTree;

public class Predicate {
    private static AvlTree thisInstance;
    static Set<AvlTree.AvlNode> visitedAvlNode;

    public static boolean repOkStructure_M_(AvlTree _this) {
        if ((_this.root != null) && (!traverse_1_M_(_this.root))) {
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
        if (!repOkStructure_M_(_this)) {
            return false;
        }
        if (!repOkPrimitive_M_(_this)) {
            return false;
        }
        return true;
    }

    private static boolean traverse_1_M_(AvlTree.AvlNode subject) {
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
            if (((current_ != null) && (current_.left != null)) && (!(current_.element >= current_.left.element))) {
                return false;
            }
            if (((current_ != null) && (current_.right != null)) && (!(current_.element < current_.right.element))) {
                return false;
            }
            if (((current_ != null) && (current_.right != null)) && (current_.height < current_.right.height)) {
                return false;
            }
            if (((current_ != null) && (current_.left != null)) && (current_.height <= current_.left.height)) {
                return false;
            }
            if (current_.right != null) {
                if (!visitedAvlNode.add(current_.right)) {
                    return false;
                }
                worklist_.add(current_.right);
            }
            if (current_.left != null) {
                if (!visitedAvlNode.add(current_.left)) {
                    return false;
                }
                worklist_.add(current_.left);
            }
        }
        return true;
    }
}