package grill24.potionsplus.core.items;

import grill24.potionsplus.core.Items;
import grill24.potionsplus.item.FishingRodItem;
import grill24.potionsplus.utility.registration.RecipeGeneratorUtility;
import grill24.potionsplus.utility.registration.item.FishItemBuilder;
import grill24.potionsplus.utility.registration.item.SimpleItemBuilder;
import grill24.potionsplus.utility.registration.RegistrationUtility;
import net.minecraft.core.Holder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biomes;

import java.util.function.BiFunction;
import java.util.function.Supplier;

import static grill24.potionsplus.data.RecipeProvider.has;

public class FishItems {
    public static Holder<Item> COPPER_FISHING_ROD;
    public static Holder<Item> WORMS;

    public static FishItemBuilder NORTHERN_PIKE, PARROTFISH, RAINFORDIA, GARDEN_EEL, ROYAL_GARDEN_EEL,
            LONGNOSE_GAR, SHRIMP, FRIED_SHRIMP, MOORISH_IDOL, MOLTEN_MOORISH_IDOL, OCEAN_SUNFISH,
            PORTUGUESE_MAN_O_WAR, BLUEGILL, NEON_TETRA, GIANT_MANTA_RAY, FROZEN_GIANT_MANTA_RAY, LIZARDFISH;

    /**
     * Forces the static fields to be initialized.
     */
    public static void init(BiFunction<String, Supplier<Item>, Holder<Item>> register) {
        COPPER_FISHING_ROD = RegistrationUtility.register(register, SimpleItemBuilder.create("copper_fishing_rod")
                .itemFactory(FishingRodItem::new)
                .properties(Items.properties().durability(80))
                .modelGenerator(null)
                .recipeGenerator(holder -> new RecipeGeneratorUtility.RecipeGenerator<>(holder, h ->
                        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, h.value())
                                .define('C', net.minecraft.world.item.Items.COPPER_INGOT)
                                .define('S', net.minecraft.world.item.Items.STICK)
                                .define('F', net.minecraft.world.item.Items.STRING)
                                .pattern("  C")
                                .pattern(" CF")
                                .pattern("S F")
                                .unlockedBy("has_copper_ingot", has(net.minecraft.world.item.Items.COPPER_INGOT))))
                ).getHolder();

        WORMS = RegistrationUtility.register(register, SimpleItemBuilder.createSimple("worms")).getHolder();

