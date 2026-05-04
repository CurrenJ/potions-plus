package grill24.potionsplus.gui;

import com.mojang.blaze3d.platform.InputConstants;
import grill24.potionsplus.utility.ClientTickHandler;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
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
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);

        if (ClientTickHandler.total() - lastTickTime > 0.25F) {
            getRootElement().tick(ClientTickHandler.total() - lastTickTime, mouseX, mouseY);
            lastTickTime = ClientTickHandler.total();
        }
        getRootElement().tryRender(graphics, partialTick, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        getRootElement().tryClick((int) event.x(), (int) event.y(), event.button());

        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        getRootElement().tryScroll((int) mouseX, (int) mouseY, scrollY);

        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
        getRootElement().tryDrag(event.x(), event.y(), event.button(), dragX, dragY);

        return super.mouseDragged(event, dragX, dragY);
    }

    // ----- Debug -----

    private boolean isShowingDebugBounds = false;
    private boolean isShowingGridLines = false;

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (event.key() == InputConstants.KEY_8) {
            isShowingDebugBounds = !isShowingDebugBounds;
            getRootElement().setShowBounds(!isShowingDebugBounds);
        } else if (event.key() == InputConstants.KEY_7) {
            getRootElement().snapToTarget();
        } else if (event.key() == InputConstants.KEY_9) {
            isShowingGridLines = !isShowingGridLines;
            getRootElement().setShowGridLines(isShowingGridLines);
        }

        return super.keyPressed(event);
    }
}
