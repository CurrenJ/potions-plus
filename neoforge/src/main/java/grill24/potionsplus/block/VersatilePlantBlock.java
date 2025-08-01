package grill24.potionsplus.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.TriState;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.VegetationBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;
import java.util.Set;
import java.util.function.BiConsumer;

import static net.minecraft.world.level.block.DoublePlantBlock.copyWaterloggedFrom;

public class VersatilePlantBlock extends VegetationBlock {
    public record VersatilePlantConfig(boolean requiresSupportBehind, boolean requiresSupportAfter, int minSegmentIndex,
                                       int maxSegmentIndex, VersatilePlantBlockTexturePattern texturePattern) {
    }

    protected static final MapCodec<VersatilePlantConfig> CONFIG_CODEC = RecordCodecBuilder.mapCodec(codecBuilder -> codecBuilder.group(
            Codec.BOOL.optionalFieldOf("requiresSupportBehind", false).forGetter(VersatilePlantConfig::requiresSupportBehind),
            Codec.BOOL.optionalFieldOf("requiresSupportAfter", false).forGetter(VersatilePlantConfig::requiresSupportAfter),
            Codec.INT.optionalFieldOf("minSegmentIndex", 0).forGetter(VersatilePlantConfig::minSegmentIndex),
            Codec.INT.optionalFieldOf("maxSegmentIndex", 0).forGetter(VersatilePlantConfig::maxSegmentIndex),
            VersatilePlantBlockTexturePattern.CODEC.fieldOf("texturePattern").forGetter(VersatilePlantConfig::texturePattern)
    ).apply(codecBuilder, VersatilePlantConfig::new));

    public static final MapCodec<VersatilePlantBlock> CODEC = RecordCodecBuilder.mapCodec(codecBuilder -> codecBuilder.group(
            propertiesCodec(),
            CONFIG_CODEC.fieldOf("config").forGetter(instance -> instance.config)
    ).apply(codecBuilder, VersatilePlantBlock::new));

    public static final IntegerProperty SEGMENT = IntegerProperty.create("segment", 0, 15); // 0 is the "base" segment, max is the distance away from the base of the furthest segment
    public static final EnumProperty<Direction> FACING = EnumProperty.create("facing", Direction.class); // UP = default plant orientation, down = hanging plant orientation, other directions are for plants mounted on walls
    public static final IntegerProperty TEXTURE_INDEX = IntegerProperty.create("texture_index", 0, 15);

    protected final VersatilePlantConfig config;

    @Override
    public MapCodec<? extends VersatilePlantBlock> codec() {
        return CODEC;
    }

