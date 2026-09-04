package grill24.potionsplus.core.neoforge.potion;

import grill24.potionsplus.core.potion.MobEffects;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Owns the NeoForge {@link DeferredRegister} that flushes {@link MobEffects}' loader-agnostic
 * effect definitions. Every effect (including the 7 that were {@code @EventBusSubscriber} classes
 * before Phase 7) is now constructed from {@link MobEffects#init}; this class just supplies the
 * NeoForge-flavoured register function. See docs/multi-loader-expansion.md Phase 4 and Phase 7.
 */
public class MobEffectsRegistrar {
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, ModInfo.MOD_ID);

    static {
        MobEffects.init(EFFECTS::register);
    }
}
