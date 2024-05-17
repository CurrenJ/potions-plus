package grill24.potionsplus.data;

import grill24.potionsplus.core.Items;
import grill24.potionsplus.recipe.BrewingCauldronRecipe;
import grill24.potionsplus.utility.PUtil;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.List;
import java.util.function.Consumer;

public class RecipeProvider extends net.minecraft.data.recipes.RecipeProvider {
    private final ExistingFileHelper existingFileHelper;

    public RecipeProvider(DataGenerator dataGenerator, ExistingFileHelper existingFileHelper) {
        super(dataGenerator);
        this.existingFileHelper = existingFileHelper;
    }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> recipeConsumer) {
        ShapedRecipeBuilder.shaped(Items.PARTICLE_EMITTER.get())
                .pattern("III")
                .pattern("IXI")
                .pattern("III")
                .define('I', net.minecraft.world.item.Items.IRON_INGOT)
                .define('X', net.minecraft.world.item.Items.SPORE_BLOSSOM)
                .unlockedBy("has_iron_ingot", has(net.minecraft.world.item.Items.SPORE_BLOSSOM))
                .save(recipeConsumer);

        // Exploding
//        brewingCauldronPotionModifierForAllContainers(recipeConsumer, 0.1F, 100, "has_potion",
//                net.minecraft.world.item.alchemy.Potions.AWKWARD,
//                Potions.ALL_EXPLODING_POTIONS[0][0].get(),
//                Ingredient.of(net.minecraft.world.item.Items.GUNPOWDER),
//                Ingredient.of(net.minecraft.world.item.Items.SUGAR));

//        brewingCauldronPotionUpgrades(recipeConsumer, 0.1F, 100, "has_potion", Potions.ALL_EXPLODING_POTIONS,
//                new Ingredient[]{Ingredient.of(net.minecraft.world.item.Items.GUNPOWDER), Ingredient.of(net.minecraft.world.item.Items.SUGAR)},
//                new Ingredient[]{Ingredient.of(net.minecraft.world.item.Items.GLOWSTONE_DUST)},
//                new Ingredient[]{Ingredient.of(net.minecraft.world.item.Items.REDSTONE)},
//                new Ingredient[]{Ingredient.of(net.minecraft.world.item.Items.GLOWSTONE_DUST), Ingredient.of(net.minecraft.world.item.Items.REDSTONE)
//        });

        // Geode Grace
//        brewingCauldronPotionModifierForAllContainers(recipeConsumer, 0.1F, 100, "has_potion",
//                net.minecraft.world.item.alchemy.Potions.AWKWARD,
//                Potions.GEODE_GRACE.get(),
//                Ingredient.of(net.minecraft.world.item.Items.GOLDEN_APPLE),
//                Ingredient.of(net.minecraft.world.item.Items.TUFF));

        // Fall of the Void
//        brewingCauldronPotionModifierForAllContainers(recipeConsumer, 0.1F, 100, "has_potion",
//                net.minecraft.world.item.alchemy.Potions.AWKWARD,
//                Potions.FALL_OF_THE_VOID.get(),
//                Ingredient.of(net.minecraft.world.item.Items.ENDER_PEARL),
//                Ingredient.of(net.minecraft.world.item.Items.PHANTOM_MEMBRANE),
//                Ingredient.of(net.minecraft.world.item.Items.FEATHER));

        // Magnetic
//        brewingCauldronPotionModifierForAllContainers(recipeConsumer, 0.1F, 100, "has_potion",
//                net.minecraft.world.item.alchemy.Potions.AWKWARD,
//                Potions.MAGNETIC.get(),
//                Ingredient.of(net.minecraft.world.item.Items.IRON_INGOT),
//                Ingredient.of(net.minecraft.world.item.Items.COPPER_INGOT));

        // Teleportation
//        brewingCauldronPotionModifierForAllContainers(recipeConsumer, 0.1F, 100, "has_potion",
//                net.minecraft.world.item.alchemy.Potions.AWKWARD,
//                Potions.TELEPORTATION.get(),
//                Ingredient.of(net.minecraft.world.item.Items.CHORUS_FRUIT));

        // Levitation
//        brewingCauldronPotionModifierForAllContainers(recipeConsumer, 0.1F, 100, "has_potion", net.minecraft.world.item.alchemy.Potions.SLOW_FALLING,
//                Potions.LEVITATION.get(), Ingredient.of(net.minecraft.world.item.Items.FERMENTED_SPIDER_EYE));
//        brewingCauldronPotionModifierForAllContainers(recipeConsumer, 0.1F, 100, "has_potion", net.minecraft.world.item.alchemy.Potions.LONG_SLOW_FALLING,
//                Potions.LONG_LEVITATION.get(), Ingredient.of(net.minecraft.world.item.Items.FERMENTED_SPIDER_EYE));
//        brewingCauldronPotionModifierForAllContainers(recipeConsumer, 0.1F, 100, "has_potion", Potions.LEVITATION.get(),
//                Potions.LONG_LEVITATION.get(), Ingredient.of(net.minecraft.world.item.Items.REDSTONE));

        // Water Bottle
        buildBrewingCauldronRecipe(recipeConsumer, PUtil.brewingCauldronRecipe(0.1F, 30, "has_potion",
                net.minecraft.world.item.alchemy.Potions.WATER, PUtil.PotionType.POTION,
                Ingredient.of(net.minecraft.world.item.Items.GLASS_BOTTLE)));

        // Water Bucket
        buildBrewingCauldronRecipe(recipeConsumer, PUtil.brewingCauldronRecipe(0.1F, 40, "has_potion",
                new ItemStack(net.minecraft.world.item.Items.WATER_BUCKET),
                Ingredient.of(net.minecraft.world.item.Items.BUCKET)));

        // Clay
        buildBrewingCauldronRecipe(recipeConsumer, PUtil.brewingCauldronRecipe(0.1F, 50, "has_potion",
                new ItemStack(net.minecraft.world.item.Items.CLAY_BALL),
                Ingredient.of(net.minecraft.world.item.Items.SAND),
                Ingredient.of(net.minecraft.world.item.Items.GRAVEL)));

        // Mushroom Conversions
        buildBrewingCauldronRecipe(recipeConsumer, PUtil.brewingCauldronRecipe(0.1F, 80, "has_potion",
                new ItemStack(net.minecraft.world.item.Items.BROWN_MUSHROOM),
                Ingredient.of(net.minecraft.world.item.Items.RED_MUSHROOM),
                Ingredient.of(net.minecraft.world.item.Items.FERMENTED_SPIDER_EYE)));

        buildBrewingCauldronRecipe(recipeConsumer, PUtil.brewingCauldronRecipe(0.1F, 80, "has_potion",
                new ItemStack(net.minecraft.world.item.Items.RED_MUSHROOM),
                Ingredient.of(net.minecraft.world.item.Items.BROWN_MUSHROOM),
                Ingredient.of(net.minecraft.world.item.Items.FERMENTED_SPIDER_EYE))
        );

        // Grass
        buildBrewingCauldronRecipe(recipeConsumer, PUtil.brewingCauldronRecipe(0.1F, 40, "has_potion",
                new ItemStack(net.minecraft.world.item.Items.GRASS_BLOCK),
                Ingredient.of(net.minecraft.world.item.Items.DIRT),
                Ingredient.of(net.minecraft.world.item.Items.MOSS_BLOCK))
        );

        // Instant Health
//        brewingCauldronPotionModifierForAllContainers(recipeConsumer, 0.1F, 200, "has_potion",
//                net.minecraft.world.item.alchemy.Potions.AWKWARD,
//                net.minecraft.world.item.alchemy.Potions.STRONG_HEALING,
//                Ingredient.of(net.minecraft.world.item.Items.GOLDEN_CARROT),
//                Ingredient.of(net.minecraft.world.item.Items.HONEY_BOTTLE),
//                Ingredient.of(net.minecraft.world.item.Items.GLOW_BERRIES));
    }

    private void buildBrewingCauldronRecipes(Consumer<FinishedRecipe> recipeConsumer, List<BrewingCauldronRecipe> recipes) {
        for (BrewingCauldronRecipe recipe : recipes) {
            buildBrewingCauldronRecipe(recipeConsumer, recipe);
        }
    }

    private void buildBrewingCauldronRecipe(Consumer<FinishedRecipe> recipeConsumer, BrewingCauldronRecipe recipe) {
        Ingredient[] ingredients = recipe.getIngredients().toArray(new Ingredient[0]);
        BrewingCauldronRecipeBuilder.brewing(ingredients, recipe.getResultItem(), recipe.getExperience(), recipe.getProcessingTime())
                .unlockedBy("has_potion", has(net.minecraft.world.item.Items.POTION))
                .save(recipeConsumer, recipe.getId());
    }
}
