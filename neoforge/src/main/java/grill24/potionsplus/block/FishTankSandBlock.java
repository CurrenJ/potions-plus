package grill24.potionsplus.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class FishTankSandBlock extends Block {
    public static final IntegerProperty SAND_VARIANT = IntegerProperty.create("sand", 0, 63);

    public FishTankSandBlock(Properties properties) {
        super(properties);

        registerDefaultState(this.stateDefinition.any().setValue(SAND_VARIANT, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> blockStateBuilder) {
        super.createBlockStateDefinition(blockStateBuilder);
        blockStateBuilder.add(SAND_VARIANT);
    }
}
