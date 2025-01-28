package heapsolving.hashmap;

import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

public class Predicate {
    private static HashMap thisInstance;
    private static Set<HashMap.Entry> visitedEntry;
    private static Set<Integer> visitedInteger;

    public static boolean repOkStructure_M_(HashMap _this, Map<Class<?>, Set<Object>> mapOfVisited) {
        if ((_this.e0 != null) && (!traverse_simple_0_M_(_this.e0, mapOfVisited))) {
            return false;
        }
        if ((_this.e15 != null) && (!traverse_simple_0_M_(_this.e15, mapOfVisited))) {
            return false;
        }
        if ((_this.e10 != null) && (!traverse_simple_0_M_(_this.e10, mapOfVisited))) {
            return false;
        }
        if ((_this.e13 != null) && (!traverse_simple_0_M_(_this.e13, mapOfVisited))) {
            return false;
        }
        if ((_this.e3 != null) && (!traverse_simple_0_M_(_this.e3, mapOfVisited))) {
            return false;
        }
        if ((_this.e1 != null) && (!traverse_simple_0_M_(_this.e1, mapOfVisited))) {
            return false;
        }
        if ((_this.e9 != null) && (!traverse_simple_0_M_(_this.e9, mapOfVisited))) {
            return false;
        }
        if ((_this.e4 != null) && (!traverse_simple_0_M_(_this.e4, mapOfVisited))) {
            return false;
        }
        if ((_this.e6 != null) && (!traverse_simple_0_M_(_this.e6, mapOfVisited))) {
            return false;
        }
        if ((_this.e12 != null) && (!traverse_simple_0_M_(_this.e12, mapOfVisited))) {
            return false;
        }
        if ((_this.e2 != null) && (!traverse_simple_0_M_(_this.e2, mapOfVisited))) {
            return false;
        }
        if ((_this.e8 != null) && (!traverse_simple_0_M_(_this.e8, mapOfVisited))) {
            return false;
        }
        if ((_this.e14 != null) && (!traverse_simple_0_M_(_this.e14, mapOfVisited))) {
            return false;
        }
        if ((_this.e7 != null) && (!traverse_simple_0_M_(_this.e7, mapOfVisited))) {
            return false;
        }
        if ((_this.e5 != null) && (!traverse_simple_0_M_(_this.e5, mapOfVisited))) {
            return false;
        }
        if ((_this.e11 != null) && (!traverse_simple_0_M_(_this.e11, mapOfVisited))) {
            return false;
        }
        return true;
    }

    public static boolean repOkPrimitive_M_(HashMap _this, Map<Class<?>, Set<Object>> mapOfVisited) {
        if (visitedEntry.size() != _this.size) {
            return false;
        }
        return true;
    }

    public static boolean predicate(HashMap _this) {
        thisInstance = _this;
        visitedEntry = Collections.newSetFromMap(new IdentityHashMap<>());
        visitedInteger = new HashSet<>();
        Map<Class<?>, Set<Object>> mapOfVisited = new IdentityHashMap<>();
        if (!repOkStructure_M_(_this, mapOfVisited)) {
            return false;
        }
        if (!repOkPrimitive_M_(_this, mapOfVisited)) {
            return false;
        }
        return true;
    }

    private static boolean traverse_simple_0_M_(HashMap.Entry subject, Map<Class<?>, Set<Object>> mapOfVisited) {
        if (subject == null) {
            return true;
        }
        HashMap.Entry rootElement = subject;
        if (!visitedEntry.add(rootElement)) {
            return false;
        }
        HashMap.Entry current_ = rootElement;

        while (current_ != null) {
            if ((current_ != null) && (current_.key != current_.hash)) {
                return false;
            }
            if (current_.next != null) {
                if (!visitedEntry.add(current_.next)) {
                    return false;
                }
            }
            current_ = current_.next;
        }
        return true;
    }
}