package grill24.potionsplus.mixin;

import grill24.potionsplus.core.potion.MobEffects;
import grill24.potionsplus.effect.NauticalNitroEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.vehicle.AbstractBoat;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import javax.annotation.Nullable;

@Mixin(AbstractBoat.class)
public abstract class BoatMixin extends Entity {
    @Shadow
    @Nullable
    public abstract LivingEntity getControllingPassenger();

    public BoatMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @ModifyVariable(method = "controlBoat", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/vehicle/AbstractBoat;getDeltaMovement()Lnet/minecraft/world/phys/Vec3;"))
    private float controlBoat(float f) {
        LivingEntity livingEntity = this.getControllingPassenger();
        if (livingEntity.hasEffect(MobEffects.NAUTICAL_NITRO)) {
            MobEffectInstance effect = livingEntity.getEffect(MobEffects.NAUTICAL_NITRO);
            if (effect != null && effect.getEffect().value() instanceof NauticalNitroEffect nauticalNitroEffect) {
                return f * nauticalNitroEffect.getSpeedMultiplier(effect);
            }
        }
        return f;
    }
}
