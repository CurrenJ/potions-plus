package grill24.potionsplus.mixin;

import grill24.potionsplus.utility.IMobEffectInstanceSetters;
import net.minecraft.world.effect.MobEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(MobEffectInstance.class)
public abstract class MobEffectInstanceMixin implements IMobEffectInstanceSetters {
    @Shadow private int duration;

    @Override
    public void potions_plus$setDuration(int duration) {
        this.duration = duration;
    }
}
