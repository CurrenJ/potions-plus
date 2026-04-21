package grill24.potionsplus.event;

import grill24.potionsplus.effect.*;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.*;

@EventBusSubscriber(modid = ModInfo.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class EffectListeners {

    @SubscribeEvent
    public static void onEffectAdded(final MobEffectEvent.Added event) {
        BoneBuddyEffect.onPotionAdded(event.getEntity(), event.getEffectInstance());
        FlyingTimeEffect.onPotionAdded(event.getEntity(), event.getEffectInstance());
        ExplodingEffect.onPotionAdded(event.getEntity(), event.getEffectInstance());
        SoulMateEffect.onPotionAdded(event.getEntity());
    }

    @SubscribeEvent
    public static void onEffectExpired(final MobEffectEvent.Expired event) {
        BoneBuddyEffect.onPotionExpired(event.getEntity(), event.getEffectInstance());
        FlyingTimeEffect.onPotionExpired(event.getEntity(), event.getEffectInstance());
        ExplodingEffect.onPotionExpiry(event.getEntity(), event.getEffectInstance());
        SoulMateEffect.onPotionExpired(event.getEntity());
    }

    @SubscribeEvent
    public static void onEffectRemoved(final MobEffectEvent.Remove event) {
        FlyingTimeEffect.onPotionRemoved(event.getEntity(), event.getEffectInstance());
        SoulMateEffect.onPotionRemoved(event.getEntity());
    }

    @SubscribeEvent
    public static void onLivingFall(final LivingFallEvent event) {
        if (event.getEntity() instanceof LivingEntity livingEntity) {
            BouncingEffect.onFall(livingEntity);
        }
        if (BouncingEffect.onLivingFall(event.getEntity(), event.getDistance())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onLivingDamagePre(final LivingDamageEvent.Pre event) {
        float damage = event.getOriginalDamage();

        float afterVoid = FallOfTheVoidEffect.onLivingEntityDamage(event.getEntity(), event.getSource(), damage);
        if (afterVoid != damage) {
            event.setNewDamage(afterVoid);
            damage = afterVoid;
        }

        float afterSoulMate = SoulMateEffect.onEntityHurt(event.getEntity(), event.getSource(), damage);
        if (afterSoulMate != damage) {
            event.setNewDamage(afterSoulMate);
        }
    }

    @SubscribeEvent
    public static void onEntityHeal(final LivingHealEvent event) {
        float newAmount = SoulMateEffect.onEntityHeal(event.getEntity(), event.getAmount());
        if (newAmount != event.getAmount()) {
            event.setAmount(newAmount);
        }
    }

    @SubscribeEvent
    public static void onEntityDeath(final LivingDeathEvent event) {
        GeodeGraceEffect.onEntityDeath(event.getEntity(), event.getSource().getEntity());
        SoulMateEffect.onEntityDeath(event.getEntity());
    }
}
