package heapsolving.schedule;

import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

public class Predicate {
    private static Schedule thisInstance;
    private static Set<Job> visitedJob;
    private static Set<List> visitedList;
    private static Set<Integer> visitedInteger;

    public static boolean repOkStructure_M_(Schedule _this, Map<Class<?>, Set<Object>> mapOfVisited) {
        if (_this.prio_3 == null) {
            return false;
        }
        if (_this.prio_1 == null) {
            return false;
        }
        if ((_this.prio_2 == null) && (_this.blockQueue != null)) {
            return false;
        }
        if (_this.blockQueue == null) {
            return false;
        }
        if (_this.prio_0 != null) {
            return false;
        }
        if ((_this.prio_1 != null) && (!traverse_simple_1_M_(_this.prio_1, mapOfVisited))) {
            return false;
        }
        if ((_this.prio_2 != null) && (!traverse_simple_1_M_(_this.prio_2, mapOfVisited))) {
            return false;
        }
        if ((_this.prio_3 != null) && (!traverse_simple_1_M_(_this.prio_3, mapOfVisited))) {
            return false;
        }
        if ((_this.blockQueue != null) && (!traverse_simple_1_M_(_this.blockQueue, mapOfVisited))) {
            return false;
        }
        if ((_this.prio_3 != null) && (!visitedList.add(_this.prio_3))) {
            return false;
        }
        if ((_this.curProc != null) && visitedJob.add(_this.curProc)) {
            return false;
        }
        if ((_this.blockQueue != null) && (!visitedList.add(_this.blockQueue))) {
            return false;
        }
        if ((_this.prio_1 != null) && (!visitedList.add(_this.prio_1))) {
            return false;
        }
        if ((_this.prio_2 != null) && (!visitedList.add(_this.prio_2))) {
            return false;
        }
        return true;
    }

    public static boolean repOkPrimitive_M_(Schedule _this, Map<Class<?>, Set<Object>> mapOfVisited) {
        if ((_this != null) && (_this.allocProcNum < _this.numProcesses)) {
            return false;
        }
        return true;
    }

    public static boolean predicate(Schedule _this) {
        thisInstance = _this;
        visitedJob = Collections.newSetFromMap(new IdentityHashMap<>());
        visitedList = Collections.newSetFromMap(new IdentityHashMap<>());
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

    private static boolean traverse_simple_1_M_(List subject, Map<Class<?>, Set<Object>> mapOfVisited) {
        if ((subject.last == null) && (subject.first != null)) {
            return false;
        }
        if ((subject.first == null) && (subject.last != null)) {
            return false;
        }
        if ((subject.last != null) && (subject.last.next != null)) {
            return false;
        }
        if ((subject == null) || (subject.first == null)) {
            return true;
        }
        Job rootElement = subject.first;
        int initialSize_ = visitedJob.size();
        if (!visitedJob.add(rootElement)) {
            return false;
        }
        Job current_ = rootElement;
        while (current_ != null) {
            if (((current_ != null) && (current_.next != null)) && (current_.next.prev != current_)) {
                return false;
            }
            if (((current_ != null) && (current_.prev != null)) && (current_.prev.next != current_)) {
                return false;
            }
            if ((current_ != null) && (current_.priority > Schedule.MAXPRIO)) {
                return false;
            }
            if ((current_ != null) && (current_.priority < 0)) {
                return false;
            }
            if ((current_ != null) && (current_.val <= (-1))) {
                return false;
            }
            if ((current_ != null) && (!visitedInteger.add(current_.val))) {
                return false;
            }
            if (current_.next != null) {
                if (!visitedJob.add(current_.next)) {
                    return false;
                }
            }
            current_ = current_.next;
        }
        if ((subject.last != null) && visitedJob.add(subject.last)) {
            return false;
        }
        if ((subject != null) && ((visitedJob.size() - initialSize_) != subject.mem_count)) {
            return false;
        }
        return true;
    }
}