package grill24.potionsplus.core.forge;

import grill24.potionsplus.block.ClotheslineBlock;
import grill24.potionsplus.blockentity.ClotheslineBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

public final class Capabilities {
    private Capabilities() {
    }

    public static void register() {
        AttachCapabilitiesEvent.BlockEntities.BUS.addListener((AttachCapabilitiesEvent.BlockEntities event) -> {
            BlockEntity be = event.getObject();
            if (!(be instanceof ClotheslineBlockEntity)) {
                return;
            }

            BlockEntity leftBe = be.getLevel() == null ? null
                    : be.getLevel().getBlockEntity(ClotheslineBlock.getLeftEnd(be.getBlockPos(), be.getBlockState()));
            if (!(leftBe instanceof ClotheslineBlockEntity leftClotheslineBlockEntity)) {
                return;
            }

            IItemHandler handler = new InvWrapper(leftClotheslineBlockEntity);
            event.addCapability(grill24.potionsplus.utility.Utility.ppId("clothesline_item_handler"), new net.minecraftforge.common.capabilities.ICapabilityProvider() {
                @Override
                public <T> LazyOptional<T> getCapability(net.minecraftforge.common.capabilities.Capability<T> cap, net.minecraft.core.Direction side) {
                    if (cap == ForgeCapabilities.ITEM_HANDLER) {
                        return LazyOptional.of(() -> handler).cast();
                    }
                    return LazyOptional.empty();
                }
            });
        });
    }
}
