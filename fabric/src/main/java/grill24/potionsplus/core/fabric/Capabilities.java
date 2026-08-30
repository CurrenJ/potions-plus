package grill24.potionsplus.core.fabric;

import grill24.potionsplus.block.ClotheslineBlock;
import grill24.potionsplus.blockentity.ClotheslineBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.item.ContainerStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;

public final class Capabilities {
    private Capabilities() {
    }

    public static void register() {
        ItemStorage.SIDED.registerForBlockEntity((be, direction) -> {
            if (be.getLevel() == null) {
                return null;
            }
            var leftBe = be.getLevel().getBlockEntity(ClotheslineBlock.getLeftEnd(be.getBlockPos(), be.getBlockState()));
            if (leftBe instanceof ClotheslineBlockEntity leftClotheslineBlockEntity) {
                return ContainerStorage.of(leftClotheslineBlockEntity, direction);
            }
            return null;
        }, grill24.potionsplus.core.Blocks.CLOTHESLINE_BLOCK_ENTITY.value());
    }
}
