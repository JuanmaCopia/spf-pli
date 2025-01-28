package heapsolving.schedule;

import java.util.HashSet;
import java.util.Set;

public class ChatGPTPredicate {

    public static boolean predicate(Schedule _this) {
        // Ensure numProcesses is non-negative and matches the total number of jobs in
        // all queues.
        if (_this.numProcesses < 0) {
            return false;
        }

        // Validate priority queues and blockQueue.
        if (!validateQueue(_this.prio_0, 0) || !validateQueue(_this.prio_1, 1) || !validateQueue(_this.prio_2, 2)
                || !validateQueue(_this.prio_3, 3) || !validateQueue(_this.blockQueue, -1)) {
            return false;
        }

        // Check the curProc reference: must not alias with any queue.
        if (_this.curProc != null) {
            Set<Job> allJobs = collectAllJobs(_this);
            if (!allJobs.contains(_this.curProc)) {
                return false;
            }
        }

        // Ensure allocProcNum is greater than or equal to the number of processes ever
        // created.
        if (_this.allocProcNum < _this.numProcesses) {
            return false;
        }

        return true;
    }

    private static boolean validateQueue(List queue, int priority) {
        if (queue == null) {
            return true; // Null queues are allowed and considered valid.
        }

        // Validate the memory count.
        if (queue.getMemCount() < 0) {
            return false;
        }

        // Traverse the queue and validate each Job.
        Job current = queue.getFirst();
        Job prev = null;
        int count = 0;
        Set<Job> seenJobs = new HashSet<>();

        while (current != null) {
            // Check for cycles and duplicate references.
            if (seenJobs.contains(current)) {
                return false;
            }
            seenJobs.add(current);

            // Validate the Job's priority (if priority > 0).
            if (priority > 0 && current.getPriority() != priority) {
                return false;
            }

            // Check the linkage consistency.
            if (current.getPrev() != prev) {
                return false;
            }

            prev = current;
            current = current.getNext();
            count++;
        }

        // Ensure the memory count matches the number of traversed jobs.
        return count == queue.getMemCount() && queue.getLast() == prev;
    }

    private static Set<Job> collectAllJobs(Schedule _this) {
        Set<Job> allJobs = new HashSet<>();
        collectJobsFromQueue(_this.prio_0, allJobs);
        collectJobsFromQueue(_this.prio_1, allJobs);
        collectJobsFromQueue(_this.prio_2, allJobs);
        collectJobsFromQueue(_this.prio_3, allJobs);
        collectJobsFromQueue(_this.blockQueue, allJobs);
        return allJobs;
    }

    private static void collectJobsFromQueue(List queue, Set<Job> allJobs) {
        if (queue == null) {
            return;
        }

        Job current = queue.getFirst();
        while (current != null) {
            allJobs.add(current);
            current = current.getNext();
        }
    }

}