        NORTHERN_PIKE = RegistrationUtility.register(register, FishItemBuilder.create("northern_pike")
                .sizeProvider(FishItemBuilder.LARGE_SIZE)
                .biomes(Biomes.BEACH, Biomes.FOREST, Biomes.PLAINS, Biomes.SNOWY_PLAINS));
        PARROTFISH = RegistrationUtility.register(register, FishItemBuilder.create("parrotfish")
                .sizeProvider(FishItemBuilder.SMALL_SIZE)
                .biomes(Biomes.DESERT, Biomes.WINDSWEPT_SAVANNA, Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU, Biomes.LUSH_CAVES));
        RAINFORDIA = RegistrationUtility.register(register, FishItemBuilder.create("rainfordia")
                .sizeProvider(FishItemBuilder.SMALL_SIZE)
                .biomes(Biomes.SAVANNA, Biomes.BAMBOO_JUNGLE, Biomes.PLAINS));
        GARDEN_EEL = RegistrationUtility.register(register, FishItemBuilder.create("garden_eel")
                .sizeProvider(FishItemBuilder.SMALL_SIZE)
                .biomes(Biomes.FLOWER_FOREST, Biomes.SUNFLOWER_PLAINS, Biomes.CHERRY_GROVE, Biomes.LUSH_CAVES));
        ROYAL_GARDEN_EEL = RegistrationUtility.register(register, FishItemBuilder.create("royal_garden_eel", true)
                .sizeProvider(FishItemBuilder.SMALL_SIZE)
                .biomes(Biomes.FLOWER_FOREST, Biomes.SUNFLOWER_PLAINS, Biomes.CHERRY_GROVE, Biomes.LUSH_CAVES)
                .canBeCaughtOutsideBiome(false)
                .baseFishWeight(5)
                .biomeBonusWeight(0)
                .quality(2));
        LONGNOSE_GAR = RegistrationUtility.register(register, FishItemBuilder.create("longnose_gar")
                .sizeProvider(FishItemBuilder.LARGE_SIZE)
                .biomes(Biomes.TAIGA, Biomes.ICE_SPIKES, Biomes.SNOWY_TAIGA, Biomes.FROZEN_RIVER));
        SHRIMP = RegistrationUtility.register(register, FishItemBuilder.create("shrimp")
                .sizeProvider(FishItemBuilder.SMALL_SIZE)
                .biomes(Biomes.MANGROVE_SWAMP, Biomes.SWAMP, Biomes.GROVE));
        FRIED_SHRIMP = RegistrationUtility.register(register, FishItemBuilder.create("fried_shrimp", true)
                .sizeProvider(FishItemBuilder.SMALL_SIZE)
                .biomes(Biomes.MUSHROOM_FIELDS, grill24.potionsplus.core.Biomes.ARID_CAVE_KEY, Biomes.MANGROVE_SWAMP)
                .canBeCaughtOutsideBiome(false)
                .baseFishWeight(5)
                .biomeBonusWeight(0)
                .quality(2));
        MOORISH_IDOL = RegistrationUtility.register(register, FishItemBuilder.create("moorish_idol")
                .sizeProvider(FishItemBuilder.MEDIUM_SIZE)
                .biomes(Biomes.JUNGLE, Biomes.BAMBOO_JUNGLE, Biomes.SPARSE_JUNGLE, Biomes.BIRCH_FOREST, Biomes.PLAINS));
        MOLTEN_MOORISH_IDOL = RegistrationUtility.register(register, FishItemBuilder.create("molten_moorish_idol", true)
                .sizeProvider(FishItemBuilder.MEDIUM_SIZE)
                .biomes(grill24.potionsplus.core.Biomes.VOLCANIC_CAVE_KEY)
                .canBeCaughtOutsideBiome(false)
                .baseFishWeight(5)
                .biomeBonusWeight(0)
                .quality(2));
        OCEAN_SUNFISH = RegistrationUtility.register(register, FishItemBuilder.create("ocean_sunfish")
                .sizeProvider(FishItemBuilder.LARGE_SIZE)
                .biomes(Biomes.OCEAN, Biomes.DEEP_OCEAN, Biomes.WARM_OCEAN, Biomes.COLD_OCEAN, Biomes.FROZEN_OCEAN, Biomes.JAGGED_PEAKS, Biomes.STONY_PEAKS));
        PORTUGUESE_MAN_O_WAR = RegistrationUtility.register(register, FishItemBuilder.create("portuguese_man_o_war")
                .sizeProvider(FishItemBuilder.LARGE_SIZE)
                .biomes(Biomes.WARM_OCEAN, Biomes.JAGGED_PEAKS, Biomes.STONY_PEAKS));
        BLUEGILL = RegistrationUtility.register(register, FishItemBuilder.create("bluegill")
                .sizeProvider(FishItemBuilder.MEDIUM_SIZE)
                .biomes(Biomes.FLOWER_FOREST, Biomes.LUSH_CAVES, Biomes.SNOWY_PLAINS, Biomes.SNOWY_BEACH));
        NEON_TETRA = RegistrationUtility.register(register, FishItemBuilder.create("neon_tetra")
                .sizeProvider(FishItemBuilder.SMALL_SIZE)
                .biomes(Biomes.FLOWER_FOREST, Biomes.SNOWY_SLOPES, Biomes.BADLANDS));
        GIANT_MANTA_RAY = RegistrationUtility.register(register, FishItemBuilder.create("giant_manta_ray")
                .sizeProvider(FishItemBuilder.LARGE_SIZE)
                .biomes(Biomes.ERODED_BADLANDS, Biomes.WOODED_BADLANDS, Biomes.BIRCH_FOREST));
        FROZEN_GIANT_MANTA_RAY = RegistrationUtility.register(register, FishItemBuilder.create("frozen_giant_manta_ray", true)
                .sizeProvider(FishItemBuilder.LARGE_SIZE)
                .biomes(grill24.potionsplus.core.Biomes.ICE_CAVE_KEY, Biomes.FROZEN_PEAKS, Biomes.FROZEN_RIVER)
                .canBeCaughtOutsideBiome(false)
                .baseFishWeight(5)
                .biomeBonusWeight(0)
                .quality(2));
        LIZARDFISH = RegistrationUtility.register(register, FishItemBuilder.create("lizardfish")
                .sizeProvider(FishItemBuilder.MEDIUM_SIZE)
                .biomes(Biomes.FOREST, Biomes.DARK_FOREST, Biomes.MUSHROOM_FIELDS, Biomes.LUSH_CAVES, Biomes.SUNFLOWER_PLAINS));
    }
}
