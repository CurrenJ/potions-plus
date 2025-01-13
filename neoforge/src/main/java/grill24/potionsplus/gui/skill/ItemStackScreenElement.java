package grill24.potionsplus.gui.skill;

import grill24.potionsplus.gui.RenderableScreenElement;
import grill24.potionsplus.utility.IGuiGraphicsMixin;
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
        ((IGuiGraphicsMixin) graphics).potions_plus$renderItem(
                this.stack,
                new Vector3f(0, this.rotation, 0),
                (float) (bounds.getMinX()), // The render method we are calling here renders an item centered at the given position. We align to top-left because that's how the screen elements assume bounds are positioned.
                (float) (bounds.getMinY()),
                this.scale,
                Anchor.DEFAULT);

        super.render(graphics, partialTick, mouseX, mouseY);
    }
}
