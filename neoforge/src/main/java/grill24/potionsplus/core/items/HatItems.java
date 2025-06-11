package grill24.potionsplus.core.items;

import grill24.potionsplus.core.ArmorMaterials;
import grill24.potionsplus.core.Items;
import grill24.potionsplus.utility.registration.RecipeGeneratorUtility;
import grill24.potionsplus.utility.registration.RegistrationUtility;
import grill24.potionsplus.utility.registration.item.ItemModelUtility;
import grill24.potionsplus.utility.registration.item.SimpleItemBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorType;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import static grill24.potionsplus.utility.Utility.ppId;

public class HatItems {
    public static ResourceLocation[] BLOCK_HAT_MODELS;
    public static Holder<Item>[] EMERALD_ORE_HATS, DIAMOND_ORE_HATS, GOLD_ORE_HATS, IRON_ORE_HATS, COPPER_ORE_HATS, COAL_ORE_HATS;

    public static Holder<Item> FROGGY_HAT, HOOK_HAT, APPLE_HAT;

    public static Holder<Item> WREATH;

    private static Holder<Item>[] generateHats(BiFunction<String, Supplier<Item>, Holder<Item>> register, String name, int count, Function<String, Item> itemFactory) {
        Holder<Item>[] hats = new Holder[count];
        for (int i = 0; i < count; i++) {
            String itemName = name + "_" + (i + 1);
            hats[i] = register.apply(itemName, () -> itemFactory.apply(itemName));
        }
        return hats;
    }

    /**
     * Forces the static fields to be initialized.
     */
    public static void init(BiFunction<String, Supplier<Item>, Holder<Item>> register) {
        BLOCK_HAT_MODELS = new ResourceLocation[]{ppId("item/block_hat_1"), ppId("item/block_hat_2"), ppId("item/block_hat_3"), ppId("item/block_hat_4")};

        final Function<String, Item> blockHatItemFactory = (name) -> new Item(Items.properties()
                .equippable(EquipmentSlot.HEAD)
                .stacksTo(1)
                .setId(ResourceKey.create(Registries.ITEM, ppId(name)))
        );
        EMERALD_ORE_HATS = generateHats(register, "emerald_ore_hat", BLOCK_HAT_MODELS.length, blockHatItemFactory);
        DIAMOND_ORE_HATS = generateHats(register, "diamond_ore_hat", BLOCK_HAT_MODELS.length, blockHatItemFactory);
        GOLD_ORE_HATS = generateHats(register, "gold_ore_hat", BLOCK_HAT_MODELS.length, blockHatItemFactory);
        IRON_ORE_HATS = generateHats(register, "iron_ore_hat", BLOCK_HAT_MODELS.length, blockHatItemFactory);
        COPPER_ORE_HATS = generateHats(register, "copper_ore_hat", BLOCK_HAT_MODELS.length, blockHatItemFactory);
        COAL_ORE_HATS = generateHats(register, "coal_ore_hat", BLOCK_HAT_MODELS.length, blockHatItemFactory);

        final Function<Item.Properties, Item> miscHatFactory = (prop -> new Item(prop
                .equippable(EquipmentSlot.HEAD)
                .stacksTo(1)
        ));
        FROGGY_HAT = RegistrationUtility.register(register, SimpleItemBuilder.create("froggy_hat")
                .modelGenerator(h -> new ItemModelUtility.ItemFromModelFileGenerator<>(h, ppId("item/froggy_hat")))
                .itemFactory(miscHatFactory)
        ).getHolder();
        HOOK_HAT = RegistrationUtility.register(register, SimpleItemBuilder.create("hook_hat")
                .modelGenerator(h -> new ItemModelUtility.ItemFromModelFileGenerator<>(h, ppId("item/hook_hat")))
                .itemFactory(miscHatFactory)
        ).getHolder();
        APPLE_HAT = RegistrationUtility.register(register, SimpleItemBuilder.create("apple_hat")
                .modelGenerator(h -> new ItemModelUtility.ItemFromModelFileGenerator<>(h, ppId("item/apple_hat")))
                .itemFactory(miscHatFactory)
        ).getHolder();


        WREATH = RegistrationUtility.register(register, SimpleItemBuilder.create("wreath")
                        .itemFactory(prop -> new Item(prop.humanoidArmor(ArmorMaterials.WREATH, ArmorType.HELMET)))
                        .recipeGenerator(holder -> new RecipeGeneratorUtility.RecipeGenerator<>(holder,
                                ppId("wreath"),
                                (recipeProvider, h) ->
                                        recipeProvider.shaped(RecipeCategory.COMBAT, h.value(), 1)
                                                .pattern("LBL")
                                                .pattern("BTB")
                                                .pattern("LBL")
                                                .define('L', ItemTags.LEAVES)
                                                .define('B', net.minecraft.world.item.Items.BONE)
                                                .define('T', net.minecraft.world.item.Items.TOTEM_OF_UNDYING)
                                                .unlockedBy("has_bone", recipeProvider.has(net.minecraft.world.item.Items.BONE))
                        ))
                        .recipeGenerator(holder -> new RecipeGeneratorUtility.RecipeGenerator<>(holder,
                                ppId("wreath_alternate"),
                                (recipeProvider, h) ->
                                        recipeProvider.shaped(RecipeCategory.COMBAT, h.value(), 1)
                                                .pattern("BLB")
                                                .pattern("LTL")
                                                .pattern("BLB")
                                                .define('L', ItemTags.LEAVES)
                                                .define('B', net.minecraft.world.item.Items.BONE)
                                                .define('T', net.minecraft.world.item.Items.TOTEM_OF_UNDYING)
                                                .unlockedBy("has_bone", recipeProvider.has(net.minecraft.world.item.Items.BONE))
                        )))
                .getHolder();

    }
}
