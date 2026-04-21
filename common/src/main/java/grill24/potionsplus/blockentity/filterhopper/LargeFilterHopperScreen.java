package grill24.potionsplus.blockentity.filterhopper;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import static grill24.potionsplus.utility.Utility.ppId;

public class LargeFilterHopperScreen extends FilterHopperScreen<LargeFilterHopperMenu> {
    private static final ResourceLocation TEX = ppId("textures/gui/container/filter_hopper_large.png");

    public LargeFilterHopperScreen(LargeFilterHopperMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, 32, 23, 8, 6);

        this.imageWidth = 195;
        this.imageHeight = 233;
    }

    @Override
    ResourceLocation getTexture() {
        return TEX;
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);
    }
}
