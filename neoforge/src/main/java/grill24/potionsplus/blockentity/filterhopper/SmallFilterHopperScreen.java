package grill24.potionsplus.blockentity.filterhopper;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import static grill24.potionsplus.utility.Utility.ppId;

public class SmallFilterHopperScreen extends FilterHopperScreen<SmallFilterHopperMenu> {
    private static final ResourceLocation TEX = ppId("textures/gui/container/filter_hopper.png");

    public SmallFilterHopperScreen(SmallFilterHopperMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    ResourceLocation getTexture() {
        return TEX;
    }
}
