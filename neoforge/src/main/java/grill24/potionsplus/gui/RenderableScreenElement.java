package grill24.potionsplus.gui;

import grill24.potionsplus.utility.ClientTickHandler;
import grill24.potionsplus.utility.RUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.util.FastColor;
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

    protected Collection<MouseListener> clickListeners;
    protected Collection<MouseListener> mouseEnterListeners;
    protected Collection<MouseListener> mouseExitListeners;
    protected boolean allowClicksOutsideBounds = false;

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
    }

    private Vector3f getPositionWithAppliedAlignment(Vector3f position) {
        Vector3f alignmentOffset = Anchor.alignmentOffset(getWidth(), getHeight(), this.settings.anchor);
        return new Vector3f(position).add(alignmentOffset);
    }

    private static final int BOUNDS_COLOR = FastColor.ARGB32.colorFromFloat(0.2F, 1, 1, 1);
    protected void render(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        if (isVisible()) {
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
        }
    }

    @Override
    public final void tryRender(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        if (isVisible()) {
            render(graphics, partialTick, mouseX, mouseY);
        }
    }

    protected void onTick(float partialTick) {
        Vector3f relativeTarget = calculateRelativeTargetFromTarget(this.targetPosition, this.targetPositionScope);

        this.currentPosition = RUtil.lerp3f(this.currentPosition, relativeTarget, Math.clamp(partialTick * this.settings.animationSpeed, 0, 1));

        if (this.snappingTicks > 0) {
            this.snappingTicks--;
            snapToTarget();
        }
    }

    @Override
    public void tick(float partialTick, int mouseX, int mouseY) {
        updateHover(mouseX, mouseY);
        onTick(partialTick);
    }

    protected void updateHover(int mouseX, int mouseY) {
        boolean hovering = getGlobalBounds().contains(mouseX, mouseY);
        if (hovering && isVisible()) {
            if (this.mouseEnteredTimestamp == -1) {
                this.mouseEnteredTimestamp = ClientTickHandler.total();
                this.mouseEnterListeners.forEach(listener -> listener.onClick(mouseX, mouseY, this));
                onMouseEnter(mouseX, mouseY);
            }
            this.mouseExitedTimestamp = -1;
        } else {
            if (this.mouseExitedTimestamp == -1) {
                this.mouseExitedTimestamp = ClientTickHandler.total();
                this.mouseExitListeners.forEach(listener -> listener.onClick(mouseX, mouseY, this));
                onMouseExit(mouseX, mouseY);
            }
            this.mouseEnteredTimestamp = -1;
        }
    }

    @Override
    public void click(int mouseX, int mouseY) {
        if (isVisible() && (this.allowClicksOutsideBounds || getGlobalBounds().contains(mouseX, mouseY))) {
            this.clickListeners.forEach(listener -> listener.onClick(mouseX, mouseY, this));
        }
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
    public void hide() {
        // Hide the element
        this.hiddenTimestamp = ClientTickHandler.total();
        this.shownTimestamp = -1;
    }

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
    public record Settings(Anchor anchor, Vector4f padding, float animationSpeed, boolean hiddenByDefault, boolean showBounds, boolean showAnchor) {
        public static final Settings DEFAULT = new Settings(Anchor.DEFAULT, new Vector4f(), 0.1F, false, false, false);

        public Settings withAnchor(Anchor anchor) {
            return new Settings(anchor, this.padding, this.animationSpeed, this.hiddenByDefault, this.showBounds, this.showAnchor);
        }

        /**
         * Set padding for the element.
         * @param padding (left, top, right, bottom)
         * @return New settings with padding applied
         */
        public Settings withPadding(Vector4f padding) {
            return new Settings(this.anchor, padding, this.animationSpeed, this.hiddenByDefault, this.showBounds, this.showAnchor);
        }

        public Settings withHiddenByDefault(boolean hiddenByDefault) {
            return new Settings(this.anchor, this.padding, this.animationSpeed, hiddenByDefault, this.showBounds, this.showAnchor);
        }

        public Settings withShowBounds(boolean showBounds) {
            return new Settings(this.anchor, this.padding, this.animationSpeed, this.hiddenByDefault, showBounds, this.showAnchor);
        }

        public Settings withShowAnchor(boolean showAnchor) {
            return new Settings(this.anchor, this.padding, this.animationSpeed, this.hiddenByDefault, this.showBounds, showAnchor);
        }
    }
}
