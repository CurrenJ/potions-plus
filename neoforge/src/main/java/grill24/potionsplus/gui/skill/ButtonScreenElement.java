package grill24.potionsplus.gui.skill;

import com.mojang.math.Axis;
import grill24.potionsplus.gui.RenderableScreenElement;
import grill24.potionsplus.render.animation.keyframe.SpatialAnimations;
import grill24.potionsplus.utility.ClientTickHandler;
import grill24.potionsplus.utility.IGuiGraphicsMixin;
import grill24.potionsplus.utility.RUtil;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FastColor;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;
import org.jetbrains.annotations.Nullable;

import java.awt.geom.Rectangle2D;

public class ButtonScreenElement extends RenderableScreenElement {
    private float width;
    private float height;
    private Component text;

    private float clickedTimestamp;
    private float renderRotation;
    private float renderScale;

    public ButtonScreenElement(Screen screen, @Nullable RenderableScreenElement parent, Settings settings, Component text, float width, float height) {
        super(screen, parent, settings);

        this.width = width;
        this.height = height;
        this.text = text;
        this.clickedTimestamp = -1;
        this.renderRotation = 0;
        this.renderScale = 1;
        addClickListener((mouseX, mouseY, element) -> {
            clickedTimestamp = ClientTickHandler.total();
        });
    }

    @Override
    protected void onTick(float partialTick, int mouseX, int mouseY) {
        float timeSinceClick = ClientTickHandler.total() - clickedTimestamp;
        this.renderRotation = SpatialAnimations.get(SpatialAnimations.BUTTON_WOBBLE).getRotation().evaluate(timeSinceClick).x();

        super.onTick(partialTick, mouseX, mouseY);
    }

    @Override
    protected void render(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        Rectangle2D bounds = getGlobalBounds();

        IGuiGraphicsMixin graphicsMixin = (IGuiGraphicsMixin) graphics;

        final float outlineSize = 0.5F;
        RUtil.drawInventoryBackgroundRect(graphics, bounds, this.renderRotation, 0.5F, 0.5F);

        // Draw text in the center of the button
        Font font = screen.getMinecraft().font;
        float textWidth = font.width(text);
        float textHeight = font.lineHeight;
        float textX = (float) bounds.getCenterX() - textWidth / 2F + outlineSize / 2F;
        float textY = (float) bounds.getCenterY() - textHeight / 2F + outlineSize / 2F;

        graphics.pose().pushPose();
        graphics.pose().translate(textX, textY, 100);
        graphics.pose().mulPose(Axis.ZP.rotationDegrees(renderRotation));
        graphicsMixin.potions_plus$drawString(font, text, 0, 0, FastColor.ARGB32.colorFromFloat(1, 1, 1, 1));
        graphics.pose().popPose();
    }

    @Override
    protected float getWidth() {
        return width;
    }

    @Override
    protected float getHeight() {
        return height;
    }
}
