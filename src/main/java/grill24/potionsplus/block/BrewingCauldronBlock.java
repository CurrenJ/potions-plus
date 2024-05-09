package grill24.potionsplus.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.CauldronBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class BrewingCauldronBlock extends CauldronBlock implements EntityBlock {
    public BrewingCauldronBlock(Properties p_51403_) {
        super(p_51403_);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
        return null;
    }
}
