package grill24.potionsplus.block;

import grill24.potionsplus.core.Particles;
import grill24.potionsplus.core.blocks.BlockEntityBlocks;
import grill24.potionsplus.entity.InvisibleFireDamager;
import grill24.potionsplus.particle.EmitterParticle;
import grill24.potionsplus.particle.ParticleConfigurations;
import grill24.potionsplus.extension.IParticleEngineExtension;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class GeyserBlock extends FaceAttachedHorizontalDirectionalBlock implements IParticleEmitter {
    private static final BooleanProperty ACTIVE = BooleanProperty.create("active");

    private static final VoxelShape SHAPE_1 = Block.box(5, 0, 5, 11, 9, 11);
    private static final VoxelShape SHAPE_2 = Block.box(6, 9, 6, 7, 10, 10);
    private static final VoxelShape SHAPE_3 = Block.box(9, 9, 6, 10, 10, 10);
    private static final VoxelShape SHAPE_4 = Block.box(7, 9, 6, 9, 10, 7);
    private static final VoxelShape SHAPE_5 = Block.box(7, 9, 9, 9, 10, 10);
    private static final VoxelShape ALL_SHAPES = Shapes.or(SHAPE_1, SHAPE_2, SHAPE_3, SHAPE_4, SHAPE_5);

    private static final Vec3 PARTICLE_SPAWN_POINT = new Vec3(0.5D, 0.625D, 0.5D);
    private static final Vec3[] PARTICLE_SPAWN_POINTS = generateRotations(PARTICLE_SPAWN_POINT);
    private static final double VELOCITY = 1.0D;
    private static final Vec3[] VELOCITIES = new Vec3[] {
            new Vec3(0.0D, 1, 0.0D), // UP
            new Vec3(0.0D, -1, 0.0D), // DOWN
            new Vec3(0.0D, 0.0D, -1), // NORTH
            new Vec3(1, 0.0D, 0.0D), // EAST
            new Vec3(0.0D, 0.0D, 1), // SOUTH
            new Vec3(-1, 0.0D, 0.0D) // WEST
    };

    public GeyserBlock(Properties properties) {
        super(properties, ALL_SHAPES);
        this.registerDefaultState(this.defaultBlockState().setValue(ACTIVE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> blockStateBuilder) {
        super.createBlockStateDefinition(blockStateBuilder);
        blockStateBuilder.add(ACTIVE);
    }

    @Override
    public boolean isRandomlyTicking(BlockState blockState) {
        return true;
    }

    @Override
    public void randomTick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource random) {
        super.randomTick(blockState, serverLevel, blockPos, random);

        boolean active = blockState.getValue(ACTIVE);

        if (!active){
            serverLevel.setBlockAndUpdate(blockPos, blockState.setValue(ACTIVE, true));
        }
        serverLevel.scheduleTick(blockPos, this, 0);
    }

    @Override
    public void animateTick(BlockState blockState, Level level, BlockPos blockPos, RandomSource random) {
        super.animateTick(blockState, level, blockPos, random);

        if (blockState.getValue(ACTIVE)) {
            level.addParticle(Particles.LAVA_GEYSER_BLOCK_LINKED_EMITTER.get(), blockPos.getX(), blockPos.getY(), blockPos.getZ(), 0, 0, 0);
        }
    }

    @Override
    public void tick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource random) {
        super.tick(blockState, serverLevel, blockPos, random);

        if (blockState.getValue(ACTIVE)) {
            Vec3 position = getDirectionalValue(blockState, PARTICLE_SPAWN_POINTS).add(blockPos.getX(), blockPos.getY(), blockPos.getZ());
            Vec3 velocity = getDirectionalValue(blockState, VELOCITIES).scale(VELOCITY);
            InvisibleFireDamager fireEntity = new InvisibleFireDamager(position.x, position.y, position.z, new Vec3(velocity.x, velocity.y, velocity.z), serverLevel);
            serverLevel.addFreshEntity(Util.make(fireEntity, (entity) -> {}));

            serverLevel.scheduleTick(blockPos, this, 5);

            if(random.nextFloat() < 0.04) {
                serverLevel.setBlockAndUpdate(blockPos, blockState.setValue(ACTIVE, false));
            }
        }
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, net.minecraft.world.entity.Entity entity) {
        if (entity instanceof Player) {
            level.setBlockAndUpdate(pos, state.setValue(ACTIVE, true));
            level.scheduleTick(pos, this, 0);

            if (!entity.fireImmune()) {
                int i = entity.getRemainingFireTicks();
                entity.igniteForTicks(5);
                boolean flag = entity.hurt(level.damageSources().hotFloor(), 5.0F);
                if (!flag) {
                    entity.setRemainingFireTicks(i);
                }
            }

        }
    }

    private ParticleEmitterBlock.ParticleEmitterConfiguration getActiveParticleConfiguration(Level level, BlockPos pos) {
        BlockState attachedState = level.getBlockState(getAttachedTo(level.getBlockState(pos), pos));
        if (attachedState.is(BlockEntityBlocks.PARTICLE_EMITTER.value())) {
            return ParticleEmitterBlock.getParticleEmitterConfiguration(attachedState);
        }

        return ParticleConfigurations.LAVA_GEYSER_PARTICLES;
    }

    @Override
    public ParticleOptions sampleParticleType(Level level, BlockState state, BlockPos pos) {
        ParticleEmitterBlock.ParticleEmitterConfiguration particleSampler = getActiveParticleConfiguration(level, pos);

        // This makes sure we don't spawn particle *spawners* with the geyser block if it is attached to the particle emitter. Instead, get that spawner's particle type and spawn it like usual.
        SimpleParticleType particleType = particleSampler.sampleParticleType(level.random);
        ParticleProvider<?> particleprovider = ((IParticleEngineExtension) Minecraft.getInstance().particleEngine).potions_plus$getProviders().get(BuiltInRegistries.PARTICLE_TYPE.getKey(particleType));
        if(particleprovider instanceof EmitterParticle.Provider) {
            return ((EmitterParticle.Provider) particleprovider).particleTypeSupplier.apply(level.random);
        }

        return particleType;
    }

    @Override
    public int getTicksPerSpawn(Level level, BlockState state, BlockPos pos) {
        return 1;
    }

    @Override
    public int getSpawnCount(Level level, BlockState state, BlockPos pos) {
        return state.getValue(ACTIVE) ? 5 : 0;
    }

    @Override
    public float getRange(Level level, BlockState state, BlockPos pos) {
        return 0.125F;
    }

    @Override
    public Vec3 getVelocity(Level level, BlockState state, BlockPos pos) {
        return getDirectionalValue(state, VELOCITIES);
    }

    @Override
    public Vec3 getPosition(Level level, BlockState state, BlockPos pos) {
        return getDirectionalValue(state, PARTICLE_SPAWN_POINTS).add(pos.getX(), pos.getY(), pos.getZ());
    }

}
