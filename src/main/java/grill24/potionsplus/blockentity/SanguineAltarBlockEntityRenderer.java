package grill24.potionsplus.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import grill24.potionsplus.core.Recipes;
import grill24.potionsplus.core.seededrecipe.PpIngredients;
import grill24.potionsplus.core.seededrecipe.TreeNode;
import grill24.potionsplus.utility.RUtil;
import grill24.potionsplus.utility.TreeLayout;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.Queue;

public class SanguineAltarBlockEntityRenderer implements BlockEntityRenderer<SanguineAltarBlockEntity> {
    private final BlockRenderDispatcher blockRenderDispatcher;

    public SanguineAltarBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        blockRenderDispatcher = context.getBlockRenderDispatcher();
    }

    @Override
    public void render(@NotNull SanguineAltarBlockEntity blockEntity, float tickDelta, @NotNull PoseStack matrices, @NotNull MultiBufferSource vertexConsumers, int light, int overlay) {
        ItemStack stack = blockEntity.getItem(0);
        if (stack.isEmpty()) {
            return;
        }

        RUtil.renderInputItemAnimation(stack, 0, 1.25F, 0, 20, true, tickDelta, blockEntity, matrices, vertexConsumers, light, overlay);

        float spin = RUtil.doSpin(blockEntity, blockEntity.getNextSpinTickDelay(), blockEntity.getNextSpinHertz(), blockEntity.getNextSpinTotalRevolutions());
        RUtil.renderBobbingItem(stack, blockEntity.getRestingPosition(), spin, 1.25F, 0.25f, 0.2F, 20, tickDelta, blockEntity, matrices, vertexConsumers, light, overlay);


        if (!stack.isEmpty()) {
            TreeNode<ItemStack> tree = Recipes.seededPotionRecipes.getItemStackTree(new PpIngredients(stack));
            if (tree != null) {
                TreeNode<TreeLayout.TreeLayoutNode> layout = TreeLayout.layout(tree);

                // Variables to keep track of depth and breadth index
                int breadthIndex = 0;
                int lastDepthIndex = 0; // Keep track of the last depth index processed

                Queue<Pair<TreeNode<TreeLayout.TreeLayoutNode>, Integer>> queue = new LinkedList<>();
                queue.add(Pair.of(layout, 0));


                while (!queue.isEmpty()) {
                    Pair<TreeNode<TreeLayout.TreeLayoutNode>, Integer> pair = queue.poll();
                    TreeNode<TreeLayout.TreeLayoutNode> node = pair.getFirst();
                    int depthIndex = pair.getSecond();

                    // Reset breadthIndex if we're at a new depth level
                    if (depthIndex != lastDepthIndex) {
                        breadthIndex = 0;
                        lastDepthIndex = depthIndex;
                    }

                    matrices.pushPose();
                    matrices.translate(0.5, 1.75, 0.5);
                    matrices.scale(0.5F, 0.5F, 0.5F);
//                    matrices.translate((double) (breadthIndex) / 2, (double) depthIndex / 2, 0);
                    matrices.translate(node.getData().x * 0.5, node.getData().y * 0.5, 0);
                    Minecraft.getInstance().getItemRenderer().renderStatic(node.getData().itemStack, ItemTransforms.TransformType.GROUND, light, overlay, matrices, vertexConsumers, 0);
                    matrices.popPose();

                    for (TreeNode<TreeLayout.TreeLayoutNode> child : node.getChildren()) {
                        queue.add(Pair.of(child, depthIndex + 1));
                    }

                    breadthIndex++; // Increment breadthIndex after processing a node
                }
            }
        }
    }

    private static int getWidthOfNode(TreeNode<ItemStack> t, int numSiblings) {

        int width = 1;
        if (t.getChildren().isEmpty())
            return width;

        for (TreeNode<ItemStack> child : t.getChildren()) {
            width += getWidthOfNode(child, t.getChildren().size());
        }
        return width;
    }
}
