package grill24.potionsplus.gui.skill;

import grill24.potionsplus.extension.IGuiGraphicsExtension;
import grill24.potionsplus.gui.FixedSizeDivScreenElement;
import grill24.potionsplus.gui.RenderableScreenElement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import org.joml.Vector2i;
import oshi.util.tuples.Pair;

import java.awt.geom.Rectangle2D;
import java.util.HashMap;

import static grill24.potionsplus.utility.Utility.ppId;

public class AbilitySelectionTree<E extends RenderableScreenElement> extends FixedSizeDivScreenElement<E> {
    public static final ResourceLocation HEX_FRAME_TEXTURE = ppId("textures/gui/hex_frame.png");
    public static final ResourceLocation HEX_BUBBLE_ICON_TEXTURE = ppId("textures/gui/hex_bubble_icon.png");
    public static final ResourceLocation HEX_PICKAXE_ICON_TEXTURE = ppId("textures/gui/hex_pickaxe_icon.png");
    public static final ResourceLocation HEX_JUMP_ICON_TEXTURE = ppId("textures/gui/hex_jump_icon.png");
    public static final ResourceLocation HEX_SPEED_ICON_TEXTURE = ppId("textures/gui/hex_speed_icon.png");
    public static final ResourceLocation HEX_FORTUNE_ICON_TEXTURE = ppId("textures/gui/hex_fortune_icon.png");

    public Vector2f cameraOffset;
    public Vector2f cameraVelocity;
    private float zoom;
    private float zoomVelocity;
    private static final float ZOOM_DRAG_COEFFICIENT = 0.9f; // How quickly the zoom velocity decays
    private static final float DRAG_COEFFICIENT = 0.9f;

    private final HashMap<Vector2i, Boolean> hexagons; // Cache for hexagon coordinates

    private Vector2i hex1;
    private Vector2i hex2;

    public AbilitySelectionTree(Screen screen, Settings settings, float width, float height) {
        super(screen, settings, Anchor.DEFAULT, null, width, height);

        this.cameraOffset = new Vector2f(0, 0);
        this.cameraVelocity = new Vector2f(0, 0);
        this.hexagons = new HashMap<>();
        this.hex1 = new Vector2i(8, 8);
        this.hex2 = new Vector2i(8, 10);
        this.zoom = 1.0f;
        this.dragListeners.add((mX, mY, button, dX, dY, el) -> {
            if (button == 1) {
                dragCamera((float) dX, (float) dY);
            }
        });
        this.scrollListeners.add((mX, mY, delta, el) -> {
            // Zoom in or out based on scroll input
            this.zoomVelocity += (float) (delta * 0.01f); // Adjust zoom sensitivity here
        });
        this.clickListeners.add((mX, mY, button, el) -> {
            if (button == 0) {
                // Handle left click to select a hexagon
                Vector2i hoveringHex = getMouseHexCoord(mX, mY, this.getGlobalBounds());
                hex2 = hex1;
                hex1 = hoveringHex;
            }
        });

        this.generateHexagonCache();
    }

