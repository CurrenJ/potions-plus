package grill24.potionsplus.gui;

import grill24.potionsplus.core.Items;
import grill24.potionsplus.extension.IGuiGraphicsExtension;
import grill24.potionsplus.render.animation.keyframe.SpatialAnimationData;
import grill24.potionsplus.render.animation.keyframe.SpatialAnimations;
import grill24.potionsplus.utility.ClientTickHandler;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.util.FastColor;
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
        Rectangle2D bounds = getGlobalBounds();
        float animationProgress = SpatialAnimations.get(SpatialAnimations.SCALE_IN_BACK).getScale().evaluate((ClientTickHandler.total() - this.timeSelectedStateChanged) / 20F);
        if (this.selected) {
            graphicsExtension.potions_plus$renderItem(Items.GENERIC_ICON_RESOURCE_LOCATIONS.getItemStackForTexture(Items.GENERIC_ICON.value(), Items.GOLD_FISHING_FRAME_TEX_LOC), new Vector3f(), (float) bounds.getMinX(), (float) bounds.getMinY(), 0, animationProgress * getChild().getCurrentScale(), Anchor.DEFAULT);
        } else if(animationProgress < 1) {
            graphicsExtension.potions_plus$renderItem(Items.GENERIC_ICON_RESOURCE_LOCATIONS.getItemStackForTexture(Items.GENERIC_ICON.value(), Items.GOLD_FISHING_FRAME_TEX_LOC), new Vector3f(), (float) bounds.getMinX(), (float) bounds.getMinY(), 0, (1 - animationProgress) * getChild().getCurrentScale(), Anchor.DEFAULT);
        }
    }
}
