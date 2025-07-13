package grill24.potionsplus.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import grill24.potionsplus.core.Blocks;
import grill24.potionsplus.core.DataComponents;
import grill24.potionsplus.utility.Genotype;
import grill24.potionsplus.utility.InvUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class GeneticCropBlock extends CropBlock implements EntityBlock {
    public static final MapCodec<GeneticCropBlock> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    propertiesCodec(),
                    BuiltInRegistries.ITEM.holderByNameCodec().fieldOf("crop_item").forGetter(GeneticCropBlock::getCropItem),
                    Codec.INT.fieldOf("max_age").orElse(25).forGetter(GeneticCropBlock::getMaxAge),
                    Codec.INT.fieldOf("ticks_per_age").orElse(1200).forGetter(GeneticCropBlock::getTicksPerAge),
                    Codec.INT.fieldOf("growth_ticks").orElse(1200).forGetter(GeneticCropBlock::getGrowthTicks),
                    Codec.INT.fieldOf("min_harvest_age").orElse(0).forGetter(GeneticCropBlock::getMinHarvestAge),
                    Codec.FLOAT.fieldOf("destroy_on_harvest_chance").orElse(0.0F).forGetter(GeneticCropBlock::getDestroyOnHarvestChance)
            ).apply(instance, GeneticCropBlock::new)
    );

    protected static final VoxelShape[] SHAPES = Block.boxes(7, (p_394586_) -> Block.column((double) 16.0F, (double) 0.0F, (double) (2 + p_394586_ * 2)));

    public static final IntegerProperty AGE = BlockStateProperties.AGE_25;
    public static final EnumProperty<HarvestState> HARVESTABLE = EnumProperty.create("harvestable", HarvestState.class);

    public enum HarvestState implements StringRepresentable {
        IMMATURE("immature"),
        POLLINATED("pollinated"),
        MATURE("mature");

        private final String name;

        HarvestState(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }

    private final Holder<Item> cropItem;

    private final int maxAge;
    private final int ticksPerAge;
    private final int growthTicks;
    private final int minHarvestAge;
    private final float destroyOnHarvestChance;

    public GeneticCropBlock(Properties prop, Holder<Item> cropItem, int maxAge, int ticksPerAge, int minHarvestAge, int pollinatedToMatureTicks, float destroyOnHarvestChance) {
        super(prop);

        this.cropItem = cropItem;

        this.maxAge = maxAge;
        this.ticksPerAge = ticksPerAge;
        this.growthTicks = pollinatedToMatureTicks;
        this.minHarvestAge = minHarvestAge;
        this.destroyOnHarvestChance = destroyOnHarvestChance;

        this.registerDefaultState(this.stateDefinition.any()
                .setValue(AGE, 0)
                .setValue(HARVESTABLE, HarvestState.IMMATURE));
    }

    public int getTicksPerAge() {
        return ticksPerAge;
    }

    public int getGrowthTicks() {
        return growthTicks;
    }

    public int getMinHarvestAge() {
        return minHarvestAge;
    }

    public float getDestroyOnHarvestChance() {
        return destroyOnHarvestChance;
    }

    @Override
    public MapCodec<GeneticCropBlock> codec() {
        return CODEC;
    }

    @Override
    protected ItemLike getBaseSeedId() {
        return getCropItem().value();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE).add(HARVESTABLE);
    }

    @Override
    public int getMaxAge() {
        return maxAge;
    }

    @Override
    protected IntegerProperty getAgeProperty() {
        return AGE;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES[Math.clamp(this.getAge(state), 0, SHAPES.length - 1)];
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new GeneticCropBlockEntity(blockPos, blockState);
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource randomSource) {
        if (!level.isAreaLoaded(pos, 1))
            return; // Forge: prevent loading unloaded chunks when checking neighbor's light

        tryAge(state, level, pos);
        tryPollinateNearby(level, pos);
    }

    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        return true;
    }

    private void tryAge(BlockState state, ServerLevel level, BlockPos pos) {
        if (level.getRawBrightness(pos, 0) >= 9) {
            Optional<GeneticCropBlockEntity> cropBlockEntity = level.getBlockEntity(pos, Blocks.GENETIC_CROP_BLOCK_ENTITY.value());
            if (cropBlockEntity.isPresent()) {
                HarvestState harvestState = cropBlockEntity.get().getHarvestState(getMinHarvestAge(), getTicksPerAge(), getGrowthTicks());

                int currentBlockStateAge = state.getValue(getAgeProperty());
                int desiredAge = cropBlockEntity.get().determineAge(getTicksPerAge());


                // Destroy the crop if it exceeds the maximum age, but only if it is not harvestable.
                // I am a benevolent god.
                if (desiredAge > getMaxAge() && harvestState != HarvestState.MATURE) {
                    level.destroyBlock(pos, true);
                } else if (currentBlockStateAge != desiredAge) {
                    BlockState newState = this.defaultBlockState().setValue(this.getAgeProperty(), Math.clamp(desiredAge, 0, getMaxAge())) // Get the new state for the desired age
                            .setValue(GeneticCropBlock.HARVESTABLE, harvestState); // Set the harvestable property
                    level.setBlock(pos, newState, 2);
                    net.neoforged.neoforge.common.CommonHooks.fireCropGrowPost(level, pos, state);
                }

            }
        }
    }

    private void tryPollinateNearby(ServerLevel level, BlockPos pos) {
        // Pollinate nearby crops
        Genotype myGenotype = level.getBlockEntity(pos, Blocks.GENETIC_CROP_BLOCK_ENTITY.value())
                .map(GeneticCropBlockEntity::getGenotype)
                .orElse(new Genotype());

        for (BlockPos nearbyPos : BlockPos.betweenClosed(pos.offset(-1, 0, -1), pos.offset(1, 0, 1))) {
            if (nearbyPos.equals(pos)) continue; // Skip the current crop block

            final float chance = 0.2F;
            if (level.random.nextFloat() > chance) continue; // Random chance to pollinate

            Optional<GeneticCropBlockEntity> cropBlockEntity = level.getBlockEntity(nearbyPos, Blocks.GENETIC_CROP_BLOCK_ENTITY.value());
            cropBlockEntity.ifPresent(blockEntity -> blockEntity.onPollinate(myGenotype));
        }
    }

    @Override
    public void setPlacedBy(Level level, BlockPos placedAt, BlockState blockState, @javax.annotation.Nullable LivingEntity placer, ItemStack itemStack) {
        super.setPlacedBy(level, placedAt, blockState, placer, itemStack);
        Optional<GeneticCropBlockEntity> cropBlockEntity = level.getBlockEntity(placedAt, Blocks.GENETIC_CROP_BLOCK_ENTITY.value());
        cropBlockEntity.ifPresent(entity -> {
            entity.onPlace(itemStack);
        });
    }

    public Holder<Item> getCropItem() {
        return cropItem;
    }

    @Override
    public InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        // Harvest the crop if it is ready
        InteractionResult harvested = tryHarvest(state, level, pos, player);
        if (harvested != InteractionResult.PASS) {
            return harvested;
        }

        InteractionResult pollinated = tryPollinate(stack, level, pos);
        if (pollinated != InteractionResult.PASS) {
            return pollinated;
        }

        return InteractionResult.PASS;
    }

    private InteractionResult tryPollinate(ItemStack stack, Level level, BlockPos pos) {
        if (stack.is(getCropItem())) {
            Genotype genotype = stack.getOrDefault(DataComponents.GENETIC_DATA, new Genotype());
            Optional<GeneticCropBlockEntity> cropBlockEntity = level.getBlockEntity(pos, Blocks.GENETIC_CROP_BLOCK_ENTITY.value());
            if (cropBlockEntity.isPresent()) {
                GeneticCropBlockEntity entity = cropBlockEntity.get();

                if (entity.onPollinate(genotype) && !level.isClientSide) {
                    stack.shrink(1); // Remove one seed from the stack
                    return InteractionResult.SUCCESS;
                }
            }
        }

        return InteractionResult.PASS;
    }

    private static InteractionResult tryHarvest(BlockState state, Level level, BlockPos pos, Player player) {
        if (state.getValue(HARVESTABLE) == HarvestState.MATURE) {
            Optional<GeneticCropBlockEntity> cropBlockEntity = level.getBlockEntity(pos, Blocks.GENETIC_CROP_BLOCK_ENTITY.value());
            if (cropBlockEntity.isPresent() && state.getBlock() instanceof GeneticCropBlock geneticCropBlock && !level.isClientSide) {
                ItemStack harvestedItem = cropBlockEntity.get().getOffspring();
                InvUtil.giveOrDropItem(player, harvestedItem);

                GeneticCropBlockEntity entity = cropBlockEntity.get();
                entity.onHarvest();

                HarvestState newHarvestState = entity.getHarvestState(geneticCropBlock.getMinHarvestAge(), geneticCropBlock.getTicksPerAge(), geneticCropBlock.getGrowthTicks());
                level.setBlock(pos, state.setValue(GeneticCropBlock.HARVESTABLE, newHarvestState), 3);
                if (geneticCropBlock.getDestroyOnHarvestChance() > 0.0F && level.random.nextFloat() < geneticCropBlock.getDestroyOnHarvestChance()) {
                    // If multiple harvests are not allowed, destroy the crop block
                    level.destroyBlock(pos, true);
                }

                return InteractionResult.SUCCESS;
            }

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }
}
