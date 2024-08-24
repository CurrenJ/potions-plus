package grill24.potionsplus.effect;

import grill24.potionsplus.core.potion.MobEffects;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mod.EventBusSubscriber(modid = ModInfo.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SoulMateEffect extends MobEffect {
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
    public boolean isDurationEffectTick(int ticks, int amp) {
        return ticks % 200 == 0; // 10 seconds
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if(!entity.level.isClientSide) {
            addEffect(entity);
        }
    }

    @SubscribeEvent
    public static void onEntityHurt(final LivingHurtEvent livingHurtEvent) {
        if (livingHurtEvent.getEntityLiving() == null || soulMates.size() < 2) {
            return;
        }


        if (livingHurtEvent.getEntityLiving().hasEffect(MobEffects.SOUL_MATE.get())) {
            int amplifier = livingHurtEvent.getEntityLiving().getEffect(MobEffects.SOUL_MATE.get()).getAmplifier();
            float totalDamageToRedirect = livingHurtEvent.getAmount() * getPercentToRedirect(amplifier);
            float damageToRedirectPerEntity = totalDamageToRedirect / ((float) soulMates.size() - 1);
            livingHurtEvent.setAmount(livingHurtEvent.getAmount() - totalDamageToRedirect);

            for (int soulMate : soulMates) {
                Entity entity = livingHurtEvent.getEntity().level.getEntity(soulMate);
                if(entity != null) {
                    entity.hurt(livingHurtEvent.getSource(), damageToRedirectPerEntity);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onEntityHeal(final LivingHealEvent livingHealEvent) {
        if(livingHealEvent.getEntityLiving() == null || soulMates.size() < 2) {
            return;
        }


        if (livingHealEvent.getEntityLiving().hasEffect(MobEffects.SOUL_MATE.get())) {
            int amplifier = livingHealEvent.getEntityLiving().getEffect(MobEffects.SOUL_MATE.get()).getAmplifier();
            float totalHealToRedirect = livingHealEvent.getAmount() * getPercentToRedirect(amplifier);
            float healToRedirectPerEntity = totalHealToRedirect / ((float) soulMates.size() - 1);
            livingHealEvent.setAmount(livingHealEvent.getAmount() - totalHealToRedirect);

            for (int soulMate : soulMates) {
                Entity entity = livingHealEvent.getEntity().level.getEntity(soulMate);
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
    public static void onUsePotion(final PotionEvent.PotionAddedEvent potionAddedEvent) {
        addEffect(potionAddedEvent.getEntityLiving());
    }

    @SubscribeEvent
    public static void onRemovePotion(final PotionEvent.PotionRemoveEvent potionExpiryEvent) {
        removeEffect(potionExpiryEvent.getEntityLiving());
    }

    @SubscribeEvent
    public static void onPotionExpiry(final PotionEvent.PotionExpiryEvent potionExpiryEvent) {
        removeEffect(potionExpiryEvent.getEntityLiving());
    }

    @SubscribeEvent
    public static void onEntityDeath(final LivingDeathEvent deathEvent) {
        removeEffect(deathEvent.getEntityLiving());
    }

    private static void removeEffect(LivingEntity entity) {
        if(!entity.level.isClientSide) {
            soulMates.remove(entity.getId());
        }
    }

    private static void addEffect(LivingEntity entity) {
        if(!entity.level.isClientSide) {
            soulMates.add(entity.getId());
        }
    }
}
