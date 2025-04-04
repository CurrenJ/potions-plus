package grill24.potionsplus.core.items;

import grill24.potionsplus.core.Tags;
import grill24.potionsplus.item.UpgradeBaseItem;
import grill24.potionsplus.utility.registration.RecipeGeneratorUtility;
import grill24.potionsplus.utility.registration.item.SimpleItemBuilder;
import grill24.potionsplus.utility.registration.RegistrationUtility;
import net.minecraft.core.Holder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Item;

import java.util.function.BiFunction;
import java.util.function.Supplier;

import static grill24.potionsplus.data.RecipeProvider.has;

public class FilterHopperUpgradeItems {
    public static Holder<Item> UPGRADE_BASE, FILTER_HOPPER_UPGRADE_BLACKLIST, FILTER_HOPPER_UPGRADE_ALLOW_ARMOR,
            FILTER_HOPPER_UPGRADE_ALLOW_FOOD, FILTER_HOPPER_UPGRADE_ALLOW_TOOLS, FILTER_HOPPER_UPGRADE_ALLOW_POTIONS,
            FILTER_HOPPER_UPGRADE_ALLOW_ENCHANTED, FILTER_HOPPER_UPGRADE_ALLOW_POTION_INGREDIENTS,
            FILTER_HOPPER_UPGRADE_ALLOW_EDIBLE_REWARDS;

