package grill24.potionsplus.block;

import grill24.potionsplus.particle.ParticleConfigurations;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.Supplier;

public class ParticleEmitterBlock extends Block {
    public static final Supplier<ParticleEmitterConfiguration>[] PARTICLE_EMITTER_CONFIGURATIONS = new Supplier[]{
            () -> ParticleConfigurations.END_ROD_RAIN,
            () -> ParticleConfigurations.BLUE_FIREY,
            () -> ParticleConfigurations.RUNES,
            () -> ParticleConfigurations.PORTAL,
            () -> ParticleConfigurations.BUBBLES,
            () -> ParticleConfigurations.WANDERING_HEARTS,
            () -> ParticleConfigurations.FIREY,
            () -> ParticleConfigurations.MUSICAL,
            () -> ParticleConfigurations.END_ROD_RAIN_EMITTER,
            () -> ParticleConfigurations.FIREY_EMITTER,
    };
    public static final IntegerProperty PARTICLE_TYPE = IntegerProperty.create("particle_type", 0, PARTICLE_EMITTER_CONFIGURATIONS.length - 1);

    public static final Map<Item, Integer> PARTICLE_INTENSITY_MAP = Map.of(
            Items.DIRT, 0,
            Items.IRON_INGOT, 1,
            Items.GOLD_INGOT, 2,
            Items.DIAMOND, 3
    );
    public static final int MAX_PARTICLE_INTENSITY = 3;
    public static final IntegerProperty PARTICLE_INTENSITY = IntegerProperty.create("particle_intensity", 0, MAX_PARTICLE_INTENSITY);

    public static final BooleanProperty ENABLED = BooleanProperty.create("enabled");

    public ParticleEmitterBlock(Properties p_49795_) {
        super(p_49795_);
        registerDefaultState(this.stateDefinition.any().setValue(PARTICLE_TYPE, 0).setValue(PARTICLE_INTENSITY, 0).setValue(ENABLED, false));
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext p_55659_) {
        return this.defaultBlockState().setValue(ENABLED, Boolean.valueOf(p_55659_.getLevel().hasNeighborSignal(p_55659_.getClickedPos())));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(PARTICLE_TYPE);
        stateBuilder.add(PARTICLE_INTENSITY);
        stateBuilder.add(ENABLED);
    }

    @Override
    public ItemInteractionResult useItemOn(ItemStack stack, BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!level.isClientSide) {
            Item itemInHand = stack.getItem();
            if (PARTICLE_INTENSITY_MAP.containsKey(itemInHand)) {
                int previousParticleIntensity = blockState.getValue(PARTICLE_INTENSITY);
                int newParticleIntensity = PARTICLE_INTENSITY_MAP.get(itemInHand);
                if (newParticleIntensity == previousParticleIntensity) {
                    return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
                } else {
                    level.setBlockAndUpdate(blockPos, blockState.setValue(PARTICLE_INTENSITY, newParticleIntensity));

                    if (previousParticleIntensity < newParticleIntensity) {
                        level.playSound(null, blockPos, SoundEvents.RESPAWN_ANCHOR_CHARGE, SoundSource.BLOCKS, 1.0F, 1.0F);
                    } else {
                        level.playSound(null, blockPos, SoundEvents.RESPAWN_ANCHOR_DEPLETE.value(), SoundSource.BLOCKS, 1.0F, 1.0F);
                    }

                    if (!player.getAbilities().instabuild) {
                        ItemStack handStack = player.getItemInHand(hand);
                        handStack.setCount(handStack.getCount() - 1);
                        player.setItemInHand(hand, handStack);
                    }
                }
            } else {
                int particleType = blockState.getValue(PARTICLE_TYPE);
                level.setBlockAndUpdate(blockPos, blockState.setValue(PARTICLE_TYPE, (particleType + 1) % PARTICLE_EMITTER_CONFIGURATIONS.length));
            }
        }

        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    /**
     * This method is called every tick to animate the block. Taken from `SporeBlossomBlock.java`.
     *
     * @param blockState
     * @param level
     * @param blockPos
     * @param random
     */
    @Override
    public void animateTick(BlockState blockState, Level level, BlockPos blockPos, RandomSource random) {
        int i = blockPos.getX();
        int j = blockPos.getY();
        int k = blockPos.getZ();

        if (blockState.getValue(ENABLED)) {
            BlockPos.MutableBlockPos mutableBlockpos = new BlockPos.MutableBlockPos();
            ParticleEmitterConfiguration particleEmitterConfiguration = getParticleEmitterConfiguration(blockState);
            for (int l = 0; l < particleEmitterConfiguration.particleCount * (blockState.getValue(PARTICLE_INTENSITY) + 1); ++l) {
                mutableBlockpos.set(i + Mth.nextInt(random, -10, 10), j - random.nextInt(-8, 8), k + Mth.nextInt(random, -10, 10));
                BlockState blockstate = level.getBlockState(mutableBlockpos);
                if (!blockstate.isCollisionShapeFullBlock(level, mutableBlockpos)) {
                    Vec3 dir = new Vec3(0.0D, 0.0D, 0.0D);
//                    if(particleEmitterConfiguration.particleType.equals(ENCHANT)) {
//                        Player player = level.getNearestPlayer((double) mutableBlockpos.getX(), (double) mutableBlockpos.getY(), (double) mutableBlockpos.getZ(), 12.0D, false);
//                        if(player != null)
//                            dir = player.position().vectorTo(new Vec3((double) mutableBlockpos.getX(), (double) mutableBlockpos.getY(), (double) mutableBlockpos.getZ())).multiply(2D, 2D, 2D);
//
//                        level.addParticle(particleEmitterConfiguration.particleType.get(), (double) mutableBlockpos.getX() + random.nextDouble(), (double) mutableBlockpos.getY() + random.nextDouble(), (double) mutableBlockpos.getZ() + random.nextDouble(), dir.x, dir.y, dir.z);
//                    }

                    level.addParticle(particleEmitterConfiguration.sampleParticleType(random), (double) mutableBlockpos.getX() + random.nextDouble(), (double) mutableBlockpos.getY() + random.nextDouble(), (double) mutableBlockpos.getZ() + random.nextDouble(), dir.x, dir.y, dir.z);
                }
            }
        }
    }

