package grill24.potionsplus.mixin;

import grill24.potionsplus.core.potion.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TemptGoal.class)
public class TemptGoalMixin {
    @Inject(method = "shouldFollow", at = @At("HEAD"), cancellable = true)
    private void shouldFollow(LivingEntity livingEntity, CallbackInfoReturnable<Boolean> cir) {
        if(livingEntity.hasEffect(MobEffects.SHEPHERDS_SERENADE)) {
            cir.setReturnValue(true);
        }
    }
}
