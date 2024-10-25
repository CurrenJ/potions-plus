package grill24.potionsplus.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import grill24.potionsplus.core.seededrecipe.PpIngredient;
import grill24.potionsplus.utility.RUtil;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipe;
import grill24.potionsplus.utility.ClientTickHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@OnlyIn(Dist.CLIENT)
public class BrewingCauldronBlockEntityRenderer implements BlockEntityRenderer<BrewingCauldronBlockEntity> {

    public final BlockRenderDispatcher blockRenderDispatcher;

    public BrewingCauldronBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        blockRenderDispatcher = context.getBlockRenderDispatcher();
    }


    @Override
    public void render(BrewingCauldronBlockEntity blockEntity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        double ticks = ClientTickHandler.total() * 2;

        // Profiler
        ProfilerFiller profiler = Minecraft.getInstance().getProfiler();
        profiler.push("brewing_cauldron_render");

        matrices.pushPose();
        // Position an item model in the center of the block and rotate it based on the tick count
        // modulate y up and down according to sine wave
        final double distance = 0.25;
        double yOffset = sin(ticks, distance, 0.25) + 1;

        matrices.translate(0.5, yOffset, 0.5);
        matrices.mulPose(RUtil.rotateY((float) ticks));

        Optional<RecipeHolder<BrewingCauldronRecipe>> activeRecipe = blockEntity.getActiveRecipe();
        if (activeRecipe.isPresent() && blockEntity.isAbleToBrew()) {
            // TODO: Calculate display stack in BlocKEntity on update and store it in a field. Needs this fix for durative upgrades
            ItemStack stack = activeRecipe.get().value().getResult();
            Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.GROUND,
                    light, overlay, matrices, vertexConsumers, blockEntity.getLevel(), 0);
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

        List<PpIngredient> ingredients;
        if (activeRecipe.isEmpty()) {
            ingredients = List.of();
        } else {
            ingredients = new ArrayList<>(activeRecipe.get().value().getPpIngredients());
        }
        for (int i = 0; i < itemStacks.length; i++) {
            matrices.pushPose();
            double timeOrbitOffset = ticks / 400 * Math.PI * 2;
            double x = Math.cos(angle * i + timeOrbitOffset) * radius;
            double z = Math.sin(angle * i + timeOrbitOffset) * radius;
            matrices.translate(0.5 + x, 0.9 + sin(ticks, 0.025, 0.25, angle * i), 0.5 + z);

            float scale = 0.5f;
            if (activeRecipe.isPresent()) {
                for (PpIngredient ingredient : ingredients) {
                    if (grill24.potionsplus.utility.PUtil.isSameItemOrPotion(ingredient.getItemStack(), itemStacks[i], activeRecipe.get().value().getMatchingCriteria())) {
                        ingredients.remove(ingredient);
                        scale *= 1 - blockEntity.getBrewTime() / (float) activeRecipe.get().value().getProcessingTime();
                        break;
                    }
                }
            }
            matrices.scale(scale, scale, scale);

            matrices.mulPose(RUtil.rotateY((float) (ticks * 2 + i * 360 / itemStacks.length)));
            Minecraft.getInstance().getItemRenderer().renderStatic(itemStacks[i], ItemDisplayContext.GROUND,
                    light, overlay, matrices, vertexConsumers, blockEntity.getLevel(), 0);
            matrices.popPose();
        }

        profiler.pop();
    }

    public static double sin(double ticks, double amplitude, double hertz, double phase) {
        return Math.sin(ticks / 20F * hertz * Math.PI * 2 + phase) * amplitude;
    }

    public static double sin(double ticks, double amplitude, double hertz) {
        return sin(ticks, amplitude, hertz, 0);
    }
}
