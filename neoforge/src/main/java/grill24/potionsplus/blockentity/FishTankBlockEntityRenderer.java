package grill24.potionsplus.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import grill24.potionsplus.core.DataComponents;
import grill24.potionsplus.utility.ClientTickHandler;
import grill24.potionsplus.utility.RUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Quaternionf;
import org.joml.Random;

import java.util.Optional;

public class FishTankBlockEntityRenderer implements BlockEntityRenderer<FishTankBlockEntity> {
    public final BlockRenderDispatcher blockRenderDispatcher;

    public FishTankBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        blockRenderDispatcher = context.getBlockRenderDispatcher();
    }

    float xRotation = 20F;
    float targetXRotation = 20F;
    float timestampChangeXTarget = 0F;

    float yRotation = 15F;
    float targetYRotation = 15F;
    float timestampChangeYTarget = 0F;

    float yBobOffset = 35F;
    float targetYBobOffset = 35F;
    float timestampChangeYBobTarget = 0F;

    @Override
    public void render(FishTankBlockEntity fishTankBlockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int i, int i1) {
        Optional<ItemStack> fish = fishTankBlockEntity.getFish();
        int kelp = fishTankBlockEntity.getKelp();
        if (fish.isPresent()) {
            renderFish(fishTankBlockEntity, partialTick, poseStack, bufferSource, i, i1, fish);
        }

        renderKelp(poseStack, bufferSource, fishTankBlockEntity, i, i1, kelp);
    }

    private void renderKelp(PoseStack poseStack, MultiBufferSource bufferSource, FishTankBlockEntity fishTankBlockEntity, int i, int i1, int kelp) {
        // Render kelp decorations
        FishTankBlockEntity.Kelp[] randomizedKelp = fishTankBlockEntity.getKelpRenderData();
        for (int k = 0; k < kelp && k < randomizedKelp.length; k++) {
            FishTankBlockEntity.Kelp kelpData = randomizedKelp[k];
            poseStack.pushPose();
            poseStack.translate(kelpData.pos().x(), kelpData.pos().y(), kelpData.pos().z());
            poseStack.scale(kelpData.size(), kelpData.size(), kelpData.size());

            int height = kelpData.height();
            for (int j = 0; j < height; j++) {
                BlockState kelpState = j != height - 1 ? Blocks.KELP_PLANT.defaultBlockState() : Blocks.KELP.defaultBlockState();
                blockRenderDispatcher.renderSingleBlock(
                        kelpState,
                        poseStack,
                        bufferSource,
                        i,
                        i1
                );
                poseStack.translate(0, 1, 0);
            }
            poseStack.popPose();
        }
    }

    private static final float R2 = (float) Math.sqrt(2);

    private void renderFish(FishTankBlockEntity fishTankBlockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int i, int i1, Optional<ItemStack> fish) {
        float scale = 1F;
        if (fish.get().has(DataComponents.FISH_SIZE)) {
            scale = fish.get().get(DataComponents.FISH_SIZE).getItemFrameSizeMultiplier();
            // My fish textures are typically aligned to the diagonal.
            // So we rotate the texture 45 to make the fish aligned in the right direction.
            // Adjust scale so 100cm = 1 block wide
            scale /= R2;
        }

        Level level = fishTankBlockEntity.getLevel();

        if (timestampChangeXTarget == 0F) {
            timestampChangeXTarget = ClientTickHandler.total() + level.getRandom().nextFloat() * 60F;
            targetXRotation = (float) ((Math.random()) * 20F + 10F);
        }
        if (timestampChangeYTarget == 0F) {
            timestampChangeYTarget = ClientTickHandler.total() + level.getRandom().nextFloat() * 60F;
            targetXRotation = (float) ((Math.random()) * 20F + 10F);
        }
        if (timestampChangeYBobTarget == 0F) {
            timestampChangeYBobTarget = ClientTickHandler.total() + level.getRandom().nextFloat() * 60F;
            targetYBobOffset = (float) ((Math.random()) * 10F + 10F);
        }

        yBobOffset = RUtil.lerp(yBobOffset, targetYBobOffset, partialTick);
        xRotation = RUtil.lerp(xRotation, targetXRotation, partialTick);
        yRotation = RUtil.lerp(yRotation, targetYRotation, partialTick);


        poseStack.pushPose();

        float ticks = ClientTickHandler.total() + new Random(fishTankBlockEntity.getBlockPos().hashCode()).nextInt(100);
        float offsetY = (float) (Math.sin(ticks / yBobOffset) * 0.05F);
        poseStack.translate(0.5, 0.5 + offsetY, 0.5);

        poseStack.scale(0.55f, 0.55f, 0.55f);

        float rotX = (float) Math.toRadians(Math.sin(ticks / xRotation) * 10);
        float rotY = (float) Math.toRadians(Math.cos(ticks / yRotation) * 8);
        float rotZ = (float) (Math.toRadians(Math.sin(ticks / 35F) * 3) + Math.toRadians(45));
        poseStack.mulPose(new Quaternionf().rotationXYZ(rotX, rotY, rotZ));
        poseStack.scale(scale, scale, scale);

        Minecraft.getInstance().getItemRenderer().renderStatic(
                fish.get(),
                ItemDisplayContext.FIXED,
                i,
                i1,
                poseStack,
                bufferSource,
                fishTankBlockEntity.getLevel(),
                0
        );
        poseStack.popPose();
    }

}
