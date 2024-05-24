package grill24.potionsplus.data;

import grill24.potionsplus.core.Items;
import grill24.potionsplus.recipe.BrewingCauldronRecipe;
import grill24.potionsplus.utility.PUtil;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.tags.ItemTags;
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

        ShapedRecipeBuilder.shaped(Items.HERBALISTS_LECTERN.get())
                .pattern("WWW")
                .pattern("S S")
                .pattern("DDD")
                .define('W', ItemTags.PLANKS)
                .define('S', net.minecraft.world.item.Items.STICK)
                .define('D', net.minecraft.world.item.Items.DEEPSLATE_BRICK_SLAB)
                .unlockedBy("has_potion", has(net.minecraft.world.item.Items.POTION))
                .save(recipeConsumer);

        ShapedRecipeBuilder.shaped(Items.SANGUINE_ALTAR.get())
                .pattern("AEA")
                .pattern("ESE")
                .pattern("AEA")
                .define('E', net.minecraft.world.item.Items.END_ROD)
                .define('A', net.minecraft.world.item.Items.AMETHYST_SHARD)
                .define('S', net.minecraft.world.item.Items.SOUL_SAND)
                .unlockedBy("has_potion", has(net.minecraft.world.item.Items.POTION))
                .save(recipeConsumer);

        ShapedRecipeBuilder.shaped(Items.ABYSSAL_TROVE.get())
                .pattern("OSO")
                .pattern("SAS")
                .pattern("OSO")
                .define('O', net.minecraft.world.item.Items.SOUL_SOIL)
                .define('A', net.minecraft.world.item.Items.AMETHYST_BLOCK)
                .define('S', net.minecraft.world.item.Items.SOUL_SAND)
                .unlockedBy("has_potion", has(net.minecraft.world.item.Items.POTION))
                .save(recipeConsumer);

        ShapelessRecipeBuilder.shapeless(Items.PRECISION_DISPENSER.get())
                .requires(net.minecraft.world.item.Items.DISPENSER)
                .requires(net.minecraft.world.item.Items.SPYGLASS)
                .unlockedBy("has_dispenser", has(net.minecraft.world.item.Items.DISPENSER))
                .save(recipeConsumer);

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