    /**
     * Force static fields to be initialized.
     */
    public static void init(BiFunction<String, Supplier<Item>, Holder<Item>> register) {
        UPGRADE_BASE = RegistrationUtility.register(register, SimpleItemBuilder.create("upgrade_base")
                .itemFactory(UpgradeBaseItem::new)
                .recipeGenerator(holder -> new RecipeGeneratorUtility.RecipeGenerator<>(holder, h ->
                        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, h.value())
                                .pattern("RGR")
                                .pattern("EQE")
                                .pattern("RGR")
                                .define('G', net.minecraft.world.item.Items.GOLD_INGOT)
                                .define('E', net.minecraft.world.item.Items.EMERALD)
                                .define('Q', net.minecraft.world.item.Items.QUARTZ)
                                .define('R', net.minecraft.world.item.Items.REDSTONE)
                                .unlockedBy("has_quartz", has(net.minecraft.world.item.Items.QUARTZ))))
                ).getHolder();
        FILTER_HOPPER_UPGRADE_BLACKLIST = RegistrationUtility.register(register, SimpleItemBuilder.create("filter_hopper_upgrade_blacklist")
                .itemFactory(UpgradeBaseItem::new)
                .recipeGenerator(holder -> new RecipeGeneratorUtility.RecipeGenerator<>(holder, h ->
                        ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, h.value())
                                .requires(FilterHopperUpgradeItems.UPGRADE_BASE.value())
                                .requires(net.minecraft.world.item.Items.REDSTONE_TORCH)
                                .group("filter_hopper_upgrade_blacklist")
                                .unlockedBy("has_upgrade_base", has(FilterHopperUpgradeItems.UPGRADE_BASE.value()))))
        ).getHolder();
        FILTER_HOPPER_UPGRADE_ALLOW_ARMOR = RegistrationUtility.register(register, SimpleItemBuilder.create("filter_hopper_upgrade_allow_armor")
                .itemFactory(UpgradeBaseItem::new)
                .recipeGenerator(holder -> new RecipeGeneratorUtility.RecipeGenerator<>(holder, h ->
                        ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, h.value())
                                .requires(FilterHopperUpgradeItems.UPGRADE_BASE.value())
                                .requires(net.neoforged.neoforge.common.Tags.Items.ARMORS)
                                .group("filter_hopper_upgrade_allow_armor")
                                .unlockedBy("has_upgrade_base", has(FilterHopperUpgradeItems.UPGRADE_BASE.value()))))
        ).getHolder();
        FILTER_HOPPER_UPGRADE_ALLOW_FOOD = RegistrationUtility.register(register, SimpleItemBuilder.create("filter_hopper_upgrade_allow_food")
                .itemFactory(UpgradeBaseItem::new)
                .recipeGenerator(holder -> new RecipeGeneratorUtility.RecipeGenerator<>(holder, h ->
                        ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, FilterHopperUpgradeItems.FILTER_HOPPER_UPGRADE_ALLOW_FOOD.value())
                                .requires(FilterHopperUpgradeItems.UPGRADE_BASE.value())
                                .requires(net.neoforged.neoforge.common.Tags.Items.FOODS)
                                .group("filter_hopper_upgrade_allow_food")
                                .unlockedBy("has_upgrade_base", has(FilterHopperUpgradeItems.UPGRADE_BASE.value()))))
        ).getHolder();
        FILTER_HOPPER_UPGRADE_ALLOW_TOOLS = RegistrationUtility.register(register, SimpleItemBuilder.create("filter_hopper_upgrade_allow_tools")
                .itemFactory(UpgradeBaseItem::new)
                .recipeGenerator(holder -> new RecipeGeneratorUtility.RecipeGenerator<>(holder, h ->
                        ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, h.value())
                                .requires(FilterHopperUpgradeItems.UPGRADE_BASE.value())
                                .requires(net.neoforged.neoforge.common.Tags.Items.TOOLS)
                                .group("filter_hopper_upgrade_allow_tools")
                                .unlockedBy("has_upgrade_base", has(FilterHopperUpgradeItems.UPGRADE_BASE.value()))))
        ).getHolder();
        FILTER_HOPPER_UPGRADE_ALLOW_POTIONS = RegistrationUtility.register(register, SimpleItemBuilder.create("filter_hopper_upgrade_allow_potions")
                .itemFactory(UpgradeBaseItem::new)
                .recipeGenerator(holder -> new RecipeGeneratorUtility.RecipeGenerator<>(holder, h ->
                        ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, h.value())
                                .requires(FilterHopperUpgradeItems.UPGRADE_BASE.value())
                                .requires(net.neoforged.neoforge.common.Tags.Items.POTIONS)
                                .group("filter_hopper_upgrade_allow_potions")
                                .unlockedBy("has_upgrade_base", has(FilterHopperUpgradeItems.UPGRADE_BASE.value()))))
        ).getHolder();
        FILTER_HOPPER_UPGRADE_ALLOW_ENCHANTED = RegistrationUtility.register(register, SimpleItemBuilder.create("filter_hopper_upgrade_allow_enchanted")
                .itemFactory(UpgradeBaseItem::new)
                .recipeGenerator(holder -> new RecipeGeneratorUtility.RecipeGenerator<>(holder, h ->
                        ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, h.value())
                                .requires(FilterHopperUpgradeItems.UPGRADE_BASE.value())
                                .requires(net.minecraft.world.item.Items.ENCHANTED_BOOK)
                                .group("filter_hopper_upgrade_allow_enchanted")
                                .unlockedBy("has_upgrade_base", has(FilterHopperUpgradeItems.UPGRADE_BASE.value()))))
        ).getHolder();
        FILTER_HOPPER_UPGRADE_ALLOW_POTION_INGREDIENTS = RegistrationUtility.register(register, SimpleItemBuilder.create("filter_hopper_upgrade_allow_potion_ingredients")
                .itemFactory(UpgradeBaseItem::new)
                .recipeGenerator(holder -> new RecipeGeneratorUtility.RecipeGenerator<>(holder, h ->
                        ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, h.value())
                                .requires(FilterHopperUpgradeItems.UPGRADE_BASE.value())
                                .requires(net.minecraft.world.item.Items.BLAZE_POWDER)
                                .group("filter_hopper_upgrade_allow_potion_ingredients")
                                .unlockedBy("has_upgrade_base", has(FilterHopperUpgradeItems.UPGRADE_BASE.value()))))
        ).getHolder();
        FILTER_HOPPER_UPGRADE_ALLOW_EDIBLE_REWARDS = RegistrationUtility.register(register, SimpleItemBuilder.create("filter_hopper_upgrade_allow_edible_rewards")
                .itemFactory(UpgradeBaseItem::new)
                .recipeGenerator(holder -> new RecipeGeneratorUtility.RecipeGenerator<>(holder, h ->
                        ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, FilterHopperUpgradeItems.FILTER_HOPPER_UPGRADE_ALLOW_EDIBLE_REWARDS.value())
                                .requires(FilterHopperUpgradeItems.UPGRADE_BASE.value())
                                .requires(Tags.Items.EDIBLE_REWARDS)
                                .group("filter_hopper_upgrade_allow_edible_rewards")
                                .unlockedBy("has_upgrade_base", has(FilterHopperUpgradeItems.UPGRADE_BASE.value()))))
        ).getHolder();
    }
}
