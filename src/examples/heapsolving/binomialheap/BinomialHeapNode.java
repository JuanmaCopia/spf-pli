
package heapsolving.binomialheap;

import java.util.Set;

public class BinomialHeapNode {

    public int key; // element in current node

    public int degree; // depth of the binomial tree having the current node as its root

    public BinomialHeapNode parent; // pointer to the parent of the current node

    public BinomialHeapNode sibling; // pointer to the next binomial tree in the list

    public BinomialHeapNode child; // pointer to the first child of the current node

    public BinomialHeapNode(int k) {
        // public BinomialHeapNode(Integer k) {
        key = k;
        degree = 0;
        parent = null;
        sibling = null;
        child = null;
    }

    public int getKey() { // returns the element in the current node
        return key;
    }

    public int getDegree() { // returns the degree of the current node
        return degree;
    }

    public BinomialHeapNode getParent() { // returns the father of the current node
        return parent;
    }

    public BinomialHeapNode getSibling() { // returns the next binomial tree in the list
        return sibling;
    }

    public BinomialHeapNode getChild() { // returns the first child of the current node
        return child;
    }

    public int getSize() {
        return (1 + ((child == null) ? 0 : child.getSize()) + ((sibling == null) ? 0 : sibling.getSize()));
    }

    /* Function reverse */
    public BinomialHeapNode reverse(BinomialHeapNode sibl) {
        BinomialHeapNode ret;
        if (sibling != null)
            ret = sibling.reverse(this);
        else
            ret = this;
        sibling = sibl;
        return ret;
    }

    BinomialHeapNode findMinNode() {
        BinomialHeapNode x = this, y = this;
        int min = x.key;

        while (x != null) {
            if (x.key < min) {
                y = x;
                min = x.key;
            }
            x = x.sibling;
        }

        return y;
    }

    // Find a node with the given key
    BinomialHeapNode findANodeWithKey(int value) {
        BinomialHeapNode temp = this, node = null;
        while (temp != null) {
            if (temp.key == value) {
                node = temp;
                return node;
            }
            if (temp.child == null)
                temp = temp.sibling;
            else {
                node = temp.child.findANodeWithKey(value);
                if (node == null)
                    temp = temp.sibling;
                else
                    return node;
            }
        }

        return node;
    }

    public boolean checkDegree(int degree) {
        if (this.degree != degree)
            return false;
        for (BinomialHeapNode current = this.child; current != null; current = current.sibling) {
            degree--;
            if (current.degree != degree)
                return false;
            if (!current.checkDegree(degree))
                return false;
        }
        return (degree == 0);
    }

//    public boolean checkDegreeShape(int degree) {
//        if (degree < 0)
//            return false;
//        if (degree == 0) {
//            return child == null;
//        }
//
//        for (BinomialHeapNode current = this.child; current != null; current = current.sibling) {
//            degree--;
//            if (degree < 0)
//                return false;
//            if (!current.checkDegreeShape(degree))
//                return false;
//        }
//        return (degree == 0);
//    }

    public boolean isHeapified() {
        for (BinomialHeapNode current = this.child; current != null; current = current.sibling) {
            if (!(key <= current.key))
                return false;
            if (!current.isHeapified())
                return false;
        }
        return true;
    }

    public boolean isTree(Set<BinomialHeapNode> visited, BinomialHeapNode parent) {
        if (this.parent != parent)
            return false;
        for (BinomialHeapNode current = this.child; current != null; current = current.sibling) {
            if (!visited.add(current))
                return false;
            if (!current.isTree(visited, this))
                return false;
        }
        return true;
    }

    public String btToString(String indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%skey == %d \n", indent, key));
        sb.append(String.format("%sdegree == %d \n", indent, degree));
        if (parent == null)
            sb.append(indent + "parent == null \n");
        else
            sb.append(indent + "parent != null \n");
        int childNum = 0;
        for (BinomialHeapNode current = this.child; current != null; current = current.sibling) {
            sb.append(String.format("%s ----- ChildNode%d  -----\n", indent, childNum));
            sb.append(current.btToString(indent + "  "));
        }
        return sb.toString();
    }

}