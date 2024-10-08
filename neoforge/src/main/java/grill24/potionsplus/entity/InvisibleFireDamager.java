package grill24.potionsplus.entity;

import grill24.potionsplus.core.Entities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Copy of {@link net.minecraft.world.entity.projectile.SmallFireball}
 * Registered with a NoopRenderer in {@link grill24.potionsplus.core.Renderers}
 */
public class InvisibleFireDamager extends Fireball {

   public InvisibleFireDamager(Level level) {
      super(Entities.INVISIBLE_FIRE_DAMAGER.get(), level);
   }

   public InvisibleFireDamager(double x, double y, double z, Vec3 movement, Level level) {
      super(Entities.INVISIBLE_FIRE_DAMAGER.get(), x, y, z, movement, level);
   }

   public InvisibleFireDamager(LivingEntity owner, Vec3 movement, Level level) {
      super(Entities.INVISIBLE_FIRE_DAMAGER.get(), owner, movement, level);
   }

   @Override
    public boolean isOnFire() {
       return false;
    }

    @Override
   protected void onHitEntity(EntityHitResult p_37386_) {
      super.onHitEntity(p_37386_);
      if (!this.level().isClientSide) {
         Entity entity = p_37386_.getEntity();
         if (!entity.fireImmune()) {
            Entity entity1 = this.getOwner();
            int i = entity.getRemainingFireTicks();
            entity.igniteForSeconds(5);
            boolean flag = entity.hurt(entity.damageSources().fireball(this, entity1), 5.0F);
            if (!flag) {
               entity.setRemainingFireTicks(i);
            }
         }

      }
   }

   @Override
   protected void onHitBlock(BlockHitResult p_37384_) {
      super.onHitBlock(p_37384_);
      if (!this.level().isClientSide) {
         Entity entity = this.getOwner();
         if (!(entity instanceof Mob)) {
            BlockPos blockpos = p_37384_.getBlockPos().relative(p_37384_.getDirection());
            if (this.level().isEmptyBlock(blockpos)) {
               this.level().setBlockAndUpdate(blockpos, BaseFireBlock.getState(this.level(), blockpos));
            }
         }

      }
   }

   @Override
   protected void onHit(HitResult p_37388_) {
      super.onHit(p_37388_);
      if (!this.level().isClientSide) {
         this.discard();
      }

   }

   @Override
   public boolean isPickable() {
      return false;
   }

   @Override
   public boolean hurt(DamageSource p_37381_, float p_37382_) {
      return false;
   }
}
