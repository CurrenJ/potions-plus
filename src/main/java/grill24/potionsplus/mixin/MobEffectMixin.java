package grill24.potionsplus.mixin;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MobEffect.class)
public class MobEffectMixin extends net.minecraftforge.registries.ForgeRegistryEntry<MobEffect> {
    public MobEffectMixin() {
        super();
    }

    @Inject(at = @At("HEAD"), method = "applyEffectTick")
    public void applyEffectTick(LivingEntity livingEntity, int i, CallbackInfo ci) {
        System.out.println("PotionsPlus: MobEffectMixin.applyEffectTick");
    }
}
