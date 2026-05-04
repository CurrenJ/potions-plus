package grill24.potionsplus.core.potion;

import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;

import java.util.Map;
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
}
