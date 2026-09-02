package grill24.potionsplus.core.potion;

import grill24.potionsplus.effect.AnyOtherPotionEffect;
import grill24.potionsplus.effect.AnyPotionEffect;
import grill24.potionsplus.effect.BoneBuddyEffect;
import grill24.potionsplus.effect.BotanicalBoostEffect;
import grill24.potionsplus.effect.BouncingEffect;
import grill24.potionsplus.effect.CropCollectorEffect;
import grill24.potionsplus.effect.ExplodingEffect;
import grill24.potionsplus.effect.FallOfTheVoidEffect;
import grill24.potionsplus.effect.FlyingTimeEffect;
import grill24.potionsplus.effect.FortuitousFateEffect;
import grill24.potionsplus.effect.GeodeGraceEffect;
import grill24.potionsplus.effect.GiantStepsEffect;
import grill24.potionsplus.effect.HarrowingHandsEffect;
import grill24.potionsplus.effect.LootingEffect;
import grill24.potionsplus.effect.MagneticEffect;
import grill24.potionsplus.effect.NauticalNitroEffect;
import grill24.potionsplus.effect.ReachForTheStarsEffect;
import grill24.potionsplus.effect.MetalDetectingEffect;
import grill24.potionsplus.effect.ShepherdsSerenadeEffect;
import grill24.potionsplus.effect.SlipNSlideEffect;
import grill24.potionsplus.effect.SoulMateEffect;
import grill24.potionsplus.effect.TeleportationEffect;
import grill24.potionsplus.alchemy.*;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * Holds every mod {@link MobEffect} holder, registered directly from {@link #init}. Every effect
 * class (including the 7 that used to be {@code @EventBusSubscriber}-coupled and living in
 * neoforge — see Phase 7) is now a plain, loader-agnostic {@code MobEffect} whose former event-bus
 * hooks live in each loader's own listener class.
 */
public class MobEffects {
    public static Holder<MobEffect> ANY_POTION;
    public static Holder<MobEffect> ANY_OTHER_POTION;
    public static Holder<MobEffect> GEODE_GRACE;
    public static Holder<MobEffect> FALL_OF_THE_VOID;
    public static Holder<MobEffect> EXPLODING;
    public static Holder<MobEffect> MAGNETIC;
    public static Holder<MobEffect> TELEPORTATION;
    public static Holder<MobEffect> LOOTING;
    public static Holder<MobEffect> FORTUITOUS_FATE;
    public static Holder<MobEffect> METAL_DETECTING;
    public static Holder<MobEffect> GIANT_STEPS;
    public static Holder<MobEffect> REACH_FOR_THE_STARS;
    public static Holder<MobEffect> NAUTICAL_NITRO;
    public static Holder<MobEffect> CROP_COLLECTOR;
    public static Holder<MobEffect> BOTANICAL_BOOST;
    public static Holder<MobEffect> SLIP_N_SLIDE;
    public static Holder<MobEffect> HARROWING_HANDS;
    public static Holder<MobEffect> BONE_BUDDY;
    public static Holder<MobEffect> SHEPHERDS_SERENADE;
    public static Holder<MobEffect> SOUL_MATE;
    public static Holder<MobEffect> FLYING_TIME;
    public static Holder<MobEffect> BOUNCING;

    public static void init(BiFunction<String, Supplier<MobEffect>, Holder<MobEffect>> register) {
        ANY_POTION = register.apply("any_potion", () -> new AnyPotionEffect(MobEffectCategory.BENEFICIAL, 0x000000));
        ANY_OTHER_POTION = register.apply("any_other_potion", () -> new AnyOtherPotionEffect(MobEffectCategory.BENEFICIAL, 0x000000));
        MAGNETIC = register.apply("magnetic", () -> new MagneticEffect(MobEffectCategory.BENEFICIAL, 0x556096));
        LOOTING = register.apply("looting", () -> new LootingEffect(MobEffectCategory.BENEFICIAL, 0x12A0A0));
        FORTUITOUS_FATE = register.apply("fortuitous_fate", () -> new FortuitousFateEffect(MobEffectCategory.BENEFICIAL, 0x43A047));
        GIANT_STEPS = register.apply("giant_steps", () -> new GiantStepsEffect(MobEffectCategory.BENEFICIAL, 0x5ac8f8));
        REACH_FOR_THE_STARS = register.apply("reach_for_the_stars", () -> new ReachForTheStarsEffect(MobEffectCategory.BENEFICIAL, 0xa8e048));
        NAUTICAL_NITRO = register.apply("nautical_nitro", () -> new NauticalNitroEffect(MobEffectCategory.BENEFICIAL, 0x0077b6));
        CROP_COLLECTOR = register.apply("crop_collector", () -> new CropCollectorEffect(MobEffectCategory.BENEFICIAL, 0x00a86b));
        BOTANICAL_BOOST = register.apply("botanical_boost", () -> new BotanicalBoostEffect(MobEffectCategory.BENEFICIAL, 0x00a86b));
        SLIP_N_SLIDE = register.apply("slip_n_slide", () -> new SlipNSlideEffect(MobEffectCategory.BENEFICIAL, 0x20709e));
        HARROWING_HANDS = register.apply("harrowing_hands", () -> new HarrowingHandsEffect(MobEffectCategory.BENEFICIAL, 0x20709e));
        SHEPHERDS_SERENADE = register.apply("shepherds_serenade", () -> new ShepherdsSerenadeEffect(MobEffectCategory.BENEFICIAL, 0xa4582b));
        TELEPORTATION = register.apply("teleportation", () -> new TeleportationEffect(MobEffectCategory.NEUTRAL, 0xab3f3f));
        METAL_DETECTING = register.apply("metal_detecting", () -> new MetalDetectingEffect(MobEffectCategory.BENEFICIAL, 0x7A7A7A));
        GEODE_GRACE = register.apply("geode_grace", () -> new GeodeGraceEffect(MobEffectCategory.NEUTRAL, 0xECD350));
        FALL_OF_THE_VOID = register.apply("fall_of_the_void", () -> new FallOfTheVoidEffect(MobEffectCategory.BENEFICIAL, 0xCE27F8));
        EXPLODING = register.apply("exploding", () -> new ExplodingEffect(MobEffectCategory.BENEFICIAL, 0xaa2320));
        BONE_BUDDY = register.apply("bone_buddy", () -> new BoneBuddyEffect(MobEffectCategory.BENEFICIAL, 0xdddddd));
        SOUL_MATE = register.apply("soul_mate", () -> new SoulMateEffect(MobEffectCategory.BENEFICIAL, 0x035690));
        FLYING_TIME = register.apply("flying_time", () -> new FlyingTimeEffect(MobEffectCategory.BENEFICIAL, 0x035690));
        BOUNCING = register.apply("bouncing", () -> new BouncingEffect(MobEffectCategory.BENEFICIAL, 0x035690));
    }

    /**
     * The literal order this mod registered its effects in. Only grows at the end - appending a new
     * effect here is the only way to add one, and doing so cannot shift any existing effect's icon
     * index. See {@link EffectRegistry#iconOrder()}.
     */
    public static List<Holder<MobEffect>> registrationOrder() {
        return List.of(
                ANY_POTION, ANY_OTHER_POTION, GEODE_GRACE, FALL_OF_THE_VOID, EXPLODING, MAGNETIC,
                TELEPORTATION, LOOTING, FORTUITOUS_FATE, METAL_DETECTING, GIANT_STEPS, REACH_FOR_THE_STARS,
                NAUTICAL_NITRO, CROP_COLLECTOR, BOTANICAL_BOOST, SLIP_N_SLIDE, HARROWING_HANDS, BONE_BUDDY,
                SHEPHERDS_SERENADE, SOUL_MATE, FLYING_TIME, BOUNCING);
    }
}
