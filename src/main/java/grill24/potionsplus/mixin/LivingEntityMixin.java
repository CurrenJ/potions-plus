package grill24.potionsplus.mixin;

import grill24.potionsplus.core.MobEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(net.minecraft.world.entity.LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }


    @Shadow
    public abstract boolean hasEffect(MobEffect p_21024_);

    @Inject(at = @At("HEAD"), method = "dropCustomDeathLoot")
    public void dropCustomDeathLoot(DamageSource damageSource, int i, boolean b, CallbackInfo ci) {
        Entity entity = damageSource.getEntity();
        if (this.hasEffect(MobEffects.GEODE_GRACE.get())) {
            // Not using rn
        }
    }
}
