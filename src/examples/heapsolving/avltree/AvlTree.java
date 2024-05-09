package heapsolving.avltree;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import korat.finitization.IFinitization;
import korat.finitization.IObjSet;
import korat.finitization.impl.FinitizationFactory;
import pli.SymHeap;

// AvlTree class
//
// CONSTRUCTION: with no initializer
//
// ******************PUBLIC OPERATIONS*********************
// void insert( x )       --> Insert x
// void remove( x )       --> Remove x
// boolean contains( x )  --> Return true if x is present
// boolean remove( x )    --> Return true if x was present
// Comparable findMin( )  --> Return smallest item
// Comparable findMax( )  --> Return largest item
// boolean isEmpty( )     --> Return true if empty; else false
// void makeEmpty( )      --> Remove all items
// void printTree( )      --> Print tree in sorted order
// ******************ERRORS********************************
// Throws UnderflowException as appropriate

/**
 * Implements an AVL tree. Note that all "matching" is based on the compareTo
 * method.
 *
 * @author Mark Allen Weiss
 */
public class AvlTree {
    /**
     * Construct the tree.
     */
    public AvlTree() {
        root = null;
    }

    /**
     * Insert into the tree; duplicates are ignored.
     *
     * @param x the item to insert.
     */
    public void insert(int x) {
        root = insert(x, root);
    }

    /**
     * Remove from the tree. Nothing is done if x is not found.
     *
     * @param x the item to remove.
     */
    public void remove(int x) {
        root = remove(x, root);
    }

    /**
     * Internal method to remove from a subtree.
     *
     * @param x the item to remove.
     * @param t the node that roots the subtree.
     * @return the new root of the subtree.
     */
    private AvlNode remove(int x, AvlNode t) {
        if (t == null)
            return t; // Item not found; do nothing

        if (x < t.element)
            t.left = remove(x, t.left);
        else if (x > t.element)
            t.right = remove(x, t.right);
        else if (t.left != null && t.right != null) // Two children
        {
            t.element = findMin(t.right).element;
            t.right = remove(t.element, t.right);
        } else
            t = (t.left != null) ? t.left : t.right;
        return balance(t);
    }

    /**
     * Find the smallest item in the tree.
     *
     * @return smallest item or null if empty.
     */
    public int findMin() {
        if (isEmpty())
            throw new UnderflowException();
        return findMin(root).element;
    }

    /**
     * Find the largest item in the tree.
     *
     * @return the largest item of null if empty.
     */
    public int findMax() {
        if (isEmpty())
            throw new UnderflowException();
        return findMax(root).element;
    }

    /**
     * Find an item in the tree.
     *
     * @param x the item to search for.
     * @return true if x is found.
     */
    public boolean contains(int x) {
        return contains(x, root);
    }

    /**
     * Make the tree logically empty.
     */
    public void makeEmpty() {
        root = null;
    }

    /**
     * Test if the tree is logically empty.
     *
     * @return true if empty, false otherwise.
     */
    public boolean isEmpty() {
        return root == null;
    }

    /**
     * Print the tree contents in sorted order.
     */
    public void printTree() {
        if (isEmpty())
            System.out.println("Empty tree");
        else
            printTree(root);
    }

    private static final int ALLOWED_IMBALANCE = 1;

    // Assume t is either balanced or within one of being balanced
    private AvlNode balance(AvlNode t) {
        if (t == null)
            return t;

        if (height(t.left) - height(t.right) > ALLOWED_IMBALANCE)
            if (height(t.left.left) >= height(t.left.right))
                t = rotateWithLeftChild(t);
            else
                t = doubleWithLeftChild(t);
        else if (height(t.right) - height(t.left) > ALLOWED_IMBALANCE)
            if (height(t.right.right) >= height(t.right.left))
                t = rotateWithRightChild(t);
            else
                t = doubleWithRightChild(t);

        t.height = Math.max(height(t.left), height(t.right)) + 1;
        return t;
    }

    public void checkBalance() {
        checkBalance(root);
    }

    private int checkBalance(AvlNode t) {
        if (t == null)
            return -1;

        if (t != null) {
            int hl = checkBalance(t.left);
            int hr = checkBalance(t.right);
            if (Math.abs(height(t.left) - height(t.right)) > 1 || height(t.left) != hl || height(t.right) != hr)
                System.out.println("OOPS!!");
        }

        return height(t);
    }

