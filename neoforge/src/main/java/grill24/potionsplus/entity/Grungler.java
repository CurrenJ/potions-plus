package grill24.potionsplus.entity;

import grill24.potionsplus.block.PotionsPlusOreBlock;
import grill24.potionsplus.core.Entities;
import grill24.potionsplus.utility.ModInfo;
import grill24.potionsplus.utility.registration.RuntimeTextureVariantModelGenerator;
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
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;

@EventBusSubscriber(modid = ModInfo.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
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

    @SubscribeEvent
    public static void onBreakBlock(final BlockDropsEvent event) {
        BlockState blockState = event.getState();
        if (!event.getDrops().isEmpty() && blockState.is(Tags.Blocks.ORES)) {
            if (event.getBreaker() instanceof Player player && !player.isCreative() && player.level() instanceof ServerLevel serverLevel) {
                float spawnChance = 0.01F; // 1/40 chance to spawn a Grungler
                if (serverLevel.getRandom().nextDouble() < spawnChance) {
                    // Check if the player has a Grungler entity nearby
                    Grungler grungler = Entities.GRUNGLER.get().spawn(serverLevel, event.getPos(), EntitySpawnReason.NATURAL);
                    grungler.getEntityData().set(Grungler.DATA, blockState);

                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onEntityDeath(final LivingDeathEvent event) {
        if (event.getEntity() instanceof Grungler grungler) {
            BlockState blockState = grungler.getBlockState();
            if (blockState != null && !blockState.isAir() && event.getEntity().level() instanceof ServerLevel serverLevel) {
                // Drop the block state as an item when the Grungler dies
                ItemStack itemStack = RuntimeTextureVariantModelGenerator.getTextureVariantItemStack(blockState, PotionsPlusOreBlock.TEXTURE);
                serverLevel.addFreshEntity(
                        new ItemEntity(serverLevel, grungler.getX(), grungler.getY(), grungler.getZ(),
                                itemStack)
                );
            }
        }
    }
}