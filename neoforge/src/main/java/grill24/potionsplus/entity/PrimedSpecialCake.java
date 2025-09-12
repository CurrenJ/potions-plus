package grill24.potionsplus.entity;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class PrimedSpecialCake extends PrimedTnt {
    private static final EntityDataAccessor<Integer> DATA_FUSE_ID = SynchedEntityData.defineId(PrimedSpecialCake.class, EntityDataSerializers.INT);
    private static final int DEFAULT_FUSE_TIME = 80;
    
    @Nullable
    private LivingEntity owner;

    public PrimedSpecialCake(EntityType<? extends PrimedSpecialCake> entityType, Level level) {
        super(entityType, level);
        this.setFuse(DEFAULT_FUSE_TIME);
        this.setDeltaMovement(Vec3.ZERO);
    }

    public PrimedSpecialCake(Level level, double x, double y, double z, @Nullable LivingEntity igniter) {
        this(grill24.potionsplus.core.Entities.PRIMED_SPECIAL_CAKE.get(), level);
        this.setPos(x, y, z);
        double d0 = level.random.nextDouble() * (double) ((float) Math.PI * 2F);
        this.setDeltaMovement(-Math.sin(d0) * 0.02D, 0.2D, -Math.cos(d0) * 0.02D);
        this.setFuse(DEFAULT_FUSE_TIME);
        this.xo = x;
        this.yo = y;
        this.zo = z;
        this.owner = igniter;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_FUSE_ID, DEFAULT_FUSE_TIME);
    }

    @Override
    public void tick() {
        if (!this.isNoGravity()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.04D, 0.0D));
        }

        this.move(MoverType.SELF, this.getDeltaMovement());
        this.setDeltaMovement(this.getDeltaMovement().scale(0.98D));
        if (this.onGround()) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.7D, -0.5D, 0.7D));
        }

        int fuse = this.getFuse() - 1;
        this.setFuse(fuse);
        if (fuse <= 0) {
            this.discard();
            if (!this.level().isClientSide) {
                this.explode();
            }
        } else {
            this.updateInWaterStateAndDoFluidPushing();
            if (this.level().isClientSide) {
                // Spawn cake-colored particles instead of smoke
                this.level().addParticle(ParticleTypes.WHITE_ASH, this.getX(), this.getY() + 0.5D, this.getZ(), 0.0D, 0.0D, 0.0D);
                this.level().addParticle(ParticleTypes.FALLING_SPORE_BLOSSOM, this.getX(), this.getY() + 0.5D, this.getZ(), 0.0D, 0.0D, 0.0D);
            }
        }
    }

    protected void explode() {
        float explosionRadius = 4.0F;
        this.level().explode(this, this.getX(), this.getY(0.0625D), this.getZ(), explosionRadius, Level.ExplosionInteraction.TNT);
    }

    @Override
    public void setFuse(int fuse) {
        this.entityData.set(DATA_FUSE_ID, fuse);
    }

    @Override
    public int getFuse() {
        return this.entityData.get(DATA_FUSE_ID);
    }

    @Nullable
    public LivingEntity getOwner() {
        return this.owner;
    }
}