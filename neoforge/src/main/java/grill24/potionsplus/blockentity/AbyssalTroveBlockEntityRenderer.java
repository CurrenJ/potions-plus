package grill24.potionsplus.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.item.ItemDisplayContext;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Vector3d;
import org.joml.Vector3f;
import grill24.potionsplus.utility.RUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class AbyssalTroveBlockEntityRenderer implements BlockEntityRenderer<AbyssalTroveBlockEntity> {
    private final BlockRenderDispatcher blockRenderDispatcher;
    private ProfilerFiller profiler;

    public AbyssalTroveBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        blockRenderDispatcher = context.getBlockRenderDispatcher();
        profiler = Minecraft.getInstance().getProfiler();
    }

    @Override
    public void render(@NotNull AbyssalTroveBlockEntity blockEntity, float tickDelta, @NotNull PoseStack matrices, @NotNull MultiBufferSource vertexConsumers, int light, int overlay) {
        profiler.push("abyssal_trove_render");

        for (Map.Entry<Integer, List<AbyssalTroveBlockEntity.RendererData.AbyssalTroveRenderedItem>> items : blockEntity.rendererData.renderedItemTiers.entrySet()) {
            int tier = items.getKey();

            float widthPadding = RUtil.ease(blockEntity, 0.1F, 0.2F, tier * 8, 1F);
            float heightPadding = 0.2F;
            float heightOffset = RUtil.ease(blockEntity, 1F, 1.25F, tier * 5, 1F);
            float scale = RUtil.ease(blockEntity, 0, 0.25F, tier * 5, 1F);
            final int hideDelay = 200;

            scale = RUtil.ease(blockEntity, scale, 0, tier * 5 + hideDelay, 1F);
            blockEntity.currentDisplayRotation = RUtil.lerpAngle(blockEntity.currentDisplayRotation, blockEntity.degreesTowardsPlayer, tickDelta * 0.02f);

            for (AbyssalTroveBlockEntity.RendererData.AbyssalTroveRenderedItem item : items.getValue()) {
                matrices.pushPose();


                Vector3d position = new Vector3d(item.position.x * widthPadding, item.position.y * heightPadding, item.position.z * widthPadding);
                position.add(new Vector3d(0.5, heightOffset, 0.5));
                position = RUtil.rotateAroundY(position, blockEntity.currentDisplayRotation + 90, new Vector3d(0.5, 0.5, 0.5));
                matrices.translate(position.x, position.y, position.z);

                matrices.mulPose(RUtil.rotateY(90 - blockEntity.currentDisplayRotation));


                matrices.scale(scale, scale, scale);
                Minecraft.getInstance().getItemRenderer().renderStatic(item.itemStack, ItemDisplayContext.GROUND,
                        light, overlay, matrices, vertexConsumers, blockEntity.getLevel(), 0);
                matrices.popPose();
            }
        }

        profiler.pop();
    }
}