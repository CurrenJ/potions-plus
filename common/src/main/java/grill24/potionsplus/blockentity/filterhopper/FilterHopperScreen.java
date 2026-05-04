package grill24.potionsplus.blockentity.filterhopper;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import grill24.potionsplus.network.ServerboundSetupFilterHopperFromContainerPacket;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;


import grill24.potionsplus.platform.PacketNetwork;


@Environment(EnvType.CLIENT)
public abstract class FilterHopperScreen<M extends FilterHopperMenu> extends AbstractContainerScreen<M> {
    private final int autoCreateFilterButtonX;
    private final int autoCreateFilterButtonY;

    public FilterHopperScreen(M menu, Inventory playerInventory, Component title, int imageWidth, int imageHeight, int autoCreateFilterButtonX, int autoCreateFilterButtonY, int titleX, int titleY) {
        super(menu, playerInventory, title, imageWidth, imageHeight);

        this.autoCreateFilterButtonX = autoCreateFilterButtonX;
        this.autoCreateFilterButtonY = autoCreateFilterButtonY;

        this.titleLabelX = titleX;
        this.titleLabelY = titleY;
    }

    @Override
    public void init() {
        super.init();

        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        this.addRenderableWidget(Button.builder(Component.literal(" "), (button) ->
                        PacketNetwork.sendToServer(new ServerboundSetupFilterHopperFromContainerPacket()))
                .pos(i + autoCreateFilterButtonX, j + autoCreateFilterButtonY).size(8, 8).build());
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor GuiGraphicsExtractor, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(GuiGraphicsExtractor, mouseX, mouseY, partialTick);
        this.extractTooltip(GuiGraphicsExtractor, mouseX, mouseY);
    }

    @Override
    public void extractContents(GuiGraphicsExtractor GuiGraphicsExtractor, int mouseX, int mouseY, float partialTick) {
        super.extractContents(GuiGraphicsExtractor, mouseX, mouseY, partialTick);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        GuiGraphicsExtractor.blit(RenderPipelines.GUI_TEXTURED, getTexture(), i, j, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
    }

    abstract Identifier getTexture();
}
