package grill24.potionsplus.mixin;

import grill24.potionsplus.render.IGameRendererMixin;
import grill24.potionsplus.render.animation.ItemActivationAnimation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin implements IGameRendererMixin {
    @Shadow
    @Final
    private Minecraft minecraft;
    @Unique
    private ItemActivationAnimation activeAnimation = null;

    // Wheel data
    @Unique
    private List<ItemStack> potions_plus$wheelItems;

    @Override
    public void potions_plus$displayItemActivation(ItemActivationAnimation animation) {
        this.activeAnimation = animation;
    }

    @Override
    public void potions_plus$cancelCurrentAnimation() {
        this.activeAnimation = null;
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void tick(CallbackInfo ci) {
        if (this.activeAnimation != null) {
            this.activeAnimation.tick();
        }
    }

    @Inject(method = "displayItemActivation", at = @At("HEAD"))
    private void displayItemActivation(ItemStack stack, CallbackInfo ci) {
        this.activeAnimation = null;
    }

    @Inject(method = "renderItemActivationAnimation", at = @At("HEAD"), cancellable = true)
    private void renderItemActivation(GuiGraphics guiGraphics, float partialTick, CallbackInfo ci) {
        if (this.activeAnimation != null && this.activeAnimation.isActive()) { // If we have an active animation, render it
            this.activeAnimation.render(this.minecraft, guiGraphics, partialTick);
            ci.cancel();
        } // Else pass to the original method
    }

    @Override
    public ItemActivationAnimation getActiveAnimation() {
        return this.activeAnimation;
    }
}