    /**
     * Internal method to insert into a subtree.
     *
     * @param x the item to insert.
     * @param t the node that roots the subtree.
     * @return the new root of the subtree.
     */
    private AvlNode insert(int x, AvlNode t) {
        if (t == null)
            return new AvlNode(x, null, null);

        if (x < t.element)
            t.left = insert(x, t.left);
        else if (x > t.element)
            t.right = insert(x, t.right);
        else
            ; // Duplicate; do nothing
        return balance(t);
    }

    /**
     * Internal method to find the smallest item in a subtree.
     *
     * @param t the node that roots the tree.
     * @return node containing the smallest item.
     */
    private AvlNode findMin(AvlNode t) {
        if (t == null)
            return t;

        while (t.left != null)
            t = t.left;
        return t;
    }

    /**
     * Internal method to find the largest item in a subtree.
     *
     * @param t the node that roots the tree.
     * @return node containing the largest item.
     */
    private AvlNode findMax(AvlNode t) {
        if (t == null)
            return t;

        while (t.right != null)
            t = t.right;
        return t;
    }

    /**
     * Internal method to find an item in a subtree.
     *
     * @param x is item to search for.
     * @param t the node that roots the tree.
     * @return true if x is found in subtree.
     */
    private boolean contains(int x, AvlNode t) {
        while (t != null) {

            if (x < t.element)
                t = t.left;
            else if (x > t.element)
                t = t.right;
            else
                return true; // Match
        }

        return false; // No match
    }

    /**
     * Internal method to print a subtree in sorted order.
     *
     * @param t the node that roots the tree.
     */
    private void printTree(AvlNode t) {
        if (t != null) {
            printTree(t.left);
            System.out.println(t.element);
            printTree(t.right);
        }
    }

    /**
     * Return the height of node t, or -1, if null.
     */
    private int height(AvlNode t) {
        return t == null ? -1 : t.height;
    }

    /**
     * Rotate binary tree node with left child. For AVL trees, this is a single
     * rotation for case 1. Update heights, then return new root.
     */
    private AvlNode rotateWithLeftChild(AvlNode k2) {
        AvlNode k1 = k2.left;
        k2.left = k1.right;
        k1.right = k2;
        k2.height = Math.max(height(k2.left), height(k2.right)) + 1;
        k1.height = Math.max(height(k1.left), k2.height) + 1;
        return k1;
    }

    /**
     * Rotate binary tree node with right child. For AVL trees, this is a single
     * rotation for case 4. Update heights, then return new root.
     */
    private AvlNode rotateWithRightChild(AvlNode k1) {
        AvlNode k2 = k1.right;
        k1.right = k2.left;
        k2.left = k1;
        k1.height = Math.max(height(k1.left), height(k1.right)) + 1;
        k2.height = Math.max(height(k2.right), k1.height) + 1;
        return k2;
    }

    /**
     * Double rotate binary tree node: first left child with its right child; then
     * node k3 with new left child. For AVL trees, this is a double rotation for
     * case 2. Update heights, then return new root.
     */
    private AvlNode doubleWithLeftChild(AvlNode k3) {
        k3.left = rotateWithRightChild(k3.left);
        return rotateWithLeftChild(k3);
    }

    /**
     * Double rotate binary tree node: first right child with its left child; then
     * node k1 with new right child. For AVL trees, this is a double rotation for
     * case 3. Update heights, then return new root.
     */
    private AvlNode doubleWithRightChild(AvlNode k1) {
        k1.right = rotateWithLeftChild(k1.right);
        return rotateWithRightChild(k1);
    }

    public static class AvlNode {
        public AvlNode() {

        }

        // Constructors
        public AvlNode(int theElement) {
            this(theElement, null, null);
        }

        public AvlNode(int theElement, AvlNode lt, AvlNode rt) {
            element = theElement;
            left = lt;
            right = rt;
            height = 0;
        }

        public int element; // The data in the node
        public AvlNode left; // Left child
        public AvlNode right; // Right child
        public int height; // Height
    }

    /** The tree root. */
    public AvlNode root;

