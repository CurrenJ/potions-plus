package grill24.potionsplus.gui;

import com.mojang.blaze3d.platform.InputConstants;
import grill24.potionsplus.utility.ClientTickHandler;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

public abstract class PotionsPlusScreen<M extends AbstractContainerMenu> extends AbstractContainerScreen<M> {
    protected final float screenOpenedTimestamp;
    private float lastTickTime = 0;

    public PotionsPlusScreen(M menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.screenOpenedTimestamp = ClientTickHandler.total();
    }

    protected abstract RenderableScreenElement getRootElement();

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);

        if (ClientTickHandler.total() - lastTickTime > 0.25F) {
            getRootElement().tick(ClientTickHandler.total() - lastTickTime, mouseX, mouseY);
            lastTickTime = ClientTickHandler.total();
        }
        getRootElement().tryRender(graphics, partialTick, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        // Don't.
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        // Don't.
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        getRootElement().tryClick((int) mouseX, (int) mouseY);

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        getRootElement().tryScroll((int) mouseX, (int) mouseY, scrollY);

        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    // ----- Debug -----

    private boolean isShowingDebugBounds = false;
    private boolean isShowingGridLines = false;
    // In some Screen subclass
    @Override
    public boolean keyPressed(int key, int scancode, int mods) {
        InputConstants.Key keyPress = InputConstants.getKey(key, scancode);

        if (keyPress.getValue() == InputConstants.KEY_8) {
            isShowingDebugBounds = !isShowingDebugBounds;
            getRootElement().setShowBounds(!isShowingDebugBounds);
        } else if (keyPress.getValue() == InputConstants.KEY_7) {
            getRootElement().snapToTarget();
        } else if (keyPress.getValue() == InputConstants.KEY_9) {
            isShowingGridLines = !isShowingGridLines;
            getRootElement().setShowGridLines(isShowingGridLines);
        }

        return super.keyPressed(key, scancode, mods);
    }
}
