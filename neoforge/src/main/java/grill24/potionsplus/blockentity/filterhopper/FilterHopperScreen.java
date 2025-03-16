package grill24.potionsplus.blockentity.filterhopper;

import grill24.potionsplus.network.ServerboundSetupFilterHopperFromContainerPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;

import static grill24.potionsplus.utility.Utility.ppId;

@OnlyIn(Dist.CLIENT)
public abstract class FilterHopperScreen<M extends FilterHopperMenu> extends AbstractContainerScreen<M> {
    /**
     * The ResourceLocation containing the gui texture for the hopper
     */

    public FilterHopperScreen(M menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageHeight = 205;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    public void init() {
        super.init();

        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        this.addRenderableWidget(Button.builder(Component.literal(" "), (button) ->
                PacketDistributor.sendToServer(new ServerboundSetupFilterHopperFromContainerPacket()))
                .pos(i + 31, j + 22).size(8, 8).build());
    }

    /**
     * Renders the graphical user interface (GUI) element.
     *
     * @param guiGraphics the GuiGraphics object used for rendering.
     * @param mouseX      the x-coordinate of the mouse cursor.
     * @param mouseY      the y-coordinate of the mouse cursor.
     * @param partialTick the partial tick time.
     */
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(getTexture(), i, j, 0, 0, this.imageWidth, this.imageHeight);
    }

    abstract ResourceLocation getTexture();
}
