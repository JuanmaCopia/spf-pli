package heapsolving.schedule;

import java.util.HashSet;
import java.util.Set;

import korat.finitization.IFinitization;
import korat.finitization.IObjSet;
import korat.finitization.impl.FinitizationFactory;
import pli.SymHeap;

public class Schedule {

    public final static int MAXPRIO = 3;

    public int allocProcNum;
    public int numProcesses;

    public Job curProc;

    public List prio_0;
    public List prio_1;
    public List prio_2;
    public List prio_3;

    public List blockQueue;

    // Init the queues with no processes
    public Schedule() {
        initialize();
        initPrioQueue(3, 0);
        initPrioQueue(2, 0);
        initPrioQueue(1, 0);
    }

    public Schedule(int numProc3, int numProc2, int numProc1) {
        initialize();
        initPrioQueue(3, numProc3);
        initPrioQueue(2, numProc2);
        initPrioQueue(1, numProc1);
    }

    private List appendEle(List aList, Job aEle) {
        if (aList == null) {
            aList = new List();/* make list without compare function */
        }

        aEle.setPrev(aList.getLast()); /* insert at the tail */
        if (aList.getLast() != null) {
            aList.getLast().setNext(aEle);
        } else {
            aList.setFirst(aEle);
        }
        aList.setLast(aEle);
        aEle.setNext(null);
        aList.setMemCount(aList.getMemCount() + 1);
        return aList;
    }

    private Job findNth(List fList, int n) {
        Job fEle;

        if (fList == null) {
            return null;
        }
        fEle = fList.getFirst();
        for (int i = 1; fEle != null && i < n; i++) {
            fEle = fEle.getNext();
        }
        return fEle;
    }

    private List delEle(List dList, Job dEle) {
        if (dList == null || dEle == null) {
            return null;
        }

        if (dEle.getNext() != null) {
            dEle.getNext().setPrev(dEle.getPrev());
        } else {
            dList.setLast(dEle.getPrev());
        }
        if (dEle.getPrev() != null) {
            dEle.getPrev().setNext(dEle.getNext());
        } else {
            dList.setFirst(dEle.getNext());
        }
        /* KEEP d_ele's data & pointers intact!! */
        dList.setMemCount(dList.getMemCount() - 1);
        return dList;
    }

    public void finishProcess() {
        schedule();
        if (curProc != null) {
            curProc = null;
            numProcesses--;
        }
    }

    public void finishAllProcesses() {
        int total;
        total = numProcesses;
        for (int i = 0; i < total; i++) {
            finishProcess();
        }
    }

    private void schedule() {
        curProc = null;
        for (int i = MAXPRIO; i > 0; i--) {
            if (getPrioQueue(i).getMemCount() > 0) {
                curProc = getPrioQueue(i).getFirst();
                setPrioQueue(i, delEle(getPrioQueue(i), curProc));
                return;
            }
        }
    }

    public void upgradeProcessPrio(int prio, float ratio) {
        if (prio < 1 || prio > MAXPRIO)
            throw new IllegalArgumentException();
        if (ratio < 0.0 || ratio > 1.0)
            throw new IllegalArgumentException();

        int count;
        int n;
        Job proc;
        List srcQueue, destQueue;

        if (prio >= MAXPRIO) {
            return;
        }
        srcQueue = getPrioQueue(prio);
        destQueue = getPrioQueue(prio + 1);
        count = srcQueue.getMemCount();

        if (count > 0) {
            n = (int) (count * ratio + 1);
            proc = findNth(srcQueue, n);
            if (proc != null) {
                srcQueue = delEle(srcQueue, proc);
                /* append to appropriate prio queue */
                proc.setPriority(prio + 1);
                destQueue = appendEle(destQueue, proc);
            }
        }
    }

    public void unblockProcess(float ratio) {
        if (ratio < 0.0 || ratio > 1.0)
            throw new IllegalArgumentException();

        int count;
        int n;
        Job proc;
        int prio;
        if (blockQueue != null) {
            count = blockQueue.getMemCount();
            n = (int) (count * ratio + 1);
            proc = findNth(blockQueue, n);
            if (proc != null) {
                blockQueue = delEle(blockQueue, proc);
                /* append to appropriate prio queue */
                prio = proc.getPriority();
                setPrioQueue(prio, appendEle(getPrioQueue(prio), proc));
            }
        }
    }

