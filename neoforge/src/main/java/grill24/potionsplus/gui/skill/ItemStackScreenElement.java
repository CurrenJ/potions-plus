package grill24.potionsplus.gui.skill;

import grill24.potionsplus.gui.RenderableScreenElement;
import grill24.potionsplus.extension.IGuiGraphicsExtension;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.awt.geom.Rectangle2D;

public class ItemStackScreenElement extends RenderableScreenElement {
    protected ItemStack stack;

    protected float scale;
    protected float rotation;

    public ItemStackScreenElement(Screen screen, @Nullable RenderableScreenElement parent, Settings settings, ItemStack stack) {
        super(screen, parent, settings);

        this.stack = stack;

        this.scale = 1F;
        this.rotation = 0F;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public void setItemStack(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    protected float getWidth() {
        return 16 * this.scale;
    }

    @Override
    protected float getHeight() {
        return 16 * this.scale;
    }

    @Override
    protected void render(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        if (this.screen.getMinecraft().player == null || this.stack == null) {
            return;
        }

        Rectangle2D bounds = getGlobalBounds();
        // By default, send the item to the back of the screen render order because we want text and other elements to render on top of it.
        graphics.pose().pushPose();
        graphics.pose().translate(0, 0, -100);
        ((IGuiGraphicsExtension) graphics).potions_plus$renderItem(
                this.stack,
                new Vector3f(0, this.rotation, 0),
                (float) (bounds.getMinX()),
                (float) (bounds.getMinY()),
                this.scale,
                Anchor.DEFAULT);
        graphics.pose().popPose();
    }
}
