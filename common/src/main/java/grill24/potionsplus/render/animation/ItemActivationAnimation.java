package grill24.potionsplus.render.animation;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

public abstract class ItemActivationAnimation {
    protected int itemActivationTicksRemaining;
    protected final int itemActivationMaxTicks;

    public ItemActivationAnimation(int tickDuration) {
        this.itemActivationTicksRemaining = tickDuration;
        this.itemActivationMaxTicks = this.itemActivationTicksRemaining;
    }

    public boolean isActive() {
        return this.itemActivationTicksRemaining > 0;
    }

    public void tick() {
        if (this.itemActivationTicksRemaining > 0) {
            this.itemActivationTicksRemaining--;
        }
    }

    public abstract void render(Minecraft minecraft, GuiGraphics guiGraphics, float partialTick);
}
