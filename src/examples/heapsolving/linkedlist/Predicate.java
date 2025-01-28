package heapsolving.linkedlist;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

public class Predicate {
    private static LinkedList thisInstance;

    public static boolean repOkStructure_M_(LinkedList _this, Map<Class<?>, Set<Object>> mapOfVisited) {
        if ((_this.header != null) && (_this.header.previous == null)) {
            return false;
        }
        if (_this.header == null) {
            return false;
        }
        if ((_this.header != null) && (_this.header.next == null)) {
            return false;
        }
        if ((_this.header != null) && (!traverse_circular_0_M_(_this.header, mapOfVisited))) {
            return false;
        }
        return true;
    }

    public static boolean repOkPrimitive_M_(LinkedList _this, Map<Class<?>, Set<Object>> mapOfVisited) {
        Set<Object> visitedEntry = mapOfVisited.computeIfAbsent(LinkedList.Entry.class,
                k -> Collections.newSetFromMap(new IdentityHashMap<>()));
        if ((visitedEntry.size() - 1) != _this.size) {
            return false;
        }
        return true;
    }

    public static boolean predicate(LinkedList _this) {
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

    private static boolean traverse_circular_0_M_(LinkedList.Entry subject, Map<Class<?>, Set<Object>> mapOfVisited) {
        if (subject == null) {
            return true;
        }
        LinkedList.Entry rootElement = subject;
        Set<Object> visitedEntry = mapOfVisited.computeIfAbsent(rootElement.getClass(),
                k -> Collections.newSetFromMap(new IdentityHashMap<>()));
        if (!visitedEntry.add(rootElement)) {
            return false;
        }
        LinkedList.Entry current_ = rootElement;
        while ((current_ != null) && (current_.previous != rootElement)) {
            if (((current_ != null) && (current_.previous != null)) && (current_.previous.next != current_)) {
                return false;
            }
            if (((current_ != null) && (current_.next != null)) && (current_.next.previous != current_)) {
                return false;
            }
            if (current_.previous != null) {
                if (!visitedEntry.add(current_.previous)) {
                    return false;
                }
            }
            current_ = current_.previous;
        }
        if ((subject.next != null) && visitedEntry.add(subject.next)) {
            return false;
        }
        if ((subject.previous != null) && visitedEntry.add(subject.previous)) {
            return false;
        }
        if (current_ == null) {
            return false;
        }
        return true;
    }
}