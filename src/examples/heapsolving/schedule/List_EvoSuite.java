package heapsolving.schedule;

public class List_EvoSuite {

    protected int mem_count;

    protected Job_EvoSuite first;

    protected Job_EvoSuite last;

    public List_EvoSuite() {
        first = null;
        last = null;
        mem_count = 0;
    }

    public Job_EvoSuite getFirst() {
        return first;
    }

    public Job_EvoSuite getLast() {
        return last;
    }

    public void setFirst(Job_EvoSuite newFirst) {
        first = newFirst;
    }

    public void setLast(Job_EvoSuite newLast) {
        last = newLast;
    }

    public int getMemCount() {
        return mem_count;
    }

    public void setMemCount(int newCount) {
        mem_count = newCount;
    }

    public String toString() {
        Job_EvoSuite curr = this.first;
        final StringBuilder buf = new StringBuilder(2 * this.getMemCount());
        buf.append('{');
        for (int i = 0; i < this.getMemCount(); i++) {
            buf.append(curr.toString());
            curr = curr.getNext();
            if (i < this.getMemCount() - 1)
                buf.append(",");
        }
        buf.append('}');
        return buf.toString();
    }
}