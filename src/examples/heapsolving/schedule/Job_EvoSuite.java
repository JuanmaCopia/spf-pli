package heapsolving.schedule;

public class Job_EvoSuite {
    protected Job_EvoSuite next;

    protected Job_EvoSuite prev;

    protected int val;

    protected int priority;

    public Job_EvoSuite(int newNum) {
        next = null;
        prev = null;
        val = newNum;
    }

    public Job_EvoSuite() {
    }

    public Job_EvoSuite getNext() {
        return next;
    }

    public Job_EvoSuite getPrev() {
        return prev;
    }

    public void setNext(Job_EvoSuite newNext) {
        next = newNext;
    }

    public void setPrev(Job_EvoSuite newPrev) {
        prev = newPrev;
    }

    public int getVal() {
        return val;
    }

    public int getPriority() {
        return priority;
    }

    public void setVal(int newVal) {
        val = newVal;
    }

    public void setPriority(int newPriority) {
        priority = newPriority;
    }

    public String toString() {
        final StringBuilder buf = new StringBuilder(1);
        // buf.append(this.val);
        // buf.append(",");
        buf.append(this.priority);
        return buf.toString();
    }

}