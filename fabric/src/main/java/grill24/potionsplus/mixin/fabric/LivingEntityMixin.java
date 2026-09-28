package grill24.potionsplus.mixin.fabric;

import grill24.potionsplus.core.Attributes;
import grill24.potionsplus.effect.BoneBuddyEffect;
import grill24.potionsplus.effect.BouncingEffect;
import grill24.potionsplus.effect.ExplodingEffect;
import grill24.potionsplus.effect.FallOfTheVoidEffect;
import grill24.potionsplus.effect.FlyingTimeEffect;
import grill24.potionsplus.effect.SoulMateEffect;
import grill24.potionsplus.core.potion.MobEffects;
import grill24.potionsplus.effect.SlipNSlideEffect;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;

/**
 * Fabric equivalent of NeoForge's {@code EffectListeners} (mob-effect add/expire/remove, fall,
 * damage, heal) plus {@code NeoItemListeners.onLivingUseItem}. Fabric has no central event bus for
 * these, so we mixin into {@link LivingEntity} at the same call-sites NeoForge patches.
 */
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Shadow
    protected abstract void onEffectsRemoved(Collection<MobEffectInstance> effects);

    @Shadow
    protected abstract float getDamageAfterMagicAbsorb(DamageSource source, float damage);

    @Shadow
    protected int useItemRemaining;

    // ----- MobEffectEvent.Added -----

    @Inject(method = "onEffectAdded", at = @At("TAIL"))
    private void potionsplus$onEffectAdded(MobEffectInstance effect, @Nullable Entity source, CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        BoneBuddyEffect.onPotionAdded(self, effect);
        FlyingTimeEffect.onPotionAdded(self, effect);
        ExplodingEffect.onPotionAdded(self, effect);
        SoulMateEffect.onPotionAdded(self);
    }

    // ----- MobEffectEvent.Expired (natural expiry via tickEffects) -----

    @Redirect(method = "tickEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;onEffectsRemoved(Ljava/util/Collection;)V"))
    private void potionsplus$onEffectExpired(LivingEntity self, Collection<MobEffectInstance> effects) {
        for (MobEffectInstance effect : effects) {
            BoneBuddyEffect.onPotionExpired(self, effect);
            FlyingTimeEffect.onPotionExpired(self, effect);
            ExplodingEffect.onPotionExpiry(self, effect);
            SoulMateEffect.onPotionExpired(self);
        }
        this.onEffectsRemoved(effects);
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

    @Redirect(method = "removeAllEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;onEffectsRemoved(Ljava/util/Collection;)V"))
    private void potionsplus$onRemoveAllEffects(LivingEntity self, Collection<MobEffectInstance> effects) {
        for (MobEffectInstance effect : effects) {
            FlyingTimeEffect.onPotionRemoved(self, effect);
            SoulMateEffect.onPotionRemoved(self);
        }
        this.onEffectsRemoved(effects);
    }

    // ----- LivingFallEvent -----

    @Inject(method = "causeFallDamage", at = @At("HEAD"), cancellable = true)
    private void potionsplus$onFall(double fallDistance, float damageModifier, DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        BouncingEffect.onFall(self);
        if (BouncingEffect.onLivingFall(self, (float) fallDistance)) {
            cir.setReturnValue(false);
        }
    }

    // ----- LivingDamageEvent.Pre (modify damage after armor/magic absorb, before absorption) -----

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

    // ----- LivingEntityUseItemEvent.Tick (shorten use duration for use-speed-bonus items) -----

    @Inject(method = "updateUsingItem", at = @At("HEAD"))
    private void potionsplus$shortenUseDuration(ItemStack useItem, CallbackInfo ci) {
        ItemAttributeModifiers modifiers = useItem.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
        for (ItemAttributeModifiers.Entry entry : modifiers.modifiers()) {
            ResourceKey<Attribute> attributeKey = entry.attribute().unwrapKey().orElse(null);
            if (attributeKey != null && attributeKey.equals(Attributes.USE_SPEED_BONUS.unwrapKey().orElseThrow())) {
                float useSpeedBonus = (float) entry.modifier().amount();
                int skipTickEveryTicks = Math.round(1.0F / useSpeedBonus);
                if (this.useItemRemaining % skipTickEveryTicks == 0) {
                    this.useItemRemaining -= 1;
                }
            }
        }
    }

    // ----- SlipNSlideEffect friction override (vanilla equivalent of NeoForge's NeoLivingEntityMixin) -----

    @Redirect(method = "travelInAir", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;getFriction()F"))
    private float potionsplus$getFriction(Block block) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self.hasEffect(MobEffects.SLIP_N_SLIDE)) {
            return SlipNSlideEffect.getFriction(self.getEffect(MobEffects.SLIP_N_SLIDE).getAmplifier());
        }
        return block.getFriction();
    }
}
