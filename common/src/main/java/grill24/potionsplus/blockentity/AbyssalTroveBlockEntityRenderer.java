package grill24.potionsplus.blockentity;


import com.mojang.blaze3d.vertex.PoseStack;
import grill24.potionsplus.core.Recipes;
import grill24.potionsplus.core.items.DynamicIconItems;
import grill24.potionsplus.core.seededrecipe.PotionUpgradeIngredients;
import grill24.potionsplus.core.seededrecipe.PpIngredient;
import grill24.potionsplus.data.loot.SeededIngredientsLootTables;
import grill24.potionsplus.utility.RUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;


import org.joml.Vector3d;

import java.util.List;
import java.util.Map;


public class AbyssalTroveBlockEntityRenderer implements BlockEntityRenderer<AbyssalTroveBlockEntity, AbyssalTroveRenderState> {
    private final BlockModelResolver BlockModelResolver;
    private ProfilerFiller profiler;

    public AbyssalTroveBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        BlockModelResolver = context.blockModelResolver();
        profiler = Profiler.get();
    }

    // Old render method - kept for reference. Rendering now uses state-based pipeline with extractRenderState() and submit().
    @Deprecated
    public void renderLegacy(AbyssalTroveBlockEntity blockEntity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay, Vec3 cameraPos) {
        // Old render implementation removed. Rendering now uses state-based pipeline with extractRenderState() and submit().
    }

    @Override
    public AbyssalTroveRenderState createRenderState() {
        return new AbyssalTroveRenderState();
    }

    @Override
    public void extractRenderState(final AbyssalTroveBlockEntity blockEntity, final AbyssalTroveRenderState state, final float partialTicks, final Vec3 cameraPosition, final ModelFeatureRenderer.@org.jspecify.annotations.Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderState.extractBase(blockEntity, state, breakProgress);
        // TODO: Extract item render states from blockEntity.rendererData into state.renderedItemTiers
    }

    @Override
    public void submit(final AbyssalTroveRenderState state, final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final CameraRenderState camera) {
        // TODO: Submit rendering using state.renderedItemTiers via submitNodeCollector
    }

    private static final float SUB_ICON_SCALE = 0.25f;
    private static final float ICON_SCALE = 0.125F;
    private static final Vector3d SUB_ICON_OFFSET = new Vector3d(-1.2, 1.2, -0.2);
    private static final Vector3d SUB_ICON_OFFSET_BLOCK = new Vector3d(-1.2, 1.2, -1);

    private static final float UNKNOWN_INGREDIENT_SCALE = 0.5F * ICON_SCALE;

    private float getTargetScale(AbyssalTroveBlockEntity.RendererData.State state, boolean isUnknownIngredient, ItemStack icon) {
        return switch (state) {
            case HIDDEN -> 0;
            case ALL_INGREDIENTS -> ICON_SCALE;
            case ALL_LABELED_INGREDIENTS -> isUnknownIngredient ? UNKNOWN_INGREDIENT_SCALE : ICON_SCALE;
            case ONLY_COMMON_INGREDIENTS ->
                    isUnknownIngredient ? 0 : SeededIngredientsLootTables.isRarity(PotionUpgradeIngredients.Rarity.COMMON, PpIngredient.of(icon)) ? ICON_SCALE : 0;
            case ONLY_RARE_INGREDIENTS ->
                    isUnknownIngredient ? 0 : SeededIngredientsLootTables.isRarity(PotionUpgradeIngredients.Rarity.RARE, PpIngredient.of(icon)) ? ICON_SCALE : 0;
            case ONLY_DURATION_UPGRADES ->
                    isUnknownIngredient ? 0 : Recipes.DURATION_UPGRADE_ANALYSIS.isIngredientUsed(PpIngredient.of(icon)) ? ICON_SCALE : 0;
            case ONLY_AMPLIFICATION_UPGRADES ->
                    isUnknownIngredient ? 0 : Recipes.AMPLIFICATION_UPGRADE_ANALYSIS.isIngredientUsed(PpIngredient.of(icon)) ? ICON_SCALE : 0;
        };
    }

    private float getTargetSubIconScale(AbyssalTroveBlockEntity.RendererData.State state, boolean isUnknownIngredient, ItemStack icon) {
        return switch (state) {
            case HIDDEN, ALL_INGREDIENTS -> 0;
            case ALL_LABELED_INGREDIENTS, ONLY_COMMON_INGREDIENTS, ONLY_RARE_INGREDIENTS, ONLY_DURATION_UPGRADES,
                 ONLY_AMPLIFICATION_UPGRADES -> SUB_ICON_SCALE;
        };
    }

    private int getAnimationDuration(AbyssalTroveBlockEntity.RendererData.State state, int row) {
        return switch (state) {
            case HIDDEN, ALL_INGREDIENTS -> row * 4;
            case ALL_LABELED_INGREDIENTS, ONLY_COMMON_INGREDIENTS, ONLY_RARE_INGREDIENTS, ONLY_DURATION_UPGRADES,
                 ONLY_AMPLIFICATION_UPGRADES -> row * 2;
        };
    }
}
