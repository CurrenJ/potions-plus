package grill24.potionsplus.core.potion;

import grill24.potionsplus.core.seededrecipe.PotionUpgradeIngredients;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * Loader-agnostic potion hub. The 23 {@link PotionBuilder.PotionsPlusPotionGenerationData} fields
 * register at class-load through {@link PotionBuilder#potionFactory}, which the loader registrar
 * wires to its potion {@code DeferredRegister} (NeoForge/Forge) or immediate fabric registry before
 * this class is first touched - so registration stays deferred on NeoForge/Forge and immediate on
 * Fabric. The 8 potions whose effects are still {@code @EventBusSubscriber}-coupled NeoForge classes
 * (GEODE_GRACE, FALL_OF_THE_VOID, EXPLODING, TELEPORTATION, METAL_DETECTING, SOUL_MATE, FLYING_TIME,
 * BOUNCING - Phase 7 bucket) are registered by {@code core.neoforge.potion.PotionsRegistrar} and
 * appended to {@link #ALL_POTION_GENERATION_DATA}. See docs/multi-loader-expansion.md Phase 4.
 */
public class Potions {
    public static final List<PotionBuilder.PotionsPlusPotionGenerationData> ALL_POTION_GENERATION_DATA = new ArrayList<>();

    public static Holder<Potion> ANY_POTION;
    public static Holder<Potion> ANY_OTHER_POTION;

    public static final PotionBuilder.PotionsPlusPotionGenerationData HASTE_POTIONS = new PotionBuilder()
            .name("haste")
            .effect(net.minecraft.world.effect.MobEffects.DIG_SPEED)
            .withRarityCount(PotionUpgradeIngredients.Rarity.RARE, 1)
            .build(ALL_POTION_GENERATION_DATA::add);

    public static final PotionBuilder.PotionsPlusPotionGenerationData LEVITATION_POTIONS = new PotionBuilder()
            .name("levitation")
            .effect(net.minecraft.world.effect.MobEffects.LEVITATION)
            .build(ALL_POTION_GENERATION_DATA::add);

    public static final PotionBuilder.PotionsPlusPotionGenerationData MAGNETIC_POTIONS = new PotionBuilder()
            .name("magnetic")
            .effect(MobEffects.MAGNETIC)
            .build(ALL_POTION_GENERATION_DATA::add);

    public static final PotionBuilder.PotionsPlusPotionGenerationData GIANT_STEPS_POTIONS = new PotionBuilder()
            .name("giant_steps")
            .effect(MobEffects.GIANT_STEPS)
            .build(ALL_POTION_GENERATION_DATA::add);

    public static final PotionBuilder.PotionsPlusPotionGenerationData REACH_FOR_THE_STARS_POTIONS = new PotionBuilder()
            .name("reach_for_the_stars")
            .effect(MobEffects.REACH_FOR_THE_STARS)
            .build(ALL_POTION_GENERATION_DATA::add);

    public static final PotionBuilder.PotionsPlusPotionGenerationData NAUTICAL_NITRO_POTIONS = new PotionBuilder()
            .name("nautical_nitro")
            .withRarityCount(PotionUpgradeIngredients.Rarity.COMMON, 2)
            .effect(MobEffects.NAUTICAL_NITRO)
            .build(ALL_POTION_GENERATION_DATA::add);

    public static final PotionBuilder.PotionsPlusPotionGenerationData CROP_COLLECTOR_POTIONS = new PotionBuilder()
            .name("crop_collector")
            .effect(MobEffects.CROP_COLLECTOR)
            .withRarityCount(PotionUpgradeIngredients.Rarity.COMMON, 2)
            .build(ALL_POTION_GENERATION_DATA::add);

    public static final PotionBuilder.PotionsPlusPotionGenerationData BOTANICAL_BOOST_POTIONS = new PotionBuilder()
            .name("botanical_boost")
            .effect(MobEffects.BOTANICAL_BOOST)
            .withRarityCount(PotionUpgradeIngredients.Rarity.COMMON, 2)
            .build(ALL_POTION_GENERATION_DATA::add);

    public static final PotionBuilder.PotionsPlusPotionGenerationData SATURATION_POTIONS = new PotionBuilder()
            .name("saturation")
            .effect(net.minecraft.world.effect.MobEffects.SATURATION)
            .withRarityCount(PotionUpgradeIngredients.Rarity.RARE, 2)
            .build(ALL_POTION_GENERATION_DATA::add);

    public static final PotionBuilder.PotionsPlusPotionGenerationData BLINDNESS_POTIONS = new PotionBuilder()
            .name("blindness")
            .effect(net.minecraft.world.effect.MobEffects.BLINDNESS)
            .build(ALL_POTION_GENERATION_DATA::add);

    public static final PotionBuilder.PotionsPlusPotionGenerationData CONFUSION_POTIONS = new PotionBuilder()
            .name("confusion")
            .effect(net.minecraft.world.effect.MobEffects.CONFUSION)
            .build(ALL_POTION_GENERATION_DATA::add);

    public static final PotionBuilder.PotionsPlusPotionGenerationData HUNGER_POTIONS = new PotionBuilder()
            .name("hunger")
            .effect(net.minecraft.world.effect.MobEffects.HUNGER)
            .build(ALL_POTION_GENERATION_DATA::add);

    public static final PotionBuilder.PotionsPlusPotionGenerationData WITHER_POTIONS = new PotionBuilder()
            .name("wither")
            .effect(net.minecraft.world.effect.MobEffects.WITHER)
            .withRarityCount(PotionUpgradeIngredients.Rarity.RARE, 1)
            .build(ALL_POTION_GENERATION_DATA::add);

    public static final PotionBuilder.PotionsPlusPotionGenerationData SLIP_N_SLIDE_POTIONS = new PotionBuilder()
            .name("slip_n_slide")
            .effect(MobEffects.SLIP_N_SLIDE)
            .build(ALL_POTION_GENERATION_DATA::add);

    public static final PotionBuilder.PotionsPlusPotionGenerationData HARROWING_HANDS_POTIONS = new PotionBuilder()
            .name("harrowing_hands")
            .effect(MobEffects.HARROWING_HANDS)
            .withRarityCount(PotionUpgradeIngredients.Rarity.COMMON, 2)
            .build(ALL_POTION_GENERATION_DATA::add);

    public static final PotionBuilder.PotionsPlusPotionGenerationData SHEPHERDS_SERENADE_POTIONS = new PotionBuilder()
            .name("shepherds_serenade")
            .effect(MobEffects.SHEPHERDS_SERENADE)
            .build(ALL_POTION_GENERATION_DATA::add);

    public static final PotionBuilder.PotionsPlusPotionGenerationData STRENGTH_POTIONS = new PotionBuilder()
            .name("strength")
            .effect(net.minecraft.world.effect.MobEffects.DAMAGE_BOOST)
            .withRarityCount(PotionUpgradeIngredients.Rarity.RARE, 1)
            .build(ALL_POTION_GENERATION_DATA::add);

    public static final PotionBuilder.PotionsPlusPotionGenerationData RESISTANCE_POTIONS = new PotionBuilder()
            .name("resistance")
            .effect(net.minecraft.world.effect.MobEffects.DAMAGE_RESISTANCE)
            .withRarityCount(PotionUpgradeIngredients.Rarity.RARE, 2)
            .build(ALL_POTION_GENERATION_DATA::add);

    public static final PotionBuilder.PotionsPlusPotionGenerationData FIRE_RESISTANCE_POTIONS = new PotionBuilder()
            .name("fire_resistance")
            .effect(net.minecraft.world.effect.MobEffects.FIRE_RESISTANCE)
            .withRarityCount(PotionUpgradeIngredients.Rarity.COMMON, 2)
            .build(ALL_POTION_GENERATION_DATA::add);

    public static final PotionBuilder.PotionsPlusPotionGenerationData WATER_BREATHING_POTIONS = new PotionBuilder()
            .name("water_breathing")
            .effect(net.minecraft.world.effect.MobEffects.WATER_BREATHING)
            .withRarityCount(PotionUpgradeIngredients.Rarity.COMMON, 1)
            .build(ALL_POTION_GENERATION_DATA::add);

    public static final PotionBuilder.PotionsPlusPotionGenerationData INVISIBILITY_POTIONS = new PotionBuilder()
            .name("invisibility")
            .effect(net.minecraft.world.effect.MobEffects.INVISIBILITY)
            .withRarityCount(PotionUpgradeIngredients.Rarity.COMMON, 1)
            .build(ALL_POTION_GENERATION_DATA::add);

    public static final PotionBuilder.PotionsPlusPotionGenerationData LOOTING_POTIONS = new PotionBuilder()
            .name("looting")
            .effect(MobEffects.LOOTING)
            .withRarityCount(PotionUpgradeIngredients.Rarity.RARE, 1)
            .build(ALL_POTION_GENERATION_DATA::add);

    public static final PotionBuilder.PotionsPlusPotionGenerationData FORTUITOUS_FATE_POTIONS = new PotionBuilder()
            .name("fortuitous_fate")
            .effect(MobEffects.FORTUITOUS_FATE)
            .withRarityCount(PotionUpgradeIngredients.Rarity.RARE, 1)
            .build(ALL_POTION_GENERATION_DATA::add);

    public static void init(BiFunction<String, Supplier<Potion>, Holder<Potion>> register) {
        ANY_POTION = register.apply("any_potion", () -> new Potion(new MobEffectInstance(MobEffects.ANY_POTION, 1200, 0)));
        ANY_OTHER_POTION = register.apply("any_other_potion", () -> new Potion(new MobEffectInstance(MobEffects.ANY_OTHER_POTION, 1200, 0)));
    }

    public static PotionBuilder.PotionsPlusPotionGenerationData[] getAllPotionAmpDurMatrices() {
        return ALL_POTION_GENERATION_DATA.toArray(new PotionBuilder.PotionsPlusPotionGenerationData[0]);
    }
}
