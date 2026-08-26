package grill24.potionsplus.blockentity;


import com.mojang.blaze3d.vertex.PoseStack;
import grill24.potionsplus.block.PotionBeaconBlock;
import grill24.potionsplus.core.blocks.OreBlocks;
import grill24.potionsplus.utility.ClientTickHandler;
import grill24.potionsplus.utility.RUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;


import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;


public class PotionBeaconBlockEntityRenderer implements BlockEntityRenderer<PotionBeaconBlockEntity, PotionBeaconRenderState> {

    public final BlockModelResolver BlockModelResolver;
    private ProfilerFiller profiler;

    public PotionBeaconBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        BlockModelResolver = context.blockModelResolver();
        profiler = Profiler.get();
    }

    @Override
    public PotionBeaconRenderState createRenderState() {
        return new PotionBeaconRenderState();
    }

    @Override
    public void extractRenderState(final PotionBeaconBlockEntity blockEntity, final PotionBeaconRenderState state, final float partialTicks, final Vec3 cameraPosition, final ModelFeatureRenderer.@org.jspecify.annotations.Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderState.extractBase(blockEntity, state, breakProgress);

        ItemStack inputStack = blockEntity.getItem(0);
        state.hasInputItem = !inputStack.isEmpty();
        if (state.hasInputItem) {
            state.timeItemPlaced = blockEntity.getTimeItemPlaced();
            state.startAnimationWorldPos.set(blockEntity.getStartAnimationWorldPos());
            state.restingPosition.set(blockEntity.getRestingPosition());
            state.restingRotation.set(blockEntity.getRestingRotation());
            state.inputAnimationDuration = blockEntity.getInputAnimationDuration();

            Minecraft.getInstance().getItemModelResolver().updateForTopItem(state.inputItem, inputStack, ItemDisplayContext.FIXED, blockEntity.getLevel(), null, 0);
        }

        state.isLit = blockEntity.getBlockState().getValue(PotionBeaconBlock.LIT);
        state.innerBlockShownTimestamp = blockEntity.rendererData.innerBlockShownTimestamp;
        state.effectDurationWhenShown = blockEntity.rendererData.effectDurationWhenShown;

        // Integrate particle physics (position/rotation drift with friction), same cadence as the old render() call
        for (PotionBeaconBlockEntity.RendererData.ItemParticle itemParticle : blockEntity.rendererData.itemParticles) {
            itemParticle.age += partialTicks;

            float translationalFriction = -0.1F;
            Vector3d velocityTick = new Vector3d(itemParticle.velocity).mul(partialTicks);
            itemParticle.position.add(velocityTick);
            itemParticle.velocity.add(velocityTick.mul(translationalFriction));

            float rotationalFriction = -0.05F;
            Vector3f rotationalVelocityTick = new Vector3f(itemParticle.rotationalVelocity).mul(partialTicks);
            itemParticle.rotation.add(rotationalVelocityTick);
            itemParticle.rotationalVelocity.add(rotationalVelocityTick.mul(rotationalFriction));
        }

        state.particles.clear();
        for (PotionBeaconBlockEntity.RendererData.ItemParticle itemParticle : blockEntity.rendererData.itemParticles) {
            state.particles.add(new PotionBeaconRenderState.ParticleEntry(itemParticle));
        }
        if (!blockEntity.rendererData.itemParticles.isEmpty()) {
            Minecraft.getInstance().getItemModelResolver().updateForTopItem(state.particleIcon, new ItemStack(OreBlocks.URANIUM_GLASS.value()), ItemDisplayContext.FIXED, blockEntity.getLevel(), null, 0);
        }
    }

    @Override
    public void submit(final PotionBeaconRenderState state, final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final CameraRenderState camera) {
        profiler.push("potion_beacon_render");

        float ticks = ClientTickHandler.total();

        if (state.hasInputItem) {
            poseStack.pushPose();
            float lerpFactor = Math.max(0, Math.min((ticks - state.timeItemPlaced) / state.inputAnimationDuration, 1));
            Quaternionf itemRotation = RUtil.fromXYZDegrees(state.restingRotation);
            if (lerpFactor < 1) {
                Vector3d lerpedPos = RUtil.lerp3d(state.startAnimationWorldPos, state.restingPosition, lerpFactor, RUtil::easeOutExpo);
                itemRotation = RUtil.slerp(new Quaternionf().identity(), itemRotation, RUtil.easeOutExpo(lerpFactor));
                poseStack.translate(lerpedPos.x, lerpedPos.y, lerpedPos.z);
            } else {
                poseStack.translate(state.restingPosition.x, state.restingPosition.y, state.restingPosition.z);
            }
            poseStack.mulPose(itemRotation);
            poseStack.scale(0.5F, 0.5F, 0.5F);
            state.inputItem.submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
            poseStack.popPose();
        }

        // Item Particles
        for (PotionBeaconRenderState.ParticleEntry itemParticle : state.particles) {
            poseStack.pushPose();
            poseStack.translate(itemParticle.position.x, itemParticle.position.y, itemParticle.position.z);
            final float age = itemParticle.age / itemParticle.lifetime;
            final float scale = RUtil.lerp(itemParticle.scale, 0, age);
            poseStack.scale(scale, scale, scale);
            poseStack.mulPose(RUtil.fromXYZDegrees(itemParticle.rotation));
            state.particleIcon.submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
            poseStack.popPose();
        }

        profiler.pop();
    }

}
