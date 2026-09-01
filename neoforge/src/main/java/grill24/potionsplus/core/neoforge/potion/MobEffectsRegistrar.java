package grill24.potionsplus.core.neoforge.potion;

import grill24.potionsplus.core.potion.MobEffects;
import grill24.potionsplus.effect.BoneBuddyEffect;
import grill24.potionsplus.effect.BouncingEffect;
import grill24.potionsplus.effect.ExplodingEffect;
import grill24.potionsplus.effect.FallOfTheVoidEffect;
import grill24.potionsplus.effect.FlyingTimeEffect;
import grill24.potionsplus.effect.GeodeGraceEffect;
import grill24.potionsplus.effect.MetalDetectingEffect;
import grill24.potionsplus.effect.SoulMateEffect;
import grill24.potionsplus.effect.TeleportationEffect;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Owns the NeoForge {@link DeferredRegister} that flushes {@link MobEffects}' loader-agnostic
 * effect definitions, plus the handful of effects that are still {@code @EventBusSubscriber}
 * classes (Phase 7 bucket) and so can't be constructed from common yet. See
 * docs/multi-loader-expansion.md Phase 4 and Phase 7.
 */
public class MobEffectsRegistrar {
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, ModInfo.MOD_ID);

    static {
        MobEffects.init(EFFECTS::register);

        MobEffects.GEODE_GRACE = EFFECTS.register("geode_grace", () -> new GeodeGraceEffect(MobEffectCategory.NEUTRAL, 0xECD350));
        MobEffects.FALL_OF_THE_VOID = EFFECTS.register("fall_of_the_void", () -> new FallOfTheVoidEffect(MobEffectCategory.BENEFICIAL, 0xCE27F8));
        MobEffects.EXPLODING = EFFECTS.register("exploding", () -> new ExplodingEffect(MobEffectCategory.BENEFICIAL, 0xaa2320));
        MobEffects.TELEPORTATION = EFFECTS.register("teleportation", () -> new TeleportationEffect(MobEffectCategory.NEUTRAL, 0xab3f3f));
        MobEffects.METAL_DETECTING = EFFECTS.register("metal_detecting", () -> new MetalDetectingEffect(MobEffectCategory.BENEFICIAL, 0x7A7A7A));
        MobEffects.BONE_BUDDY = EFFECTS.register("bone_buddy", () -> new BoneBuddyEffect(MobEffectCategory.BENEFICIAL, 0xdddddd));
        MobEffects.SOUL_MATE = EFFECTS.register("soul_mate", () -> new SoulMateEffect(MobEffectCategory.BENEFICIAL, 0x035690));
        MobEffects.FLYING_TIME = EFFECTS.register("flying_time", () -> new FlyingTimeEffect(MobEffectCategory.BENEFICIAL, 0x035690));
        MobEffects.BOUNCING = EFFECTS.register("bouncing", () -> new BouncingEffect(MobEffectCategory.BENEFICIAL, 0x035690));
    }
}
