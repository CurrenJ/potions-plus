package grill24.potionsplus.core.items;

import grill24.potionsplus.core.Items;
import grill24.potionsplus.core.Translations;
import grill24.potionsplus.item.FishingRodItem;
import grill24.potionsplus.item.PotionsPlusFishItem;
import grill24.potionsplus.loot.IsInBiomeTagCondition;
import grill24.potionsplus.utility.registration.RecipeGeneratorUtility;
import grill24.potionsplus.utility.registration.RegistrationUtility;
import grill24.potionsplus.utility.registration.block.FishingRodItemModelGenerator;
import grill24.potionsplus.utility.registration.item.*;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public class FishItems {
    public static Holder<Item> COPPER_FISHING_ROD, OBSIDIAN_FISHING_ROD;
    public static BaitItemBuilder NO_BAIT, WORMS, GUMMY_WORMS, BLAZED_GRUB;

    public static FishItemBuilder NORTHERN_PIKE, PARROTFISH, RAINFORDIA, GARDEN_EEL, ROYAL_GARDEN_EEL,
            LONGNOSE_GAR, SHRIMP, FRIED_SHRIMP, MOORISH_IDOL, MOLTEN_MOORISH_IDOL, OCEAN_SUNFISH,
            PORTUGUESE_MAN_O_WAR, BLUEGILL, NEON_TETRA, GIANT_MANTA_RAY, FROZEN_GIANT_MANTA_RAY, LIZARDFISH, STARFISH;

    /**
     * Forces the static fields to be initialized.
     */
    public static void init(BiFunction<String, Supplier<Item>, Holder<Item>> register) {
        COPPER_FISHING_ROD = RegistrationUtility.register(register, SimpleItemBuilder.create("copper_fishing_rod")
                .itemFactory(FishingRodItem::new)
                .properties(Items.properties().durability(80))
                .modelGenerator(itemSupplier -> new FishingRodItemModelGenerator(itemSupplier))
                .recipeGenerator(holder -> new RecipeGeneratorUtility.RecipeGenerator<>(holder,
                        (recipeProvider, h) ->
                                recipeProvider.shaped(RecipeCategory.TOOLS, h.value())
                                        .define('C', net.minecraft.world.item.Items.COPPER_INGOT)
                                        .define('S', net.minecraft.world.item.Items.STICK)
                                        .define('F', net.minecraft.world.item.Items.STRING)
                                        .pattern("  C")
                                        .pattern(" CF")
                                        .pattern("S F")
                                        .unlockedBy("has_copper_ingot", recipeProvider.has(net.minecraft.world.item.Items.COPPER_INGOT))))
        ).getHolder();

        OBSIDIAN_FISHING_ROD = RegistrationUtility.register(register, SimpleItemBuilder.create("obsidian_fishing_rod")
                .itemFactory(FishingRodItem::new)
                .properties(Items.properties().durability(160))
                .modelGenerator(itemSupplier -> new FishingRodItemModelGenerator(itemSupplier))
                .recipeGenerator(holder -> new RecipeGeneratorUtility.RecipeGenerator<>(holder,
                        (recipeProvider, h) ->
                                recipeProvider.shaped(RecipeCategory.TOOLS, h.value())
                                        .define('C', COPPER_FISHING_ROD.value())
                                        .define('O', net.minecraft.world.item.Items.OBSIDIAN)
                                        .pattern(" O ")
                                        .pattern("OCO")
                                        .pattern(" O ")
                                        .unlockedBy("has_obsidian", recipeProvider.has(net.minecraft.world.item.Items.OBSIDIAN))))
        ).getHolder();

        // No bait - register using our system but don't actually register a new item with the game.
        // Just used to link the fishing loot table for when the player has no bait.
        NO_BAIT = RegistrationUtility.register(register, BaitItemBuilder.create("no_bait")
                .itemFactory(null)
                .modelGenerator(null)
                .fishingLoot(builder -> builder
                        .add(BaitItemBuilder.whenBaitConditionMet(NO_BAIT, LootItem.lootTableItem(WORMS.getValue()).setWeight(95).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3)))))// 95% worms
                        .add(LootItem.lootTableItem(GUMMY_WORMS.getValue()).setWeight(5)) // 5% gummy worms = 1 in 20
                        .add(LootItem.lootTableItem(BLAZED_GRUB.getValue()).when(IsInBiomeTagCondition.isInBiomeTag(BiomeTags.IS_NETHER)).setWeight(5))
                )
                .sizeProvider(new GaussianSizeBand(0, 0)));
        WORMS = RegistrationUtility.register(register, DefaultBaitItemBuilder.create("worms", 30)
                .sizeProvider(new GaussianSizeBand(0, 0))
                .descriptionKey(Translations.BAIT_POTIONSPLUS_WORMS_DESCRIPTION));
        GUMMY_WORMS = RegistrationUtility.register(register, DefaultBaitItemBuilder.create("gummy_worms", 10)
                .properties(Items.properties().food(Foods.GOLDEN_CARROT).component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true))
                .sizeProvider(new GaussianSizeBand(10, 5))
                .descriptionKey(Translations.BAIT_POTIONSPLUS_GUMMY_WORMS_DESCRIPTION));
        BLAZED_GRUB = RegistrationUtility.register(register, DefaultBaitItemBuilder.create("blazed_grub", 25)
                .properties(Items.properties().food(Foods.COOKED_BEEF))
                .sizeProvider(new GaussianSizeBand(5, -5))
                .descriptionKey(Translations.BAIT_POTIONSPLUS_BLAZED_GRUB_DESCRIPTION));

        NORTHERN_PIKE = RegistrationUtility.register(register, FishItemBuilder.create("northern_pike")
                .sizeProvider(GaussianSizeBand.LARGE)
                .biomes(Biomes.BEACH, Biomes.FOREST, Biomes.PLAINS, Biomes.SNOWY_PLAINS));
        PARROTFISH = RegistrationUtility.register(register, FishItemBuilder.create("parrotfish")
                .sizeProvider(GaussianSizeBand.SMALL)
                .biomes(Biomes.DESERT, Biomes.WINDSWEPT_SAVANNA, Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU, Biomes.LUSH_CAVES));
        RAINFORDIA = RegistrationUtility.register(register, FishItemBuilder.create("rainfordia")
                .sizeProvider(GaussianSizeBand.SMALL)
                .biomes(Biomes.SAVANNA, Biomes.BAMBOO_JUNGLE, Biomes.PLAINS));
        GARDEN_EEL = RegistrationUtility.register(register, FishItemBuilder.create("garden_eel")
                .itemFactory(prop -> new PotionsPlusFishItem(prop, PotionsPlusFishItem.FishTankRenderType.GROUND_ANCHORED_NO_ROTATION))
                .sizeProvider(GaussianSizeBand.SMALL)
                .biomes(Biomes.FLOWER_FOREST, Biomes.SUNFLOWER_PLAINS, Biomes.CHERRY_GROVE, Biomes.LUSH_CAVES));
        ROYAL_GARDEN_EEL = RegistrationUtility.register(register, FishItemBuilder.create("royal_garden_eel", true)
                .itemFactory(prop -> new PotionsPlusFishItem(prop, PotionsPlusFishItem.FishTankRenderType.GROUND_ANCHORED_NO_ROTATION))
                .sizeProvider(GaussianSizeBand.SMALL)
                .biomes(Biomes.FLOWER_FOREST, Biomes.SUNFLOWER_PLAINS, Biomes.CHERRY_GROVE, Biomes.LUSH_CAVES)
                .canBeCaughtOutsideBiome(false)
                .baseFishWeight(5)
                .biomeBonusWeight(0)
                .quality(2));
        LONGNOSE_GAR = RegistrationUtility.register(register, FishItemBuilder.create("longnose_gar")
                .sizeProvider(GaussianSizeBand.LARGE)
                .biomes(Biomes.TAIGA, Biomes.ICE_SPIKES, Biomes.SNOWY_TAIGA, Biomes.FROZEN_RIVER));
        SHRIMP = RegistrationUtility.register(register, FishItemBuilder.create("shrimp")
                .itemFactory(prop -> new PotionsPlusFishItem(prop, PotionsPlusFishItem.FishTankRenderType.NO_ROTATION))
                .sizeProvider(GaussianSizeBand.SMALL)
                .biomes(Biomes.MANGROVE_SWAMP, Biomes.SWAMP, Biomes.GROVE));
        FRIED_SHRIMP = RegistrationUtility.register(register, FishItemBuilder.create("fried_shrimp", true)
                .itemFactory(prop -> new PotionsPlusFishItem(prop, PotionsPlusFishItem.FishTankRenderType.NO_ROTATION))
                .sizeProvider(GaussianSizeBand.SMALL)
                .biomeTag(BiomeTags.IS_NETHER)
                .requiredBait(BLAZED_GRUB.getHolder())
                .canBeCaughtOutsideBiome(false)
                .baseFishWeight(5)
                .biomeBonusWeight(0)
                .quality(2));
        MOORISH_IDOL = RegistrationUtility.register(register, FishItemBuilder.create("moorish_idol")
                .sizeProvider(GaussianSizeBand.MEDIUM)
                .biomes(Biomes.JUNGLE, Biomes.BAMBOO_JUNGLE, Biomes.SPARSE_JUNGLE, Biomes.BIRCH_FOREST, Biomes.PLAINS));
        MOLTEN_MOORISH_IDOL = RegistrationUtility.register(register, FishItemBuilder.create("molten_moorish_idol", true)
                .sizeProvider(GaussianSizeBand.MEDIUM)
                .biomes(grill24.potionsplus.core.Biomes.VOLCANIC_CAVE_KEY, Biomes.NETHER_WASTES, Biomes.CRIMSON_FOREST)
                .canBeCaughtOutsideBiome(false)
                .baseFishWeight(5)
                .biomeBonusWeight(0)
                .quality(2));
        OCEAN_SUNFISH = RegistrationUtility.register(register, FishItemBuilder.create("ocean_sunfish")
                .sizeProvider(GaussianSizeBand.LARGE)
                .biomes(Biomes.OCEAN, Biomes.DEEP_OCEAN, Biomes.WARM_OCEAN, Biomes.COLD_OCEAN, Biomes.FROZEN_OCEAN, Biomes.JAGGED_PEAKS, Biomes.STONY_PEAKS));
        PORTUGUESE_MAN_O_WAR = RegistrationUtility.register(register, FishItemBuilder.create("portuguese_man_o_war")
                .itemFactory(prop -> new PotionsPlusFishItem(prop, PotionsPlusFishItem.FishTankRenderType.UPRIGHT_FISH))
                .sizeProvider(GaussianSizeBand.LARGE)
                .biomes(Biomes.WARM_OCEAN, Biomes.JAGGED_PEAKS, Biomes.STONY_PEAKS));
        BLUEGILL = RegistrationUtility.register(register, FishItemBuilder.create("bluegill")
                .sizeProvider(GaussianSizeBand.MEDIUM)
                .biomes(Biomes.FLOWER_FOREST, Biomes.LUSH_CAVES, Biomes.SNOWY_PLAINS, Biomes.SNOWY_BEACH));
        NEON_TETRA = RegistrationUtility.register(register, FishItemBuilder.create("neon_tetra")
                .sizeProvider(GaussianSizeBand.SMALL)
                .biomes(Biomes.FLOWER_FOREST, Biomes.SNOWY_SLOPES, Biomes.BADLANDS));
        GIANT_MANTA_RAY = RegistrationUtility.register(register, FishItemBuilder.create("giant_manta_ray")
                .sizeProvider(GaussianSizeBand.LARGE)
                .biomes(Biomes.ERODED_BADLANDS, Biomes.WOODED_BADLANDS, Biomes.BIRCH_FOREST));
        FROZEN_GIANT_MANTA_RAY = RegistrationUtility.register(register, FishItemBuilder.create("frozen_giant_manta_ray", true)
                .sizeProvider(GaussianSizeBand.LARGE)
                .biomes(grill24.potionsplus.core.Biomes.ICE_CAVE_KEY, Biomes.FROZEN_PEAKS, Biomes.FROZEN_RIVER)
                .canBeCaughtOutsideBiome(false)
                .baseFishWeight(5)
                .biomeBonusWeight(0)
                .quality(2));
        LIZARDFISH = RegistrationUtility.register(register, FishItemBuilder.create("lizardfish")
                .sizeProvider(GaussianSizeBand.MEDIUM)
                .biomes(Biomes.FOREST, Biomes.DARK_FOREST, Biomes.MUSHROOM_FIELDS, Biomes.LUSH_CAVES, Biomes.SUNFLOWER_PLAINS));
        STARFISH = RegistrationUtility.register(register, FishItemBuilder.create("starfish")
                .sizeProvider(GaussianSizeBand.MEDIUM)
                .biomes(Biomes.PALE_GARDEN, Biomes.SUNFLOWER_PLAINS, Biomes.BEACH, Biomes.SNOWY_BEACH));
    }
}