    // // Test program
    // public static void main(String[] args) {
    // AvlTree<Integer> t = new AvlTree<>();
    // final int SMALL = 40;
    // final int NUMS = 1000000; // must be even
    // final int GAP = 37;
    //
    // System.out.println("Checking... (no more output means success)");
    //
    // for (int i = GAP; i != 0; i = (i + GAP) % NUMS) {
    // // System.out.println( "INSERT: " + i );
    // t.insert(i);
    // if (NUMS < SMALL)
    // t.checkBalance();
    // }
    //
    // for (int i = 1; i < NUMS; i += 2) {
    // // System.out.println( "REMOVE: " + i );
    // t.remove(i);
    // if (NUMS < SMALL)
    // t.checkBalance();
    // }
    // if (NUMS < SMALL)
    // t.printTree();
    // if (t.findMin() != 2 || t.findMax() != NUMS - 2)
    // System.out.println("FindMin or FindMax error!");
    //
    // for (int i = 2; i < NUMS; i += 2)
    // if (!t.contains(i))
    // System.out.println("Find error1!");
    //
    // for (int i = 1; i < NUMS; i += 2) {
    // if (t.contains(i))
    // System.out.println("Find error2!");
    // }
    // }

    public boolean preH() {
        if (!isBinTreeWithParentReferences())
            return false;
        if (!isBalanced())
            return false;
        if (!heightsOK(root))
            return false;
        return true;
    }

    public boolean preP() {

        if (!isSorted())
            return false;
        return true;
    }

    public boolean pre() {
        return preH() && preP();
    }

    public boolean isBinTreeWithParentReferences() {
        if (root == null)
            return true;
        Set<AvlNode> visited = new HashSet<AvlNode>();
        LinkedList<AvlNode> worklist = new LinkedList<AvlNode>();
        visited.add(root);
        worklist.add(root);

        while (!worklist.isEmpty()) {
            AvlNode node = worklist.removeFirst();
            AvlNode left = node.left;
            if (left != null) {
                if (!visited.add(left))
                    return false;
                worklist.add(left);
            }
            AvlNode right = node.right;
            if (right != null) {
                if (!visited.add(right))
                    return false;
                worklist.add(right);
            }
        }
        return true;
    }

    private boolean isBalanced() {
        if (root == null)
            return true;
        return isBalanced(root) >= 0;
    }

    private int isBalanced(AvlNode node) {
        if (node == null)
            return -1;
        int lh = isBalanced(node.left);
        if (lh == Integer.MIN_VALUE)
            return Integer.MIN_VALUE;
        int rh = isBalanced(node.right);
        if (rh == Integer.MIN_VALUE)
            return Integer.MIN_VALUE;

        int abs = lh - rh;
        if (abs < 0)
            abs = abs * -1;

        if (abs > 1)
            return Integer.MIN_VALUE;
        else
            return max(lh, rh) + 1;
    }

    int calculateHeight(AvlNode node) {
        if (node == null)
            return -1;
        return 1 + max(calculateHeight(node.left), calculateHeight(node.right));
    }

    boolean heightsOK(AvlNode node) {
        if (node == null)
            return true;
        if (!heightsOK(node.left))
            return false;
        if (!heightsOK(node.right))
            return false;
        return node.height == calculateHeight(node);
    }

    private int max(int a, int b) {
        if (a > b)
            return a;
        return b;
    }

    private boolean isSorted() {
        if (root == null)
            return true;
        return isSorted(root, null, null);
    }

    private boolean isSorted(AvlNode n, Integer min, Integer max) {
        if ((min != null && n.element <= (min)) || (max != null && n.element >= (max)))
            return false;

        if (n.left != null)
            if (!isSorted(n.left, min, n.element))
                return false;
        if (n.right != null)
            if (!isSorted(n.right, n.element, max))
                return false;
        return true;
    }

    public static void runPrePConcreteHeap() {
        AvlTree toBuild = new AvlTree();
        SymHeap.buildSolutionHeap(toBuild);
        SymHeap.handleRepOKResult(toBuild, toBuild.preP());
    }

    public static void runPrePPartialHeap() {
        AvlTree toBuild = new AvlTree();
        SymHeap.buildPartialHeapInput(toBuild);
        SymHeap.handlePrePWithSymbolicHeapResult(toBuild, toBuild.preP());
    }

    public static void runCompleteSpecification() {
        AvlTree toBuild = new AvlTree();
        SymHeap.buildPartialHeapInput(toBuild);
        SymHeap.handleRepOKResult(toBuild, toBuild.pre());
    }

    public static IFinitization finAvlTree(int size) {
        IFinitization f = FinitizationFactory.create(AvlTree.class);
        IObjSet nodes = f.createObjSet(AvlNode.class, size, true);
        f.set(AvlTree.class, "root", nodes);
        f.set(AvlNode.class, "element", f.createIntSet(0, size - 1));
        f.set(AvlNode.class, "left", nodes);
        f.set(AvlNode.class, "right", nodes);
        f.set(AvlNode.class, "height", f.createIntSet(0, size));
        return f;
    }

}
