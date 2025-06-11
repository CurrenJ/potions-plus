package grill24.potionsplus.gui.skill;

import grill24.potionsplus.core.items.DynamicIconItems;
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

    protected float rotation;
    protected boolean onlyShowSilhouette;

    public ItemStackScreenElement(Screen screen, @Nullable RenderableScreenElement parent, Settings settings, ItemStack stack) {
        super(screen, parent, settings);

        this.stack = stack;

        setCurrentScale(1F);
        this.rotation = 0F;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public void setItemStack(ItemStack stack) {
        this.stack = stack;
    }

    public void setOnlyShowSilhouette(boolean onlyShowSilhouette) {
        this.onlyShowSilhouette = onlyShowSilhouette;
    }

    @Override
    protected float getWidth() {
        return 16 * this.getCurrentScale();
    }

    @Override
    protected float getHeight() {
        return 16 * this.getCurrentScale();
    }

    @Override
    protected void render(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        if (this.screen.getMinecraft().player == null || this.stack == null) {
            return;
        }

        // TODO: Fix silhouette
        if (onlyShowSilhouette) {
//            graphics.setColor(0, 0, 0, 1);
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
                this.getCurrentScale(),
                Anchor.DEFAULT);
        graphics.pose().popPose();
//        graphics.setColor(1, 1, 1, 1);

        if (onlyShowSilhouette) {
            // Render question mark
            graphics.pose().pushPose();
            graphics.pose().translate(0, 0, -100);
            ((IGuiGraphicsExtension) graphics).potions_plus$renderItem(
                    DynamicIconItems.GENERIC_ICON.getItemStackForTexture(DynamicIconItems.UNKNOWN_TEX_LOC),
                    new Vector3f(0, 0, 0),
                    (float) (bounds.getMinX() + bounds.getWidth() / 4F), // The render method we are calling here renders an item centered at the given position. We align to top-left because that's how the screen elements assume bounds are positioned.
                    (float) (bounds.getMinY() + bounds.getHeight() / 4F),
                    10,
                    this.getCurrentScale() * 0.5F,
                    Anchor.DEFAULT);
            graphics.pose().popPose();
        }
    }
}