    private void generateHexagonCache() {
        // Generate a cache of hexagon coordinates for quick access
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 12; y++) {
                if (Math.random() < 0.05) { // Randomly decide whether to cache this hexagon
                    hexagons.put(new Vector2i(x, y), true);
                }
            }
        }
    }

    @Override
    public void render(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        // Custom rendering logic for the ability selection tree can be added here
        super.render(graphics, partialTicks, mouseX, mouseY);
        IGuiGraphicsExtension graphicsExtension = (IGuiGraphicsExtension) graphics;
        Rectangle2D bounds = this.getGlobalBounds();

        Vector2i hoveringHex = getMouseHexCoord(mouseX, mouseY, bounds);
        Vector2i lastHex = new Vector2i(-1, -1); // Initialize to an invalid hex coordinate

        graphics.enableScissor((int) bounds.getMinX(), (int) bounds.getMinY(), (int) bounds.getMaxX(), (int) bounds.getMaxY());
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 12; y++) {
                // Calculate the position of the hexagon in the grid at (x, y)
                Vector2f localPosition = getHexGridLocalPosition(x, y, 20, 18, this.zoom);

                // Is mouse over this coord?
                boolean hovered = hoveringHex.x == x && hoveringHex.y == y;

                // Set highlight
                int color = ARGB.white(0.5F);
                if (hovered) {
                    // Highlight the hovered hexagon
                    color = ARGB.white(1F);
                }

                if (hexagons.containsKey(new Vector2i(x, y))) {
                    blitSpriteWithinFrame(graphics, partialTicks, HEX_FRAME_TEXTURE, localPosition.x, localPosition.y, 0, 0, 19, 22, 32, 32, color);
                    if (x % 3 == 0 && y % 2 == 0) {
                        blitSpriteWithinFrame(graphics, partialTicks, HEX_BUBBLE_ICON_TEXTURE, localPosition.x, localPosition.y, 0, 0, 17, 19, 32, 32, color);
                    }
                    if (x % 3 == 1 && y % 2 == 1) {
                        blitSpriteWithinFrame(graphics, partialTicks, HEX_PICKAXE_ICON_TEXTURE, localPosition.x, localPosition.y, 0, 0, 16, 17, 32, 32, color);
                    }
                    if (x % 3 == 2 && y % 2 == 0) {
                        blitSpriteWithinFrame(graphics, partialTicks, HEX_JUMP_ICON_TEXTURE, localPosition.x, localPosition.y, 0, 0, 16, 18, 32, 32, color);
                    }
                    if (x % 3 == 0 && y % 2 == 1) {
                        blitSpriteWithinFrame(graphics, partialTicks, HEX_SPEED_ICON_TEXTURE, localPosition.x, localPosition.y, 0, 0, 16, 17, 32, 32, color);
                    }
                    if (x % 3 == 1 && y % 2 == 0) {
                        blitSpriteWithinFrame(graphics, partialTicks, HEX_FORTUNE_ICON_TEXTURE, localPosition.x, localPosition.y, 0, 0, 15, 17, 32, 32, color);
                    }

                    if (!lastHex.equals(new Vector2i(-1, -1))) {
                        // Draw a connection between the last hex and the current hex
                        drawConnectionBetween(lastHex, new Vector2i(x, y), graphics);
                    }
                    lastHex = new Vector2i(x, y);
                }
            }
        }
        graphics.disableScissor();


