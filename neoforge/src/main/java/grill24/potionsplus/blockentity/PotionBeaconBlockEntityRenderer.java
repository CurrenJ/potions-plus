package grill24.potionsplus.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import grill24.potionsplus.block.PotionBeaconBlock;
import grill24.potionsplus.core.blocks.OreBlocks;
import grill24.potionsplus.utility.ClientTickHandler;
import grill24.potionsplus.utility.RUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public class PotionBeaconBlockEntityRenderer implements BlockEntityRenderer<PotionBeaconBlockEntity> {

    public final BlockRenderDispatcher blockRenderDispatcher;
    private ProfilerFiller profiler;

    public PotionBeaconBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        blockRenderDispatcher = context.getBlockRenderDispatcher();
        profiler = Minecraft.getInstance().getProfiler();
    }


    @Override
    public void render(PotionBeaconBlockEntity blockEntity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        double ticks = ClientTickHandler.total();

        //  Profiler
        profiler.push("herbalists_lectern_render");

        // Render
        ItemStack stack = blockEntity.getItem(0);
        if (!stack.isEmpty()) {
            // Lerp the item from the player's hand to the resting position
            final float animationDuration = 20F;
            float lerpFactor = (float) (ticks - blockEntity.getTimeItemPlaced()) / animationDuration;
            lerpFactor = Math.max(0, Math.min(lerpFactor, 1));


            RUtil.renderInputItemAnimation(stack, 0.5F, 0, false, blockEntity, matrices, vertexConsumers, light, overlay);
        }

        if (blockEntity.getBlockState().getValue(PotionBeaconBlock.LIT)) {
            // Render glass block
            matrices.pushPose();
            matrices.translate(0.5, 0.5 + (1 / 16F), 0.5);

            final int DURATION = 15;
            float timeSincePlaced = ClientTickHandler.total() - blockEntity.rendererData.innerBlockShownTimestamp;
            final float BASE_SCALE = 1.1F;
            float scale = RUtil.lerp(0, BASE_SCALE, RUtil.easeOutElastic(Math.clamp(timeSincePlaced / DURATION, 0, 1)));
            // 3 minutes = full scale -> Expired = 0 scale
            scale = RUtil.lerp(0, scale, Math.clamp((blockEntity.rendererData.effectDurationWhenShown - timeSincePlaced) / 3600, 0, 1));
            matrices.scale(scale, scale, scale);

            // Rotate cube rhythmically
            float x, y, z;
            x = (float) (Math.sin(ticks / 10) * 0.2);
            y = (float) (Math.cos(ticks / 10) * 0.2);
            z = (float) (Math.sin(ticks / 10) * 0.2);
            Quaternionf rotation = new Quaternionf();
            rotation.rotateXYZ(x, y, z);
            matrices.mulPose(rotation);

//            Minecraft.getInstance().getItemRenderer().renderStatic(new ItemStack(Blocks.URANIUM_GLASS.value()), ItemDisplayContext.FIXED,
//                    light, overlay, matrices, vertexConsumers, null, 0);

            matrices.popPose();
        }

        // Item Particles
        for (PotionBeaconBlockEntity.RendererData.ItemParticle itemParticle : blockEntity.rendererData.itemParticles) {
            itemParticle.age += tickDelta;

            float translationalFriction = -0.1F;
            Vector3d velocityTick = new Vector3d(itemParticle.velocity).mul(tickDelta);
            itemParticle.position.add(velocityTick);
            itemParticle.velocity.add(velocityTick.mul(translationalFriction));

            float rotationalFriction = -0.05F;
            Vector3f rotationalVelocityTick = new Vector3f(itemParticle.rotationalVelocity).mul(tickDelta);
            itemParticle.rotation.add(rotationalVelocityTick);
            itemParticle.rotationalVelocity.add(rotationalVelocityTick.mul(rotationalFriction));

            matrices.pushPose();
            matrices.translate(itemParticle.position.x, itemParticle.position.y, itemParticle.position.z);
            final float age = itemParticle.age / itemParticle.lifetime;
            final float scale = RUtil.lerp(itemParticle.scale, 0, age);
            matrices.scale(scale, scale, scale);
            matrices.mulPose(RUtil.fromXYZDegrees(itemParticle.rotation));
            Minecraft.getInstance().getItemRenderer().renderStatic(new ItemStack(OreBlocks.URANIUM_GLASS.value()), ItemDisplayContext.FIXED,
                    light, overlay, matrices, vertexConsumers, null, 0);
            matrices.popPose();
        }

        profiler.pop();
    }

}
