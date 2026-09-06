package grill24.potionsplus.mixin.forge;

import grill24.potionsplus.core.potion.MobEffects;
import grill24.potionsplus.effect.SlipNSlideEffect;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow
    public abstract boolean hasEffect(net.minecraft.core.Holder<net.minecraft.world.effect.MobEffect> effect);

    @Shadow
    public abstract MobEffectInstance getEffect(net.minecraft.core.Holder<net.minecraft.world.effect.MobEffect> effect);

    @Redirect(method = "travelInAir", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getFriction(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/Entity;)F"))
    public float getFriction(BlockState state, LevelReader level, BlockPos pos, Entity entity) {
        if (hasEffect(MobEffects.SLIP_N_SLIDE)) {
            return SlipNSlideEffect.getFriction(getEffect(MobEffects.SLIP_N_SLIDE).getAmplifier());
        }
        return state.getBlock().getFriction();
    }
}
