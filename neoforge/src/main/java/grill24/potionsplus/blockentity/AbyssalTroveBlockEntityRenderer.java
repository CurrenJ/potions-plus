package grill24.potionsplus.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import grill24.potionsplus.core.Items;
import grill24.potionsplus.core.seededrecipe.PotionUpgradeIngredients;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
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
        profiler = Minecraft.getInstance().getProfiler();
    }

    @Override
    public void render(@NotNull AbyssalTroveBlockEntity blockEntity, float tickDelta, @NotNull PoseStack matrices, @NotNull MultiBufferSource vertexConsumers, int light, int overlay) {
        profiler.push("abyssal_trove_render");

        // For all entries in abyssal trove
        for (Map.Entry<Integer, List<AbyssalTroveBlockEntity.RendererData.AbyssalTroveRenderedItem>> items : blockEntity.rendererData.renderedItemTiers.entrySet()) {
            int row = items.getKey();
            float size = 0.125F;

            // Ingredient scale
            float ingredientLocalScale = RUtil.ease(blockEntity, 0, size, row * 5, 1F);
            ingredientLocalScale = RUtil.ease(blockEntity, ingredientLocalScale, 0, row * 5 + hideDelay + blockEntity.extendHideDelayBy, 1F);
            // Rarity icon scale
            float rarityIconLocalScale = RUtil.ease(blockEntity::getTimeRarityIconsShown, 0, rarityIconScale, row * 2, 1F, RUtil::easeOutElastic);
            rarityIconLocalScale = RUtil.ease(blockEntity::getTimeRarityIconsShown, rarityIconLocalScale, 0, row * 5 + hideDelay + blockEntity.extendHideDelayBy, 1F);
            // Unknown ingredient scale
            float unknownIngredientLocalScale = RUtil.ease(blockEntity, 0, size * 0.8F, row * 5, 1F);
            if(blockEntity.getTimeRarityIconsShown() > blockEntity.getTimeItemPlaced()) {
                unknownIngredientLocalScale = RUtil.ease(blockEntity::getTimeRarityIconsShown, unknownIngredientLocalScale, unknownIngredientLocalScale * 0.8F, row * 2, 2F);
            }
            unknownIngredientLocalScale = RUtil.ease(blockEntity, unknownIngredientLocalScale, 0, row * 5 + hideDelay + blockEntity.extendHideDelayBy, 1F);


            float horizontalPaddingScalar = RUtil.ease(blockEntity, 0.5F, 1F, row * 7, 1F) * size;
            float verticalPaddingScalar = 1F * size;

            float verticalOffset = RUtil.ease(blockEntity, 1F, 1.25F, row * 5, 1F);


            blockEntity.currentDisplayRotation = RUtil.lerpAngle(blockEntity.currentDisplayRotation, blockEntity.degreesTowardsPlayer, tickDelta * 0.02f);

            for (AbyssalTroveBlockEntity.RendererData.AbyssalTroveRenderedItem item : items.getValue()) {
                matrices.pushPose();

                boolean isUnknownIngredient = item.icon.is(Items.GENERIC_ICON);

                // Render ingredients and unknown ingredients
                Vector3d position = new Vector3d(item.position.x * horizontalPaddingScalar, item.position.y * verticalPaddingScalar, item.position.z * horizontalPaddingScalar);
                position.add(new Vector3d(0.5, verticalOffset, 0.5));
                position = RUtil.rotateAroundY(position, blockEntity.currentDisplayRotation + 90, new Vector3d(0.5, 0.5, 0.5));
                matrices.translate(position.x, position.y, position.z);

                matrices.mulPose(RUtil.rotateY(-90 - blockEntity.currentDisplayRotation));

                if(isUnknownIngredient) {
                    matrices.scale(unknownIngredientLocalScale, unknownIngredientLocalScale, unknownIngredientLocalScale);
                } else {
                    matrices.scale(ingredientLocalScale, ingredientLocalScale, ingredientLocalScale);
                }
                Minecraft.getInstance().getItemRenderer().renderStatic(item.icon, ItemDisplayContext.FIXED,
                        light, overlay, matrices, vertexConsumers, blockEntity.getLevel(), 0);

                // Render sub icons
                if (rarityIconLocalScale > 0 && blockEntity.getTimeRarityIconsShown() > blockEntity.getTimeItemPlaced() && !isUnknownIngredient) {
                    for (ItemStack subIcon : item.subIcon) {
                        matrices.scale(rarityIconLocalScale, rarityIconLocalScale, rarityIconLocalScale);
                        if (item.icon.getItem() instanceof BlockItem) {
                            matrices.translate(rarityIconPositionOffsetBlock.x, rarityIconPositionOffsetBlock.y, rarityIconPositionOffsetBlock.z);
                        } else {
                            matrices.translate(rarityIconPositionOffset.x, rarityIconPositionOffset.y, rarityIconPositionOffset.z);
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

    public static final int hideDelay = 200;

    public static final float rarityIconScale = 0.25f;
    public static final Vector3d rarityIconPositionOffset = new Vector3d(-1.2, 1.2, -0.2);
    public static final Vector3d rarityIconPositionOffsetBlock = new Vector3d(-1.2, 1.2, -1);
}
