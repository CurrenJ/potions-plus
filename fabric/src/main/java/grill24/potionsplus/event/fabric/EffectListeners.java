package grill24.potionsplus.event.fabric;

import grill24.potionsplus.effect.GeodeGraceEffect;
import grill24.potionsplus.effect.SoulMateEffect;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;

/**
 * Fabric equivalent of the NeoForge/Forge {@code EffectListeners.onEntityDeath} dispatch (Phase 7).
 * Everything else in that group is handled by {@code mixin/fabric/LivingEntityMixin.java} - death
 * has a first-class fabric-api event, so it doesn't need a mixin.
 */
public final class EffectListeners {
    private EffectListeners() {
    }

    public static void register() {
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
            GeodeGraceEffect.onEntityDeath(entity, source.getEntity());
            SoulMateEffect.onEntityDeath(entity);
        });
    }
}
