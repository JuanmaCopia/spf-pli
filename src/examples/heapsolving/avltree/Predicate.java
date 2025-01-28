package heapsolving.avltree;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

public class Predicate {
    private static AvlTree thisInstance;

    public static boolean repOkStructure_M_(AvlTree _this, Map<Class<?>, Set<Object>> mapOfVisited) {
        if ((_this.root != null) && (!traverse_worklist_0_M_(_this.root, mapOfVisited))) {
            return false;
        }
        return true;
    }

    public static boolean repOkPrimitive_M_(AvlTree _this, Map<Class<?>, Set<Object>> mapOfVisited) {
        return true;
    }

    public static boolean predicate(AvlTree _this) {
        thisInstance = _this;
        Map<Class<?>, Set<Object>> mapOfVisited = new IdentityHashMap<>();
        if (!repOkStructure_M_(_this, mapOfVisited)) {
            return false;
        }
        if (!repOkPrimitive_M_(_this, mapOfVisited)) {
            return false;
        }
        return true;
    }

    private static boolean traverse_worklist_0_M_(AvlTree.AvlNode subject, Map<Class<?>, Set<Object>> mapOfVisited) {
        if (subject == null) {
            return true;
        }
        AvlTree.AvlNode rootElement = subject;
        Set<Object> visitedAvlNode = mapOfVisited.computeIfAbsent(rootElement.getClass(),
                k -> Collections.newSetFromMap(new IdentityHashMap<>()));
        if (!visitedAvlNode.add(rootElement)) {
            return false;
        }
        LinkedList<AvlTree.AvlNode> worklist_ = new LinkedList<>();
        worklist_.add(rootElement);
        Set<Object> visitedInteger = mapOfVisited.computeIfAbsent(Integer.class,
                k -> Collections.newSetFromMap(new IdentityHashMap<>()));
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