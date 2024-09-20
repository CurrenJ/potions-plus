package grill24.potionsplus.mixin;

import grill24.potionsplus.core.potion.MobEffects;
import grill24.potionsplus.effect.SlipNSlideEffect;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Shadow
    public abstract boolean hasEffect(MobEffect p_21024_);

    @Shadow
    @Nullable
    public abstract MobEffectInstance getEffect(MobEffect p_21125_);

    // ----- SlipNSlideEffect -----

    @Shadow public abstract Iterable<ItemStack> getArmorSlots();

    @Shadow protected abstract void serverAiStep();

    @Shadow public abstract void setHealth(float p_21154_);

    @Shadow public abstract boolean removeAllEffects();

    @Shadow public abstract boolean addEffect(MobEffectInstance p_21165_);

    @Redirect(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getFriction(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/Entity;)F"))
    public float getFriction(BlockState instance, LevelReader levelReader, BlockPos blockPos, Entity entity) {
        if (hasEffect(MobEffects.SLIP_N_SLIDE.get())) {
            return SlipNSlideEffect.getFriction(getEffect(MobEffects.SLIP_N_SLIDE.get()).getAmplifier());
        }

        return instance.getFriction(levelReader, blockPos, entity);
    }

    @Redirect(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;setDeltaMovement(DDD)V", ordinal = 3))
    public void setDeltaMovement(LivingEntity livingEntity, double x, double y, double z) {
        if (hasEffect(MobEffects.SLIP_N_SLIDE.get()) && livingEntity.onGround()) {
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
        for (ItemStack itemStack : getArmorSlots()) {
            if(itemStack.is(grill24.potionsplus.core.Items.WREATH.get())) {
                wreathe = itemStack.copy();
                itemStack.shrink(1);
                break;
            }
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
            this.level().broadcastEntityEvent(this, (byte)35);
        }

        if(wreathe != null) {
            cir.setReturnValue(true);
        }
    }
}
