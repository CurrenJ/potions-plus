package grill24.potionsplus.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class FishTankFrameBlock extends Block {
    public static final IntegerProperty FRAME_VARIANT = IntegerProperty.create("frame", 0, 200);

    public FishTankFrameBlock(Properties properties) {
        super(properties);

        registerDefaultState(this.stateDefinition.any().setValue(FRAME_VARIANT, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> blockStateBuilder) {
        super.createBlockStateDefinition(blockStateBuilder);
        blockStateBuilder.add(FRAME_VARIANT);
    }
}
