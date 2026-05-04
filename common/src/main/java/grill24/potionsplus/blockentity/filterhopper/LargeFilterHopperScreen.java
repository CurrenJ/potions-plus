package grill24.potionsplus.blockentity.filterhopper;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

import static grill24.potionsplus.utility.Utility.ppId;

public class LargeFilterHopperScreen extends FilterHopperScreen<LargeFilterHopperMenu> {
    private static final Identifier TEX = ppId("textures/gui/container/filter_hopper_large.png");

    public LargeFilterHopperScreen(LargeFilterHopperMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, 32, 23, 8, 6);

        this.imageWidth = 195;
        this.imageHeight = 233;
    }

    @Override
    Identifier getTexture() {
        return TEX;
    }

    @Override
    protected void renderLabels(GuiGraphicsExtractor GuiGraphicsExtractor, int mouseX, int mouseY) {
        GuiGraphicsExtractor.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);
    }
}
