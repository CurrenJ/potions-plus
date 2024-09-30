package grill24.potionsplus.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Random;

public class DecorativeFireBlock extends FireBlock {
    public DecorativeFireBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockState updateShape(BlockState p_53458_, Direction p_53459_, BlockState p_53460_, LevelAccessor p_53461_, BlockPos p_53462_, BlockPos p_53463_) {
        return this.canSurvive(p_53458_, p_53461_, p_53462_) ? this.defaultBlockState() : Blocks.AIR.defaultBlockState();
    }

    @Override
    public void tick(BlockState p_221160_, ServerLevel p_221161_, BlockPos p_221162_, RandomSource p_221163_) {
        // Do nothing
    }

        @Override
    public void entityInside(BlockState p_49260_, Level p_49261_, BlockPos p_49262_, Entity p_49263_) {
        // Do nothing
    }
}
