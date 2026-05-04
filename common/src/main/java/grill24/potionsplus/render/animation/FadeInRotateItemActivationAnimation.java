package grill24.potionsplus.render.animation;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
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
    public void render(Minecraft minecraft, GuiGraphicsExtractor GuiGraphicsExtractor, float partialTick) {
        if (this.itemStack != null && this.itemActivationTicksRemaining > 0) {
            GuiGraphicsExtractor.item(this.itemStack, GuiGraphicsExtractor.guiWidth() / 2, GuiGraphicsExtractor.guiHeight() / 2);
        }
    }
}
