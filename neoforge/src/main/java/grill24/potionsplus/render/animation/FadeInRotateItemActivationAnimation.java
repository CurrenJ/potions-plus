package grill24.potionsplus.render.animation;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import grill24.potionsplus.utility.RUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class FadeInRotateItemActivationAnimation extends ItemActivationAnimation {
    private final ItemStack itemStack;

    public FadeInRotateItemActivationAnimation(int tickDuration, ItemStack itemStack) {
        super(tickDuration);
        this.itemStack = itemStack;
    }

    public static FadeInRotateItemActivationAnimation defaultAnimation(ItemStack stack) {
        return new FadeInRotateItemActivationAnimation(120, stack);
    }

    @Override
    public void render(Minecraft minecraft, GuiGraphics guiGraphics, float partialTick) {
        if (this.itemStack != null && this.itemActivationTicksRemaining > 0) {
            int elapsedTicks = this.itemActivationMaxTicks - this.itemActivationTicksRemaining;
            float animationProgress = RUtil.getProgress(0, this.itemActivationMaxTicks, 0, elapsedTicks + partialTick);
            float animationProgessFirstQuarter = RUtil.getProgress(0, this.itemActivationMaxTicks * 0.25F, 0, elapsedTicks + partialTick);
            float animationProgressHalf = RUtil.getProgress(0, this.itemActivationMaxTicks * 0.3F, 0, elapsedTicks + partialTick);
            float animationProgressLastQuarter = RUtil.getProgress(this.itemActivationMaxTicks * 0.75F, this.itemActivationMaxTicks * 0.25F, 0, elapsedTicks + partialTick);

            float fadeInAlpha = RUtil.lerp(0.0F, 1.0F, animationProgressHalf);
            fadeInAlpha = RUtil.lerp(fadeInAlpha, 0.0F, animationProgressLastQuarter);
            float rotationAngleZ = RUtil.lerp(0, 1080, RUtil.easeOutSine(animationProgressHalf));
            float rotationAngleY = RUtil.lerp(0, 1080, RUtil.easeOutSine(animationProgressHalf));

            PoseStack poseStack = new PoseStack();
            poseStack.pushPose();
            poseStack.translate(guiGraphics.guiWidth() / 2.0F, guiGraphics.guiHeight() / 2.0F, -50.0F);
            poseStack.mulPose(Axis.ZP.rotationDegrees(rotationAngleZ));
            poseStack.mulPose(Axis.YP.rotationDegrees(rotationAngleY));
            float scale = 175.0F * RUtil.lerp(0, 1, animationProgessFirstQuarter);
            scale = RUtil.lerp(scale, 0.0F, animationProgressLastQuarter);
            scale += 50.0F;
            poseStack.scale(-scale, -scale, scale);

            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, fadeInAlpha);

            guiGraphics.drawManaged(
                    () -> minecraft
                            .getItemRenderer()
                            .renderStatic(
                                    this.itemStack,
                                    ItemDisplayContext.FIXED,
                                    15728880,
                                    OverlayTexture.NO_OVERLAY,
                                    poseStack,
                                    guiGraphics.bufferSource(),
                                    minecraft.level,
                                    0
                            )
            );

            poseStack.popPose();
        }
    }
}
