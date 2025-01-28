package heapsolving.hashmap;

import java.util.HashSet;
import java.util.Set;

public class ChatGPTPredicate {

    public static boolean predicate(HashMap _this) {
        // 1. Check if size is non-negative and does not exceed the threshold.
        if (_this.size < 0 || _this.size > HashMap.THRESHOLD) {
            return false;
        }

        // 2. Check each bucket for consistency.
        Set<Integer> seenKeys = new HashSet<>(); // To detect duplicate keys.
        int computedSize = 0; // To verify size consistency.

        for (int i = 0; i < HashMap.DEFAULT_INITIAL_CAPACITY; i++) {
            HashMap.Entry current = _this.getTable(i);

            while (current != null) {
                // 3. Check if the entry is not null.
                if (current == null) {
                    return false;
                }

                // 4. Validate key and hash consistency.
                if (current.key != 0 && current.hash != HashMap.hash(current.key)) {
                    return false;
                }

                // 5. Check for duplicate keys.
                if (!seenKeys.add(current.key)) {
                    return false;
                }

                // 6. Verify that the value can be null, but the entry itself must not loop.
                if (current.next == current) {
                    return false;
                }

                current = current.next;
                computedSize++;
            }
        }

        // 7. Validate size consistency.
        if (computedSize != _this.size) {
            return false;
        }

        return true;
    }

}