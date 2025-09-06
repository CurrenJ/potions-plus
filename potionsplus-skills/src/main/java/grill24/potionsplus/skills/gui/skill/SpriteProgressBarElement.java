package grill24.potionsplus.gui.skill;

import grill24.potionsplus.extension.IGuiGraphicsExtension;
import grill24.potionsplus.gui.FixedSizeDivScreenElement;
import grill24.potionsplus.gui.RenderableScreenElement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;

import java.awt.geom.Rectangle2D;

import static grill24.potionsplus.utility.Utility.ppId;

public class SpriteProgressBarElement extends FixedSizeDivScreenElement<RenderableScreenElement> {
    // 10, 5 -> 63x19
    public static final ResourceLocation BAR_BACKGROUND = ppId("textures/gui/progress_bar_background.png");
    public static final ResourceLocation BAR_GOLD_OUTLINE = ppId("textures/gui/progress_bar_gold_outline.png");
    public static final ResourceLocation BAR_FILLED_METER = ppId("textures/gui/progress_bar_filled_meter.png");
    public static final ResourceLocation BAR_EMBELLISHMENT_1 = ppId("textures/gui/progress_bar_embellishment_1.png");
    public static final ResourceLocation BAR_EMBELLISHMENT_2 = ppId("textures/gui/progress_bar_embellishment_2.png");
    public static final ResourceLocation BAR_EMBELLISHMENT_3 = ppId("textures/gui/progress_bar_embellishment_3.png");

    public static final int WIDTH = 63;
    public static final int HEIGHT = 19;

    public float progress;
    public int skillLevel;

    public SpriteProgressBarElement(Screen screen, Settings settings) {
        super(screen, settings, Anchor.CENTER, null, WIDTH, HEIGHT);

        this.progress = 0F;
        this.skillLevel = 0;
    }

    @Override
    public void render(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        super.render(graphics, partialTick, mouseX, mouseY);

        Rectangle2D bounds = this.getGlobalBounds();

        blitProgressBarSprite(graphics, BAR_BACKGROUND, bounds, 1F);
        if (skillLevel >= 15) {
            blitProgressBarSprite(graphics, BAR_GOLD_OUTLINE, bounds, 1F);
        }
        if (skillLevel >= 30) {
            blitProgressBarSprite(graphics, BAR_EMBELLISHMENT_1, bounds, 1F);
        }
        if (skillLevel >= 45) {
            blitProgressBarSprite(graphics, BAR_EMBELLISHMENT_2, bounds, 1F);
        }
        if (skillLevel >= 60) {
            blitProgressBarSprite(graphics, BAR_EMBELLISHMENT_3, bounds, 1F);
        }

        blitProgressBarSprite(graphics, BAR_FILLED_METER, bounds, progress);
    }

    public static void blitProgressBarSprite(GuiGraphics graphics, ResourceLocation sprite, Rectangle2D bounds, float progress) {
        IGuiGraphicsExtension extension = (IGuiGraphicsExtension) graphics;
        int filledWidth = (int) Math.ceil(WIDTH * progress);

        extension.potions_plus$blit(RenderType::guiTextured, sprite,
                (float) bounds.getMinX(), (float) bounds.getMinY(),
                10, 5, filledWidth, HEIGHT, filledWidth, HEIGHT, 128, 128, ARGB.white(1F));
    }

    public void setProgress(float progress) {
        this.progress = progress;
    }

    public void setSkillLevel(int skillLevel) {
        if (skillLevel < 0) {
            throw new IllegalArgumentException("Skill level cannot be negative");
        }
        this.skillLevel = skillLevel;
    }
}
