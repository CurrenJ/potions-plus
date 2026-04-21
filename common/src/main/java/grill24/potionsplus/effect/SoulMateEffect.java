package grill24.potionsplus.effect;

import grill24.potionsplus.core.Translations;
import grill24.potionsplus.core.potion.MobEffects;
import grill24.potionsplus.event.AnimatedItemTooltipEvent;
import grill24.potionsplus.utility.Utility;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SoulMateEffect extends MobEffect implements IEffectTooltipDetails {
    public static Set<Integer> soulMates = new HashSet<>();

    public SoulMateEffect(MobEffectCategory mobEffectCategory, int color) {
        super(mobEffectCategory, color);
    }

    private static float getPercentToRedirect(int amplifier) {
        return switch (amplifier) {
            case 0 -> 0.25F;
            case 1 -> 0.5F;
            case 2 -> 0.75F;
            default -> 1.0F;
        };
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int ticks, int amp) {
        return ticks % 200 == 0;
    }

    @Override
    public boolean applyEffectTick(ServerLevel serverLevel, LivingEntity entity, int amplifier) {
        if (!entity.level().isClientSide) {
            addEffect(entity);
        }

        return true;
    }

    /**
     * Returns the new damage value to apply, or the original damage if no change.
     */
    public static float onEntityHurt(LivingEntity entity, DamageSource source, float originalDamage) {
        if (entity == null || soulMates.size() < 2) {
            return originalDamage;
        }

        if (entity.hasEffect(MobEffects.SOUL_MATE)) {
            int amplifier = entity.getEffect(MobEffects.SOUL_MATE).getAmplifier();
            float totalDamageToRedirect = originalDamage * getPercentToRedirect(amplifier);
            float damageToRedirectPerEntity = totalDamageToRedirect / ((float) soulMates.size() - 1);

            for (int soulMate : soulMates) {
                Entity soulMateEntity = entity.level().getEntity(soulMate);
                if (soulMateEntity != null) {
                    soulMateEntity.hurt(source, damageToRedirectPerEntity);
                }
            }
            return originalDamage - totalDamageToRedirect;
        }
        return originalDamage;
    }

    /**
     * Returns the new heal amount to apply, or the original amount if no change.
     */
    public static float onEntityHeal(LivingEntity entity, float amount) {
        if (entity == null || soulMates.size() < 2) {
            return amount;
        }

        if (entity.hasEffect(MobEffects.SOUL_MATE)) {
            int amplifier = entity.getEffect(MobEffects.SOUL_MATE).getAmplifier();
            float totalHealToRedirect = amount * getPercentToRedirect(amplifier);
            float healToRedirectPerEntity = totalHealToRedirect / ((float) soulMates.size() - 1);

            for (int soulMate : soulMates) {
                Entity soulMateEntity = entity.level().getEntity(soulMate);
                if (soulMateEntity instanceof LivingEntity livingEntity) {
                    if (healToRedirectPerEntity <= 0) return amount - totalHealToRedirect;
                    float health = livingEntity.getHealth();
                    if (health > 0.0F) {
                        livingEntity.setHealth(health + healToRedirectPerEntity);
                    }
                }
            }
            return amount - totalHealToRedirect;
        }
        return amount;
    }

    public static void onPotionAdded(LivingEntity entity) {
        addEffect(entity);
    }

    public static void onPotionExpired(LivingEntity entity) {
        removeEffect(entity);
    }

    public static void onPotionRemoved(LivingEntity entity) {
        removeEffect(entity);
    }

    public static void onEntityDeath(LivingEntity entity) {
        removeEffect(entity);
    }

    private static void removeEffect(LivingEntity entity) {
        if (!entity.level().isClientSide) {
            soulMates.remove(entity.getId());
        }
    }

    private static void addEffect(LivingEntity entity) {
        if (!entity.level().isClientSide) {
            soulMates.add(entity.getId());
        }
    }

    @Override
    public AnimatedItemTooltipEvent.TooltipLines getTooltipDetails(MobEffectInstance effectInstance) {
        Component percentageComponent = Utility.formatEffectNumber(getPercentToRedirect(effectInstance.getAmplifier()) * 100F, 0, "%");

        List<Component> text = List.of(
                Component.translatable(Translations.EFFECT_POTIONSPLUS_SOUL_MATE_TOOLTIP_1).withStyle(ChatFormatting.LIGHT_PURPLE),
                percentageComponent,
                Component.translatable(Translations.EFFECT_POTIONSPLUS_SOUL_MATE_TOOLTIP_2).withStyle(ChatFormatting.LIGHT_PURPLE));

        return createTooltipLine(text);
    }
}