//            graphics.enableScissor((int) bounds.getMinX(), (int) bounds.getMinY(), (int) bounds.getMaxX(), (int) bounds.getMaxY());
//
//            // Draw a connection between two hexagons
//            this.drawConnectionBetween(hex1, hex2, graphics);
//
//            // Draw the hexagon frames for the selected hexagons
//            Vector2f hex1Pos = getHexGridLocalPosition(hex1.x, hex1.y, 20, 18, this.zoom);
//            blitSpriteWithinFrame(graphics, partialTicks, HEX_FRAME_TEXTURE, hex1Pos.x, hex1Pos.y, 0, 0, 19, 22, 32, 32, ARGB.white(1F));
//            Vector2f hex2Pos = getHexGridLocalPosition(hex2.x, hex2.y, 20, 18, this.zoom);
//            blitSpriteWithinFrame(graphics, partialTicks, HEX_FRAME_TEXTURE, hex2Pos.x, hex2Pos.y, 0, 0, 19, 22, 32, 32, ARGB.white(1F));
//
//            graphics.disableScissor();

        // Render border as white 1px thick rectangle, 4 lines
        int minX = (int) getGlobalBounds().getMinX();
        int minY = (int) getGlobalBounds().getMinY();
        int maxX = (int) getGlobalBounds().getMaxX();
        int maxY = (int) getGlobalBounds().getMaxY();
        float thickness = 1f;

        graphicsExtension.potions_plus$fill(minX, minY, maxX, minY + thickness, 0xFFFFFFFF); // Top border
        graphicsExtension.potions_plus$fill(minX, maxY - thickness, maxX, maxY, 0xFFFFFFFF); // Bottom border
        graphicsExtension.potions_plus$fill(minX, minY, minX + thickness, maxY, 0xFFFFFFFF); // Left border
        graphicsExtension.potions_plus$fill(maxX - thickness, minY, maxX, maxY, 0xFFFFFFFF); // Right border
    }

    private @NotNull Vector2i getMouseHexCoord(int mouseX, int mouseY, Rectangle2D bounds) {
        Vector2f localMousePosition = new Vector2f(mouseX - (int) bounds.getMinX() + (int) cameraOffset.x, mouseY - (int) bounds.getMinY() + (int) cameraOffset.y);
        // Find the closest hexagonal grid coordinate based on mouse position
        Vector2i hoveringHex = getClosestHexGridCoord(localMousePosition.x, localMousePosition.y, 20, 18, this.zoom);
        return hoveringHex;
    }

    @Override
    public void onTick(float partialTick, int mouseX, int mouseY) {
        super.onTick(partialTick, mouseX, mouseY);

        if (cameraVelocity.length() > 0.01f) {
            cameraOffset.add(cameraVelocity.x, cameraVelocity.y);
            cameraVelocity.mul(DRAG_COEFFICIENT);
        }
        if (zoomVelocity > 0.01f || zoomVelocity < -0.01f) {
            zoom += zoomVelocity;
            zoom = Math.max(0.1f, Math.min(2.0f, zoom));
            zoomVelocity *= ZOOM_DRAG_COEFFICIENT;
        }
    }

    public static Vector2f getHexGridLocalPosition(int x, int y, int xOffset, int yOffset, float zoom) {
        return applyZoom(new Vector2f(x * xOffset + (y % 2 == 0 ? xOffset / 2f : 0), y * yOffset), zoom);
    }

    public static Vector2f applyZoom(Vector2f position, float zoom) {
        return new Vector2f(position.x * zoom, position.y * zoom);
    }

    public static Vector2i getClosestHexGridCoord(float localX, float localY, int xOffset, int yOffset, float zoom) {
        float scaledX = localX / zoom;
        float scaledY = localY / zoom;

        int gridY = (int) Math.floor(scaledY / ((float) yOffset));
        float xBase = scaledX - ((gridY % 2 == 0) ? xOffset / 2f : 0);
        int gridX = (int) Math.floor(xBase / ((float) xOffset));
        return new Vector2i(gridX, gridY);
    }

    public Vector2f localToGlobalCameraPosition(float localX, float localY, float partialTick) {
        Rectangle2D bounds = this.getGlobalBounds();
        return new Vector2f((float) bounds.getMinX() + localX - cameraOffset.x + cameraVelocity.x * partialTick, (float) bounds.getMinY() + localY - cameraOffset.y + cameraVelocity.x + partialTick);
    }

    public void blitSpriteWithinFrame(GuiGraphics graphics, float partialTick, ResourceLocation texture, float localX, float localY, int uOffset, int vOffset, int uWidth, int vHeight, int textureWidth, int textureHeight, int color) {
        IGuiGraphicsExtension graphicsExtension = (IGuiGraphicsExtension) graphics;
        Rectangle2D bounds = this.getGlobalBounds();

        Vector2f cameraRelativePosition = localToGlobalCameraPosition(localX, localY, partialTick);

        graphics.enableScissor((int) bounds.getMinX(), (int) bounds.getMinY(), (int) bounds.getMaxX(), (int) bounds.getMaxY());
        graphicsExtension.potions_plus$blit(RenderType::guiTextured, texture, cameraRelativePosition.x, cameraRelativePosition.y,
                uOffset, vOffset, uWidth * this.zoom, vHeight * this.zoom, uWidth, vHeight, textureWidth, textureHeight, color);
        graphics.disableScissor();
    }

    public enum HexFrameVertex {
        // Manually determiend from hex frame texture. Remember top left of pixel is (0, 0)
        TOP(9.5F, 1),
        TOP_RIGHT(18, 5),
        BOTTOM_RIGHT(18, 17),
        BOTTOM(9.5F, 21),
        BOTTOM_LEFT(1, 17),
        TOP_LEFT(1, 5);

        public final Vector2f position;

        HexFrameVertex(float x, float y) {
            this.position = new Vector2f(x, y);
        }

        public Pair<HexFrameVertex, HexFrameVertex> getConnection(Vector2i coord1, Vector2i coord2) {
            if (coord1.equals(coord2)) {
                return null; // No connection to itself
            }

            // Determine the direction of the connection based on the coordinates
            if (coord1.x == coord2.x) {
                // Vertical connection
                if (coord1.y > coord2.y) {
                    return new Pair<>(TOP, BOTTOM);
                } else {
                    return new Pair<>(BOTTOM, TOP);
                }
            } else if (coord1.y == coord2.y) {
                // Horizontal connection
                if (coord1.x < coord2.x) {
                    return new Pair<>(TOP_RIGHT, TOP_LEFT);
                } else {
                    return new Pair<>(TOP_LEFT, TOP_RIGHT);
                }
            } else {
                // Diagonal connection
                if (coord1.x < coord2.x && coord1.y > coord2.y) {
                    return new Pair<>(TOP_RIGHT, BOTTOM_LEFT);
                } else if (coord1.x < coord2.x && coord1.y < coord2.y) {
                    return new Pair<>(BOTTOM_RIGHT, TOP_LEFT);
                } else if (coord1.x > coord2.x && coord1.y > coord2.y) {
                    return new Pair<>(TOP_LEFT, BOTTOM_RIGHT);
                } else {
                    return new Pair<>(BOTTOM_LEFT, TOP_RIGHT);
                }
            }
        }
    }

    private static final int CONNECTION_COLOR = ARGB.color(120, 222, 198, 255);

    public void drawConnectionBetween(Vector2i coord1, Vector2i coord2, GuiGraphics graphics) {
        IGuiGraphicsExtension graphicsExtension = (IGuiGraphicsExtension) graphics;
        Vector2f pos1 = getHexGridLocalPosition(coord1.x, coord1.y, 20, 18, this.zoom);
        Vector2f pos2 = getHexGridLocalPosition(coord2.x, coord2.y, 20, 18, this.zoom);

        Pair<HexFrameVertex, HexFrameVertex> connection = HexFrameVertex.TOP.getConnection(coord1, coord2);
        if (connection == null) {
            return; // No connection to itself
        }

        HexFrameVertex startVertex = connection.getA();
        HexFrameVertex endVertex = connection.getB();

        Vector2f startPos = new Vector2f(pos1.x + startVertex.position.x * this.zoom, pos1.y + startVertex.position.y * this.zoom);
        startPos = localToGlobalCameraPosition(startPos.x, startPos.y, 0);
        Vector2f endPos = new Vector2f(pos2.x + endVertex.position.x * this.zoom, pos2.y + endVertex.position.y * this.zoom);
        endPos = localToGlobalCameraPosition(endPos.x, endPos.y, 0);

        // Draw a line of 1px thickness between the two hexagons
        // To do this, we offset the start and end positions by the normal of the line
        final float thickness = 0.75F * this.zoom; // Thickness of the line
        Vector2f direction = new Vector2f(endPos).sub(startPos).normalize();
        Vector2f perpendicular = new Vector2f(-direction.y, direction.x).normalize().mul(thickness);
        graphicsExtension.potions_plus$fillQuad(
                RenderType.gui(),
                startPos.x + perpendicular.x, startPos.y + perpendicular.y,
                endPos.x + perpendicular.x, endPos.y + perpendicular.y,
                endPos.x - perpendicular.x, endPos.y - perpendicular.y,
                startPos.x - perpendicular.x, startPos.y - perpendicular.y,
                0, CONNECTION_COLOR);
    }

    public void dragCamera(float deltaX, float deltaY) {
        this.cameraVelocity = new Vector2f(-deltaX, -deltaY);
    }
}
