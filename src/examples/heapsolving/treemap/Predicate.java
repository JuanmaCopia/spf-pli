package heapsolving.treemap;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

public class Predicate {
    private static TreeMap thisInstance;

    public static boolean repOkStructure_M_(TreeMap _this, Map<Class<?>, Set<Object>> mapOfVisited) {
        if ((_this.root != null) && (_this.root.parent != null)) {
            return false;
        }
        if ((_this.root != null) && (!traverse_worklist_0_M_(_this.root, mapOfVisited))) {
            return false;
        }
        return true;
    }

    public static boolean repOkPrimitive_M_(TreeMap _this, Map<Class<?>, Set<Object>> mapOfVisited) {
        Set<Object> visitedEntry = mapOfVisited.computeIfAbsent(TreeMap.Entry.class,
                k -> Collections.newSetFromMap(new IdentityHashMap<>()));
        if (visitedEntry.size() != _this.size) {
            return false;
        }
        return true;
    }

    public static boolean predicate(TreeMap _this) {
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

    private static boolean traverse_worklist_0_M_(TreeMap.Entry subject, Map<Class<?>, Set<Object>> mapOfVisited) {
        if (subject == null) {
            return true;
        }
        TreeMap.Entry rootElement = subject;
        Set<Object> visitedEntry = mapOfVisited.computeIfAbsent(rootElement.getClass(),
                k -> Collections.newSetFromMap(new IdentityHashMap<>()));
        int initialSize_ = visitedEntry.size();
        if (!visitedEntry.add(rootElement)) {
            return false;
        }
        LinkedList<TreeMap.Entry> worklist_ = new LinkedList<>();
        worklist_.add(rootElement);
        Set<Object> visitedInteger = mapOfVisited.computeIfAbsent(Integer.class,
                k -> Collections.newSetFromMap(new IdentityHashMap<>()));
        while (!worklist_.isEmpty()) {
            TreeMap.Entry current_ = worklist_.removeFirst();
            if (((current_ != null) && (current_.left != null)) && (current_.left.parent != current_)) {
                return false;
            }
            if (((current_ != null) && (current_.right != null)) && (current_.right.parent != current_)) {
                return false;
            }
            if (((current_ != null) && (current_.left != null)) && (current_.key < current_.left.key)) {
                return false;
            }
            if (((current_ != null) && (current_.right != null)) && (current_.key > current_.right.key)) {
                return false;
            }
            if ((current_ != null) && (!visitedInteger.add(current_.key))) {
                return false;
            }
            if (current_.left != null) {
                if (!visitedEntry.add(current_.left)) {
                    return false;
                }
                worklist_.add(current_.left);
            }
            if (current_.right != null) {
                if (!visitedEntry.add(current_.right)) {
                    return false;
                }
                worklist_.add(current_.right);
            }
        }
        return true;
    }
}