package grill24.potionsplus;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import grill24.potionsplus.blockentity.BrewingCauldronBlockEntity;
import grill24.potionsplus.recipe.BrewingCauldronRecipe;
import grill24.potionsplus.utility.ClientTickHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;
import java.util.stream.IntStream;

public class BrewingCauldronBlockEntityRenderer implements BlockEntityRenderer<BrewingCauldronBlockEntity> {

    private final BlockRenderDispatcher blockRenderDispatcher;

    public BrewingCauldronBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.blockRenderDispatcher = context.getBlockRenderDispatcher();
    }


    @Override
    public void render(BrewingCauldronBlockEntity blockEntity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        double ticks = ClientTickHandler.total();

        matrices.pushPose();
        // Position an item model in the center of the block and rotate it based on the tick count
        // modulate y up and down according to sine wave
        final double distance = 0.25;
        double yOffset = sin(ticks, distance, 0.25) + 1;

        matrices.translate(0.5, yOffset, 0.5);
        matrices.mulPose(Vector3f.YP.rotationDegrees((float) ticks));

        Optional<BrewingCauldronRecipe> activeRecipe = blockEntity.getActiveRecipe();
        if (activeRecipe.isPresent()) {
            ItemStack stack = activeRecipe.get().getResultItem();
            Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemTransforms.TransformType.GROUND,
                    light, overlay, matrices, vertexConsumers, 0);
        }
        matrices.popPose();


        // Get non-empty item stacks from the block entity
        ItemStack[] itemStacks = IntStream.range(0, blockEntity.getContainerSize())
                .mapToObj(blockEntity::getItem)
                .filter(item -> item != ItemStack.EMPTY)
                .toArray(ItemStack[]::new);
        // Distribute items on perimeter of a circle
        final double radius = 0.32;
        final double angle = Math.PI * 2 / itemStacks.length;
        for (int i = 0; i < itemStacks.length; i++) {
            matrices.pushPose();
            double timeOrbitOffset = ticks / 400 * Math.PI * 2;
            double x = Math.cos(angle * i + timeOrbitOffset) * radius;
            double z = Math.sin(angle * i + timeOrbitOffset) * radius;
            matrices.translate(0.5 + x, 0.9 + sin(ticks, 0.025, 0.25, angle * i), 0.5 + z);

            float scale = 0.5f;
            if (activeRecipe.isPresent()) {
                scale *= 1 - blockEntity.getBrewTime() / (float) activeRecipe.get().getProcessingTime();
            }
            matrices.scale(scale, scale, scale);

            matrices.mulPose(Vector3f.YP.rotationDegrees((float) (ticks * 2 + i * 360 / itemStacks.length)));
            Minecraft.getInstance().getItemRenderer().renderStatic(itemStacks[i], ItemTransforms.TransformType.GROUND,
                    light, overlay, matrices, vertexConsumers, 0);
            matrices.popPose();
        }
    }

    public static double sin(double ticks, double amplitude, double hertz, double phase) {
        return Math.sin(ticks / 20 * hertz * Math.PI * 2 + phase) * amplitude;
    }

    public static double sin(double ticks, double amplitude, double hertz) {
        return sin(ticks, amplitude, hertz, 0);
    }
}
