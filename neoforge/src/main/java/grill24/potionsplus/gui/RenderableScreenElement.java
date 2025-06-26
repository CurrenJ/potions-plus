package grill24.potionsplus.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import grill24.potionsplus.render.animation.keyframe.AnimationCurve;
import grill24.potionsplus.utility.ClientTickHandler;
import grill24.potionsplus.utility.RUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.util.ARGB;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;

public abstract class RenderableScreenElement implements IRenderableScreenElement {
    /**
     * Timestamps for when the element was last shown and hidden.
     */
    protected float shownTimestamp;
    protected float hiddenTimestamp;

    protected float mouseEnteredTimestamp;
    protected float mouseExitedTimestamp;

    /**
     * Parent element of this element.
     * Children are rendered relative to their parent.
     */
    protected RenderableScreenElement parent;

    /**
     *  Current position of the element - always represented as a global position.
     *  Managed by this class only, not children.
     *  Use {@link #setTargetPosition(Vector3f, Scope, boolean)} to set the target position and move the element.
     *  Use {@link #getGlobalBounds()} to get the position and bounds of the element for rendering / interaction purposes.
     *  */
    private Vector3f currentPosition;

    public enum Scope {
        GLOBAL,
        LOCAL
    }

    /**
     * Target position / set-point of the element - may be global or local.
     */
    private Scope targetPositionScope;
    private Vector3f targetPosition;

    /**
     * {@link #setCurrentScale(float)}
     */
    private float currentScale;

    /**
     * Listeners for mouse clicks, and enter and exiting of the element.
     */
    protected Collection<MouseListener> clickListeners;
    protected Collection<ScrollListener> scrollListeners;
    protected Collection<DragListener> dragListeners;
    protected Collection<MouseListener> mouseEnterListeners;
    protected Collection<MouseListener> mouseExitListeners;
    protected boolean allowClicksOutsideBounds = false;

    protected RenderableScreenElement tooltip;

    /**
     * Render settings for the element.
     */
    protected Settings settings;

    /**
     * Screen that the element is rendered on.
     * Allows access to {@link net.minecraft.client.Minecraft} and the {@link net.minecraft.world.entity.player.Player} amongst other things.
     */
    protected Screen screen;

    private int snappingTicks;

    public RenderableScreenElement(Screen screen, @Nullable RenderableScreenElement parent, Settings settings) {
        this.shownTimestamp = settings.hiddenByDefault ? -1 : ClientTickHandler.total();
        this.hiddenTimestamp = -1;

        this.parent = parent;
        this.currentPosition = new Vector3f();
        this.targetPosition = new Vector3f();

        this.clickListeners = new ArrayList<>();
        this.scrollListeners = new ArrayList<>();
        this.dragListeners = new ArrayList<>();
        this.mouseEnterListeners = new ArrayList<>();
        this.mouseExitListeners = new ArrayList<>();

        this.settings = settings;
        this.screen = screen;

        this.snappingTicks = 10;
    }

    public void setAllowClicksOutsideBounds(boolean allowClicksOutsideBounds) {
        this.allowClicksOutsideBounds = allowClicksOutsideBounds;
    }

    public void setParent(RenderableScreenElement parent) {
        this.parent = parent;
    }

    public void addClickListener(MouseListener listener) {
        this.clickListeners.add(listener);
    }

    public void addMouseEnterListener(MouseListener listener) {
        this.mouseEnterListeners.add(listener);
    }

    public void addMouseExitListener(MouseListener listener) {
        this.mouseExitListeners.add(listener);
    }

    private Vector3f calculateRelativeTargetFromTarget(Vector3f targetPosition, Scope targetPositionScope) {
        Vector3f target = new Vector3f(targetPosition);
        if (this.parent != null && targetPositionScope == Scope.LOCAL) {
            target.add(this.parent.currentPosition);
        }
        return getPositionWithAppliedAlignment(target);
    }

    @Override
    public void setTargetPosition(Vector3f targetPosition, Scope targetPositionScope, boolean instant) {
        this.targetPosition = targetPosition;
        this.targetPositionScope = targetPositionScope;

        if (instant) {
            snapToTarget();
        }
    }

