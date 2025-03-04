package grill24.potionsplus.core;

import grill24.potionsplus.item.*;
import grill24.potionsplus.item.FishingRodItem;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.*;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static grill24.potionsplus.utility.Utility.mc;
import static grill24.potionsplus.utility.Utility.ppId;

public class Items {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, ModInfo.MOD_ID);

    // ----- Items -----

    public static final Holder<Item> WREATH = ITEMS.register("wreath", () ->  new ArmorItem(ArmorMaterials.WREATH, ArmorItem.Type.HELMET, properties()));

    public static final ResourceLocation[] BLOCK_HAT_MODELS = new ResourceLocation[] { ppId("item/block_hat_1"), ppId("item/block_hat_2"), ppId("item/block_hat_3"), ppId("item/block_hat_4") };
    public static final Holder<Item>[] COAL_ORE_HATS = Items.generateHats("coal_ore_hat", BLOCK_HAT_MODELS.length, () -> new EquipableHatItem(properties()));
    public static final Holder<Item>[] COPPER_ORE_HATS = Items.generateHats("copper_ore_hat", BLOCK_HAT_MODELS.length, () -> new EquipableHatItem(properties()));
    public static final Holder<Item>[] IRON_ORE_HATS = Items.generateHats("iron_ore_hat", BLOCK_HAT_MODELS.length, () -> new EquipableHatItem(properties()));
    public static final Holder<Item>[] GOLD_ORE_HATS = Items.generateHats("gold_ore_hat", BLOCK_HAT_MODELS.length, () -> new EquipableHatItem(properties()));
    public static final Holder<Item>[] DIAMOND_ORE_HATS = Items.generateHats("diamond_ore_hat", BLOCK_HAT_MODELS.length, () -> new EquipableHatItem(properties()));
    public static final Holder<Item>[] EMERALD_ORE_HATS = Items.generateHats("emerald_ore_hat", BLOCK_HAT_MODELS.length, () -> new EquipableHatItem(properties()));

    public static final Holder<Item> FROGGY_HAT = ITEMS.register("froggy_hat", () -> new EquipableHatItem(properties()));
    public static final Holder<Item> HOOK_HAT = ITEMS.register("hook_hat", () -> new EquipableHatItem(properties()));
    public static final Holder<Item> APPLE_HAT = ITEMS.register("apple_hat", () -> new EquipableHatItem(properties()));

    public static final Holder<Item> LUNAR_BERRIES = ITEMS.register("lunar_berries", () -> new ItemNameBlockItem(Blocks.LUNAR_BERRY_BUSH.value(), properties().food(Foods.SWEET_BERRIES)));
    public static final Holder<Item> MOSS = ITEMS.register("moss", () -> new Item(properties()));
    public static final Holder<Item> SALT = ITEMS.register("salt", () -> new Item(properties()));
    public static final Holder<Item> WORMROOT = ITEMS.register("wormroot", () -> new WormrootItem(properties()));
    public static final Holder<Item> ROTTEN_WORMROOT = ITEMS.register("rotten_wormroot", () -> new Item(properties()));

    public static final Holder<Item> REMNANT_DEBRIS = ITEMS.register("remnant_debris", () -> new BlockItem(Blocks.REMNANT_DEBRIS.value(), properties().fireResistant().rarity(Rarity.UNCOMMON)));
    public static final Holder<Item> DEEPSLATE_REMNANT_DEBRIS = ITEMS.register("deepslate_remnant_debris", () -> new BlockItem(Blocks.DEEPSLATE_REMNANT_DEBRIS.value(), properties().fireResistant().rarity(Rarity.UNCOMMON)));
    public static final Holder<Item> NETHERITE_REMNANT = ITEMS.register("netherite_remnant", () -> new Item(properties().rarity(Rarity.UNCOMMON)));

    public static final Holder<Item> RAW_URANIUM = ITEMS.register("raw_uranium", () -> new Item(properties().rarity(Rarity.UNCOMMON)));
    public static final Holder<Item> URANIUM_INGOT = ITEMS.register("uranium_ingot", () -> new Item(properties().rarity(Rarity.UNCOMMON)));
    public static final Holder<Item> SULFUR_SHARD = ITEMS.register("sulfur_shard", () -> new Item(properties()));
    public static final Holder<Item> SULFURIC_ACID = ITEMS.register("sulfuric_acid", () -> new Item(properties()));

    public static final ResourceLocation EDIBLE_CHOICE_ITEM_FLAG_PROPERTY_NAME = ppId("edible_choice_flag");
    public static final DeferredHolder<Item, EdibleChoiceItem> SPARKLING_SQUASH = ITEMS.register("sparkling_squash", () -> new EdibleChoiceItem(properties().food(Foods.GOLDEN_CARROT)));
    public static final ItemOverrideUtility.EdibleChoiceItemOverrideModelData SPARKLING_SQUASH_MODEL = registerEdibleChoiceItemModel(SPARKLING_SQUASH, ppId("item/sparkling_squash"));
    public static final Holder<Item> BLUEB_BERRIES = ITEMS.register("blueb_berries", () -> new EdibleChoiceItem(properties().food(Foods.GOLDEN_CARROT)));
    public static final ItemOverrideUtility.EdibleChoiceItemOverrideModelData BLUEB_BERRIES_MODEL = registerEdibleChoiceItemModel(BLUEB_BERRIES, ppId("item/blueb_berries"));
    public static final Holder<Item> FORTIFYING_FUDGE = ITEMS.register("fortifying_fudge", () -> new EdibleChoiceItem(properties().food(Foods.GOLDEN_CARROT)));
    public static final ItemOverrideUtility.EdibleChoiceItemOverrideModelData FORTIFYING_FUDGE_MODEL = registerEdibleChoiceItemModel(FORTIFYING_FUDGE, ppId("item/fortifying_fudge"));
    public static final Holder<Item> GRASS_CLIPPINGS = ITEMS.register("grass_clippings", () -> new EdibleChoiceItem(properties().food(Foods.GOLDEN_CARROT)));
    public static final ItemOverrideUtility.EdibleChoiceItemOverrideModelData GRASS_CLIPPINGS_MODEL = registerEdibleChoiceItemModel(GRASS_CLIPPINGS, ppId("item/grass_clippings"));
    public static final Holder<Item> STONE_FRUIT = ITEMS.register("stone_fruit", () -> new EdibleChoiceItem(properties().food(Foods.GOLDEN_CARROT)));
    public static final ItemOverrideUtility.EdibleChoiceItemOverrideModelData STONE_FRUIT_MODEL = registerEdibleChoiceItemModel(STONE_FRUIT, ppId("item/stone_fruit"));
    public static final Holder<Item> CHOCOLATE_BOOK = ITEMS.register("chocolate_book", () -> new EdibleChoiceItem(properties().food(Foods.GOLDEN_CARROT)));
    public static final ItemOverrideUtility.EdibleChoiceItemOverrideModelData CHOCOLATE_BOOK_MODEL = registerEdibleChoiceItemModel(CHOCOLATE_BOOK, ppId("item/chocolate_book"));
    public static final Holder<Item> ROASTED_BAMBOO = ITEMS.register("roasted_bamboo", () -> new EdibleChoiceItem(properties().food(Foods.GOLDEN_CARROT)));
    public static final ItemOverrideUtility.EdibleChoiceItemOverrideModelData ROASTED_BAMBOO_MODEL = registerEdibleChoiceItemModel(ROASTED_BAMBOO, ppId("item/roasted_bamboo"));
    public static final Holder<Item> MOSSASHIMI = ITEMS.register("mossashimi", () -> new EdibleChoiceItem(properties().food(Foods.GOLDEN_CARROT)));
    public static final ItemOverrideUtility.EdibleChoiceItemOverrideModelData MOSSASHIMI_MODEL = registerEdibleChoiceItemModel(MOSSASHIMI, ppId("item/mossashimi"));
    public static final Holder<Item> PYRAMIDS_OF_SALT = ITEMS.register("pyramids_of_salt", () -> new EdibleChoiceItem(properties().food(Foods.GOLDEN_CARROT)));
    public static final ItemOverrideUtility.EdibleChoiceItemOverrideModelData PYRAMIDS_OF_SALT_MODEL = registerEdibleChoiceItemModel(PYRAMIDS_OF_SALT, ppId("item/pyramids_of_salt"));

    public static final Holder<Item> BASIC_LOOT = ITEMS.register("basic_loot", () -> new EdibleChoiceItem(properties().food(Foods.GOLDEN_CARROT)));
    public static final ItemOverrideUtility.EdibleChoiceItemOverrideModelData BASIC_LOOT_MODEL = registerEdibleChoiceItemModel(BASIC_LOOT, ppId("item/basic"));
    public static final Holder<Item> INTERMEDIATE_LOOT = ITEMS.register("intermediate_loot", () -> new EdibleChoiceItem(properties().food(Foods.GOLDEN_CARROT)));
    public static final ItemOverrideUtility.EdibleChoiceItemOverrideModelData INTERMEDIATE_LOOT_MODEL = registerEdibleChoiceItemModel(INTERMEDIATE_LOOT, ppId("item/intermediate"));
    public static final Holder<Item> ADVANCED_LOOT = ITEMS.register("advanced_loot", () -> new EdibleChoiceItem(properties().food(Foods.GOLDEN_CARROT)));
    public static final ItemOverrideUtility.EdibleChoiceItemOverrideModelData ADVANCED_LOOT_MODEL = registerEdibleChoiceItemModel(ADVANCED_LOOT, ppId("item/advanced"));
    public static final Holder<Item> MASTER_LOOT = ITEMS.register("master_loot", () -> new EdibleChoiceItem(properties().food(Foods.GOLDEN_CARROT)));
    public static final ItemOverrideUtility.EdibleChoiceItemOverrideModelData MASTER_LOOT_MODEL = registerEdibleChoiceItemModel(MASTER_LOOT, ppId("item/master"));

    public static final Holder<Item> WHEEL = ITEMS.register("wheel", () -> new EdibleChoiceItem(properties().food(Foods.GOLDEN_CARROT)));
    public static final ItemOverrideUtility.EdibleChoiceItemOverrideModelData WHEEL_MODEL = registerEdibleChoiceItemModel(WHEEL, ppId("item/wheel_0"));

    public static final Holder<Item> COPPER_FISHING_ROD = ITEMS.register("copper_fishing_rod", () -> new FishingRodItem(properties()));

    public static final Holder<Item> NORTHERN_PIKE = register("northern_pike", () -> new Item(properties().food(Foods.COD)));
    public static final Holder<Item> PARROTFISH = register("parrotfish", () -> new Item(properties().food(Foods.COD)));
    public static final Holder<Item> RAINFORDIA = register("rainfordia", () -> new Item(properties().food(Foods.COD)));
    public static final Holder<Item> GARDEN_EEL = register("garden_eel", () -> new Item(properties().food(Foods.COD)));
    public static final Holder<Item> LONGNOSE_GAR = register("longnose_gar", () -> new Item(properties().food(Foods.COD)));
    public static final Holder<Item> SHRIMP = register("shrimp", () -> new Item(properties().food(Foods.COD)));
    public static final Holder<Item> MOORISH_IDOL = register("moorish_idol", () -> new Item(properties().food(Foods.COD)));
    public static final Holder<Item> OCEAN_SUNFISH = register("ocean_sunfish", () -> new Item(properties().food(Foods.COD)));
    public static final Holder<Item> PORTUGUESE_MAN_O_WAR = register("portuguese_man_o_war", () -> new Item(properties().food(Foods.COD)));
    public static final Holder<Item> BLUEGILL = register("bluegill", () -> new Item(properties().food(Foods.COD)));
    public static final Holder<Item> NEON_TETRA = register("neon_tetra", () -> new Item(properties().food(Foods.COD)));

    // ----- Dynamically Rendered Display Items -----
    public static final ResourceLocation DYNAMIC_ICON_INDEX_PROPERTY_NAME = ppId("dynamic_icon_index");

    public static final Holder<Item> POTION_EFFECT_ICON = ITEMS.register("potion_effect_icon", () -> new Item(properties()));
    public static final ItemOverrideUtility.PotionEffectIconOverrideModelData POTION_EFFECT_ICON_RESOURCE_LOCATIONS = register(new ItemOverrideUtility.PotionEffectIconOverrideModelData(
        DYNAMIC_ICON_INDEX_PROPERTY_NAME));

    public static final ResourceLocation AMP_TEX_LOC = ppId("item/amplifier_upgrade");
    public static final ResourceLocation DUR_TEX_LOC = ppId("item/duration_upgrade");
    public static final ResourceLocation I_TEX_LOC = ppId("item/i");
    public static final ResourceLocation II_TEX_LOC = ppId("item/ii");
    public static final ResourceLocation III_TEX_LOC = ppId("item/iii");
    public static final ResourceLocation IV_TEX_LOC = ppId("item/iv");
    public static final ResourceLocation V_TEX_LOC = ppId("item/v");
    public static final ResourceLocation VI_TEX_LOC = ppId("item/vi");
    public static final ResourceLocation VII_TEX_LOC = ppId("item/vii");
    public static final ResourceLocation VIII_TEX_LOC = ppId("item/viii");
    public static final ResourceLocation ARROW_TEX_LOC = ppId("item/arrow");
    public static final ResourceLocation UNKNOWN_TEX_LOC = ppId("item/unknown");
    public static final ResourceLocation SGA_A_TEX_LOC = mc("particle/sga_a");
    public static final ResourceLocation SGA_B_TEX_LOC = mc("particle/sga_b");
    public static final ResourceLocation SGA_C_TEX_LOC = mc("particle/sga_c");
    public static final ResourceLocation SGA_D_TEX_LOC = mc("particle/sga_d");
    public static final ResourceLocation COMMON_TEX_LOC = ppId("item/common");
    public static final ResourceLocation RARE_TEX_LOC = ppId("item/rare");
    public static final ResourceLocation NO_EXP_TEX_LOC = ppId("item/no_experience");
    public static final ResourceLocation NO_HEAT_TEX_LOC = ppId("item/no_heat");
    public static final ResourceLocation FISHING_BAR_TEX_LOC = ppId("item/fishing_bar");
    public static final ResourceLocation FISHING_BOBBER_TEX_LOC = ppId("item/fishing_bobber");
    public static final ResourceLocation COPPER_FISHING_FRAME_TEX_LOC = ppId("item/copper_fishing_frame");
    public static final ResourceLocation GOLD_FISHING_FRAME_TEX_LOC = ppId("item/gold_fishing_frame");
    public static final ResourceLocation DIAMOND_FISHING_FRAME_TEX_LOC = ppId("item/diamond_fishing_frame");
    public static final ResourceLocation PURPLE_FISHING_FRAME_TEX_LOC = ppId("item/purple_fishing_frame");
    public static final Holder<Item> GENERIC_ICON = ITEMS.register("generic_icon", () -> new Item(properties()));
    public static final ItemOverrideUtility.DynamicItemOverrideModelData GENERIC_ICON_RESOURCE_LOCATIONS = register(new ItemOverrideUtility.DynamicItemOverrideModelData(
        DYNAMIC_ICON_INDEX_PROPERTY_NAME,
        new ArrayList<>() {{
            add(AMP_TEX_LOC); /* 1 */ add(DUR_TEX_LOC) /* 2 */; add(I_TEX_LOC); /* 3 */ add(II_TEX_LOC); /* 4 */
            add(III_TEX_LOC); /* 5 */ add(IV_TEX_LOC); /* 6 */ add(V_TEX_LOC); /* 7 */ add(VI_TEX_LOC); /* 8 */
            add(VII_TEX_LOC); /* 9 */ add(VIII_TEX_LOC) /* 10 */; add(ARROW_TEX_LOC); /* 11 */
            add(UNKNOWN_TEX_LOC); /* 12 */ add(SGA_A_TEX_LOC); /* 13 */ add(SGA_B_TEX_LOC); /* 14 */
            add(SGA_C_TEX_LOC); /* 15 */ add(SGA_D_TEX_LOC); /* 16 */ add(COMMON_TEX_LOC); /* 17 */
            add(RARE_TEX_LOC); /* 18 */ add(NO_EXP_TEX_LOC); /* 19 */ add(NO_HEAT_TEX_LOC); /* 20 */
            add(FISHING_BAR_TEX_LOC); /* 21 */ add(FISHING_BOBBER_TEX_LOC); /* 22 */ add(COPPER_FISHING_FRAME_TEX_LOC); /* 23 */
            add(GOLD_FISHING_FRAME_TEX_LOC); /* 24 */ add(DIAMOND_FISHING_FRAME_TEX_LOC); /* 25 */ add(PURPLE_FISHING_FRAME_TEX_LOC); /* 26 */
    }}));

    public static Item.Properties properties() {
        return new Item.Properties();
    }

    public static List<IItemModelGenerator> ITEM_MODEL_GENERATORS;
    public static <T extends IItemModelGenerator> T register(T generator) {
        if (ITEM_MODEL_GENERATORS == null) {
            ITEM_MODEL_GENERATORS = new ArrayList<>();
        }
        ITEM_MODEL_GENERATORS.add(generator);
        return generator;
    }

    /**
     * Registers an {@link Item} and a corresponding {@link IItemModelGenerator}
     * @param itemName The name of the item. Used to generate the item's registry name and determine the item's model location.
     * @param itemFactory A supplier that creates the {@link Item} instance for this item type.
     * @param generator The {@link IItemModelGenerator} that generates the item's model data.
     * @return A {@link DeferredHolder} that holds the registered item.
     * @param <I> The type of item to register.
     */
    public static <I extends Item> DeferredHolder<Item, ? extends I> register(String itemName, Supplier<? extends I> itemFactory, IItemModelGenerator generator) {
        register(generator);
        return ITEMS.register(itemName, itemFactory);
    }

    /**
     * Registers an {@link Item} and a corresponding {@link grill24.potionsplus.item.ItemModelUtility.SimpleItemModelGenerator}
     * @param itemName The name of the item. Used to generate the item's registry name and determine the item's model location.
     * @param itemFactory A supplier that creates the {@link Item} instance for this item type.
     * @return A {@link DeferredHolder} that holds the registered item.
     * @param <I> The type of item to register.
     */
    public static <I extends Item> DeferredHolder<Item, ? extends I> register(String itemName, Supplier<? extends I> itemFactory) {
        DeferredHolder<Item, ? extends I> holder = ITEMS.register(itemName, itemFactory);
        register(new ItemModelUtility.SimpleItemModelGenerator(holder));
        return holder;
    }

    public static <T extends EdibleChoiceItem> Holder<Item> registerEdibleChoiceItem(String name, Supplier<T> supplier) {
        return ITEMS.register(name, supplier);
    }

    public static ItemOverrideUtility.EdibleChoiceItemOverrideModelData registerEdibleChoiceItemModel(Holder<Item> item, ResourceLocation itemTexture) {
        return register(new ItemOverrideUtility.EdibleChoiceItemOverrideModelData(
                EDIBLE_CHOICE_ITEM_FLAG_PROPERTY_NAME,
                item,
                itemTexture,
                new ArrayList<>() {{
                    add(ppId("item/red_flag"));
                    add(ppId("item/green_flag"));
                    add(ppId("item/blue_flag"));
                    add(ppId("item/yellow_flag"));
                    add(ppId("item/orange_flag"));
                }}));
    }

    private static Holder<Item>[] generateHats(String name, int count, Supplier<Item> supplier) {
        Holder<Item>[] hats = new Holder[count];
        for (int i = 0; i < count; i++) {
            hats[i] = ITEMS.register(name + "_" + (i+1) , supplier);
        }
        return hats;
    }
}
