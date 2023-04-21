package heapsolving.schedule;

import java.util.HashSet;
import java.util.Set;

public class Schedule_EvoSuite {

    private final static int MAXPRIO = 3;

    private int allocProcNum;
    private int numProcesses;

    private Job_EvoSuite curProc;

    private List_EvoSuite prio_0;
    private List_EvoSuite prio_1;
    private List_EvoSuite prio_2;
    private List_EvoSuite prio_3;

    private List_EvoSuite blockQueue;

    // Init the queues with no processes
    public Schedule_EvoSuite() {
        initialize();
        initPrioQueue(3, 0);
        initPrioQueue(2, 0);
        initPrioQueue(1, 0);
    }

    public Schedule_EvoSuite(int numProc3, int numProc2, int numProc1) {
        initialize();
        initPrioQueue(3, numProc3);
        initPrioQueue(2, numProc2);
        initPrioQueue(1, numProc1);
    }

    private List_EvoSuite appendEle(List_EvoSuite aList, Job_EvoSuite aEle) {
        if (aList == null) {
            aList = new List_EvoSuite();/* make list without compare function */
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

    private Job_EvoSuite findNth(List_EvoSuite fList, int n) {
        Job_EvoSuite fEle;

        if (fList == null) {
            return null;
        }
        fEle = fList.getFirst();
        for (int i = 1; fEle != null && i < n; i++) {
            fEle = fEle.getNext();
        }
        return fEle;
    }

    private List_EvoSuite delEle(List_EvoSuite dList, Job_EvoSuite dEle) {
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
        Job_EvoSuite proc;
        List_EvoSuite srcQueue, destQueue;

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
        Job_EvoSuite proc;
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

    private Job_EvoSuite newProcess(int prio) {
        if (prio < 1 || prio > MAXPRIO)
            throw new IllegalArgumentException();
        Job_EvoSuite proc = new Job_EvoSuite(allocProcNum++);
        proc.setPriority(prio);
        numProcesses++;
        return proc;
    }

    public void addProcess(int prio) {
        if (prio < 1 || prio > MAXPRIO)
            throw new IllegalArgumentException();
        Job_EvoSuite proc;
        proc = newProcess(prio);

        setPrioQueue(prio, appendEle(getPrioQueue(prio), proc));
    }

    public void initPrioQueue(int prio, int numProc) {
        if (prio < 1 || prio > MAXPRIO)
            throw new IllegalArgumentException();
        List_EvoSuite queue;
        Job_EvoSuite proc;

        queue = new List_EvoSuite();
        for (int i = 0; i < numProc; i++) {
            proc = newProcess(prio);
            queue = appendEle(queue, proc);
        }

        setPrioQueue(prio, queue);
    }

    private void setPrioQueue(int prio, List_EvoSuite queue) {
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

    private List_EvoSuite getPrioQueue(int prio) {
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
        blockQueue = new List_EvoSuite();
    }

    public String toString() {
        if (numProcesses == 0) {
            return "Schedule={}";
        } else {
            StringBuffer buf = new StringBuffer();
            buf.append("Schedule = {");
            buf.append(" prioQueue = { ");
            for (int i = 1; i <= MAXPRIO; i++) {
                List_EvoSuite proc = getPrioQueue(i);
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

    public boolean repOKSymSolve() {
        if (prio_0 != null)
            return false;
        if (prio_1 == null)
            return false;
        if (prio_2 == null)
            return false;
        if (prio_3 == null)
            return false;
        if (blockQueue == null)
            return false;

        HashSet<List_EvoSuite> visitedPQ = new HashSet<List_EvoSuite>();
        visitedPQ.add(prio_1);
        if (!visitedPQ.add(prio_2))
            return false;
        if (!visitedPQ.add(prio_3))
            return false;
        if (!visitedPQ.add(blockQueue))
            return false;

        Set<Job_EvoSuite> visitedJobs = new HashSet<Job_EvoSuite>();
        if (!isDoublyLinkedList(prio_1, visitedJobs))
            return false;
        if (!isDoublyLinkedList(prio_2, visitedJobs))
            return false;
        if (!isDoublyLinkedList(prio_3, visitedJobs))
            return false;

        if (!isDoublyLinkedList(blockQueue, visitedJobs))
            return false;

        return numProcesses == visitedJobs.size();
    }

    public boolean repOKSymbolicExecution() {
        if (!checkCurrentProcess())
            return false;
        if (!checkPriorityQueues())
            return false;
        if (!checkBlockQueue())
            return false;
        return true;
    }

    public boolean repOKComplete() {
        return repOKSymSolve() && repOKSymbolicExecution();
    }

    private boolean isDoublyLinkedList(List_EvoSuite list, Set<Job_EvoSuite> visited) {
        Job_EvoSuite current = list.getFirst();
        Job_EvoSuite last = list.getLast();

        if (current == null)
            return last == null;
        if (last == null)
            return current == null;
        if (current.getPrev() != null || last.getNext() != null)
            return false;
        if (!visited.add(current))
            return false;

        Job_EvoSuite next = current.getNext();
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

    private boolean checkPriorityQueues() {
        for (int i = 1; i <= MAXPRIO; i++) {
            List_EvoSuite prioQueue = getPrioQueue(i);
            if (!isPriorityQueueOK(prioQueue, i))
                return false;
        }
        return true;
    }

    private boolean isPriorityQueueOK(List_EvoSuite prioQueue, int priority) {
        Job_EvoSuite current = prioQueue.getFirst();
        int size = 0;

        while (current != null) {
            if (current.priority != priority)
                return false;
            size++;
            current = current.getNext();
        }
        return size == prioQueue.getMemCount();
    }

    private boolean checkBlockQueue() {
        Job_EvoSuite current = blockQueue.getFirst();
        int size = 0;

        while (current != null) {
            if (current.priority < 1 || current.priority > MAXPRIO)
                return false;
            size++;
            current = current.getNext();
        }
        return size == blockQueue.getMemCount();
    }

    private boolean checkCurrentProcess() {
        if (curProc != null) {
            if (curProc.priority < 1 || curProc.priority > MAXPRIO)
                return false;
        }
        return true;
    }

}
