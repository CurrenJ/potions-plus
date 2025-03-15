package grill24.potionsplus.blockentity;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.HopperMenu;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import static grill24.potionsplus.utility.Utility.ppId;

@OnlyIn(Dist.CLIENT)
public class FilterHopperScreen extends AbstractContainerScreen<FilterHopperMenu> {
    /**
     * The ResourceLocation containing the gui texture for the hopper
     */
    private static final ResourceLocation FILTER_HOPPER_LOCATION = ppId("textures/gui/container/filter_hopper.png");

    public FilterHopperScreen(FilterHopperMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageHeight = 205;
        this.inventoryLabelY = this.imageHeight - 94;
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
        guiGraphics.blit(FILTER_HOPPER_LOCATION, i, j, 0, 0, this.imageWidth, this.imageHeight);
    }
}
