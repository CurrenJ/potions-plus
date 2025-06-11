package grill24.potionsplus.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import grill24.potionsplus.core.Recipes;
import grill24.potionsplus.core.items.DynamicIconItems;
import grill24.potionsplus.core.seededrecipe.PotionUpgradeIngredients;
import grill24.potionsplus.core.seededrecipe.PpIngredient;
import grill24.potionsplus.data.loot.SeededIngredientsLootTables;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Vector3d;
import grill24.potionsplus.utility.RUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
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
        profiler = Profiler.get();
    }

    @Override
    public void render(AbyssalTroveBlockEntity blockEntity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay, Vec3 cameraPos) {
        profiler.push("abyssal_trove_render");

        // For all entries in abyssal trove
        for (Map.Entry<Integer, List<AbyssalTroveBlockEntity.RendererData.AbyssalTroveRenderedItem>> items : blockEntity.rendererData.renderedItemTiers.entrySet()) {
            int row = items.getKey();

            float horizontalPaddingScalar = RUtil.ease(blockEntity, 0.5F, 1F, row * 4, 1F) * ICON_SCALE;
            float verticalPaddingScalar = 1F * ICON_SCALE;

            float verticalOffset = RUtil.ease(blockEntity, 1F, 1.25F, row * 4, 1F);

            blockEntity.currentDisplayRotation = RUtil.lerpAngle(blockEntity.currentDisplayRotation, blockEntity.degreesTowardsPlayer, tickDelta * 0.02f);

            for (AbyssalTroveBlockEntity.RendererData.AbyssalTroveRenderedItem item : items.getValue()) {
                matrices.pushPose();

                boolean isUnknownIngredient = item.icon.is(DynamicIconItems.GENERIC_ICON.getValue());

                // Render ingredients and unknown ingredients
                Vector3d position = new Vector3d(item.position.x * horizontalPaddingScalar, item.position.y * verticalPaddingScalar, item.position.z * horizontalPaddingScalar);
                position.add(new Vector3d(0.5, verticalOffset, 0.5));
                position = RUtil.rotateAroundY(position, blockEntity.currentDisplayRotation + 90, new Vector3d(0.5, 0.5, 0.5));
                matrices.translate(position.x, position.y, position.z);

                matrices.mulPose(RUtil.rotateY(-90 - blockEntity.currentDisplayRotation));

                // Calculate scale based on state
                int startTime = blockEntity.getTimeLastStateChange(blockEntity.rendererData.getState());
                AbyssalTroveBlockEntity.RendererData.State state = blockEntity.rendererData.getState();
                item.scale = RUtil.ease(() -> startTime, item.scale, getTargetScale(state, isUnknownIngredient, item.icon), getAnimationDuration(state, row), 1F);
                item.subIconScale = RUtil.ease(() -> startTime, item.subIconScale, getTargetSubIconScale(state, isUnknownIngredient, item.icon), getAnimationDuration(state, row), 1F);

                matrices.scale(item.scale, item.scale, item.scale);

                Minecraft.getInstance().getItemRenderer().renderStatic(item.icon, ItemDisplayContext.FIXED,
                        light, overlay, matrices, vertexConsumers, blockEntity.getLevel(), 0);

                // Render sub icons
                if (item.subIconScale > 0) {
                    for (ItemStack subIcon : item.subIcon) {
                        matrices.scale(item.subIconScale, item.subIconScale, item.subIconScale);
                        if (item.icon.getItem() instanceof BlockItem) {
                            matrices.translate(SUB_ICON_OFFSET_BLOCK.x, SUB_ICON_OFFSET_BLOCK.y, SUB_ICON_OFFSET_BLOCK.z);
                        } else {
                            matrices.translate(SUB_ICON_OFFSET.x, SUB_ICON_OFFSET.y, SUB_ICON_OFFSET.z);
                        }
                        Minecraft.getInstance().getItemRenderer().renderStatic(subIcon, ItemDisplayContext.FIXED,
                                light, overlay, matrices, vertexConsumers, blockEntity.getLevel(), 0);
                    }
                }

                matrices.popPose();
            }
        }

        profiler.pop();
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
            case ONLY_COMMON_INGREDIENTS -> isUnknownIngredient ? 0 : SeededIngredientsLootTables.isRarity(PotionUpgradeIngredients.Rarity.COMMON, PpIngredient.of(icon)) ? ICON_SCALE : 0;
            case ONLY_RARE_INGREDIENTS -> isUnknownIngredient ? 0 : SeededIngredientsLootTables.isRarity(PotionUpgradeIngredients.Rarity.RARE, PpIngredient.of(icon)) ? ICON_SCALE : 0;
            case ONLY_DURATION_UPGRADES -> isUnknownIngredient ? 0 : Recipes.DURATION_UPGRADE_ANALYSIS.isIngredientUsed(PpIngredient.of(icon)) ? ICON_SCALE : 0;
            case ONLY_AMPLIFICATION_UPGRADES -> isUnknownIngredient ? 0 : Recipes.AMPLIFICATION_UPGRADE_ANALYSIS.isIngredientUsed(PpIngredient.of(icon)) ? ICON_SCALE : 0;
        };
    }

    private float getTargetSubIconScale(AbyssalTroveBlockEntity.RendererData.State state, boolean isUnknownIngredient, ItemStack icon) {
        return switch (state) {
            case HIDDEN, ALL_INGREDIENTS -> 0;
            case ALL_LABELED_INGREDIENTS, ONLY_COMMON_INGREDIENTS, ONLY_RARE_INGREDIENTS, ONLY_DURATION_UPGRADES, ONLY_AMPLIFICATION_UPGRADES -> SUB_ICON_SCALE;
        };
    }

    private int getAnimationDuration(AbyssalTroveBlockEntity.RendererData.State state, int row) {
        return switch (state) {
            case HIDDEN, ALL_INGREDIENTS -> row * 4;
            case ALL_LABELED_INGREDIENTS, ONLY_COMMON_INGREDIENTS, ONLY_RARE_INGREDIENTS, ONLY_DURATION_UPGRADES, ONLY_AMPLIFICATION_UPGRADES -> row * 2;
        };
    }
}
