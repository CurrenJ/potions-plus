package grill24.potionsplus.core.items;

import grill24.potionsplus.core.Items;
import grill24.potionsplus.core.Tags;
import grill24.potionsplus.utility.registration.RecipeGeneratorUtility;
import grill24.potionsplus.utility.registration.RegistrationUtility;
import grill24.potionsplus.utility.registration.item.SimpleItemBuilder;
import net.minecraft.core.Holder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.BiFunction;
import java.util.function.Supplier;

import static grill24.potionsplus.utility.Utility.ppId;

public class OreItems {
    public static Holder<Item> RAW_URANIUM, URANIUM_INGOT;
    public static Holder<Item> SULFUR_SHARD, SULFURIC_ACID;

    /**
     * Force static fields to be initialized.
     */
    public static void init(BiFunction<String, Supplier<Item>, Holder<Item>> register) {
        RAW_URANIUM = RegistrationUtility.register(register, SimpleItemBuilder.createSimple("raw_uranium").properties(Items.properties().rarity(Rarity.UNCOMMON))
                .recipeGenerator(holder -> new RecipeGeneratorUtility.RecipeGenerator<>(holder, ppId("raw_uranium_to_uranium_ingot_smelting"),
                        (recipeProvider, h) ->
                                SimpleCookingRecipeBuilder.smelting(
                                        Ingredient.of(OreItems.RAW_URANIUM.value()),
                                        RecipeCategory.MISC,
                                        new ItemStack(OreItems.URANIUM_INGOT.value()),
                                        2.0F,
                                        100
                                ).unlockedBy("has_raw_uranium", recipeProvider.has(OreItems.RAW_URANIUM.value()))))
                .recipeGenerator(holder -> new RecipeGeneratorUtility.RecipeGenerator<>(holder, ppId("raw_uranium_to_uranium_ingot_blasting"),
                        (recipeProvider, h) ->
                                SimpleCookingRecipeBuilder.blasting(
                                        Ingredient.of(OreItems.RAW_URANIUM.value()),
                                        RecipeCategory.MISC,
                                        new ItemStack(OreItems.URANIUM_INGOT.value()),
                                        2.0F,
                                        50
                                ).unlockedBy("has_raw_uranium", recipeProvider.has(OreItems.RAW_URANIUM.value()))))
        ).getHolder();

        URANIUM_INGOT = RegistrationUtility.register(register, SimpleItemBuilder.createSimple("uranium_ingot").properties(Items.properties().rarity(Rarity.UNCOMMON))
                .recipeGenerator(holder -> new RecipeGeneratorUtility.RecipeGenerator<>(holder, ppId("uranium_ore_to_uranium_ingot_smelting"),
                        (recipeProvider, h) ->
                                SimpleCookingRecipeBuilder.smelting(
                                        recipeProvider.tag(Tags.Items.URANIUM_ORE),
                                        RecipeCategory.MISC,
                                        new ItemStack(h.value()),
                                        2.0F,
                                        100
                                ).unlockedBy("has_uranium_ore", recipeProvider.has(Tags.Items.URANIUM_ORE))))
                .recipeGenerator(holder -> new RecipeGeneratorUtility.RecipeGenerator<>(holder, ppId("uranium_ore_to_uranium_ingot_blasting"),
                        (recipeProvider, h) ->
                                SimpleCookingRecipeBuilder.blasting(
                                        recipeProvider.tag(Tags.Items.URANIUM_ORE),
                                        RecipeCategory.MISC,
                                        new ItemStack(h.value()),
                                        2.0F,
                                        50
                                ).unlockedBy("has_uranium_ore", recipeProvider.has(Tags.Items.URANIUM_ORE))))
        ).getHolder();

        SULFUR_SHARD = RegistrationUtility.register(register, SimpleItemBuilder.createSimple("sulfur_shard")).getHolder();
        SULFURIC_ACID = RegistrationUtility.register(register, SimpleItemBuilder.createSimple("sulfuric_acid")).getHolder();
    }
}
