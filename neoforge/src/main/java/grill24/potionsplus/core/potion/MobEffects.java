package grill24.potionsplus.core.potion;


import grill24.potionsplus.effect.*;
import grill24.potionsplus.utility.ModInfo;
import grill24.potionsplus.utility.PUtil;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Map;

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

    // New fun potion effects
    public static final Holder<MobEffect> CHICKEN_FRENZY = EFFECTS.register("chicken_frenzy", () ->
            new ChickenFrenzyEffect(MobEffectCategory.NEUTRAL, 0xffc649));

    public static final Holder<MobEffect> BACKWARDS_DAY = EFFECTS.register("backwards_day", () ->
            new BackwardsDayEffect(MobEffectCategory.HARMFUL, 0x8b008b));

    public static final Holder<MobEffect> SQUEAKY_VOICE = EFFECTS.register("squeaky_voice", () ->
            new SqueakyVoiceEffect(MobEffectCategory.NEUTRAL, 0xff69b4));

    public static final Holder<MobEffect> DIZZY_SPELLS = EFFECTS.register("dizzy_spells", () ->
            new DizzySpellsEffect(MobEffectCategory.HARMFUL, 0x9932cc));

    public static final Holder<MobEffect> MIDAS_TOUCH = EFFECTS.register("midas_touch", () ->
            new MidasTouchEffect(MobEffectCategory.BENEFICIAL, 0xffd700));


    public static final Lazy<Map<ResourceLocation, Integer>> POTION_ICON_INDEX_MAP = Lazy.of(PUtil::getAllMobEffectsIconStackSizeMap);
    public static final int POTION_EFFECT_INDEX_PROPERTY_DIVIDEND = 64;
}
