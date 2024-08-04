package grill24.potionsplus.core.potion;

import grill24.potionsplus.core.seededrecipe.SeededPotionRecipeGenerator;
import grill24.potionsplus.data.loot.SeededIngredientsLootTables;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.common.Tags;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = ModInfo.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Potions {
    public static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(ForgeRegistries.POTIONS, ModInfo.MOD_ID);
    public static final List<PotionBuilder.PotionsAmpDurMatrix> ALL_POTION_AMPLIFICATION_DURATION_MATRICES = new ArrayList<>();


    public static final SeededPotionRecipeGenerator GEODE_GRACE_RECIPE_GENERATOR = new SeededPotionRecipeGenerator()
            .addItemsInTagsToTierPool(0, SeededIngredientsLootTables.WeightingMode.DISTRIBUTED, 1, Tags.Items.ORES);
    public static final PotionBuilder.PotionsAmpDurMatrix GEODE_GRACE_POTION = new PotionBuilder()
            .name("geode_grace")
            .maxAmp(1)
            .maxDur(2)
            .effects(MobEffects.GEODE_GRACE::get)
            .recipeGenerator(GEODE_GRACE_RECIPE_GENERATOR)
            .build(ALL_POTION_AMPLIFICATION_DURATION_MATRICES::add);

    public static final PotionBuilder.PotionsAmpDurMatrix FALL_OF_THE_VOID_POTIONS = new PotionBuilder()
            .name("fall_of_the_void")
            .maxAmp(1)
            .maxDur(2)
            .effects(MobEffects.FALL_OF_THE_VOID::get)
            .build(ALL_POTION_AMPLIFICATION_DURATION_MATRICES::add);

    public static final PotionBuilder.PotionsAmpDurMatrix HASTE_POTIONS = new PotionBuilder()
            .name("haste")
            .maxAmp(3)
            .maxDur(2)
            .effects(PotionBuilder.DEFAULT_DURATION_FUNCTION, () -> net.minecraft.world.effect.MobEffects.DIG_SPEED)
            .build(ALL_POTION_AMPLIFICATION_DURATION_MATRICES::add);

    public static final PotionBuilder.PotionsAmpDurMatrix LEVITATION_POTIONS = new PotionBuilder()
            .name("levitation")
            .maxAmp(3)
            .maxDur(2)
            .effects(() -> net.minecraft.world.effect.MobEffects.LEVITATION)
            .build(ALL_POTION_AMPLIFICATION_DURATION_MATRICES::add);

    public static final PotionBuilder.PotionsAmpDurMatrix MAGNETIC_POTIONS = new PotionBuilder()
            .name("magnetic")
            .maxAmp(3)
            .maxDur(4)
            .effects(PotionBuilder.LONGER_DURATION_FUNCTION, MobEffects.MAGNETIC::get)
            .build(ALL_POTION_AMPLIFICATION_DURATION_MATRICES::add);

    public static final PotionBuilder.PotionsAmpDurMatrix EXPLODING_POTIONS = new PotionBuilder()
            .name("exploding")
            .maxAmp(3)
            .maxDur(1)
            .effects((int durationLevel) -> 60, MobEffects.EXPLODING::get)
            .build(ALL_POTION_AMPLIFICATION_DURATION_MATRICES::add);

    public static final PotionBuilder.PotionsAmpDurMatrix TELEPORTATION_POTIONS = new PotionBuilder()
            .name("teleportation")
            .maxAmp(4)
            .maxDur(1)
            .effects((int durationLevel) -> 60, MobEffects.TELEPORTATION::get)
            .build(ALL_POTION_AMPLIFICATION_DURATION_MATRICES::add);

    public static final PotionBuilder.PotionsAmpDurMatrix LOOTING_POTIONS = new PotionBuilder()
            .name("looting")
            .maxAmp(4)
            .maxDur(1)
            .effects(MobEffects.LOOTING::get)
            .build(ALL_POTION_AMPLIFICATION_DURATION_MATRICES::add);

    public static final PotionBuilder.PotionsAmpDurMatrix FORTUITOUS_FATE_POTIONS = new PotionBuilder()
            .name("fortuitous_fate")
            .maxAmp(4)
            .maxDur(1)
            .effects(MobEffects.FORTUITOUS_FATE::get)
            .build(ALL_POTION_AMPLIFICATION_DURATION_MATRICES::add);

    public static final PotionBuilder.PotionsAmpDurMatrix METAL_DETECTING_POTIONS = new PotionBuilder()
            .name("metal_detecting")
            .maxAmp(3)
            .maxDur(2)
            .effects(MobEffects.METAL_DETECTING::get)
            .build(ALL_POTION_AMPLIFICATION_DURATION_MATRICES::add);

    public static final PotionBuilder.PotionsAmpDurMatrix GIANT_STEPS_POTIONS = new PotionBuilder()
            .name("giant_steps")
            .maxAmp(3)
            .maxDur(2)
            .effects(MobEffects.GIANT_STEPS::get)
            .build(ALL_POTION_AMPLIFICATION_DURATION_MATRICES::add);

    public static final PotionBuilder.PotionsAmpDurMatrix REACH_FOR_THE_STARS_POTIONS = new PotionBuilder()
            .name("reach_for_the_stars")
            .maxAmp(3)
            .maxDur(2)
            .effects(PotionBuilder.LONGER_DURATION_FUNCTION, MobEffects.REACH_FOR_THE_STARS::get)
            .build(ALL_POTION_AMPLIFICATION_DURATION_MATRICES::add);

    public static final PotionBuilder.PotionsAmpDurMatrix NAUTICAL_NITRO_POTIONS = new PotionBuilder()
            .name("nautical_nitro")
            .maxAmp(3)
            .maxDur(2)
            .effects(PotionBuilder.LONGER_DURATION_FUNCTION, MobEffects.NAUTICAL_NITRO::get)
            .build(ALL_POTION_AMPLIFICATION_DURATION_MATRICES::add);

    public static final PotionBuilder.PotionsAmpDurMatrix CROP_COLLECTOR_POTIONS = new PotionBuilder()
            .name("crop_collector")
            .maxAmp(3)
            .maxDur(2)
            .effects(PotionBuilder.LONGER_DURATION_FUNCTION, MobEffects.CROP_COLLECTOR::get)
            .build(ALL_POTION_AMPLIFICATION_DURATION_MATRICES::add);

    public static final PotionBuilder.PotionsAmpDurMatrix BOTANICAL_BOOST_POTIONS = new PotionBuilder()
            .name("botanical_boost")
            .maxAmp(3)
            .maxDur(2)
            .effects(PotionBuilder.LONGER_DURATION_FUNCTION, MobEffects.BOTANICAL_BOOST::get)
            .build(ALL_POTION_AMPLIFICATION_DURATION_MATRICES::add);

    public static final PotionBuilder.PotionsAmpDurMatrix SATURATION_POTIONS = new PotionBuilder()
            .name("saturation")
            .maxAmp(3)
            .maxDur(2)
            .effects(PotionBuilder.LONGER_DURATION_FUNCTION, () -> net.minecraft.world.effect.MobEffects.SATURATION)
            .clearAllTierPools()
            .addTagToTierPool(0, SeededIngredientsLootTables.WeightingMode.DISTRIBUTED, 1, grill24.potionsplus.core.Tags.Items.FOOD_INGREDIENTS_COMMON)
            .addTagToTierPool(1, SeededIngredientsLootTables.WeightingMode.DISTRIBUTED, 1, grill24.potionsplus.core.Tags.Items.FOOD_INGREDIENTS_UNCOMMON)
            .addTagToTierPool(2, SeededIngredientsLootTables.WeightingMode.DISTRIBUTED, 1, grill24.potionsplus.core.Tags.Items.FOOD_INGREDIENTS_RARE)
            .build(ALL_POTION_AMPLIFICATION_DURATION_MATRICES::add);

    public static final PotionBuilder.PotionsAmpDurMatrix BLINDNESS_POTIONS = new PotionBuilder()
            .name("blindness")
            .maxAmp(1)
            .maxDur(3)
            .effects(PotionBuilder.SHORTER_DURATION_FUNCTION, () -> net.minecraft.world.effect.MobEffects.BLINDNESS)
            .build(ALL_POTION_AMPLIFICATION_DURATION_MATRICES::add);

    public static final PotionBuilder.PotionsAmpDurMatrix CONFUSION_POTIONS = new PotionBuilder()
            .name("confusion")
            .maxAmp(1)
            .maxDur(3)
            .effects(PotionBuilder.SHORTER_DURATION_FUNCTION, () -> net.minecraft.world.effect.MobEffects.CONFUSION)
            .build(ALL_POTION_AMPLIFICATION_DURATION_MATRICES::add);

    public static final PotionBuilder.PotionsAmpDurMatrix HUNGER_POTIONS = new PotionBuilder()
            .name("hunger")
            .maxAmp(2)
            .maxDur(1)
            .effects(PotionBuilder.SHORTER_DURATION_FUNCTION, () -> net.minecraft.world.effect.MobEffects.HUNGER)
            .build(ALL_POTION_AMPLIFICATION_DURATION_MATRICES::add);

    public static final PotionBuilder.PotionsAmpDurMatrix WITHER_POTIONS = new PotionBuilder()
            .name("wither")
            .maxAmp(2)
            .maxDur(1)
            .effects(PotionBuilder.SHORTER_DURATION_FUNCTION, () -> net.minecraft.world.effect.MobEffects.WITHER)
            .build(ALL_POTION_AMPLIFICATION_DURATION_MATRICES::add);

    public static final PotionBuilder.PotionsAmpDurMatrix SLIP_N_SLIDE_POTIONS = new PotionBuilder()
            .name("slip_n_slide")
            .maxAmp(3)
            .maxDur(1)
            .effects(PotionBuilder.MEDIUM_DURATION_FUNCTION, MobEffects.SLIP_N_SLIDE::get)
            .build(ALL_POTION_AMPLIFICATION_DURATION_MATRICES::add);

    public static final PotionBuilder.PotionsAmpDurMatrix HARROWING_HANDS_POTIONS = new PotionBuilder()
            .name("harrowing_hands")
            .maxAmp(1)
            .maxDur(3)
            .effects(PotionBuilder.TEN_MINUTES_DURATION_FUNCTION, MobEffects.HARROWING_HANDS::get)
            .build(ALL_POTION_AMPLIFICATION_DURATION_MATRICES::add);

    public static final PotionBuilder.PotionsAmpDurMatrix SHEPHERDS_SERENADE = new PotionBuilder()
            .name("shepherds_serenade")
            .maxAmp(1)
            .maxDur(2)
            .effects(PotionBuilder.LONGER_DURATION_FUNCTION, MobEffects.SHEPHERDS_SERENADE::get)
            .build(ALL_POTION_AMPLIFICATION_DURATION_MATRICES::add);

    @SubscribeEvent
    public static void onRegisterPotions(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
//            BrewingRecipeRegistry.addRecipe(new BetterBrewingRecipe(net.minecraft.world.item.alchemy.Potions.AWKWARD, Items.AMETHYST_CLUSTER, Potions.GEODE_GRACE.get()));
        });
    }

    public static PotionBuilder.PotionsAmpDurMatrix[] getAllPotionAmpDurMatrices() {
        return ALL_POTION_AMPLIFICATION_DURATION_MATRICES.toArray(new PotionBuilder.PotionsAmpDurMatrix[0]);
    }
}
