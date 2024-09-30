package grill24.potionsplus.core;

import grill24.potionsplus.block.ClotheslineBlock;
import grill24.potionsplus.blockentity.ClotheslineBlockEntity;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.core.Direction;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.InvWrapper;

import static grill24.potionsplus.utility.Utility.ppId;

@EventBusSubscriber(modid = ModInfo.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class Capabilities {
    public static final BlockCapability<IItemHandler, Direction> CLOTHESLINE_ITEM_HANDLER =
            BlockCapability.createSided(
                    // Provide a name to uniquely identify the capability.
                    ppId("clothesline_item_handler"),
                    // Provide the queried type. Here, we want to look up `IItemHandler` instances.
                    IItemHandler.class);

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlock(
                CLOTHESLINE_ITEM_HANDLER, // capability to register for
                (level, pos, state, be, side) -> {
                    if (be instanceof ClotheslineBlockEntity clotheslineBlockEntity) {
                        ClotheslineBlockEntity leftClotheslineBlockEntity = level.getBlockEntity(ClotheslineBlock.getLeftEnd(pos, state), Blocks.CLOTHESLINE_BLOCK_ENTITY.get()).orElse(null);
                        if (leftClotheslineBlockEntity == null) {
                            return null;
                        }

                        return new InvWrapper(leftClotheslineBlockEntity);
                    }
                    return null;
                },
                // blocks to register for
                Blocks.CLOTHESLINE.value());
    }

}
