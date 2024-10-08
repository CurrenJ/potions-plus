package grill24.potionsplus.core.potion;

import grill24.potionsplus.core.Tags;
import grill24.potionsplus.core.seededrecipe.PotionUpgradeIngredients;
import grill24.potionsplus.core.seededrecipe.SeededPotionRecipeBuilder;
import grill24.potionsplus.data.loot.SeededIngredientsLootTables;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.alchemy.Potion;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(modid = ModInfo.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class Potions {
    public static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(Registries.POTION, ModInfo.MOD_ID);
    public static final List<PotionBuilder.PotionsPlusPotionGenerationData> ALL_POTION_GENERATION_DATA = new ArrayList<>();

    public static final Holder<Potion> ANY_POTION = POTIONS.register("any_potion", () -> new Potion(new net.minecraft.world.effect.MobEffectInstance(MobEffects.ANY_POTION, 1200, 0)));

    public static final SeededPotionRecipeBuilder GEODE_GRACE_RECIPE_GENERATOR = SeededPotionRecipeBuilder.defaultPools()
            .addItemsInTagsToRaritySamplingPool(PotionUpgradeIngredients.Rarity.COMMON, SeededIngredientsLootTables.WeightingMode.DISTRIBUTED, 1, Tags.Items.ORE_FLOWERS_COMMON)
            .addItemsInTagsToRaritySamplingPool(PotionUpgradeIngredients.Rarity.RARE, SeededIngredientsLootTables.WeightingMode.DISTRIBUTED, 1, Tags.Items.ORE_FLOWERS_RARE);
    public static final PotionBuilder.PotionsPlusPotionGenerationData GEODE_GRACE_POTIONS = new PotionBuilder()
            .name("geode_grace")
            .effect(MobEffects.GEODE_GRACE)
            .recipeGenerator(GEODE_GRACE_RECIPE_GENERATOR)
            .build(ALL_POTION_GENERATION_DATA::add);

    public static final PotionBuilder.PotionsPlusPotionGenerationData FALL_OF_THE_VOID_POTIONS = new PotionBuilder()
            .name("fall_of_the_void")
            .effect(MobEffects.FALL_OF_THE_VOID)
            .withRarityCount(PotionUpgradeIngredients.Rarity.COMMON, 2)
            .build(ALL_POTION_GENERATION_DATA::add);

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

    public static final PotionBuilder.PotionsPlusPotionGenerationData EXPLODING_POTIONS = new PotionBuilder()
            .name("exploding")
            .effect(MobEffects.EXPLODING, 60)
            .withRarityCount(PotionUpgradeIngredients.Rarity.COMMON, 2)
            .build(ALL_POTION_GENERATION_DATA::add);

    public static final PotionBuilder.PotionsPlusPotionGenerationData TELEPORTATION_POTIONS = new PotionBuilder()
            .name("teleportation")
            .effect(MobEffects.TELEPORTATION, 60)
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

    public static final PotionBuilder.PotionsPlusPotionGenerationData METAL_DETECTING_POTIONS = new PotionBuilder()
            .name("metal_detecting")
            .effect(MobEffects.METAL_DETECTING)
            .withRarityCount(PotionUpgradeIngredients.Rarity.RARE, 1)
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
            .clearAllRaritySamplingConfigs()
            .addItemsInTagsToRarityPool(PotionUpgradeIngredients.Rarity.RARE, SeededIngredientsLootTables.WeightingMode.DISTRIBUTED, 1, grill24.potionsplus.core.Tags.Items.FOOD_INGREDIENTS_UNCOMMON)
            .addItemsInTagsToRarityPool(PotionUpgradeIngredients.Rarity.RARE, SeededIngredientsLootTables.WeightingMode.DISTRIBUTED, 1, grill24.potionsplus.core.Tags.Items.FOOD_INGREDIENTS_RARE)
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

    public static final PotionBuilder.PotionsPlusPotionGenerationData SOUL_MATE_POTIONS = new PotionBuilder()
            .name("soul_mate")
            .effect(MobEffects.SOUL_MATE)
            .withRarityCount(PotionUpgradeIngredients.Rarity.RARE, 1)
            .build(ALL_POTION_GENERATION_DATA::add);

    @SubscribeEvent
    public static void onRegisterPotions(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
//            BrewingRecipeRegistry.addRecipe(new BetterBrewingRecipe(net.minecraft.world.item.alchemy.Potions.AWKWARD, Items.AMETHYST_CLUSTER, Potions.GEODE_GRACE.get()));
        });
    }

    public static PotionBuilder.PotionsPlusPotionGenerationData[] getAllPotionAmpDurMatrices() {
        return ALL_POTION_GENERATION_DATA.toArray(new PotionBuilder.PotionsPlusPotionGenerationData[0]);
    }
}
