package grill24.potionsplus.utility;

import grill24.potionsplus.core.seededrecipe.TreeNode;
import net.minecraft.world.item.ItemStack;

import java.util.LinkedList;
import java.util.Queue;

public class TreeLayout {
    public static class TreeLayoutNode {
        public ItemStack itemStack;
        public int x;
        public int y;
    }

    private static final int NODE_WIDTH = 1; // horizontal distance between nodes
    private static final int NODE_HEIGHT = 1; // vertical distance between nodes

    public static <T> TreeNode<TreeLayoutNode> getLayoutNode(TreeNode<T> node) {
        TreeNode<TreeLayoutNode> layoutNode = new TreeNode<>(new TreeLayoutNode());
        layoutNode.getData().x = 0;
        layoutNode.getData().y = 0;
        layoutNode.getData().itemStack = (ItemStack) node.getData();
        for (TreeNode<T> child : node.getChildren()) {
            TreeNode<TreeLayoutNode> layoutChild = getLayoutNode(child);
            layoutNode.addChild(layoutChild);
        }
        return layoutNode;
    }

    public static TreeNode<TreeLayoutNode> layout(TreeNode<ItemStack> root) {
        TreeNode<TreeLayoutNode> layoutRoot = getLayoutNode(root);
        layoutInternal(layoutRoot);
        return layoutRoot;
    }

    private static int calculateSubtreeWidth(TreeNode<TreeLayoutNode> node) {
        if (node.getChildren().isEmpty()) {
            return NODE_WIDTH;
        }

        int width = 0;
        for (TreeNode<TreeLayoutNode> child : node.getChildren()) {
            width += calculateSubtreeWidth(child);
        }

        return Math.max(width, NODE_WIDTH);
    }

    private static void assignXPositions(TreeNode<TreeLayoutNode> node, int currentX) {
        if (node == null) return;

        int subtreeWidth = calculateSubtreeWidth(node);
        int startX = currentX - subtreeWidth / 2;

        for (TreeNode<TreeLayoutNode> child : node.getChildren()) {
            int childWidth = calculateSubtreeWidth(child);
            int childX = startX + childWidth / 2;
            child.getData().x = childX;
            assignXPositions(child, childX);
            startX += childWidth;
        }
    }

    private static void layoutInternal(TreeNode<TreeLayoutNode> root) {
        if (root == null) return;

        root.getData().x = 0;
        root.getData().y = 0;
        assignXPositions(root, 0);

        Queue<TreeNode<TreeLayoutNode>> queue = new LinkedList<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            TreeNode<TreeLayoutNode> node = queue.poll();
            if (node != null) {
                int depth = node.getData().y / NODE_HEIGHT;
                node.getData().y = depth * NODE_HEIGHT;
                for (TreeNode<TreeLayoutNode> child : node.getChildren()) {
                    child.getData().y = node.getData().y + NODE_HEIGHT;
                    queue.add(child);
                }
            }
        }
    }

    public void printTree(TreeNode<TreeLayoutNode> root) {
        Queue<TreeNode<TreeLayoutNode>> queue = new LinkedList<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            TreeNode<TreeLayoutNode> node = queue.poll();
            if (node != null) {
                System.out.printf("Node %d: (x=%d, y=%d)%n", node.getData().itemStack.hashCode(), node.getData().x, node.getData().y);
                queue.addAll(node.getChildren());
            }
        }
    }
}

