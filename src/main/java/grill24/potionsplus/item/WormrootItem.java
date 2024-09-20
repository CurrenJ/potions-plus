package grill24.potionsplus.item;

import grill24.potionsplus.core.Items;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class WormrootItem extends Item {
    private static final int CONVERSION_TICKS = 1200; // 1 minute

    public WormrootItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        if (entity.getAge() > CONVERSION_TICKS) {
            Level level = entity.level();
            boolean isInWater = level.isWaterAt(entity.blockPosition());
            if (isInWater) {
                for (int i = 0; i < entity.getItem().getCount(); i++) {
                    Block.popResource(level, entity.blockPosition(), new ItemStack(Items.ROTTEN_WORMROOT.get()));
                }
                entity.discard();
                return true;
            }
        }

        return false;
    }
}
