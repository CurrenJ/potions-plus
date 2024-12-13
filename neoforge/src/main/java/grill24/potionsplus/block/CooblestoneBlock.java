package grill24.potionsplus.block;

import grill24.potionsplus.core.Blocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class CooblestoneBlock extends Block {
    public CooblestoneBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (entity instanceof Player) {
            // Look for icicle above
            final int searchDistance = 32;
            for (int i = 1; i <= searchDistance; i++) {
                BlockPos above = pos.above(i);
                BlockState aboveState = level.getBlockState(above);
                if (aboveState.is(Blocks.ICICLE.value())) {
                    BlockPos aboveAbove = above.above();
                    BlockState aboveAboveState = level.getBlockState(aboveAbove);
                    if(!aboveAboveState.is(Blocks.ICICLE.value()) && !aboveAboveState.isAir()) {
                        // Make icicle fall on player hehehehe
                        level.destroyBlock(above, true);

                        return;
                    }
                }
            }
        }
    }
}
