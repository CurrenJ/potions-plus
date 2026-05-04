package grill24.potionsplus.blockentity.filterhopper;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

import static grill24.potionsplus.utility.Utility.ppId;

public class HugeFilterHopperScreen extends FilterHopperScreen<HugeFilterHopperMenu> {
    private static final Identifier TEX = ppId("textures/gui/container/filter_hopper_huge.png");

    public HugeFilterHopperScreen(HugeFilterHopperMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, 68, 29, 8, 12);

        this.imageWidth = 256;
        this.imageHeight = 239;
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
