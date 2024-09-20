package grill24.potionsplus.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import org.joml.Vector3d;
import org.joml.Vector3f;
import grill24.potionsplus.core.Items;
import grill24.potionsplus.core.seededrecipe.TreeNode;
import grill24.potionsplus.utility.ClientTickHandler;
import grill24.potionsplus.utility.RUtil;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SanguineAltarBlockEntityRenderer implements BlockEntityRenderer<SanguineAltarBlockEntity> {
    private final BlockRenderDispatcher blockRenderDispatcher;
    private static final int CONVERTED_ITEM_DESCENT_TICKS = 20;
    private static final int CONVERTED_ITEM_SHRINK_DELAY_TICKS = 200;

    public SanguineAltarBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        blockRenderDispatcher = context.getBlockRenderDispatcher();
    }

    @Override
    public void render(@NotNull SanguineAltarBlockEntity blockEntity, float tickDelta, @NotNull PoseStack matrices, @NotNull MultiBufferSource vertexConsumers, int light, int overlay) {
        ItemStack stack = blockEntity.state == SanguineAltarBlockEntity.State.CONVERTED ? blockEntity.chainedIngredientToDisplay : blockEntity.getItem(0);
        if (stack.isEmpty()) {
            return;
        }

        final float scale = 0.625F;

        RUtil.renderInputItemAnimation(stack, scale, 0, true, blockEntity, matrices, vertexConsumers, light, overlay);
        switch (blockEntity.state) {
            case CONVERTING -> {
                float yOffset = RUtil.ease(blockEntity, 0, 1F, blockEntity.getInputAnimationDuration(), SanguineAltarBlockEntity.CONVERSION_DURATION_HERTZ, RUtil::easeOutExpo);
                RUtil.renderItemWithYaw(blockEntity, stack, new Vector3d(blockEntity.getRestingPosition().x, blockEntity.getRestingPosition().y + yOffset, blockEntity.getRestingPosition().z), 20, 0, blockEntity.getRestingRotation().y(), scale, matrices, vertexConsumers, light, overlay);

                Vector3f offset = new Vector3f(0, yOffset, 0);
                offset.add((float) blockEntity.getRestingPosition().x, (float) blockEntity.getRestingPosition().y, (float) blockEntity.getRestingPosition().z);
                Vector3f axis = new Vector3f(1f, 0, 0);

                float expansion = RUtil.ease(blockEntity, 0, 1, blockEntity.getInputAnimationDuration(), SanguineAltarBlockEntity.CONVERSION_DURATION_HERTZ, RUtil::easeOutBack);
                float spin = RUtil.ease(blockEntity, 0, 1080, blockEntity.getInputAnimationDuration(), SanguineAltarBlockEntity.CONVERSION_DURATION_HERTZ, RUtil::easeInExpo);
                drawRuneCircle(blockEntity, offset, axis, matrices, vertexConsumers, light, overlay, 0.1f, 0.1F, 0.3f * expansion, 0 + spin);
                drawRuneCircle(blockEntity, offset, axis, matrices, vertexConsumers, light, overlay, 0.2f, 0.08F, 0.33f * expansion, 90 - spin * 2);
                drawRuneCircle(blockEntity, offset, axis, matrices, vertexConsumers, light, overlay, 0.08f, 0.16F, 0.36f * expansion, 45 + spin * 3);
            }
            case CONVERTED -> {
                float spin = RUtil.doSpin(blockEntity, blockEntity.getNextSpinTickDelay(), blockEntity.getNextSpinHertz(), blockEntity.getNextSpinTotalRevolutions()) + blockEntity.getRestingRotation().y();
                float bobbingOffset = RUtil.getBobbingOffset(blockEntity, 0.25f, 0.2F, blockEntity.getInputAnimationDuration() + SanguineAltarBlockEntity.CONVERSION_TICKS + CONVERTED_ITEM_DESCENT_TICKS);

                // Fall back down to the resting position
                float yOffset = RUtil.ease(blockEntity, 1F, 0, blockEntity.getInputAnimationDuration() + SanguineAltarBlockEntity.CONVERSION_TICKS, CONVERTED_ITEM_DESCENT_TICKS / 20F, RUtil::easeOutBack);

                // Linear shrink sloooowly
                float size = RUtil.ease(blockEntity, 1F, 0, blockEntity.getInputAnimationDuration() + SanguineAltarBlockEntity.CONVERSION_TICKS + CONVERTED_ITEM_DESCENT_TICKS + CONVERTED_ITEM_SHRINK_DELAY_TICKS, 0.033F, RUtil::easeInSine);

                Vector3d pos = new Vector3d(blockEntity.getRestingPosition().x, blockEntity.getRestingPosition().y + bobbingOffset + yOffset, blockEntity.getRestingPosition().z);
                RUtil.renderItemWithYaw(blockEntity, stack, pos, 20, 0, spin, scale * size, matrices, vertexConsumers, light, overlay);
            }
        }

    }

    private static void drawRuneCircle(SanguineAltarBlockEntity blockEntity, Vector3f offset, Vector3f axis, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay, float rollHertz, float itemScale, float radius, float spinDegrees) {
        // Get radians of rotation from spinHertz and total ticks (time)

        float healthDrain = blockEntity.getHealthDrainProgress();
        Vector3f[] points = RUtil.distributePointsOnCircle(8, axis, offset, ((float) Math.PI * 2) * ClientTickHandler.total() / 20 * rollHertz, radius, spinDegrees);
        for (int p = 0; p < points.length; p++) {
            if ((float) p / (float) points.length > healthDrain)
                break;

            Vector3f point = points[p];
            // Added 4 runes to the generic icon, so pick a different one for each point.
            ItemStack runeStack = new ItemStack(Items.GENERIC_ICON.get(), 13 + p % 4);
            RUtil.renderItemWithYaw(blockEntity, runeStack, new Vector3d(point.x(), point.y(), point.z()), 20, 0, p * 10, itemScale, matrices, vertexConsumers, light, overlay);
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