    public void snapToTarget() {
        this.currentPosition = calculateRelativeTargetFromTarget(this.targetPosition, this.targetPositionScope);

        if (this.tooltip != null) {
            this.tooltip.snapToTarget();
        }
    }

    private Vector3f getPositionWithAppliedAlignment(Vector3f position) {
        Vector3f alignmentOffset = Anchor.alignmentOffset(getWidth(), getHeight(), this.settings.anchor);
        return new Vector3f(position).add(alignmentOffset);
    }

    /**
     * Set the scale of the element. Use of scale is up to the extending class.
     * Does not affect the bounds of the element by default.
     * @param scale
     */
    @Override
    public void setCurrentScale(float scale) {
        this.currentScale = scale;
    }

    protected abstract void render(GuiGraphics graphics, float partialTick, int mouseX, int mouseY);
    protected void renderTooltip(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {}

    @Override
    public final void tryRender(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        if (isVisible()) {
            renderDebug(graphics);
            render(graphics, partialTick, mouseX, mouseY);
            if (this.tooltip != null) {
                this.tooltip.tryRender(graphics, partialTick, mouseX, mouseY);
            }
        }
    }

    private static final int BOUNDS_COLOR = ARGB.colorFromFloat(0.2F, 1, 1, 1);
    private static final int GRID_COLOR = ARGB.colorFromFloat(0.3F, 0, 0, 1);
    private static final int OUTLINE_COLOR = ARGB.colorFromFloat(0.3F, 1, 0, 0);
    private void renderDebug(GuiGraphics graphics) {
        if (this.settings.showBounds) {
            Rectangle2D bounds = getGlobalBounds();
            // Render the element
            graphics.fill((int) bounds.getMinX(), (int) bounds.getMinY(), (int) bounds.getMaxX(), (int) bounds.getMaxY(), BOUNDS_COLOR);
        }

        if (this.settings.showAnchor) {
            Rectangle2D bounds = getGlobalBounds();
            // Render the element
            graphics.fill((int) bounds.getMinX(), (int) bounds.getMinY(), (int) bounds.getMinX() + 1, (int) bounds.getMinY() + 1, BOUNDS_COLOR);
        }

        if (this.settings.showGridLines) {
            // Lines at 1/4, 1/2, 3/4 of the way across the element
            Rectangle2D bounds = getGlobalBounds();
            final int thickness = 1;
            // Vertical lines
            graphics.fill((int) (bounds.getMinX() + bounds.getWidth() / 4), (int) bounds.getMinY(), (int) (bounds.getMinX() + bounds.getWidth() / 4) + thickness, (int) bounds.getMaxY(), GRID_COLOR);
            graphics.fill((int) (bounds.getMinX() + bounds.getWidth() / 2), (int) bounds.getMinY(), (int) (bounds.getMinX() + bounds.getWidth() / 2) + thickness, (int) bounds.getMaxY(), GRID_COLOR);
            graphics.fill((int) (bounds.getMinX() + bounds.getWidth() * 3 / 4), (int) bounds.getMinY(), (int) (bounds.getMinX() + bounds.getWidth() * 3 / 4) + thickness, (int) bounds.getMaxY(), GRID_COLOR);
            // Horizontal lines
            graphics.fill((int) bounds.getMinX(), (int) (bounds.getMinY() + bounds.getHeight() / 4), (int) bounds.getMaxX(), (int) (bounds.getMinY() + bounds.getHeight() / 4) + thickness, GRID_COLOR);
            graphics.fill((int) bounds.getMinX(), (int) (bounds.getMinY() + bounds.getHeight() / 2), (int) bounds.getMaxX(), (int) (bounds.getMinY() + bounds.getHeight() / 2) + thickness, GRID_COLOR);
            graphics.fill((int) bounds.getMinX(), (int) (bounds.getMinY() + bounds.getHeight() * 3 / 4), (int) bounds.getMaxX(), (int) (bounds.getMinY() + bounds.getHeight() * 3 / 4) + thickness, GRID_COLOR);

            // Outline
            graphics.fill((int) bounds.getMinX(), (int) bounds.getMinY(), (int) bounds.getMaxX(), (int) bounds.getMinY() + thickness, OUTLINE_COLOR);
            graphics.fill((int) bounds.getMinX(), (int) bounds.getMinY(), (int) bounds.getMinX() + thickness, (int) bounds.getMaxY(), OUTLINE_COLOR);
            graphics.fill((int) bounds.getMaxX() - thickness, (int) bounds.getMinY(), (int) bounds.getMaxX(), (int) bounds.getMaxY(), OUTLINE_COLOR);
            graphics.fill((int) bounds.getMinX(), (int) bounds.getMaxY() - thickness, (int) bounds.getMaxX(), (int) bounds.getMaxY(), OUTLINE_COLOR);
        }
    }

    protected void onTick(float partialTick, int mouseX, int mouseY) {
        Vector3f relativeTarget = calculateRelativeTargetFromTarget(this.targetPosition, this.targetPositionScope);
        this.currentPosition = RUtil.lerp3f(this.currentPosition, relativeTarget, Math.clamp(partialTick * this.settings.animationSpeed, 0, 1));

        if (this.tooltip != null) {
            this.tooltip.setTargetPosition(new Vector3f(mouseX, mouseY, 0), Scope.GLOBAL, false);
        }

        if (this.snappingTicks > 0) {
            this.snappingTicks--;
            snapToTarget();
        }
    }

    @Override
    public final void tick(float partialTick, int mouseX, int mouseY) {
        updateHover(mouseX, mouseY);
        if (this.isVisible()) {
            onTick(partialTick, mouseX, mouseY);
            if (this.tooltip != null) {
                this.tooltip.tick(partialTick, mouseX, mouseY);
            }
        }

        if (this.settings.snapToTargetPosition) {
            snapToTarget();
        }
    }

    protected void updateHover(int mouseX, int mouseY) {
        boolean hovering = getGlobalBounds().contains(mouseX, mouseY);
        if (hovering && isVisible()) {
            if (this.mouseEnteredTimestamp == -1) {
                this.mouseEnteredTimestamp = ClientTickHandler.total();
                this.mouseEnterListeners.forEach(listener -> listener.onClick(mouseX, mouseY, 0, this));
                onMouseEnter(mouseX, mouseY);
            }
            this.mouseExitedTimestamp = -1;
        } else {
            if (this.mouseExitedTimestamp == -1) {
                this.mouseExitedTimestamp = ClientTickHandler.total();
                this.mouseExitListeners.forEach(listener -> listener.onClick(mouseX, mouseY, 0, this));
                onMouseExit(mouseX, mouseY);
            }
            this.mouseEnteredTimestamp = -1;
        }
    }

    @Override
    public void tryClick(int mouseX, int mouseY, int button) {
        if (isVisible() && (this.allowClicksOutsideBounds || getGlobalBounds().contains(mouseX, mouseY))) {
            onClick(mouseX, mouseY, button);
        }
    }

    @Override
    public void onClick(int mouseX, int mouseY, int button) {
        this.clickListeners.forEach(listener -> listener.onClick(mouseX, mouseY, button, this));
    }

    @Override
    public void tryScroll(int mouseX, int mouseY, double scrollDelta) {
        if (isVisible() && getGlobalBounds().contains(mouseX, mouseY)) {
            onScroll(mouseX, mouseY, scrollDelta);
        }
    }

    @Override
    public void onScroll(int mouseX, int mouseY, double scrollDelta) {
        this.scrollListeners.forEach(listener -> listener.onScroll(mouseX, mouseY, scrollDelta, this));
    }

    @Override
    public void tryDrag(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (isVisible() && getGlobalBounds().contains(mouseX, mouseY)) {
            onDrag(mouseX, mouseY, button, dragX, dragY);
        }
    }

    @Override
    public void onDrag(double mouseX, double mouseY, int button, double dragX, double dragY) {
        this.dragListeners.forEach(listener -> listener.onDrag(mouseX, mouseY, button, dragX, dragY, this));
    }

    protected void onMouseExit(int mouseX, int mouseY) {}
    protected void onMouseEnter(int mouseX, int mouseY) {}

    @Override
    public void show() {
        // Show the element
        this.shownTimestamp = ClientTickHandler.total();
        this.hiddenTimestamp = -1;
    }

    @Override
    public void hide(boolean playHideAnimation) {
        // Hide the element
        this.hiddenTimestamp = playHideAnimation ? ClientTickHandler.total() : -1;
        this.shownTimestamp = -1;
    }

    protected void onHide() {}

    @Override
    public boolean isVisible() {
        return this.shownTimestamp != -1 || this.hiddenTimestamp != -1;
    }

    @Override
    public boolean isHovering() {
        return this.mouseEnteredTimestamp != -1;
    }

    @Override
    public Rectangle2D getGlobalBounds() {
        if (isVisible()) {
            return new Rectangle2D.Float(this.currentPosition.x, this.currentPosition.y, getWidth(), getHeight());
        } else {
            return new Rectangle2D.Float(this.currentPosition.x, this.currentPosition.y, 0, 0);
        }
    }

    @Override
    public Vector3f getCurrentPosition() {
        return this.currentPosition;
    }

    @Override
    public float getCurrentScale() {
        return this.currentScale;
    }

    @Override
    public float getMouseEnterTimestamp() {
        return this.mouseEnteredTimestamp;
    }

    @Override
    public float getMouseExitTimestamp() {
        return this.mouseExitedTimestamp;
    }

    protected <T> T getAnimationProgress(AnimationCurve<T> shownCurve, AnimationCurve<T> hiddenCurve) {
        if (this.shownTimestamp != -1) {
            return shownCurve.evaluate(ClientTickHandler.total() - this.shownTimestamp);
        } else if (this.hiddenTimestamp != -1) {
            return hiddenCurve.evaluate(ClientTickHandler.total() - this.hiddenTimestamp);
        } else {
            return shownCurve.evaluate(0);
        }
    }

    public void setShowBounds(boolean showBounds) {
        this.settings = this.settings.withShowBounds(showBounds);

        if (this.tooltip != null) {
            this.tooltip.setShowBounds(showBounds);
        }
    }

    public void setShowGridLines(boolean showGridLines) {
        this.settings = this.settings.withShowGridLines(showGridLines);

        if (this.tooltip != null) {
            this.tooltip.setShowGridLines(showGridLines);
        }
    }

    /**
     * These methods should be implemented by the extending class. They define the width and height of the element.
     * It is queried constantly by the rendering system to determine the bounds of the element.
     * <p>
     * Do NOT call these methods directly; instead call {@link #getGlobalBounds()}. Global bounds take into account visibility.
     * @return Width and height of the element
     */
    abstract protected float getWidth();
    abstract protected float getHeight();

    public record Anchor(XAlignment xAlignment, YAlignment yAlignment) {
        public static Anchor DEFAULT = new Anchor(XAlignment.LEFT, YAlignment.TOP);
        public static Anchor CENTER = new Anchor(XAlignment.CENTER, YAlignment.CENTER);
        public static Anchor BOTTOM_LEFT = new Anchor(XAlignment.LEFT, YAlignment.BOTTOM);

        public static Vector3f alignmentOffset(float width, float height, Anchor anchor) {
            float x = switch (anchor.xAlignment) {
                case LEFT -> 0;
                case CENTER -> -width / 2F;
                case RIGHT -> -width;
            };

            float y = switch (anchor.yAlignment) {
                case TOP -> 0;
                case CENTER -> -height / 2F;
                case BOTTOM -> -height;
            };

            return new Vector3f(x, y, 0);
        }
    }
    // LEFT = root position on left bound of abilities text, etc.
    public enum XAlignment {
        CENTER,
        LEFT,
        RIGHT
    }
    // TOP = root position on top bound of abilities text, etc.
    public enum YAlignment {
        CENTER,
        TOP,
        BOTTOM
    }


    /**
     * Render settings for the element.
     */
    public record Settings(
            Anchor anchor, 
            Vector4f padding, 
            float animationSpeed, 
            float minWidth,
            float maxWidth,
            float minHeight,
            float maxHeight,
            boolean snapToTargetPosition,
            boolean hiddenByDefault,
            boolean showBounds,
            boolean showAnchor,
            boolean showGridLines) {
        public static final Settings DEFAULT = new Settings(Anchor.DEFAULT, new Vector4f(), 0.25F, Float.MIN_VALUE, Float.MAX_VALUE, Float.MIN_VALUE, Float.MAX_VALUE, false, false, false, false, false);

        public Settings withAnchor(Anchor anchor) {
            return new Settings(anchor, this.padding, this.animationSpeed, this.minWidth, this.maxWidth, this.minHeight, this.maxHeight, this.snapToTargetPosition, this.hiddenByDefault, this.showBounds, this.showAnchor, this.showGridLines);
        }

        /**
         * Set padding for the element.
         * @param padding (left, top, right, bottom)
         * @return New settings with padding applied
         */
        public Settings withPadding(Vector4f padding) {
            return new Settings(this.anchor, padding, this.animationSpeed, this.minWidth, this.maxWidth, this.minHeight, this.maxHeight, this.snapToTargetPosition, this.hiddenByDefault, this.showBounds, this.showAnchor, this.showGridLines);
        }

        public Settings withHiddenByDefault(boolean hiddenByDefault) {
            return new Settings(this.anchor, this.padding, this.animationSpeed, this.minWidth, this.maxWidth, this.minHeight, this.maxHeight, this.snapToTargetPosition, hiddenByDefault, this.showBounds, this.showAnchor, this.showGridLines);
        }

        public Settings withShowBounds(boolean showBounds) {
            return new Settings(this.anchor, this.padding, this.animationSpeed, this.minWidth, this.maxWidth, this.minHeight, this.maxHeight, this.snapToTargetPosition, this.hiddenByDefault, showBounds, this.showAnchor, this.showGridLines);
        }

        public Settings withShowAnchor(boolean showAnchor) {
            return new Settings(this.anchor, this.padding, this.animationSpeed, this.minWidth, this.maxWidth, this.minHeight, this.maxHeight, this.snapToTargetPosition, this.hiddenByDefault, this.showBounds, showAnchor, this.showGridLines);
        }

        public Settings withAnimationSpeed(float animationSpeed) {
            return new Settings(this.anchor, this.padding, animationSpeed, this.minWidth, this.maxWidth, this.minHeight, this.maxHeight, this.snapToTargetPosition, this.hiddenByDefault, this.showBounds, this.showAnchor, this.showGridLines);
        }

        public Settings withSnapToTargetPosition(boolean snapToTargetPosition) {
            return new Settings(this.anchor, this.padding, this.animationSpeed, this.minWidth, this.maxWidth, this.minHeight, this.maxHeight, snapToTargetPosition, this.hiddenByDefault, this.showBounds, this.showAnchor, this.showGridLines);
        }

        public Settings withShowGridLines(boolean showGridLines) {
            return new Settings(this.anchor, this.padding, this.animationSpeed, this.minWidth, this.maxWidth, this.minHeight, this.maxHeight, this.snapToTargetPosition, this.hiddenByDefault, this.showBounds, this.showAnchor, showGridLines);
        }

        public Settings withMinWidth(float minWidth) {
            return new Settings(this.anchor, this.padding, this.animationSpeed, minWidth, this.maxWidth, this.minHeight, this.maxHeight, this.snapToTargetPosition, this.hiddenByDefault, this.showBounds, this.showAnchor, this.showGridLines);
        }

        public Settings withMaxWidth(float maxWidth) {
            return new Settings(this.anchor, this.padding, this.animationSpeed, this.minWidth, maxWidth, this.minHeight, this.maxHeight, this.snapToTargetPosition, this.hiddenByDefault, this.showBounds, this.showAnchor, this.showGridLines);
        }

        public Settings withMinHeight(float minHeight) {
            return new Settings(this.anchor, this.padding, this.animationSpeed, this.minWidth, this.maxWidth, minHeight, this.maxHeight, this.snapToTargetPosition, this.hiddenByDefault, this.showBounds, this.showAnchor, this.showGridLines);
        }

        public Settings withMaxHeight(float maxHeight) {
            return new Settings(this.anchor, this.padding, this.animationSpeed, this.minWidth, this.maxWidth, this.minHeight, maxHeight, this.snapToTargetPosition, this.hiddenByDefault, this.showBounds, this.showAnchor, this.showGridLines);
        }
    }
}
