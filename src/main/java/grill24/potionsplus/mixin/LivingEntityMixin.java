package grill24.potionsplus.mixin;

import grill24.potionsplus.core.potion.MobEffects;
import grill24.potionsplus.effect.SlipNSlideEffect;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow public abstract boolean hasEffect(MobEffect p_21024_);

    @Shadow @Nullable public abstract MobEffectInstance getEffect(MobEffect p_21125_);

    @Redirect(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getFriction(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/Entity;)F"))
    public float getFriction(BlockState instance, LevelReader levelReader, BlockPos blockPos, Entity entity) {
        if(hasEffect(MobEffects.SLIP_N_SLIDE.get())) {
            return SlipNSlideEffect.getFriction(getEffect(MobEffects.SLIP_N_SLIDE.get()).getAmplifier());
        }

        return instance.getFriction(levelReader, blockPos, entity);
    }

    @Redirect(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;setDeltaMovement(DDD)V", ordinal = 3))
    public void setDeltaMovement(LivingEntity livingEntity, double x, double y, double z) {
        if(hasEffect(MobEffects.SLIP_N_SLIDE.get()) && livingEntity.isOnGround()) {
            double d = 0.91D;
            livingEntity.setDeltaMovement(x / d, y, z / d);
        } else {
            livingEntity.setDeltaMovement(x, y, z);
        }
    }
}
