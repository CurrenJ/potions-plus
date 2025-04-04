package grill24.potionsplus.gui;

import grill24.potionsplus.core.items.DynamicIconItems;
import grill24.potionsplus.extension.IGuiGraphicsExtension;
import grill24.potionsplus.render.animation.keyframe.SpatialAnimations;
import grill24.potionsplus.utility.ClientTickHandler;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.awt.geom.Rectangle2D;

public class SelectableDivScreenElement extends DivScreenElement<RenderableScreenElement> {
    protected boolean selected;
    protected float timeSelectedStateChanged;

    public SelectableDivScreenElement(Screen screen, @Nullable RenderableScreenElement parent, Settings settings, Anchor childAlignment, @Nullable RenderableScreenElement child) {
        super(screen, parent, settings, childAlignment, child);

        this.selected = false;
        this.timeSelectedStateChanged = -1;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        this.timeSelectedStateChanged = ClientTickHandler.total();
    }

    @Override
    public void render(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        super.render(graphics, partialTick, mouseX, mouseY);

        IGuiGraphicsExtension graphicsExtension = (IGuiGraphicsExtension) graphics;
        float selectedFrameAnimationProgress = SpatialAnimations.get(SpatialAnimations.SCALE_IN_BACK).getScale().evaluate((ClientTickHandler.total() - this.timeSelectedStateChanged) / 10F);
        Rectangle2D childBounds = getChild().getGlobalBounds();

        ItemStack itemStack = ItemStack.EMPTY;
        float x = (float) childBounds.getMinX();
        float y = (float) childBounds.getMinY();
        float scale = 1;
        if (this.selected) {
            scale = selectedFrameAnimationProgress * getChild().getCurrentScale();
            itemStack = DynamicIconItems.GENERIC_ICON.getItemStackForTexture(DynamicIconItems.GOLD_FISHING_FRAME_TEX_LOC);
        } else if(selectedFrameAnimationProgress < 1) {
            scale = Math.max(0, (1 - selectedFrameAnimationProgress) * getChild().getCurrentScale());
            itemStack = DynamicIconItems.GENERIC_ICON.getItemStackForTexture(DynamicIconItems.GOLD_FISHING_FRAME_TEX_LOC);
        } else if(this.isHovering()){
            float hoverFrameAnimationProgress = SpatialAnimations.get(SpatialAnimations.SCALE_IN_BACK).getScale().evaluate((ClientTickHandler.total() - this.mouseEnteredTimestamp) / 10F);
            scale = hoverFrameAnimationProgress * getChild().getCurrentScale();
            itemStack = DynamicIconItems.GENERIC_ICON.getItemStackForTexture(DynamicIconItems.GOLD_SELECTION_FRAME_TEX_LOC);
        } else if(!this.isHovering()) {
            float hoverFrameAnimationProgress = SpatialAnimations.get(SpatialAnimations.SCALE_IN_BACK).getScale().evaluate((ClientTickHandler.total() - this.mouseExitedTimestamp) / 10F);
            scale = Math.max(0, (1 - hoverFrameAnimationProgress) * getChild().getCurrentScale());
            itemStack = DynamicIconItems.GENERIC_ICON.getItemStackForTexture(DynamicIconItems.GOLD_SELECTION_FRAME_TEX_LOC);
        }
        graphicsExtension.potions_plus$renderItem(itemStack, new Vector3f(), x, y, -100, scale, Anchor.DEFAULT);
    }
}
