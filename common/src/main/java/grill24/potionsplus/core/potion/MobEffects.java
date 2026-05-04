package grill24.potionsplus.core.potion;

import grill24.potionsplus.effect.*;
import grill24.potionsplus.utility.PUtil;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Supplier;

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

    public static Supplier<Map<Identifier, Integer>> POTION_ICON_INDEX_MAP;
    public static final int POTION_EFFECT_INDEX_PROPERTY_DIVIDEND = 64;

    @SuppressWarnings("unchecked")
    public static void init(BiFunction<String, Supplier<MobEffect>, Holder<MobEffect>> register) {
        ANY_POTION = register.apply("any_potion", () ->
                new AnyPotionEffect(MobEffectCategory.BENEFICIAL, 0x000000));
        ANY_OTHER_POTION = register.apply("any_other_potion", () ->
                new AnyOtherPotionEffect(MobEffectCategory.BENEFICIAL, 0x000000));
        GEODE_GRACE = register.apply("geode_grace", () ->
                new GeodeGraceEffect(MobEffectCategory.NEUTRAL, 0xECD350));
        FALL_OF_THE_VOID = register.apply("fall_of_the_void", () ->
                new FallOfTheVoidEffect(MobEffectCategory.BENEFICIAL, 0xCE27F8));
        EXPLODING = register.apply("exploding", () ->
                new ExplodingEffect(MobEffectCategory.BENEFICIAL, 0xaa2320));
        MAGNETIC = register.apply("magnetic", () ->
                new MagneticEffect(MobEffectCategory.BENEFICIAL, 0x556096));
        TELEPORTATION = register.apply("teleportation", () ->
                new TeleportationEffect(MobEffectCategory.NEUTRAL, 0xab3f3f));
        LOOTING = register.apply("looting", () ->
                new LootingEffect(MobEffectCategory.BENEFICIAL, 0x12A0A0));
        FORTUITOUS_FATE = register.apply("fortuitous_fate", () ->
                new FortuitousFateEffect(MobEffectCategory.BENEFICIAL, 0x43A047));
        METAL_DETECTING = register.apply("metal_detecting", () ->
                new MetalDetectingEffect(MobEffectCategory.BENEFICIAL, 0x7A7A7A));
        GIANT_STEPS = register.apply("giant_steps", () ->
                new GiantStepsEffect(MobEffectCategory.BENEFICIAL, 0x5ac8f8));
        REACH_FOR_THE_STARS = register.apply("reach_for_the_stars", () ->
                new ReachForTheStarsEffect(MobEffectCategory.BENEFICIAL, 0xa8e048));
        NAUTICAL_NITRO = register.apply("nautical_nitro", () ->
                new NauticalNitroEffect(MobEffectCategory.BENEFICIAL, 0x0077b6));
        CROP_COLLECTOR = register.apply("crop_collector", () ->
                new CropCollectorEffect(MobEffectCategory.BENEFICIAL, 0x00a86b));
        BOTANICAL_BOOST = register.apply("botanical_boost", () ->
                new BotanicalBoostEffect(MobEffectCategory.BENEFICIAL, 0x00a86b));
        SLIP_N_SLIDE = register.apply("slip_n_slide", () ->
                new SlipNSlideEffect(MobEffectCategory.BENEFICIAL, 0x20709e));
        HARROWING_HANDS = register.apply("harrowing_hands", () ->
                new HarrowingHandsEffect(MobEffectCategory.BENEFICIAL, 0x20709e));
        BONE_BUDDY = register.apply("bone_buddy", () ->
                new BoneBuddyEffect(MobEffectCategory.BENEFICIAL, 0xdddddd));
        SHEPHERDS_SERENADE = register.apply("shepherds_serenade", () ->
                new ShepherdsSerenadeEffect(MobEffectCategory.BENEFICIAL, 0xa4582b));
        SOUL_MATE = register.apply("soul_mate", () ->
                new SoulMateEffect(MobEffectCategory.BENEFICIAL, 0x035690));
        FLYING_TIME = register.apply("flying_time", () ->
                new FlyingTimeEffect(MobEffectCategory.BENEFICIAL, 0x035690));
        BOUNCING = register.apply("bouncing", () ->
                new BouncingEffect(MobEffectCategory.BENEFICIAL, 0x035690));
    }

    /**
     * Initializes the POTION_ICON_INDEX_MAP supplier. Called after effects are registered.
     */
    public static void initIconIndexMap() {
        POTION_ICON_INDEX_MAP = PUtil::getAllMobEffectsIconStackSizeMap;
    }
}