    @Override
    public void neighborChanged(BlockState blockState, Level level, BlockPos blockPos, Block block, BlockPos blockPos1, boolean b) {
        if (!level.isClientSide) {
            boolean flag = blockState.getValue(ENABLED);
            if (flag != level.hasNeighborSignal(blockPos)) {
                if (flag) {
                    level.scheduleTick(blockPos, this, 4);
                } else {
                    level.setBlock(blockPos, blockState.cycle(ENABLED), 2);
                }
            }
        }
    }

    @Override
    public void tick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource random) {
        if (blockState.getValue(ENABLED) && !serverLevel.hasNeighborSignal(blockPos)) {
            serverLevel.setBlock(blockPos, blockState.cycle(ENABLED), 2);
        }
    }

    public static class ParticleEmitterConfiguration {
        public Block blockModel;
        public WeightedParticleType[] weightedParticleTypes;
        public int particleCount;
        public boolean isEmitter;

        public static class WeightedParticleType {
            public SimpleParticleType particleType;
            public int weight;

            public WeightedParticleType(SimpleParticleType particleType, int weight) {
                this.particleType = particleType;
                this.weight = weight;
            }
        }

        public ParticleEmitterConfiguration(Block blockModel, int particleCount, WeightedParticleType... weightedParticleTypes) {
            this.blockModel = blockModel;
            this.weightedParticleTypes = weightedParticleTypes;
            this.particleCount = particleCount;
        }

        public ParticleEmitterConfiguration(Block blockModel, int particleCount, boolean isEmitter, WeightedParticleType... weightedParticleTypes) {
            this.blockModel = blockModel;
            this.weightedParticleTypes = weightedParticleTypes;
            this.particleCount = particleCount;
            this.isEmitter = isEmitter;
        }

        public SimpleParticleType sampleParticleType(RandomSource random) {
            int totalWeight = 0;
            for (WeightedParticleType weightedParticleType : weightedParticleTypes) {
                totalWeight += weightedParticleType.weight;
            }

            int randomWeight = random.nextInt(totalWeight);
            int currentWeight = 0;
            for (WeightedParticleType weightedParticleType : weightedParticleTypes) {
                currentWeight += weightedParticleType.weight;
                if (randomWeight < currentWeight) {
                    return weightedParticleType.particleType;
                }
            }

            throw new IllegalStateException("Could not sample a particle type. This should never happen.");
        }
    }

    public static ParticleEmitterConfiguration getParticleEmitterConfiguration(BlockState blockState) {
        return PARTICLE_EMITTER_CONFIGURATIONS[blockState.getValue(PARTICLE_TYPE)].get();
    }

}
