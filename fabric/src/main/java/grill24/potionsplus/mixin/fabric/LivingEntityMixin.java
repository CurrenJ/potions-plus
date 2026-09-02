package grill24.potionsplus.mixin.fabric;

import grill24.potionsplus.effect.BoneBuddyEffect;
import grill24.potionsplus.effect.BouncingEffect;
import grill24.potionsplus.effect.ExplodingEffect;
import grill24.potionsplus.effect.FallOfTheVoidEffect;
import grill24.potionsplus.effect.FlyingTimeEffect;
import grill24.potionsplus.effect.SoulMateEffect;
import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Fabric equivalent of NeoForge's {@code event/neoforge/EffectListeners.java} (Phase 7). Fabric has
 * no central bus for mob-effect add/expire/remove/fall/damage/heal, so we mixin into
 * {@link LivingEntity} at the same call-sites NeoForge/Forge patch.
 *
 * <p>Unlike the 26.1.2 reference (unobfuscated, where {@code onEffectsRemoved(Collection)} exists),
 * 1.21.1's {@code LivingEntity} predates that batching refactor - it only has a singular
 * {@code onEffectRemoved(MobEffectInstance)}, called from several places with no way to tell natural
 * expiry apart from explicit removal at that single injection point. So expiry is detected by
 * diffing {@code getActiveEffectsMap()} across {@code tickEffects()} (verified via javap against the
 * vanilla merged jar - no reliance on undocumented internal call graphs), and explicit single/all
 * removal is handled at {@code removeEffectNoUpdate}/{@code removeAllEffects} directly.
 */
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Shadow
    public abstract Map<Holder<MobEffect>, MobEffectInstance> getActiveEffectsMap();

    @Shadow
    public abstract Collection<MobEffectInstance> getActiveEffects();

    @Shadow
    protected abstract float getDamageAfterMagicAbsorb(DamageSource source, float damage);

    private Map<Holder<MobEffect>, MobEffectInstance> potionsplus$preTickEffects;
    private List<MobEffectInstance> potionsplus$preRemoveAllEffects;

    // ----- MobEffectEvent.Added -----

    @Inject(method = "onEffectAdded", at = @At("TAIL"))
    private void potionsplus$onEffectAdded(MobEffectInstance effect, @Nullable Entity source, CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        BoneBuddyEffect.onPotionAdded(self, effect);
        FlyingTimeEffect.onPotionAdded(self, effect);
        ExplodingEffect.onPotionAdded(self, effect);
        SoulMateEffect.onPotionAdded(self);
    }

    // ----- MobEffectEvent.Expired (natural expiry, detected by diffing across tickEffects) -----

    @Inject(method = "tickEffects", at = @At("HEAD"))
    private void potionsplus$onTickEffectsHead(CallbackInfo ci) {
        potionsplus$preTickEffects = new HashMap<>(this.getActiveEffectsMap());
    }

    @Inject(method = "tickEffects", at = @At("RETURN"))
    private void potionsplus$onTickEffectsReturn(CallbackInfo ci) {
        if (potionsplus$preTickEffects == null) {
            return;
        }
        LivingEntity self = (LivingEntity) (Object) this;
        Map<Holder<MobEffect>, MobEffectInstance> after = this.getActiveEffectsMap();
        for (Map.Entry<Holder<MobEffect>, MobEffectInstance> entry : potionsplus$preTickEffects.entrySet()) {
            if (!after.containsKey(entry.getKey())) {
                MobEffectInstance effect = entry.getValue();
                BoneBuddyEffect.onPotionExpired(self, effect);
                FlyingTimeEffect.onPotionExpired(self, effect);
                ExplodingEffect.onPotionExpiry(self, effect);
                SoulMateEffect.onPotionExpired(self);
            }
        }
        potionsplus$preTickEffects = null;
    }

    // ----- MobEffectEvent.Remove (explicit single removal) -----

    @Inject(method = "removeEffectNoUpdate", at = @At("RETURN"))
    private void potionsplus$onEffectRemoved(Holder<MobEffect> effect, CallbackInfoReturnable<MobEffectInstance> cir) {
        if (cir.getReturnValue() != null) {
            LivingEntity self = (LivingEntity) (Object) this;
            FlyingTimeEffect.onPotionRemoved(self, cir.getReturnValue());
            SoulMateEffect.onPotionRemoved(self);
        }
    }

    // ----- MobEffectEvent.Remove (explicit remove-all) -----

    @Inject(method = "removeAllEffects", at = @At("HEAD"))
    private void potionsplus$onRemoveAllEffectsHead(CallbackInfoReturnable<Boolean> cir) {
        potionsplus$preRemoveAllEffects = new ArrayList<>(this.getActiveEffects());
    }

    @Inject(method = "removeAllEffects", at = @At("RETURN"))
    private void potionsplus$onRemoveAllEffectsReturn(CallbackInfoReturnable<Boolean> cir) {
        if (potionsplus$preRemoveAllEffects == null) {
            return;
        }
        LivingEntity self = (LivingEntity) (Object) this;
        for (MobEffectInstance effect : potionsplus$preRemoveAllEffects) {
            FlyingTimeEffect.onPotionRemoved(self, effect);
            SoulMateEffect.onPotionRemoved(self);
        }
        potionsplus$preRemoveAllEffects = null;
    }

    // ----- LivingFallEvent -----

    @Inject(method = "causeFallDamage", at = @At("HEAD"), cancellable = true)
    private void potionsplus$onFall(float fallDistance, float damageMultiplier, DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        BouncingEffect.onFall(self);
        if (BouncingEffect.onLivingFall(self, fallDistance)) {
            cir.setReturnValue(false);
        }
    }

    // ----- LivingDamageEvent.Pre (modify damage after armor/magic absorb) -----

    @Redirect(method = "actuallyHurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getDamageAfterMagicAbsorb(Lnet/minecraft/world/damagesource/DamageSource;F)F"))
    private float potionsplus$modifyDamage(LivingEntity self, DamageSource source, float damage) {
        float absorbed = this.getDamageAfterMagicAbsorb(source, damage);
        float afterVoid = FallOfTheVoidEffect.onLivingEntityDamage(self, source, absorbed);
        if (afterVoid != absorbed) {
            absorbed = afterVoid;
        }
        float afterSoulMate = SoulMateEffect.onEntityHurt(self, source, absorbed);
        if (afterSoulMate != absorbed) {
            absorbed = afterSoulMate;
        }
        return absorbed;
    }

    // ----- LivingHealEvent -----

    @ModifyVariable(method = "heal", at = @At("HEAD"), argsOnly = true)
    private float potionsplus$modifyHeal(float amount) {
        return SoulMateEffect.onEntityHeal((LivingEntity) (Object) this, amount);
    }
}
