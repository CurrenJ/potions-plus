package grill24.potionsplus.core.potion;


import grill24.potionsplus.effect.*;
import grill24.potionsplus.utility.ModInfo;
import grill24.potionsplus.utility.PUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Map;

public class MobEffects {
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, ModInfo.MOD_ID);

    public static final RegistryObject<MobEffect> GEODE_GRACE = EFFECTS.register("geode_grace", () ->
            new GeodeGraceEffect(MobEffectCategory.NEUTRAL, 0xECD350));

    public static final RegistryObject<MobEffect> FALL_OF_THE_VOID = EFFECTS.register("fall_of_the_void", () ->
            new FallOfTheVoidEffect(MobEffectCategory.BENEFICIAL, 0xCE27F8));

    public static final RegistryObject<MobEffect> EXPLODING = EFFECTS.register("exploding", () ->
            new ExplodingEffect(MobEffectCategory.BENEFICIAL, 0xaa2320));

    public static final RegistryObject<MobEffect> MAGNETIC = EFFECTS.register("magnetic", () ->
            new MagneticEffect(MobEffectCategory.BENEFICIAL, 0x556096));

    public static final RegistryObject<MobEffect> TELEPORTATION = EFFECTS.register("teleportation", () ->
            new TeleportationEffect(MobEffectCategory.NEUTRAL, 0xab3f3f));

    public static final RegistryObject<MobEffect> LOOTING = EFFECTS.register("looting", () ->
            new LootingEffect(MobEffectCategory.BENEFICIAL, 0x12A0A0));

    public static final RegistryObject<MobEffect> FORTUITOUS_FATE = EFFECTS.register("fortuitous_fate", () ->
            new FortuitousFateEffect(MobEffectCategory.BENEFICIAL, 0x43A047));

    public static final RegistryObject<MobEffect> METAL_DETECTING = EFFECTS.register("metal_detecting", () ->
            new MetalDetectingEffect(MobEffectCategory.BENEFICIAL, 0x7A7A7A));

    public static final RegistryObject<MobEffect> GIANT_STEPS = EFFECTS.register("giant_steps", () ->
            new GiantStepsEffect(MobEffectCategory.BENEFICIAL, 0x5ac8f8));

    public static final RegistryObject<MobEffect> REACH_FOR_THE_STARS = EFFECTS.register("reach_for_the_stars", () ->
            new ReachForTheStarsEffect(MobEffectCategory.BENEFICIAL, 0xa8e048));

    public static final RegistryObject<MobEffect> NAUTICAL_NITRO = EFFECTS.register("nautical_nitro", () ->
            new NauticalNitroEffect(MobEffectCategory.BENEFICIAL, 0x0077b6));

    public static final RegistryObject<MobEffect> CROP_COLLECTOR = EFFECTS.register("crop_collector", () ->
            new CropCollectorEffect(MobEffectCategory.BENEFICIAL, 0x00a86b));

    public static final RegistryObject<MobEffect> BOTANICAL_BOOST = EFFECTS.register("botanical_boost", () ->
            new BotanicalBoostEffect(MobEffectCategory.BENEFICIAL, 0x00a86b));

    public static final RegistryObject<MobEffect> SLIP_N_SLIDE = EFFECTS.register("slip_n_slide", () ->
            new SlipNSlideEffect(MobEffectCategory.BENEFICIAL, 0x20709e));

    public static final RegistryObject<MobEffect> HARROWING_HANDS = EFFECTS.register("harrowing_hands", () ->
            new HarrowingHandsEffect(MobEffectCategory.BENEFICIAL, 0x20709e));

    public static final RegistryObject<MobEffect> BONE_BUDDY = EFFECTS.register("bone_buddy", () ->
            new BoneBuddyEffect(MobEffectCategory.BENEFICIAL, 0xdddddd));


    public static final Lazy<Map<ResourceLocation, Integer>> POTION_ICON_INDEX_MAP = Lazy.of(PUtil::getAllMobEffectsIconStackSizeMap);
    public static final int POTION_EFFECT_INDEX_PROPERTY_DIVIDEND = 64;
}
