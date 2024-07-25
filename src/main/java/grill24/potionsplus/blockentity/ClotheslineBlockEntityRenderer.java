package grill24.potionsplus.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import grill24.potionsplus.block.ClotheslineBlock;
import grill24.potionsplus.render.LeashRenderer;
import grill24.potionsplus.utility.ClientTickHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class ClotheslineBlockEntityRenderer implements BlockEntityRenderer<ClotheslineBlockEntity> {
    public final BlockRenderDispatcher blockRenderDispatcher;
    private ProfilerFiller profiler;

    private static final Vector3f OFFSET_IN_POST_BLOCKS = new Vector3f(0.5f, 0.9375f, 0.5f);
    private static final Vector3f ITEM_OFFSET = new Vector3f(0, -0.2f, 0);

    public ClotheslineBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        blockRenderDispatcher = context.getBlockRenderDispatcher();
        profiler = Minecraft.getInstance().getProfiler();
    }

    @Override
    public void render(ClotheslineBlockEntity blockEntity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        profiler.push("clothesline_render");

        if(blockEntity.getLevel() != null) {
            BlockPos left = ClotheslineBlock.getLeftEnd(blockEntity.getBlockPos(), blockEntity.getBlockState());
            BlockPos right = ClotheslineBlock.getOtherEnd(left, blockEntity.getLevel().getBlockState(left));

            // Render the "clothesline" (repurposed lead rendering code from vanilla) between the two posts
            Level level = blockEntity.getLevel();
            int blockLightStart = 0, blockLightEnd = 0, skyLightStart = 0, skyLightEnd = 0;
            if (level != null) {
                blockLightStart = level.getBrightness(LightLayer.BLOCK, left);
                blockLightEnd = level.getBrightness(LightLayer.BLOCK, right);
                skyLightStart = level.getBrightness(LightLayer.SKY, left);
                skyLightEnd = level.getBrightness(LightLayer.SKY, right);
            }

            Vector3f[] leashPoints = LeashRenderer.renderLeashBetweenPoints(blockEntity.getBlockPos(),
                    Vec3.atLowerCornerOf(left).add(OFFSET_IN_POST_BLOCKS.x(), OFFSET_IN_POST_BLOCKS.y(), OFFSET_IN_POST_BLOCKS.z()),
                    Vec3.atLowerCornerOf(right).add(OFFSET_IN_POST_BLOCKS.x(), OFFSET_IN_POST_BLOCKS.y(), OFFSET_IN_POST_BLOCKS.z()),
                    matrices, vertexConsumers, blockLightStart, blockLightEnd, skyLightStart, skyLightEnd);

            // Render items on the clothesline
            float leashPointsPerItemRendered = (float) leashPoints.length / (blockEntity.getItemHandler().getContainerSize() + 1);
            for(int i = 0; i < blockEntity.getItemHandler().getContainerSize(); i++) {
                ItemStack stack = blockEntity.getItemHandler().getItem(i);
                if(!stack.isEmpty()) {
                    matrices.pushPose();
                    Vector3f position = leashPoints[(int) (leashPointsPerItemRendered * (i+1))];
                    position.add(OFFSET_IN_POST_BLOCKS);

                    // If this is the right end, adjust the position to the left end
                    if(!ClotheslineBlock.isLeftEnd(blockEntity.getBlockState())) {
                        BlockPos difference = ClotheslineBlock.getOtherEnd(blockEntity.getBlockPos(), blockEntity.getBlockState()).subtract(blockEntity.getBlockPos());
                        position.add(difference.getX(), difference.getY(), difference.getZ());
                    }

                    // Render the item
                    matrices.translate(position.x(), position.y(), position.z());
                    matrices.scale(0.5f, 0.5f, 0.5f);
                    // Swing the item a bit :)
                    matrices.mulPose(orientItemToClotheslineOrientation(blockEntity.getBlockState()));
                    matrices.mulPose(Vector3f.XP.rotationDegrees((float) (Math.sin(ClientTickHandler.total() / 10 + i * 7) * 10)));
                    matrices.translate(ITEM_OFFSET.x(), ITEM_OFFSET.y(), ITEM_OFFSET.z());
                    Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemTransforms.TransformType.FIXED,
                            light, overlay, matrices, vertexConsumers, 0);
                    matrices.popPose();
                }
            }
        }

        profiler.pop();
    }

    private Quaternion orientItemToClotheslineOrientation(BlockState clothesLine) {
        Direction property = clothesLine.getValue(ClotheslineBlock.FACING);
        if(property.getAxis() == Direction.Axis.X) {
            return Vector3f.YP.rotationDegrees(90);
        }
        return Quaternion.ONE;
    }
}
