package grill24.potionsplus.blockentity;


import com.mojang.blaze3d.vertex.PoseStack;
import grill24.potionsplus.core.seededrecipe.PpIngredient;
import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipe;
import grill24.potionsplus.utility.ClientTickHandler;
import grill24.potionsplus.utility.PUtil;
import grill24.potionsplus.utility.RUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.phys.Vec3;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;


public class BrewingCauldronBlockEntityRenderer implements BlockEntityRenderer<BrewingCauldronBlockEntity, BrewingCauldronRenderState> {

    public final BlockModelResolver BlockModelResolver;
    private final ProfilerFiller profiler;

    public BrewingCauldronBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        BlockModelResolver = context.blockModelResolver();
        profiler = Profiler.get();
    }

    @Override
    public BrewingCauldronRenderState createRenderState() {
        return new BrewingCauldronRenderState();
    }

    @Override
    public void extractRenderState(final BrewingCauldronBlockEntity blockEntity, final BrewingCauldronRenderState state, final float partialTicks, final Vec3 cameraPosition, final ModelFeatureRenderer.@org.jspecify.annotations.Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderState.extractBase(blockEntity, state, breakProgress);

        Optional<RecipeHolder<BrewingCauldronRecipe>> activeRecipe = blockEntity.getActiveRecipe();

        // Brew result, floating in the centre of the block
        state.showResult = activeRecipe.isPresent() && blockEntity.isAbleToBrew();
        if (state.showResult) {
            // TODO: Calculate display stacks in BlockEntity on update and store it in a field. Needs this fix for durative upgrades
            Minecraft.getInstance().getItemModelResolver().updateForTopItem(state.resultItem, blockEntity.getResultWithTransformations(), ItemDisplayContext.GROUND, blockEntity.getLevel(), null, 0);
        }

        // Orbiting ingredients - shrink as the recipe they belong to progresses
        state.ingredients.clear();
        ItemStack[] itemStacks = IntStream.range(0, blockEntity.getContainerSize())
                .mapToObj(blockEntity::getItem)
                .filter(item -> item != ItemStack.EMPTY)
                .toArray(ItemStack[]::new);

        List<PpIngredient> ingredients = activeRecipe
                .map(holder -> new ArrayList<>(holder.value().getPpIngredients()))
                .orElseGet(ArrayList::new);

        for (ItemStack itemStack : itemStacks) {
            float scale = 0.5f;
            if (activeRecipe.isPresent()) {
                for (PpIngredient ingredient : ingredients) {
                    if (PUtil.isSameItemOrPotion(ingredient.getItemStack(), itemStack, activeRecipe.get().value().getMatchingCriteria())) {
                        ingredients.remove(ingredient);
                        scale *= 1 - blockEntity.getBrewTime() / (float) activeRecipe.get().value().getProcessingTime();
                        break;
                    }
                }
            }

            ItemStackRenderState itemState = new ItemStackRenderState();
            Minecraft.getInstance().getItemModelResolver().updateForTopItem(itemState, itemStack, ItemDisplayContext.GROUND, blockEntity.getLevel(), null, 0);
            state.ingredients.add(new BrewingCauldronRenderState.IngredientEntry(itemState, scale));
        }

        // Status icon (no xp / no heat) above the cauldron
        ItemStack statusIcon = blockEntity.getStatusIcon();
        state.hasStatusIcon = !statusIcon.isEmpty();
        if (state.hasStatusIcon) {
            Minecraft.getInstance().getItemModelResolver().updateForTopItem(state.statusIcon, statusIcon, ItemDisplayContext.FIXED, blockEntity.getLevel(), null, 0);
        }
    }

    @Override
    public void submit(final BrewingCauldronRenderState state, final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final CameraRenderState camera) {
        profiler.push("brewing_cauldron_render");

        double ticks = ClientTickHandler.total() * 2;

        if (state.showResult) {
            poseStack.pushPose();
            // Centre the result and bob it up and down along a sine wave
            final double distance = 0.25;
            double yOffset = sin(ticks, distance, 0.25) + 1;
            poseStack.translate(0.5, yOffset, 0.5);
            poseStack.mulPose(RUtil.rotateY((float) ticks));
            state.resultItem.submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
            poseStack.popPose();
        }

        // Distribute ingredients on the perimeter of a circle
        final int count = state.ingredients.size();
        final double radius = 0.32;
        final double angle = Math.PI * 2 / count;
        for (int i = 0; i < count; i++) {
            BrewingCauldronRenderState.IngredientEntry entry = state.ingredients.get(i);

            poseStack.pushPose();
            double timeOrbitOffset = ticks / 400 * Math.PI * 2;
            double x = Math.cos(angle * i + timeOrbitOffset) * radius;
            double z = Math.sin(angle * i + timeOrbitOffset) * radius;
            poseStack.translate(0.5 + x, 0.9 + sin(ticks, 0.025, 0.25, angle * i), 0.5 + z);

            poseStack.scale(entry.scale(), entry.scale(), entry.scale());
            poseStack.mulPose(RUtil.rotateY((float) (ticks * 2 + i * 360 / count)));
            entry.state().submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
            poseStack.popPose();
        }

        if (state.hasStatusIcon) {
            poseStack.pushPose();
            poseStack.translate(0.5, 1.4, 0.5);
            poseStack.scale(0.25f, 0.25f, 0.25f);
            poseStack.mulPose(RUtil.rotateY((float) ticks));
            state.statusIcon.submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
            poseStack.popPose();
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
