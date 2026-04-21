package grill24.potionsplus.entity;

import grill24.potionsplus.core.Entities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import grill24.potionsplus.core.ConventionalTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class Grungler extends Monster {
    public static final EntityDataAccessor<BlockState> DATA =
            SynchedEntityData.defineId(
                    Grungler.class,
                    EntityDataSerializers.BLOCK_STATE
            );

    public Grungler(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.getEntityData().set(DATA, Blocks.STONE.defaultBlockState());
    }

    public Grungler(EntityType<? extends Monster> type, Level level, BlockState blockState) {
        super(type, level);
        this.getEntityData().set(DATA, blockState);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 40.0).add(Attributes.MOVEMENT_SPEED, 0.5F);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new AvoidEntityGoal<>(this, Player.class, 32.0F, 1.0, 1.2));
        this.goalSelector.addGoal(4, new PanicGoal(this, 1.2));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        if (compoundTag.contains("BlockState")) {
            BlockState blockState = BlockState.CODEC.parse(NbtOps.INSTANCE, compoundTag.get("BlockState"))
                    .resultOrPartial(error -> {
                        throw new RuntimeException("Failed to decode BlockState: " + error);
                    })
                    .orElse(Blocks.AIR.defaultBlockState());
            this.getEntityData().set(DATA, blockState);
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        BlockState blockState = this.getEntityData().get(DATA);
        BlockState.CODEC.encodeStart(NbtOps.INSTANCE, blockState)
                .resultOrPartial(error -> {
                    throw new RuntimeException("Failed to encode BlockState: " + error);
                })
                .ifPresent(encodedBlockState -> compoundTag.put("BlockState", encodedBlockState));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        // Define any additional data here if needed
        builder.define(DATA, Blocks.AIR.defaultBlockState());
    }

    public BlockState getBlockState() {
        return this.getEntityData().get(DATA);
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource damageSource, float amount) {
        super.hurtServer(level, damageSource, amount);
        return true;
    }

    public static boolean onBreakBlock(BlockState blockState, java.util.List<ItemStack> drops, @org.jetbrains.annotations.Nullable Entity breaker, BlockPos pos) {
        if (!drops.isEmpty() && blockState.is(ConventionalTags.Blocks.ORES)) {
            if (breaker instanceof Player player && !player.isCreative() && player.level() instanceof ServerLevel serverLevel) {
                float spawnChance = 0.01F;
                if (serverLevel.getRandom().nextDouble() < spawnChance) {
                    Grungler grungler = Entities.GRUNGLER.get().spawn(serverLevel, pos, EntitySpawnReason.NATURAL);
                    grungler.getEntityData().set(Grungler.DATA, blockState);
                    return true;
                }
            }
        }
        return false;
    }

    public static void onEntityDeath(LivingEntity entity) {
        if (entity instanceof Grungler grungler) {
            BlockState blockState = grungler.getBlockState();
            if (blockState != null && !blockState.isAir() && entity.level() instanceof ServerLevel serverLevel) {
                ItemStack itemStack = new ItemStack(blockState.getBlock());
                serverLevel.addFreshEntity(
                        new ItemEntity(serverLevel, grungler.getX(), grungler.getY(), grungler.getZ(), itemStack)
                );
            }
        }
    }
}