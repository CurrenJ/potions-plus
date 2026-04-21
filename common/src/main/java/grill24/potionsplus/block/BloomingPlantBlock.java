package grill24.potionsplus.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class BloomingPlantBlock extends VersatilePlantBlock {
    public static final MapCodec<BloomingPlantBlock> CODEC = RecordCodecBuilder.mapCodec(codecBuilder -> codecBuilder.group(
            propertiesCodec(),
            CONFIG_CODEC.fieldOf("config").forGetter(instance -> instance.config),
            Codec.INT.optionalFieldOf("max_blooming", 15).forGetter(BloomingPlantBlock::getMaxBlooming)
    ).apply(codecBuilder, BloomingPlantBlock::new));

    public static IntegerProperty BLOOMING = IntegerProperty.create("blooming", 0, 15);

    public int maxBlooming;

    @Override
    public MapCodec<? extends VersatilePlantBlock> codec() {
        return CODEC;
    }

    public BloomingPlantBlock(Properties properties, VersatilePlantConfig versatilePlantConfig, int maxBlooming) {
        super(properties, versatilePlantConfig);

        this.registerDefaultState(this.stateDefinition.any().setValue(SEGMENT, 0).setValue(BLOOMING, 0));
        this.maxBlooming = maxBlooming;
    }

    public int getMaxBlooming() {
        return maxBlooming;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(BLOOMING);
    }

    @Override
    public BlockState setPropertiesOnPlacement(LevelAccessor levelAccessor, BlockState state, BlockPos pos, int segment, int plantLength) {
        return super.setPropertiesOnPlacement(levelAccessor, state, pos, segment, plantLength).setValue(BLOOMING, levelAccessor.getRandom().nextInt(maxBlooming + 1));
    }
}
