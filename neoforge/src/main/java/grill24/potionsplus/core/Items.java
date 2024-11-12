package grill24.potionsplus.core;

import grill24.potionsplus.item.WormrootItem;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.*;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;

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

    // ----- Dynamically Rendered Display Items -----

    public static final Holder<Item> POTION_EFFECT_ICON = ITEMS.register("potion_effect_icon", () -> new Item(properties()));

    public static final Holder<Item> GENERIC_ICON = ITEMS.register("generic_icon", () -> new Item(properties()));
    public static final List<ResourceLocation> GENERIC_ICON_RESOURCE_LOCATIONS = new ArrayList<>() {{
        add(ppId("amplifier_upgrade")); // ItemStack count: 1
        add(ppId("duration_upgrade")); // ItemStack count: 2
        add(ppId("i")); // ItemStack count: 3
        add(ppId("ii")); // ItemStack count: 4
        add(ppId("iii")); // ItemStack count: 5
        add(ppId("iv")); // ItemStack count: 6
        add(ppId("v")); // ItemStack count: 7
        add(ppId("vi")); // ItemStack count: 8
        add(ppId("vii")); // ItemStack count: 9
        add(ppId("viii")); // ItemStack count: 10
        add(ppId("arrow")); // ItemStack count: 11
        add(ppId("unknown")); // ItemStack count: 12
        add(mc("sga_a")); // ItemStack count: 13
        add(mc("sga_b")); // ItemStack count: 14
        add(mc("sga_c")); // ItemStack count: 15
        add(mc("sga_d")); // ItemStack count: 16
        add(ppId("common")); // ItemStack count: 17
        add(ppId("rare")); // ItemStack count: 18
    }};

    public static final String DYNAMIC_ICON_INDEX_PROPERTY_NAME = "dynamic_icon_index";

    public static Item.Properties properties() {
        return new Item.Properties();
    }
}
