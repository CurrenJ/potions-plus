package grill24.potionsplus.effect;

import grill24.potionsplus.core.potion.MobEffects;
import grill24.potionsplus.utility.ModInfo;
import grill24.potionsplus.utility.Utility;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@EventBusSubscriber(modid = ModInfo.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class SoulMateEffect extends MobEffect implements IEffectTooltipDetails{
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
        return ticks % 200 == 0; // 10 seconds
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if(!entity.level().isClientSide) {
            addEffect(entity);
        }

        return true;
    }

    @SubscribeEvent
    public static void onEntityHurt(final LivingDamageEvent.Pre livingHurtEvent) {
        if (livingHurtEvent.getEntity() == null || soulMates.size() < 2) {
            return;
        }


        if (livingHurtEvent.getEntity().hasEffect(MobEffects.SOUL_MATE)) {
            int amplifier = livingHurtEvent.getEntity().getEffect(MobEffects.SOUL_MATE).getAmplifier();
            float totalDamageToRedirect = livingHurtEvent.getOriginalDamage() * getPercentToRedirect(amplifier);
            float damageToRedirectPerEntity = totalDamageToRedirect / ((float) soulMates.size() - 1);
            livingHurtEvent.setNewDamage(livingHurtEvent.getOriginalDamage() - totalDamageToRedirect);

            for (int soulMate : soulMates) {
                Entity entity = livingHurtEvent.getEntity().level().getEntity(soulMate);
                if(entity != null) {
                    entity.hurt(livingHurtEvent.getSource(), damageToRedirectPerEntity);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onEntityHeal(final LivingHealEvent livingHealEvent) {
        if(livingHealEvent.getEntity() == null || soulMates.size() < 2) {
            return;
        }


        if (livingHealEvent.getEntity().hasEffect(MobEffects.SOUL_MATE)) {
            int amplifier = livingHealEvent.getEntity().getEffect(MobEffects.SOUL_MATE).getAmplifier();
            float totalHealToRedirect = livingHealEvent.getAmount() * getPercentToRedirect(amplifier);
            float healToRedirectPerEntity = totalHealToRedirect / ((float) soulMates.size() - 1);
            livingHealEvent.setAmount(livingHealEvent.getAmount() - totalHealToRedirect);

            for (int soulMate : soulMates) {
                Entity entity = livingHealEvent.getEntity().level().getEntity(soulMate);
                if(entity instanceof LivingEntity livingEntity) {
                    if (healToRedirectPerEntity <= 0) return;
                    float health = livingEntity.getHealth();
                    if (health > 0.0F) {
                        livingEntity.setHealth(health + healToRedirectPerEntity);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onUsePotion(final MobEffectEvent.Added potionAddedEvent) {
        addEffect(potionAddedEvent.getEntity());
    }

    @SubscribeEvent
    public static void onRemovePotion(final MobEffectEvent.Expired potionRemoveEvent) {
        removeEffect(potionRemoveEvent.getEntity());
    }

    @SubscribeEvent
    public static void onPotionExpiry(final MobEffectEvent.Remove potionExpiryEvent) {
        removeEffect(potionExpiryEvent.getEntity());
    }

    @SubscribeEvent
    public static void onEntityDeath(final LivingDeathEvent deathEvent) {
        removeEffect(deathEvent.getEntity());
    }

    private static void removeEffect(LivingEntity entity) {
        if(!entity.level().isClientSide) {
            soulMates.remove(entity.getId());
        }
    }

    private static void addEffect(LivingEntity entity) {
        if(!entity.level().isClientSide) {
            soulMates.add(entity.getId());
        }
    }

    @Override
    public List<Component> getTooltipDetails(MobEffectInstance effectInstance) {
        Component percentageComponent = Utility.formatEffectNumber(getPercentToRedirect(effectInstance.getAmplifier()) * 100F, 0, "%");

        return List.of(
                Component.translatable("effect.potionsplus.soul_mate.tooltip_1").withStyle(ChatFormatting.LIGHT_PURPLE),
                percentageComponent,
                Component.translatable("effect.potionsplus.soul_mate.tooltip_2").withStyle(ChatFormatting.LIGHT_PURPLE));
    }
}