    public VersatilePlantBlock(BlockBehaviour.Properties properties, VersatilePlantConfig config) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(SEGMENT, 0));

        this.config = config;
    }

    public boolean requiresSupportBehind() {
        return config.requiresSupportBehind();
    }

    public boolean requiresSupportAfter() {
        return config.requiresSupportAfter();
    }

    public int getMaxSegmentIndex() {
        return config.maxSegmentIndex();
    }


    // ----- Methods below here need to be changed!!

    @Override
    protected BlockState updateShape(
            BlockState state,
            LevelReader level,
            ScheduledTickAccess scheduledTickAccess,
            BlockPos pos,
            Direction direction,
            BlockPos neighborPos,
            BlockState neighborState,
            RandomSource random
    ) {
        if (!state.canSurvive(level, pos)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, level, scheduledTickAccess, pos, direction, neighborPos, neighborState, random);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos blockpos = context.getClickedPos();
        Level level = context.getLevel();

        Direction facing = context.getClickedFace();
        // Check all segments can be placed and are withing build height
        for (int i = 0; i <= config.minSegmentIndex(); i++) {
            BlockPos currentPos = blockpos.relative(facing, i);
            if (currentPos.getY() > level.getMaxY() || !level.getBlockState(currentPos).canBeReplaced(context)) {
                return null;
            }
        }

        return super.getStateForPlacement(context).setValue(FACING, context.getClickedFace());
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        placeAt(level, state, pos, config.minSegmentIndex() + 1, 3);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        int segment = state.getValue(SEGMENT);
        Direction facing = state.getValue(FACING);

        boolean canSurvive = true;
        if (requiresSupportBehind()) {
            BlockPos behindPos = pos.relative(facing.getOpposite());
            BlockState behindState = level.getBlockState(behindPos);
            if (segment != 0) {
                canSurvive &= behindState.is(this) && behindState.getValue(SEGMENT) == segment - 1;
            } else {
                canSurvive &= canSurviveFacing(state, level, pos, facing);
            }
        }
        if (requiresSupportAfter()) {
            BlockPos afterPos = pos.relative(facing);
            BlockState afterState = level.getBlockState(afterPos);
            if (segment != getMaxSegmentIndex()) {
                canSurvive &= afterState.is(this) && afterState.getValue(SEGMENT) == segment + 1;
            }
        }
        return canSurvive;
    }

    /**
     * Copy of canSurvive in BushBlock method adapted to work with a specific face
     *
     * @param state  The block state of the plant
     * @param level  The level
     * @param pos    The position of the plant
     * @param facing The direction the plant is facing (towards sky = up, towards ground = down, other directions are for wall mounted plants)
     * @return Whether the plant can survive in the given position
     */
    public boolean canSurviveFacing(BlockState state, LevelReader level, BlockPos pos, Direction facing) {
        BlockPos blockpos = pos.relative(facing.getOpposite());
        BlockState belowBlockState = level.getBlockState(blockpos);
        TriState soilDecision = belowBlockState.canSustainPlant(level, blockpos, facing.getOpposite(), state);
        if (!soilDecision.isDefault()) return soilDecision.isTrue();
        return this.mayPlaceOn(belowBlockState, level, blockpos);
    }

    public void placeAt(LevelAccessor level, BlockState state, BlockPos basePos, int length, int flags) {
        int segmentsToPlace = Math.min(length, getMaxSegmentIndex() + 1);
        for (int i = 0; i < segmentsToPlace; i++) {
            BlockPos currentPos = basePos.relative(state.getValue(FACING), i);

            BlockState existingState = level.getBlockState(currentPos);
            BlockState placedState = copyWaterloggedFrom(level, currentPos, state);
            placedState = setPropertiesOnPlacement(level, placedState, currentPos, i, segmentsToPlace);

            if (existingState.canBeReplaced() || existingState.is(this)) {
                level.setBlock(currentPos, placedState, flags);
            } else {
                break;
            }
        }
    }

    public BlockState setPropertiesOnPlacement(LevelAccessor levelAccessor, BlockState state, BlockPos pos, int segment, int plantLength) {
        return setTextureIndex(levelAccessor, state.setValue(SEGMENT, segment), pos, plantLength);
    }

    public boolean extend(LevelAccessor level, BlockState state, BlockPos pos, int flags) {
        BlockPos basePos = getBasePos(pos, state);
        int extendedLength = getCurrentPlantSegments(level, pos) + 1;

        if (extendedLength <= getMaxSegmentIndex() + 1 && level.getBlockState(basePos.relative(state.getValue(FACING), extendedLength - 1)).canBeReplaced()) {
            placeAt(level, state, basePos, extendedLength, flags);
            return true;
        } else {
            return false;
        }
    }

    public static BlockPos getBasePos(BlockPos pos, BlockState state) {
        return pos.relative(state.getValue(FACING).getOpposite(), state.getValue(SEGMENT));
    }

    public BlockPos getTailPos(LevelAccessor levelAccessor, BlockPos pos, BlockState state) {
        BlockPos.MutableBlockPos mutable = getBasePos(pos, state).mutable();
        BlockState currentState = levelAccessor.getBlockState(mutable);
        while (currentState.is(this) && currentState.getValue(SEGMENT) <= getMaxSegmentIndex()) {
            mutable.move(state.getValue(FACING));
            currentState = levelAccessor.getBlockState(mutable);
        }

        mutable.move(state.getValue(FACING).getOpposite());
        return mutable.immutable();
    }

    public int getCurrentPlantSegments(LevelAccessor levelAccessor, BlockPos pos) {
        BlockState state = levelAccessor.getBlockState(pos);
        if (state.is(this)) {
            BlockPos basePos = getBasePos(pos, state);
            BlockPos tailPos = getTailPos(levelAccessor, pos, state);
            return tailPos.distManhattan(basePos) + 1;
        }
        return 0;
    }

    public void forSegments(LevelAccessor levelAccessor, BlockPos pos, BiConsumer<Integer, BlockPos> consumer) {
        BlockState state = levelAccessor.getBlockState(pos);
        if (state.is(this)) {
            BlockPos basePos = getBasePos(pos, state);
            int currentPlantSegments = getCurrentPlantSegments(levelAccessor, pos);
            for (int segmentIndex = 0; segmentIndex <= currentPlantSegments; segmentIndex++) {
                consumer.accept(segmentIndex, basePos.relative(state.getValue(FACING), segmentIndex));
            }
        }
    }

    /**
     * Given a versatile plant block state, set the textureIndex property to the correct value from the texture pattern.
     *
     * @param levelAccessor The level accessor
     * @param pos           The position of the block
     * @return The modified block state
     */
    protected BlockState setTextureIndex(LevelAccessor levelAccessor, BlockState state, BlockPos pos, int plantLength) {
        if (state.is(this)) {
            int segment = state.getValue(SEGMENT);
            int textureIndex = ((VersatilePlantBlock) state.getBlock()).calculateTextureIndex(segment, plantLength);

            return state.setValue(TEXTURE_INDEX, textureIndex);
        }

        return state;
    }

    public Set<Integer> getUsedTextures() {
        return config.texturePattern().getUsedTextures();
    }

    public int calculateTextureIndex(int segment, int plantLength) {
        return config.texturePattern().calculateTextureIndex(segment, plantLength);
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide) {
            if (player.isCreative()) {
                // TODO: Implement this method
                // preventDropFromNonBaseSegments(level, pos, state, player);
            } else {
                dropResources(state, level, pos, null, player, player.getMainHandItem());
            }

            // Update the textures of the remaining plant segments
            int currentPlantSegments = state.getValue(SEGMENT);
            forSegments(level, pos, (segmentIndex, blockPos) -> {
                if (segmentIndex <= currentPlantSegments) {
                    BlockState blockState = level.getBlockState(blockPos);
                    blockState = setTextureIndex(level, blockState, blockPos, currentPlantSegments);
                    level.setBlock(blockPos, blockState, 3);
                }
            });
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity te, ItemStack stack) {
        super.playerDestroy(level, player, pos, Blocks.AIR.defaultBlockState(), te, stack);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);

        builder.add(SEGMENT, FACING, TEXTURE_INDEX);
    }

    @Override
    protected long getSeed(BlockState state, BlockPos pos) {
        return Mth.getSeed(pos.getX(), pos.relative(state.getValue(FACING).getOpposite(), state.getValue(SEGMENT)).getY(), pos.getZ());
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (stack.is(this.asItem())) {
            if (extend(level, state, pos, 3)) {
                if (!player.isCreative()) {
                    stack.shrink(1);
                }
                level.playSound(null, pos, this.getSoundType(state, level, pos, player).getPlaceSound(), SoundSource.BLOCKS, 1.0F, 1.0F);
            }

            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }
}
