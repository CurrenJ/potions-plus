package grill24.potionsplus.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import grill24.potionsplus.utility.IGameRendererMixin;
import grill24.potionsplus.utility.ItemActivationAnimationEnum;
import grill24.potionsplus.utility.RUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin implements IGameRendererMixin {
    @Shadow @Nullable private ItemStack itemActivationItem;
    @Shadow private int itemActivationTicks;
    @Shadow @Final private Minecraft minecraft;
    @Shadow private float itemActivationOffX;
    @Shadow private float itemActivationOffY;
    @Shadow @Final private RandomSource random;

    @Unique private int potions_plus$itemActivationMaxTicks = 40;
    @Unique private ItemActivationAnimationEnum potions_plus$itemActivationAnimation = ItemActivationAnimationEnum.TOTEM;

    public void potions_plus$displayItemActivation(ItemStack stack, ItemActivationAnimationEnum animation) {
        this.itemActivationItem = stack;
        this.itemActivationTicks = 120;
        this.potions_plus$itemActivationMaxTicks = this.itemActivationTicks;
        this.potions_plus$itemActivationAnimation = ItemActivationAnimationEnum.FADE_IN;
        this.itemActivationOffX = this.random.nextFloat() * 2.0F - 1.0F;
        this.itemActivationOffY = this.random.nextFloat() * 2.0F - 1.0F;
    }

    @Inject(method = "displayItemActivation", at = @At("HEAD"))
    private void displayItemActivation(ItemStack stack, CallbackInfo ci) {
        this.potions_plus$itemActivationAnimation = ItemActivationAnimationEnum.TOTEM;
    }

    @Inject(method = "renderItemActivationAnimation", at = @At("HEAD"), cancellable = true)
    private void renderItemActivation(GuiGraphics guiGraphics, float partialTick, CallbackInfo ci) {
        if (this.potions_plus$itemActivationAnimation != ItemActivationAnimationEnum.TOTEM) {
            switch (this.potions_plus$itemActivationAnimation) {
                case FADE_IN:
                    potions_plus$renderItemFadeInRotateAnimation(guiGraphics, partialTick);
                    break;
            }
            ci.cancel();
        } // Else pass to the original method
    }

    @Unique
    private void potions_plus$renderItemFadeInRotateAnimation(GuiGraphics guiGraphics, float partialTick) {
        if (this.itemActivationItem != null && this.itemActivationTicks > 0) {
            int elapsedTicks = this.potions_plus$itemActivationMaxTicks - this.itemActivationTicks;
            float animationProgress = RUtil.getProgress(0, this.potions_plus$itemActivationMaxTicks, 0, elapsedTicks + partialTick);
            float animationProgessFirstQuarter = RUtil.getProgress(0, this.potions_plus$itemActivationMaxTicks * 0.25F, 0, elapsedTicks + partialTick);
            float animationProgressHalf = RUtil.getProgress(0, this.potions_plus$itemActivationMaxTicks * 0.3F, 0, elapsedTicks + partialTick);
            float animationProgressLastQuarter = RUtil.getProgress(this.potions_plus$itemActivationMaxTicks * 0.75F, this.potions_plus$itemActivationMaxTicks * 0.25F, 0, elapsedTicks + partialTick);

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
                    () -> this.minecraft
                            .getItemRenderer()
                            .renderStatic(
                                    this.itemActivationItem,
                                    ItemDisplayContext.FIXED,
                                    15728880,
                                    OverlayTexture.NO_OVERLAY,
                                    poseStack,
                                    guiGraphics.bufferSource(),
                                    this.minecraft.level,
                                    0
                            )
            );

            poseStack.popPose();
        }
    }
}
