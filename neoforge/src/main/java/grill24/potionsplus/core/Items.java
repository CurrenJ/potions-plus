package grill24.potionsplus.core;

import grill24.potionsplus.item.EdibleChoiceItem;
import grill24.potionsplus.item.ItemOverrideUtility;
import grill24.potionsplus.item.WormrootItem;
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

    // ----- Dynamically Rendered Display Items -----
    public static final ResourceLocation DYNAMIC_ICON_INDEX_PROPERTY_NAME = ppId("dynamic_icon_index");

    public static final Holder<Item> POTION_EFFECT_ICON = ITEMS.register("potion_effect_icon", () -> new Item(properties()));
    public static final ItemOverrideUtility.PotionEffectIconOverrideModelData POTION_EFFECT_ICON_RESOURCE_LOCATIONS = register(new ItemOverrideUtility.PotionEffectIconOverrideModelData(
        DYNAMIC_ICON_INDEX_PROPERTY_NAME));

    public static final Holder<Item> GENERIC_ICON = ITEMS.register("generic_icon", () -> new Item(properties()));
    public static final ItemOverrideUtility.DynamicItemOverrideModelData GENERIC_ICON_RESOURCE_LOCATIONS = register(new ItemOverrideUtility.DynamicItemOverrideModelData(
        DYNAMIC_ICON_INDEX_PROPERTY_NAME,
        new ArrayList<>() {{
        add(ppId("item/amplifier_upgrade")); // ItemStack count: 1
        add(ppId("item/duration_upgrade")); // ItemStack count: 2
        add(ppId("item/i")); // ItemStack count: 3
        add(ppId("item/ii")); // ItemStack count: 4
        add(ppId("item/iii")); // ItemStack count: 5
        add(ppId("item/iv")); // ItemStack count: 6
        add(ppId("item/v")); // ItemStack count: 7
        add(ppId("item/vi")); // ItemStack count: 8
        add(ppId("item/vii")); // ItemStack count: 9
        add(ppId("item/viii")); // ItemStack count: 10
        add(ppId("item/arrow")); // ItemStack count: 11
        add(ppId("item/unknown")); // ItemStack count: 12
        add(mc("particle/sga_a")); // ItemStack count: 13
        add(mc("particle/sga_b")); // ItemStack count: 14
        add(mc("particle/sga_c")); // ItemStack count: 15
        add(mc("particle/sga_d")); // ItemStack count: 16
        add(ppId("item/common")); // ItemStack count: 17
        add(ppId("item/rare")); // ItemStack count: 18
        add(ppId("item/no_experience")); // ItemStack count: 19
        add(ppId("item/no_heat")); // ItemStack count: 20
    }}));

    public static Item.Properties properties() {
        return new Item.Properties();
    }

    public static List<ItemOverrideUtility.ItemOverrideModelGenerator> ITEM_OVERRIDE_MODEL_GENERATORS;
    public static <T extends ItemOverrideUtility.ItemOverrideModelGenerator> T register(T generator) {
        if (ITEM_OVERRIDE_MODEL_GENERATORS == null) {
            ITEM_OVERRIDE_MODEL_GENERATORS = new ArrayList<>();
        }
        ITEM_OVERRIDE_MODEL_GENERATORS.add(generator);
        return generator;
    }

    public static <T extends EdibleChoiceItem> Holder<Item> registerEdibleChoiceItem(String name, Supplier<T> supplier) {
        Holder<Item> holder = ITEMS.register(name, supplier);
        return holder;
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
}
