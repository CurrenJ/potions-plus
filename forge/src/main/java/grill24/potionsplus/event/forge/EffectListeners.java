package grill24.potionsplus.event.forge;

import grill24.potionsplus.effect.BoneBuddyEffect;
import grill24.potionsplus.effect.BouncingEffect;
import grill24.potionsplus.effect.ExplodingEffect;
import grill24.potionsplus.effect.FallOfTheVoidEffect;
import grill24.potionsplus.effect.FlyingTimeEffect;
import grill24.potionsplus.effect.GeodeGraceEffect;
import grill24.potionsplus.effect.SoulMateEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;

/**
 * Forge equivalent of NeoForge's {@code event/neoforge/EffectListeners.java} (Phase 7). Forge
 * 52.1.2 keeps the classic {@link net.minecraftforge.eventbus.api.IEventBus} API - listeners are
 * registered as plain consumers against {@link MinecraftForge#EVENT_BUS}, not via
 * {@code @SubscribeEvent} auto-discovery, matching the rest of this module's explicit-registration
 * style (see {@code core/forge/Packets.java}).
 */
public final class EffectListeners {
    private EffectListeners() {
    }

    public static void register() {
        MinecraftForge.EVENT_BUS.addListener((MobEffectEvent.Added event) -> {
            BoneBuddyEffect.onPotionAdded(event.getEntity(), event.getEffectInstance());
            FlyingTimeEffect.onPotionAdded(event.getEntity(), event.getEffectInstance());
            ExplodingEffect.onPotionAdded(event.getEntity(), event.getEffectInstance());
            SoulMateEffect.onPotionAdded(event.getEntity());
        });

        MinecraftForge.EVENT_BUS.addListener((MobEffectEvent.Expired event) -> {
            BoneBuddyEffect.onPotionExpired(event.getEntity(), event.getEffectInstance());
            FlyingTimeEffect.onPotionExpired(event.getEntity(), event.getEffectInstance());
            ExplodingEffect.onPotionExpiry(event.getEntity(), event.getEffectInstance());
            SoulMateEffect.onPotionExpired(event.getEntity());
        });

        MinecraftForge.EVENT_BUS.addListener((MobEffectEvent.Remove event) -> {
            FlyingTimeEffect.onPotionRemoved(event.getEntity(), event.getEffectInstance());
            SoulMateEffect.onPotionRemoved(event.getEntity());
        });

        MinecraftForge.EVENT_BUS.addListener((LivingFallEvent event) -> {
            if (event.getEntity() instanceof LivingEntity livingEntity) {
                BouncingEffect.onFall(livingEntity);
            }
            if (BouncingEffect.onLivingFall(event.getEntity(), event.getDistance())) {
                event.setCanceled(true);
            }
        });

        MinecraftForge.EVENT_BUS.addListener((LivingDamageEvent event) -> {
            float damage = event.getAmount();

            float afterVoid = FallOfTheVoidEffect.onLivingEntityDamage(event.getEntity(), event.getSource(), damage);
            if (afterVoid != damage) {
                event.setAmount(afterVoid);
                damage = afterVoid;
            }

            float afterSoulMate = SoulMateEffect.onEntityHurt(event.getEntity(), event.getSource(), damage);
            if (afterSoulMate != damage) {
                event.setAmount(afterSoulMate);
            }
        });

        MinecraftForge.EVENT_BUS.addListener((LivingHealEvent event) -> {
            float newAmount = SoulMateEffect.onEntityHeal(event.getEntity(), event.getAmount());
            if (newAmount != event.getAmount()) {
                event.setAmount(newAmount);
            }
        });

        MinecraftForge.EVENT_BUS.addListener((LivingDeathEvent event) -> {
            GeodeGraceEffect.onEntityDeath(event.getEntity(), event.getSource().getEntity());
            SoulMateEffect.onEntityDeath(event.getEntity());
        });
    }
}
