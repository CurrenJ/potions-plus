package grill24.potionsplus.core.items;

import grill24.potionsplus.core.ArmorMaterials;
import grill24.potionsplus.core.Items;
import grill24.potionsplus.item.EquipableHatItem;
import grill24.potionsplus.utility.registration.RecipeGeneratorUtility;
import grill24.potionsplus.utility.registration.item.SimpleItemBuilder;
import grill24.potionsplus.utility.registration.RegistrationUtility;
import net.minecraft.core.Holder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;

import java.util.function.BiFunction;
import java.util.function.Supplier;

import static grill24.potionsplus.data.RecipeProvider.has;
import static grill24.potionsplus.utility.Utility.ppId;

public class HatItems {
    public static ResourceLocation[] BLOCK_HAT_MODELS;
    public static Holder<Item>[] EMERALD_ORE_HATS, DIAMOND_ORE_HATS, GOLD_ORE_HATS, IRON_ORE_HATS, COPPER_ORE_HATS, COAL_ORE_HATS;

    public static Holder<Item> FROGGY_HAT, HOOK_HAT, APPLE_HAT;

    public static Holder<Item> WREATH;

    private static Holder<Item>[] generateHats(BiFunction<String, Supplier<Item>, Holder<Item>> register, String name, int count, Supplier<Item> supplier) {
        Holder<Item>[] hats = new Holder[count];
        for (int i = 0; i < count; i++) {
            hats[i] = register.apply(name + "_" + (i+1) , supplier);
        }
        return hats;
    }

    /**
     * Forces the static fields to be initialized.
     */
    public static void init(BiFunction<String, Supplier<Item>, Holder<Item>> register) {
        BLOCK_HAT_MODELS = new ResourceLocation[] { ppId("item/block_hat_1"), ppId("item/block_hat_2"), ppId("item/block_hat_3"), ppId("item/block_hat_4") };

        EMERALD_ORE_HATS = generateHats(register, "emerald_ore_hat", BLOCK_HAT_MODELS.length, () -> new EquipableHatItem(Items.properties()));
        DIAMOND_ORE_HATS = generateHats(register, "diamond_ore_hat", BLOCK_HAT_MODELS.length, () -> new EquipableHatItem(Items.properties()));
        GOLD_ORE_HATS = generateHats(register, "gold_ore_hat", BLOCK_HAT_MODELS.length, () -> new EquipableHatItem(Items.properties()));
        IRON_ORE_HATS = generateHats(register, "iron_ore_hat", BLOCK_HAT_MODELS.length, () -> new EquipableHatItem(Items.properties()));
        COPPER_ORE_HATS = generateHats(register, "copper_ore_hat", BLOCK_HAT_MODELS.length, () -> new EquipableHatItem(Items.properties()));
        COAL_ORE_HATS = generateHats(register, "coal_ore_hat", BLOCK_HAT_MODELS.length, () -> new EquipableHatItem(Items.properties()));

        FROGGY_HAT = register.apply("froggy_hat", () -> new EquipableHatItem(Items.properties()));
        HOOK_HAT = register.apply("hook_hat", () -> new EquipableHatItem(Items.properties()));
        APPLE_HAT = register.apply("apple_hat", () -> new EquipableHatItem(Items.properties()));

        WREATH = RegistrationUtility.register(register, SimpleItemBuilder.create("wreath")
                        .itemFactory(prop -> new ArmorItem(ArmorMaterials.WREATH, ArmorItem.Type.HELMET, prop))
                        .recipeGenerator(holder -> new RecipeGeneratorUtility.RecipeGenerator<>(holder,
                                ppId("wreath"),
                                recipeBuilder -> ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, holder.get().value(), 1)
                                        .pattern("LBL")
                                        .pattern("BTB")
                                        .pattern("LBL")
                                        .define('L', ItemTags.LEAVES)
                                        .define('B', net.minecraft.world.item.Items.BONE)
                                        .define('T', net.minecraft.world.item.Items.TOTEM_OF_UNDYING)
                                        .unlockedBy("has_bone", has(net.minecraft.world.item.Items.BONE))
                        ))
                        .recipeGenerator(holder -> new RecipeGeneratorUtility.RecipeGenerator<>(holder,
                                ppId("wreath_alternate"),
                                recipeBuilder -> ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, holder.get().value(), 1)
                                        .pattern("BLB")
                                        .pattern("LTL")
                                        .pattern("BLB")
                                        .define('L', ItemTags.LEAVES)
                                        .define('B', net.minecraft.world.item.Items.BONE)
                                        .define('T', net.minecraft.world.item.Items.TOTEM_OF_UNDYING)
                                        .unlockedBy("has_bone", has(net.minecraft.world.item.Items.BONE))
                        )))
                .getHolder();

    }
}
