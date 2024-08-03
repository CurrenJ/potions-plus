package grill24.potionsplus.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import grill24.potionsplus.block.ClotheslineBlock;
import grill24.potionsplus.core.Blocks;
import grill24.potionsplus.render.LeashRenderer;
import grill24.potionsplus.utility.ClientTickHandler;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashSet;
import java.util.Set;

@Mod.EventBusSubscriber(modid = ModInfo.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClotheslineBlockEntityRenderer implements BlockEntityRenderer<ClotheslineBlockEntity> {
    public final BlockRenderDispatcher blockRenderDispatcher;
    private ProfilerFiller profiler;

    public static final Vector3f OFFSET_IN_POST_BLOCKS = new Vector3f(0.5f, 0.9375f, 0.5f);
    public static final Vector3f ITEM_OFFSET = new Vector3f(0, -0.2f, 0);

    public ClotheslineBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        blockRenderDispatcher = context.getBlockRenderDispatcher();
        profiler = Minecraft.getInstance().getProfiler();
    }

    @Override
    public void render(ClotheslineBlockEntity blockEntity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        profiler.push("clothesline_render");

        if (blockEntity.getLevel() != null) {
            BlockPos left = ClotheslineBlock.getLeftEnd(blockEntity.getBlockPos(), blockEntity.getBlockState());
            BlockPos right = ClotheslineBlock.getOtherEnd(left, blockEntity.getLevel().getBlockState(left));

            // Prevent rendering the same clothesline twice (once for each end)
            if (clotheslinesRendered.contains(left) || clotheslinesRendered.contains(right))
                return;
            clotheslinesRendered.add(blockEntity.getBlockPos());

            // Render the "clothesline" (repurposed lead rendering code from vanilla) between the two posts
            Level level = blockEntity.getLevel();
            int blockLightStart = 0, blockLightEnd = 0, skyLightStart = 0, skyLightEnd = 0;
            if (level != null) {
                blockLightStart = level.getBrightness(LightLayer.BLOCK, left);
                blockLightEnd = level.getBrightness(LightLayer.BLOCK, right);
                skyLightStart = level.getBrightness(LightLayer.SKY, left);
                skyLightEnd = level.getBrightness(LightLayer.SKY, right);
            }

            LeashRenderer.renderLeashBetweenPoints(blockEntity.getBlockPos(),
                    Vec3.atLowerCornerOf(left).add(OFFSET_IN_POST_BLOCKS.x(), OFFSET_IN_POST_BLOCKS.y(), OFFSET_IN_POST_BLOCKS.z()),
                    Vec3.atLowerCornerOf(right).add(OFFSET_IN_POST_BLOCKS.x(), OFFSET_IN_POST_BLOCKS.y(), OFFSET_IN_POST_BLOCKS.z()),
                    matrices, vertexConsumers, blockLightStart, blockLightEnd, skyLightStart, skyLightEnd);

            ClotheslineBlockEntity leftBlockEntity = level.getBlockEntity(left, Blocks.CLOTHESLINE_BLOCK_ENTITY.get()).orElse(null);
            if (leftBlockEntity == null)
                return;

            // Render items on the clothesline
            for (int i = 0; i < leftBlockEntity.getContainerSize(); i++) {
                ItemStack stack = leftBlockEntity.getItem(i);
                if (!stack.isEmpty()) {
                    matrices.pushPose();

                    Vector3f position = ClotheslineBlockEntityBakedRenderData.getItemPoint(blockEntity.getBlockPos(), blockEntity.getBlockState(), i, false);

                    // If this is the right end, adjust the position to the left end
                    if (!ClotheslineBlock.isLeftEnd(blockEntity.getBlockState())) {
                        BlockPos difference = ClotheslineBlock.getOtherEnd(blockEntity.getBlockPos(), blockEntity.getBlockState()).subtract(blockEntity.getBlockPos());
                        position.add(difference.getX(), difference.getY(), difference.getZ());
                    }

                    // Render the item
                    matrices.translate(position.x(), position.y(), position.z());
                    matrices.scale(0.5f, 0.5f, 0.5f);
                    matrices.mulPose(orientItemToClotheslineOrientation(blockEntity.getBlockState()));
                    // Swing the item a bit :)
                    float amplitude = 15 * (1 - leftBlockEntity.getProgress(i));
                    System.out.println(leftBlockEntity.getProgress(i));

                    float swing = (float) (Math.sin(ClientTickHandler.total() / 10 + i * 7) * amplitude);
                    matrices.mulPose(Vector3f.XP.rotationDegrees(swing));
                    matrices.translate(ITEM_OFFSET.x(), ITEM_OFFSET.y(), ITEM_OFFSET.z());

                    // TODO: Duplicate code from leashrenderer - optimize
                    float stepFraction = (i + 1f) / (leftBlockEntity.getContainerSize() + 1);
                    int mixedBlockLight = (int) Mth.lerp(stepFraction, blockLightStart, blockLightEnd);
                    int mixedSkyLight = (int) Mth.lerp(stepFraction, skyLightStart, skyLightEnd);

                    Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemTransforms.TransformType.FIXED,
                            LightTexture.pack(mixedBlockLight, mixedSkyLight), overlay, matrices, vertexConsumers, 0);
                    matrices.popPose();
                }
            }
        }

        profiler.pop();
    }

    private Quaternion orientItemToClotheslineOrientation(BlockState clothesLine) {
        Direction property = clothesLine.getValue(ClotheslineBlock.FACING);
        if (property.getAxis() == Direction.Axis.X) {
            return Vector3f.YP.rotationDegrees(90);
        }
        return Quaternion.ONE;
    }

    private static final Set<BlockPos> clotheslinesRendered = new HashSet<>();

    @SubscribeEvent
    public static void onRender(final RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_SOLID_BLOCKS) {
            clotheslinesRendered.clear();
        }
    }
}
