package grill24.potionsplus.core.potion;


import grill24.potionsplus.effect.*;
import grill24.potionsplus.utility.ModInfo;
import grill24.potionsplus.alchemy.*;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

public class MobEffects {
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, ModInfo.MOD_ID);

    public static final Holder<MobEffect> ANY_POTION = EFFECTS.register("any_potion", () ->
            new AnyPotionEffect(MobEffectCategory.BENEFICIAL, 0x000000));
    public static final Holder<MobEffect> ANY_OTHER_POTION = EFFECTS.register("any_other_potion", () ->
            new AnyOtherPotionEffect(MobEffectCategory.BENEFICIAL, 0x000000));

    public static final Holder<MobEffect> GEODE_GRACE = EFFECTS.register("geode_grace", () ->
            new GeodeGraceEffect(MobEffectCategory.NEUTRAL, 0xECD350));

    public static final Holder<MobEffect> FALL_OF_THE_VOID = EFFECTS.register("fall_of_the_void", () ->
            new FallOfTheVoidEffect(MobEffectCategory.BENEFICIAL, 0xCE27F8));

    public static final Holder<MobEffect> EXPLODING = EFFECTS.register("exploding", () ->
            new ExplodingEffect(MobEffectCategory.BENEFICIAL, 0xaa2320));

    public static final Holder<MobEffect> MAGNETIC = EFFECTS.register("magnetic", () ->
            new MagneticEffect(MobEffectCategory.BENEFICIAL, 0x556096));

    public static final Holder<MobEffect> TELEPORTATION = EFFECTS.register("teleportation", () ->
            new TeleportationEffect(MobEffectCategory.NEUTRAL, 0xab3f3f));

    public static final Holder<MobEffect> LOOTING = EFFECTS.register("looting", () ->
            new LootingEffect(MobEffectCategory.BENEFICIAL, 0x12A0A0));

    public static final Holder<MobEffect> FORTUITOUS_FATE = EFFECTS.register("fortuitous_fate", () ->
            new FortuitousFateEffect(MobEffectCategory.BENEFICIAL, 0x43A047));

    public static final Holder<MobEffect> METAL_DETECTING = EFFECTS.register("metal_detecting", () ->
            new MetalDetectingEffect(MobEffectCategory.BENEFICIAL, 0x7A7A7A));

    public static final Holder<MobEffect> GIANT_STEPS = EFFECTS.register("giant_steps", () ->
            new GiantStepsEffect(MobEffectCategory.BENEFICIAL, 0x5ac8f8));

    public static final Holder<MobEffect> REACH_FOR_THE_STARS = EFFECTS.register("reach_for_the_stars", () ->
            new ReachForTheStarsEffect(MobEffectCategory.BENEFICIAL, 0xa8e048));

    public static final Holder<MobEffect> NAUTICAL_NITRO = EFFECTS.register("nautical_nitro", () ->
            new NauticalNitroEffect(MobEffectCategory.BENEFICIAL, 0x0077b6));

    public static final Holder<MobEffect> CROP_COLLECTOR = EFFECTS.register("crop_collector", () ->
            new CropCollectorEffect(MobEffectCategory.BENEFICIAL, 0x00a86b));

    public static final Holder<MobEffect> BOTANICAL_BOOST = EFFECTS.register("botanical_boost", () ->
            new BotanicalBoostEffect(MobEffectCategory.BENEFICIAL, 0x00a86b));

    public static final Holder<MobEffect> SLIP_N_SLIDE = EFFECTS.register("slip_n_slide", () ->
            new SlipNSlideEffect(MobEffectCategory.BENEFICIAL, 0x20709e));

    public static final Holder<MobEffect> HARROWING_HANDS = EFFECTS.register("harrowing_hands", () ->
            new HarrowingHandsEffect(MobEffectCategory.BENEFICIAL, 0x20709e));

    public static final Holder<MobEffect> BONE_BUDDY = EFFECTS.register("bone_buddy", () ->
            new BoneBuddyEffect(MobEffectCategory.BENEFICIAL, 0xdddddd));

    public static final Holder<MobEffect> SHEPHERDS_SERENADE = EFFECTS.register("shepherds_serenade", () ->
            new ShepherdsSerenadeEffect(MobEffectCategory.BENEFICIAL, 0xa4582b));

    public static final Holder<MobEffect> SOUL_MATE = EFFECTS.register("soul_mate", () ->
            new SoulMateEffect(MobEffectCategory.BENEFICIAL, 0x035690));

    public static final Holder<MobEffect> FLYING_TIME = EFFECTS.register("flying_time", () ->
            new FlyingTimeEffect(MobEffectCategory.BENEFICIAL, 0x035690));

    public static final Holder<MobEffect> BOUNCING = EFFECTS.register("bouncing", () ->
            new BouncingEffect(MobEffectCategory.BENEFICIAL, 0x035690));

    /**
     * The literal order this class registered its effects in. Only grows at the end - appending a new
     * effect here is the only way to add one, and doing so cannot shift any existing effect's icon
     * index. See {@link EffectRegistry#iconOrder()}.
     */
    private static final List<Holder<MobEffect>> REGISTRATION_ORDER = List.of(
            ANY_POTION, ANY_OTHER_POTION, GEODE_GRACE, FALL_OF_THE_VOID, EXPLODING, MAGNETIC,
            TELEPORTATION, LOOTING, FORTUITOUS_FATE, METAL_DETECTING, GIANT_STEPS, REACH_FOR_THE_STARS,
            NAUTICAL_NITRO, CROP_COLLECTOR, BOTANICAL_BOOST, SLIP_N_SLIDE, HARROWING_HANDS, BONE_BUDDY,
            SHEPHERDS_SERENADE, SOUL_MATE, FLYING_TIME, BOUNCING);

    public static List<Holder<MobEffect>> registrationOrder() {
        return REGISTRATION_ORDER;
    }
}
