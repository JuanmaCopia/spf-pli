package heapsolving.treemap;

public class ChatGPTPredicate {

    public static boolean predicate(TreeMap _this) {
        // Check size consistency
        if (_this.size < 0) {
            return false;
        }

        // If the root is null, the size must be 0
        if (_this.root == null) {
            return _this.size == 0;
        }

        // Ensure the _this.root is black
        if (_this.root.color != TreeMap.BLACK) {
            return false;
        }

        // Check for tree integrity using a recursive helper
        if (!isBST(_this.root, Integer.MIN_VALUE, Integer.MAX_VALUE)) {
            return false;
        }

        // Check Red-Black properties
        if (!isValidRedBlackTree(_this.root)) {
            return false;
        }

        // Check that the _this.size matches the actual number of nodes
        if (_this.size != countNodes(_this.root)) {
            return false;
        }

        return true;
    }

    private static boolean isBST(TreeMap.Entry node, int min, int max) {
        if (node == null) {
            return true;
        }

        // Check the BST property
        if (node.key <= min || node.key >= max) {
            return false;
        }

        // Check left and right subtrees
        return isBST(node.left, min, node.key) && isBST(node.right, node.key, max);
    }

    private static boolean isValidRedBlackTree(TreeMap.Entry node) {
        if (node == null) {
            return true; // Null nodes are black
        }

        // Red-Black Tree property: Red nodes cannot have red children
        if (node.color == TreeMap.RED) {
            if ((node.left != null && node.left.color == TreeMap.RED)
                    || (node.right != null && node.right.color == TreeMap.RED)) {
                return false;
            }
        }

        // Check Black Height consistency
        int leftBlackHeight = getBlackHeight(node.left);
        int rightBlackHeight = getBlackHeight(node.right);
        if (leftBlackHeight != rightBlackHeight) {
            return false;
        }

        // Recursively check subtrees
        return isValidRedBlackTree(node.left) && isValidRedBlackTree(node.right);
    }

    private static int getBlackHeight(TreeMap.Entry node) {
        if (node == null) {
            return 1; // Null nodes contribute a black height of 1
        }

        int leftBlackHeight = getBlackHeight(node.left);
        int rightBlackHeight = getBlackHeight(node.right);

        // If the black height is inconsistent, propagate an error
        if (leftBlackHeight != rightBlackHeight) {
            throw new IllegalStateException("Inconsistent black height");
        }

        // Add 1 if the current node is black
        return leftBlackHeight + (node.color == TreeMap.BLACK ? 1 : 0);
    }

    private static int countNodes(TreeMap.Entry node) {
        if (node == null) {
            return 0;
        }

        return 1 + countNodes(node.left) + countNodes(node.right);
    }

}
