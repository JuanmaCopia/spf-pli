package heapsolving.binomialheap;

import java.util.HashSet;
import java.util.Set;

import heapsolving.binomialheap.BinomialHeap.BinomialHeapNode;

public class ChatGPTPredicate {

    /**
     * Representation invariant check method.
     */
    public static boolean predicate(BinomialHeap _this) {
        if (_this.size < 0) {
            return false; // Size cannot be negative.
        }

        if (_this.Nodes == null) {
            return _this.size == 0; // If there are no nodes, size must be 0.
        }

        Set<BinomialHeapNode> visited = new HashSet<>();

        // Check the integrity of the heap starting from the root nodes.
        if (!validateHeapStructure(_this.Nodes, visited)) {
            return false;
        }

        // Validate size consistency.
        if (visited.size() != _this.size) {
            return false;
        }

        return true;
    }

    private static boolean validateHeapStructure(BinomialHeapNode node, Set<BinomialHeapNode> visited) {
        while (node != null) {
            // Check for cycles (no node should be visited more than once).
            if (!visited.add(node)) {
                return false;
            }

            // Check that degree matches the number of children.
            int childCount = countChildren(node.child);
            if (node.degree != childCount) {
                return false;
            }

            // Validate parent-child relationships.
            if (node.child != null) {
                BinomialHeapNode child = node.child;
                while (child != null) {
                    if (child.parent != node) {
                        return false;
                    }
                    child = child.sibling;
                }
            }

            // Check sibling degree order.
            if (node.sibling != null && node.degree >= node.sibling.degree) {
                return false;
            }

            // Recursively validate the children.
            if (node.child != null && !validateHeapStructure(node.child, visited)) {
                return false;
            }

            // Move to the next sibling.
            node = node.sibling;
        }
        return true;
    }

    private static int countChildren(BinomialHeapNode child) {
        int count = 0;
        while (child != null) {
            count++;
            child = child.sibling;
        }
        return count;
    }

}