    public void quantumExpire() {
        int prio;
        schedule();
        if (curProc != null) {
            prio = curProc.getPriority();
            setPrioQueue(prio, appendEle(getPrioQueue(prio), curProc));
        }
    }

    public void blockProcess() {
        schedule();
        if (curProc != null) {
            blockQueue = appendEle(blockQueue, curProc);
        }
    }

    private Job newProcess(int prio) {
        Job proc = new Job(allocProcNum++);
        proc.setPriority(prio);
        numProcesses++;
        return proc;
    }

    public void addProcess(int prio) {
        if (prio > MAXPRIO)
            return;
        if (prio < 1)
            return;

        Job proc;
        proc = newProcess(prio);

        setPrioQueue(prio, appendEle(getPrioQueue(prio), proc));
    }

    private void initPrioQueue(int prio, int numProc) {
        if (prio < 1 || prio > MAXPRIO)
            throw new IllegalArgumentException();
        List queue;
        Job proc;

        queue = new List();
        for (int i = 0; i < numProc; i++) {
            proc = newProcess(prio);
            queue = appendEle(queue, proc);
        }

        setPrioQueue(prio, queue);
    }

    private void setPrioQueue(int prio, List queue) {
        switch (prio) {
        case 0:
            prio_0 = queue;
            break;
        case 1:
            prio_1 = queue;
            break;
        case 2:
            prio_2 = queue;
            break;
        case 3:
            prio_3 = queue;
            break;
        default:
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    private List getPrioQueue(int prio) {
        switch (prio) {
        case 0:
            return prio_0;
        case 1:
            return prio_1;
        case 2:
            return prio_2;
        case 3:
            return prio_3;
        default:
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    private void initialize() {
        allocProcNum = 0;
        numProcesses = 0;
        blockQueue = new List();
    }

    public String toString() {
        if (numProcesses == 0) {
            return "Schedule={}";
        } else {
            StringBuffer buf = new StringBuffer();
            buf.append("Schedule = {");
            buf.append(" prioQueue = { ");
            for (int i = 1; i <= MAXPRIO; i++) {
                List proc = getPrioQueue(i);
                buf.append(proc.toString());
            }
            buf.append(" }");
            buf.append(" , ");
            buf.append(" blockQueue = { ");
            buf.append(blockQueue.toString());
            buf.append(" }");
            buf.append(" currProc = { ");
            buf.append(curProc == null ? "null" : curProc.toString());
            buf.append(" }");
            buf.append(" }");
            return buf.toString();
        }
    }

    public static IFinitization finSchedule(int jobsNum) {
        IFinitization f = FinitizationFactory.create(Schedule.class);

        IObjSet jobs = f.createObjSet(Job.class, jobsNum, true);
        f.set(Schedule.class, "allocProcNum", f.createIntSet(0, jobsNum + 1));
        f.set(Schedule.class, "numProcesses", f.createIntSet(0, jobsNum));
        f.set(Schedule.class, "curProc", jobs);

        IObjSet lists = f.createObjSet(List.class, 5, true);
        f.set(Schedule.class, "prio_0", lists);
        f.set(Schedule.class, "prio_1", lists);
        f.set(Schedule.class, "prio_2", lists);
        f.set(Schedule.class, "prio_3", lists);
        f.set(Schedule.class, "blockQueue", lists);

        f.set(Job.class, "next", jobs);
        f.set(Job.class, "prev", jobs);
        f.set(Job.class, "val", f.createIntSet(0, jobsNum - 1));
        f.set(Job.class, "priority", f.createIntSet(1, MAXPRIO));

        f.set(List.class, "mem_count", f.createIntSet(0, jobsNum));
        f.set(List.class, "first", jobs);
        f.set(List.class, "last", jobs);

        return f;
    }

    public boolean pre() {
        return preH() && preP();
    }

    public boolean preH() {
        if (getPrioQueue(0) != null)
            return false;

        Set<List> visitedPQ = new HashSet<>();
        Set<Job> visitedJobs = new HashSet<>();
        for (int i = 1; i <= MAXPRIO; i++) {
            if (!checkQueue(getPrioQueue(i), visitedPQ, visitedJobs))
                return false;
        }

        if (!checkQueue(blockQueue, visitedPQ, visitedJobs))
            return false;
        return numProcesses == visitedJobs.size();
    }

    boolean checkQueue(List queue, Set<List> visitedPQ, Set<Job> visitedJobs) {
        if (queue == null || !visitedPQ.add(queue))
            return false;
        if (!isDoublyLinkedList(queue, visitedJobs))
            return false;
        return true;
    }

    public boolean preP() {
        if (curProc != null && (curProc.priority < 1 || curProc.priority > MAXPRIO))
            return false;

        for (int i = 1; i <= MAXPRIO; i++)
            if (!checkPriorities(getPrioQueue(i), i))
                return false;

        return checkPrioritiesBlockQueue();
    }

    private boolean isDoublyLinkedList(List list, Set<Job> visited) {
        Job current = list.getFirst();
        Job last = list.getLast();

        if (current == null)
            return last == null;
        if (!visited.add(current))
            return false;
        if (last == null)
            return current == null;
        if (current.getPrev() != null || last.getNext() != null)
            return false;

        Job next = current.getNext();
        while (next != null) {
            if (next.getPrev() != current)
                return false;
            if (!visited.add(next))
                return false;
            current = next;
            next = next.getNext();
        }

        return last == current;
    }

    private boolean checkPriorities(List prioQueue, int priority) {
        Job current = prioQueue.getFirst();
        int size = 0;

        while (current != null) {
            if (current.priority != priority)
                return false;
            size++;
            current = current.getNext();
        }
        return size == prioQueue.getMemCount();
    }

    private boolean checkPrioritiesBlockQueue() {
        Job current = blockQueue.getFirst();
        int size = 0;

        while (current != null) {
            if (current.priority < 1 || current.priority > MAXPRIO)
                return false;
            size++;
            current = current.getNext();
        }
        return size == blockQueue.getMemCount();
    }

    // Hardcoded methods that SPF need to intercept to implement PLI

    public static void runPrePConcreteHeap() {
        Schedule toBuild = new Schedule();
        SymHeap.buildSolutionHeap(toBuild);
        SymHeap.handleRepOKResult(toBuild, toBuild.preP());
    }

    public static void runPrePPartialHeap() {
        Schedule toBuild = new Schedule();
        SymHeap.buildPartialHeapInput(toBuild);
        SymHeap.handlePrePWithSymbolicHeapResult(toBuild, toBuild.preP());
    }

    public static void runCompleteSpecification() {
        Schedule toBuild = new Schedule();
        SymHeap.buildPartialHeapInput(toBuild);
        SymHeap.handleRepOKResult(toBuild, toBuild.pre());
    }

    // optimized repOKs

    // public boolean preH() {
    // if (prio_0 != null || prio_1 == null || prio_2 == null || prio_3 == null ||
    // blockQueue == null)
    // return false;
    //
    // HashSet<List> visitedPQ = new HashSet<List>();
    // for (int i = 1; i <= MAXPRIO; i++) {
    // if (!visitedPQ.add(getPrioQueue(i)))
    // return false;
    // }
    // if (!visitedPQ.add(blockQueue))
    // return false;
    //
    // Set<Job> visitedJobs = new HashSet<Job>();
    // for (int i = 1; i <= MAXPRIO; i++) {
    // if (!isDoublyLinkedList(getPrioQueue(i), visitedJobs))
    // return false;
    // }
    //
    // if (!isDoublyLinkedList(blockQueue, visitedJobs))
    // return false;
    //
    // return numProcesses == visitedJobs.size();
    // }
    //
    // public boolean preP() {
    // if (curProc != null && (curProc.priority < 1 || curProc.priority > MAXPRIO))
    // return false;
    //
    // for (int i = 1; i <= MAXPRIO; i++)
    // if (!isPriorityQueueOK(getPrioQueue(i), i))
    // return false;
    //
    // if (!checkBlockQueue())
    // return false;
    // return true;
    // }
}
