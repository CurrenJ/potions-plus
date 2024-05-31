package grill24.potionsplus.mixin;

import grill24.potionsplus.core.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(Boat.class)
public abstract class BoatMixin extends Entity {
    @Shadow @Nullable public abstract Entity getControllingPassenger();

    public BoatMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @ModifyVariable(method = "controlBoat", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/vehicle/Boat;getDeltaMovement()Lnet/minecraft/world/phys/Vec3;"))
    private float controlBoat(float f) {
        if(this.getControllingPassenger() instanceof LivingEntity livingEntity) {
            if(livingEntity.hasEffect(MobEffects.NAUTICAL_NITRO.get())) {
                MobEffectInstance effect = livingEntity.getEffect(MobEffects.NAUTICAL_NITRO.get());
                if(effect != null) {
                    return f * (effect.getAmplifier()+1) * 1.2f;
                }
            }
        }
        return f;
    }
}
