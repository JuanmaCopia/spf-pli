package heapsolving.avltree;

import java.util.HashSet;
import java.util.Set;

public class ChatGPTPredicate {

    public static boolean predicate(AvlTree _this) {
        // Check if the root is null (empty tree is valid)
        if (_this.root == null) {
            return true;
        }

        // Ensure there are no duplicate elements (detect aliasing) and validate AVL
        // constraints
        Set<Integer> seenElements = new HashSet<>();
        return validateNode(_this.root, null, null, seenElements);
    }

    private static boolean validateNode(AvlTree.AvlNode node, Integer min, Integer max, Set<Integer> seenElements) {
        if (node == null) {
            return true;
        }

        // Check if the element violates the BST property
        if ((min != null && node.element <= min) || (max != null && node.element >= max)) {
            return false;
        }

        // Check for duplicate elements
        if (!seenElements.add(node.element)) {
            return false;
        }

        // Check if the height is correctly maintained
        int leftHeight = height(node.left);
        int rightHeight = height(node.right);
        if (node.height != Math.max(leftHeight, rightHeight) + 1) {
            return false;
        }

        // Check if the node satisfies the AVL balance condition
        if (Math.abs(leftHeight - rightHeight) > AvlTree.ALLOWED_IMBALANCE) {
            return false;
        }

        // Recursively validate the left and right subtrees
        return validateNode(node.left, min, node.element, seenElements)
                && validateNode(node.right, node.element, max, seenElements);
    }

    private static int height(AvlTree.AvlNode t) {
        return t == null ? -1 : t.height;
    }
}