package heapsolving.binomialheap;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

public class Predicate {
    private static BinomialHeap thisInstance;

    public static boolean repOkStructure_M_(BinomialHeap _this, Map<Class<?>, Set<Object>> mapOfVisited) {
        if ((_this.Nodes != null) && (_this.Nodes.parent != null)) {
            return false;
        }
        if ((_this.Nodes != null) && (!traverse_worklist_0_M_(_this.Nodes, mapOfVisited))) {
            return false;
        }
        return true;
    }

    public static boolean repOkPrimitive_M_(BinomialHeap _this, Map<Class<?>, Set<Object>> mapOfVisited) {
        Set<Object> visitedBinomialHeapNode = mapOfVisited.computeIfAbsent(BinomialHeap.BinomialHeapNode.class,
                k -> Collections.newSetFromMap(new IdentityHashMap<>()));
        if (visitedBinomialHeapNode.size() != _this.size) {
            return false;
        }
        return true;
    }

    public static boolean predicate(BinomialHeap _this) {
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

    private static boolean traverse_worklist_0_M_(BinomialHeap.BinomialHeapNode subject,
            Map<Class<?>, Set<Object>> mapOfVisited) {
        if ((subject.sibling != null) && (subject.sibling.child == null)) {
            return false;
        }
        if (subject == null) {
            return true;
        }
        BinomialHeap.BinomialHeapNode rootElement = subject;
        Set<Object> visitedBinomialHeapNode = mapOfVisited.computeIfAbsent(rootElement.getClass(),
                k -> Collections.newSetFromMap(new IdentityHashMap<>()));
        int initialSize_ = visitedBinomialHeapNode.size();
        if (!visitedBinomialHeapNode.add(rootElement)) {
            return false;
        }
        LinkedList<BinomialHeap.BinomialHeapNode> worklist_ = new LinkedList<>();
        worklist_.add(rootElement);
        while (!worklist_.isEmpty()) {
            BinomialHeap.BinomialHeapNode current_ = worklist_.removeFirst();
            if (((current_ != null) && (current_.parent != null)) && (current_.parent.child == null)) {
                return false;
            }
            if (((current_ != null) && (current_.child != null)) && (current_.child.parent != current_)) {
                return false;
            }
            if ((((current_ != null) && (current_.child != null)) && (current_.child.sibling != null))
                    && (current_.child.sibling.parent != current_)) {
                return false;
            }
            if ((current_ != null) && (current_.degree < 0)) {
                return false;
            }
            if (((current_ != null) && (current_.child != null)) && (current_.degree <= current_.child.degree)) {
                return false;
            }
            if (((current_ != null) && (current_.parent != null)) && (current_.degree > current_.parent.degree)) {
                return false;
            }
            if (((current_ != null) && (current_.sibling != null)) && (current_.degree == current_.sibling.degree)) {
                return false;
            }
            if (current_.child != null) {
                if (!visitedBinomialHeapNode.add(current_.child)) {
                    return false;
                }
                worklist_.add(current_.child);
            }
            if (current_.sibling != null) {
                if (!visitedBinomialHeapNode.add(current_.sibling)) {
                    return false;
                }
                worklist_.add(current_.sibling);
            }
        }
        return true;
    }
}