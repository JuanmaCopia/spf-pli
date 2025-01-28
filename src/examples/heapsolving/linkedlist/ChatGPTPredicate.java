package heapsolving.linkedlist;

import java.util.HashSet;
import java.util.Set;

public class ChatGPTPredicate {

    public static boolean predicate(LinkedList _this) {
        // Check if the header is null
        if (_this.header == null) {
            return false;
        }

        // Check if the header forms a proper circular reference
        if (_this.header.next == null || _this.header.previous == null) {
            return false;
        }

        // Check if the size is non-negative
        if (_this.size < 0) {
            return false;
        }

        // Use a set to detect cycles and verify the structure
        Set<LinkedList.Entry> visited = new HashSet<>();
        LinkedList.Entry current = _this.header.next;

        int count = 0;

        while (current != _this.header) {
            // Check for null references in the list
            if (current == null || current.next == null || current.previous == null) {
                return false;
            }

            // Detect cycles by ensuring we don't visit the same entry twice
            if (!visited.add(current)) {
                return false;
            }

            // Ensure the doubly linked structure is consistent
            if (current.next.previous != current || current.previous.next != current) {
                return false;
            }

            current = current.next;
            count++;
        }

        // Ensure the _this.size matches the actual number of elements
        if (count != _this.size) {
            return false;
        }

        // All checks passed
        return true;
    }

}