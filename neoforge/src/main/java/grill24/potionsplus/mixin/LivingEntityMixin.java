package grill24.potionsplus.mixin;

import grill24.potionsplus.core.items.HatItems;
import grill24.potionsplus.core.potion.MobEffects;
import grill24.potionsplus.effect.SlipNSlideEffect;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    // ----- SlipNSlideEffect -----

    @Shadow
    protected abstract void serverAiStep();

    @Shadow
    public abstract void setHealth(float p_21154_);

    @Shadow
    public abstract boolean removeAllEffects();

    @Shadow
    public abstract boolean addEffect(MobEffectInstance p_21165_);

    @Shadow
    @Nullable
    public abstract MobEffectInstance getEffect(Holder<MobEffect> effect);

    @Shadow
    public abstract boolean hasEffect(Holder<MobEffect> effect);

    @Shadow
    @Nullable
    public abstract AttributeInstance getAttribute(Holder<Attribute> attribute);

    @Shadow
    public abstract double getAttributeValue(Holder<Attribute> attribute);

    @Shadow
    public abstract AttributeMap getAttributes();

    @Shadow
    @Final
    protected EntityEquipment equipment;

    @Shadow public abstract ItemStack getItemBySlot(EquipmentSlot slot);

    @Redirect(method = "travelInAir", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getFriction(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/Entity;)F"))
    public float getFriction(BlockState instance, LevelReader levelReader, BlockPos blockPos, Entity entity) {
        if (hasEffect(MobEffects.SLIP_N_SLIDE)) {
            return SlipNSlideEffect.getFriction(getEffect(MobEffects.SLIP_N_SLIDE).getAmplifier());
        }

        return instance.getFriction(levelReader, blockPos, entity);
    }

    @Redirect(method = "travelInAir", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;setDeltaMovement(DDD)V", ordinal = 1))
    public void setDeltaMovement(LivingEntity livingEntity, double x, double y, double z) {
        if (hasEffect(MobEffects.SLIP_N_SLIDE) && livingEntity.onGround()) {
            double d = 0.91D;
            livingEntity.setDeltaMovement(x / d, y, z / d);
        } else {
            livingEntity.setDeltaMovement(x, y, z);
        }
    }

    // ----- Wreath -----

    @Inject(method = "checkTotemDeathProtection", at = @At("TAIL"), cancellable = true)
    public void checkTotemDeathProtection(DamageSource p_21263_, CallbackInfoReturnable<Boolean> cir) {
        ItemStack wreathe = null;
        ItemStack itemStack = this.getItemBySlot(EquipmentSlot.HEAD);
        if (itemStack.is(HatItems.WREATH)) {
            wreathe = itemStack.copy();
            itemStack.shrink(1);
        }

        if (wreathe != null) {
            if (((Object) this) instanceof ServerPlayer serverPlayer) {
                serverPlayer.awardStat(Stats.ITEM_USED.get(Items.TOTEM_OF_UNDYING), 1);
                CriteriaTriggers.USED_TOTEM.trigger(serverPlayer, wreathe);
            }

            this.setHealth(1.0F);
            this.removeAllEffects();
            this.addEffect(new MobEffectInstance(net.minecraft.world.effect.MobEffects.REGENERATION, 900, 1));
            this.addEffect(new MobEffectInstance(net.minecraft.world.effect.MobEffects.ABSORPTION, 100, 1));
            this.addEffect(new MobEffectInstance(net.minecraft.world.effect.MobEffects.FIRE_RESISTANCE, 800, 0));
            this.level().broadcastEntityEvent(this, (byte) 35);
        }

        if (wreathe != null) {
            cir.setReturnValue(true);
        }
    }

    // ----- Entity Speed Attributes -----

    /**
     * Create attribute modifiers from our custom attributes so that we can add them as modifiers on the entity movement speed attribute.
     * {@link grill24.potionsplus.core.Attributes#SPRINTING_SPEED}
     */
    @Unique
    private static final List<Holder<Attribute>> SPRINT_SPEED_ATTRIBUTES = new ArrayList<>() {{
        add(grill24.potionsplus.core.Attributes.SPRINTING_SPEED);
    }};

    @Inject(method = "setSprinting", at = @At("TAIL"))
    public void setSprinting(boolean sprinting, CallbackInfo ci) {
        Holder<Attribute> movementSpeed = Attributes.MOVEMENT_SPEED;
        AttributeInstance movementSpeedAttributeInstance = this.getAttribute(movementSpeed);
        for (Holder<Attribute> attribute : SPRINT_SPEED_ATTRIBUTES) {
            if (movementSpeedAttributeInstance != null) {
                if (attribute.getKey() != null) {
                    ResourceLocation key = attribute.getKey().location();
                    movementSpeedAttributeInstance.removeModifier(key);
                    if (sprinting && this.getAttributes().hasAttribute(attribute)) {
                        movementSpeedAttributeInstance.addTransientModifier(new AttributeModifier(key, this.getAttributeValue(attribute), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
                    }
                }
            }
        }
    }
}
