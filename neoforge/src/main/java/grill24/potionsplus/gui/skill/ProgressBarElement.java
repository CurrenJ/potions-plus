package grill24.potionsplus.gui.skill;

import grill24.potionsplus.gui.ColoredRectangleScreenElement;
import grill24.potionsplus.gui.HorizontalListScreenElement;
import grill24.potionsplus.utility.RUtil;
import net.minecraft.client.gui.screens.Screen;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.List;

public class ProgressBarElement extends HorizontalListScreenElement<ColoredRectangleScreenElement> {
    private float progress;
    private float targetProgress;

    private float width;
    private float targetWidth;

    public ProgressBarElement(Screen screen, Settings settings, float progress) {
        super(screen, settings, YAlignment.CENTER);

        this.progress = 0;
        this.targetProgress = progress;

        this.width = 0;
        this.targetWidth = 0;

        setChildren(createProgressBars());
    }

    private List<ColoredRectangleScreenElement> createProgressBars() {
        Rectangle2D bounds = getGlobalBounds();
        float totalWidth = (float) bounds.getWidth();
        float totalHeight = (float) bounds.getHeight();

        return List.of(
                new ColoredRectangleScreenElement(
                        this.screen,
                        this,
                        totalWidth * progress,
                        totalHeight,
                        Color.GREEN
                ),
                new ColoredRectangleScreenElement(
                        this.screen,
                        this,
                        totalWidth * (1 - progress),
                        totalHeight,
                        Color.WHITE
                )
        );
    }

    public float getDefaultWidth() {
        return this.screen.width / 6F;
    }

    @Override
    protected float getWidth() {
        return this.width;
    }

    @Override
    protected float getHeight() {
        return getWidth() == 0 ? 0 : this.screen.getMinecraft().font.lineHeight;
    }

    @Override
    protected void onTick(float partialTick) {
        this.progress = RUtil.lerp(this.progress, this.targetProgress, Math.clamp(partialTick * this.settings.animationSpeed(), 0, 1));
        this.width = RUtil.lerp(this.width, this.targetWidth, Math.clamp(partialTick * this.settings.animationSpeed() * 3, 0, 1));

        boolean first = true;
        Rectangle2D bounds = getGlobalBounds();
        for (ColoredRectangleScreenElement element : getChildren()) {
            if (first) {
                element.setWidth((float) (bounds.getWidth() * this.progress));
                first = false;
            } else {
                element.setWidth((float) (bounds.getWidth() * (1 - this.progress)));
            }
        }

        super.onTick(partialTick);
    }

    public void setProgress(float progress) {
        this.targetProgress = progress;
    }

    public void setTargetWidth(float width) {
        this.targetWidth = width;
    }
}
