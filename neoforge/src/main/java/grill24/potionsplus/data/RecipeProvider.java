package grill24.potionsplus.data;

import grill24.potionsplus.core.Blocks;
import grill24.potionsplus.core.Items;
import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipe;
import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipeBuilder;
import grill24.potionsplus.recipe.clotheslinerecipe.ClotheslineRecipeBuilder;
import grill24.potionsplus.utility.ModInfo;
import grill24.potionsplus.utility.PUtil;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SmeltingRecipe;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static grill24.potionsplus.utility.Utility.ppId;

public class RecipeProvider extends net.minecraft.data.recipes.RecipeProvider {
    public RecipeProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registryAccess) {
        super(packOutput, registryAccess);
    }

    private void buildBrewingCauldronRecipes(RecipeOutput recipeOutput, List<RecipeHolder<BrewingCauldronRecipe>> recipes) {
        for (RecipeHolder<BrewingCauldronRecipe> recipe : recipes) {
            buildBrewingCauldronRecipe(recipeOutput, recipe);
        }
    }

    private void buildBrewingCauldronRecipe(RecipeOutput recipeOutput, RecipeHolder<BrewingCauldronRecipe> recipe) {
        new BrewingCauldronRecipeBuilder(recipe.value())
                .unlockedBy("has_potion", has(net.minecraft.world.item.Items.POTION))
                .save(recipeOutput);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeConsumer) {
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, Blocks.PARTICLE_EMITTER.value())
                .pattern("III")
                .pattern("IXI")
                .pattern("III")
                .define('I', net.minecraft.world.item.Items.IRON_INGOT)
                .define('X', net.minecraft.world.item.Items.SPORE_BLOSSOM)
                .unlockedBy("has_iron_ingot", has(net.minecraft.world.item.Items.SPORE_BLOSSOM))
                .save(recipeConsumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.BREWING, Blocks.HERBALISTS_LECTERN.value())
                .pattern("WWW")
                .pattern("S S")
                .pattern("DDD")
                .define('W', ItemTags.PLANKS)
                .define('S', net.minecraft.world.item.Items.STICK)
                .define('D', net.minecraft.world.item.Items.DEEPSLATE_BRICK_SLAB)
                .unlockedBy("has_potion", has(net.minecraft.world.item.Items.POTION))
                .save(recipeConsumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.BREWING, Blocks.SANGUINE_ALTAR.value())
                .pattern("AEA")
                .pattern("ESE")
                .pattern("AEA")
                .define('E', net.minecraft.world.item.Items.END_ROD)
                .define('A', net.minecraft.world.item.Items.AMETHYST_SHARD)
                .define('S', net.minecraft.world.item.Items.SOUL_SAND)
                .unlockedBy("has_potion", has(net.minecraft.world.item.Items.POTION))
                .save(recipeConsumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.BREWING, Blocks.ABYSSAL_TROVE.value())
                .pattern("OSO")
                .pattern("SAS")
                .pattern("OSO")
                .define('O', net.minecraft.world.item.Items.SOUL_SOIL)
                .define('A', net.minecraft.world.item.Items.AMETHYST_BLOCK)
                .define('S', net.minecraft.world.item.Items.SOUL_SAND)
                .unlockedBy("has_potion", has(net.minecraft.world.item.Items.POTION))
                .save(recipeConsumer);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, Blocks.PRECISION_DISPENSER.value())
                .requires(net.minecraft.world.item.Items.DISPENSER)
                .requires(net.minecraft.world.item.Items.SPYGLASS)
                .unlockedBy("has_dispenser", has(net.minecraft.world.item.Items.DISPENSER))
                .save(recipeConsumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, Items.WREATH.value())
                .pattern("LBL")
                .pattern("BTB")
                .pattern("LBL")
                .define('L', ItemTags.LEAVES)
                .define('B', net.minecraft.world.item.Items.BONE)
                .define('T', net.minecraft.world.item.Items.TOTEM_OF_UNDYING)
                .unlockedBy("has_bone", has(net.minecraft.world.item.Items.BONE))
                .save(recipeConsumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, Items.WREATH.value())
                .pattern("BLB")
                .pattern("LTL")
                .pattern("BLB")
                .define('L', ItemTags.LEAVES)
                .define('B', net.minecraft.world.item.Items.BONE)
                .define('T', net.minecraft.world.item.Items.TOTEM_OF_UNDYING)
                .unlockedBy("has_bone", has(net.minecraft.world.item.Items.BONE))
                .save(recipeConsumer, "wreath_alternate");

        // ----- Static Brewing Cauldron Recipes -----

        // Water Bottle
        buildBrewingCauldronRecipe(recipeConsumer, PUtil.brewingCauldronRecipe(0.1F, 30, "has_potion",
                net.minecraft.world.item.alchemy.Potions.WATER.value(), PUtil.PotionType.POTION,
                -1,
                Ingredient.of(net.minecraft.world.item.Items.GLASS_BOTTLE)));

        // Water Bucket
        buildBrewingCauldronRecipe(recipeConsumer, PUtil.brewingCauldronRecipe(0.1F, 40, "has_potion",
                new ItemStack(net.minecraft.world.item.Items.WATER_BUCKET),
                -1,
                Ingredient.of(net.minecraft.world.item.Items.BUCKET)));

        // Clay
        buildBrewingCauldronRecipe(recipeConsumer, PUtil.brewingCauldronRecipe(0.1F, 50, "has_potion",
                new ItemStack(net.minecraft.world.item.Items.CLAY_BALL),
                -1,
                Ingredient.of(net.minecraft.world.item.Items.SAND),
                Ingredient.of(net.minecraft.world.item.Items.GRAVEL)));

        // Mushroom Conversions
        buildBrewingCauldronRecipe(recipeConsumer, PUtil.brewingCauldronRecipe(0.1F, 80, "has_potion",
                new ItemStack(net.minecraft.world.item.Items.BROWN_MUSHROOM),
                -1,
                Ingredient.of(net.minecraft.world.item.Items.RED_MUSHROOM),
                Ingredient.of(net.minecraft.world.item.Items.FERMENTED_SPIDER_EYE)));

        buildBrewingCauldronRecipe(recipeConsumer, PUtil.brewingCauldronRecipe(0.1F, 80, "has_potion",
                new ItemStack(net.minecraft.world.item.Items.RED_MUSHROOM),
                -1,
                Ingredient.of(net.minecraft.world.item.Items.BROWN_MUSHROOM),
                Ingredient.of(net.minecraft.world.item.Items.FERMENTED_SPIDER_EYE))
        );

        // Grass
        buildBrewingCauldronRecipe(recipeConsumer, PUtil.brewingCauldronRecipe(0.1F, 40, "has_potion",
                new ItemStack(net.minecraft.world.item.Items.GRASS_BLOCK),
                -1,
                Ingredient.of(net.minecraft.world.item.Items.DIRT),
                Ingredient.of(net.minecraft.world.item.Items.MOSS_BLOCK))
        );


        // ----- Clothesline Recipes -----

        new ClotheslineRecipeBuilder()
                .ingredients(Ingredient.of(net.minecraft.world.item.Items.ROTTEN_FLESH))
                .result(new ItemStack(net.minecraft.world.item.Items.LEATHER))
                .processingTime(100)
                .unlockedBy("has_rotten_flesh", has(net.minecraft.world.item.Items.ROTTEN_FLESH))
                .save(recipeConsumer, ResourceLocation.fromNamespaceAndPath(ModInfo.MOD_ID, "rotten_flesh_to_leather"));

        new ClotheslineRecipeBuilder()
                .ingredients(Ingredient.of(net.minecraft.world.item.Items.BONE))
                .result(new ItemStack(net.minecraft.world.item.Items.BONE_MEAL, 4))
                .processingTime(600)
                .unlockedBy("has_bone", has(net.minecraft.world.item.Items.BONE))
                .save(recipeConsumer, ppId("bone_to_bone_meal"));

        new ClotheslineRecipeBuilder()
                .ingredient(net.minecraft.world.item.Items.SOUL_SAND)
                .result(net.minecraft.world.item.Items.SAND)
                .processingTime(200)
                .unlockedBy("has_soul_sand", has(net.minecraft.world.item.Items.SOUL_SAND))
                .save(recipeConsumer, ppId("soul_sand_to_sand"));

        new ClotheslineRecipeBuilder()
                .ingredient(net.minecraft.world.item.Items.LAVA_BUCKET)
                .result(net.minecraft.world.item.Items.OBSIDIAN)
                .processingTime(2400)
                .unlockedBy("has_lava_bucket", has(net.minecraft.world.item.Items.LAVA_BUCKET))
                .save(recipeConsumer, ppId("lava_bucket_to_obsidian"));

        new ClotheslineRecipeBuilder()
                .ingredient(net.minecraft.world.item.Items.WET_SPONGE)
                .result(net.minecraft.world.item.Items.SPONGE)
                .processingTime(300)
                .unlockedBy("has_wet_sponge", has(net.minecraft.world.item.Items.WET_SPONGE))
                .save(recipeConsumer, ppId("wet_sponge_to_sponge"));

        new ClotheslineRecipeBuilder()
                .ingredient(net.minecraft.world.item.Items.POTION)
                .result(net.minecraft.world.item.Items.GLASS_BOTTLE)
                .processingTime(100)
                .unlockedBy("has_potion", has(net.minecraft.world.item.Items.POTION))
                .save(recipeConsumer, ppId("potion_to_glass_bottle"));

        new ClotheslineRecipeBuilder()
                .ingredient(net.minecraft.world.item.Items.WATER_BUCKET)
                .result(net.minecraft.world.item.Items.BUCKET)
                .processingTime(100)
                .unlockedBy("has_water_bucket", has(net.minecraft.world.item.Items.WATER_BUCKET))
                .save(recipeConsumer, ppId("water_bucket_to_bucket"));

        new ClotheslineRecipeBuilder()
                .ingredient(Blocks.IRON_OXIDE_DAISY.value())
                .result(new ItemStack(net.minecraft.world.item.Items.GRAY_DYE, 2))
                .processingTime(60)
                .unlockedBy("has_iron_oxide_daisy", has(Blocks.IRON_OXIDE_DAISY.value()))
                .save(recipeConsumer, ppId("iron_oxide_daisy_to_gray_dye"));

        new ClotheslineRecipeBuilder()
                .ingredient(Blocks.COPPER_CHRYSANTHEMUM.value())
                .result(new ItemStack(net.minecraft.world.item.Items.ORANGE_DYE, 2))
                .processingTime(60)
                .unlockedBy("has_copper_chrysanthemum", has(Blocks.COPPER_CHRYSANTHEMUM.value()))
                .save(recipeConsumer, ppId("copper_chrysanthemum_to_orange_dye"));

        new ClotheslineRecipeBuilder()
                .ingredient(Blocks.GOLDEN_CUBENSIS.value())
                .result(new ItemStack(net.minecraft.world.item.Items.YELLOW_DYE, 2))
                .processingTime(60)
                .unlockedBy("has_golden_cubensis", has(Blocks.GOLDEN_CUBENSIS.value()))
                .save(recipeConsumer, ppId("golden_cubensis_to_yellow_dye"));

        new ClotheslineRecipeBuilder()
                .ingredient(Blocks.LAPIS_LILAC.value())
                .result(new ItemStack(net.minecraft.world.item.Items.BLUE_DYE, 2))
                .processingTime(60)
                .unlockedBy("has_lapis_lilac", has(Blocks.LAPIS_LILAC.value()))
                .save(recipeConsumer, ppId("lapis_lilac_to_blue_dye"));

        new ClotheslineRecipeBuilder()
                .ingredient(Blocks.DIAMOUR.value())
                .result(new ItemStack(net.minecraft.world.item.Items.LIGHT_BLUE_DYE, 2))
                .processingTime(60)
                .unlockedBy("has_diamour", has(Blocks.DIAMOUR.value()))
                .save(recipeConsumer, ppId("diamour_to_light_blue_dye"));

        new ClotheslineRecipeBuilder()
                .ingredient(Blocks.BLACK_COALLA_LILY.value())
                .result(new ItemStack(net.minecraft.world.item.Items.BLACK_DYE, 2))
                .processingTime(60)
                .unlockedBy("has_black_coalla_lily", has(Blocks.BLACK_COALLA_LILY.value()))
                .save(recipeConsumer, ppId("black_coalla_lily_to_black_dye"));

        new ClotheslineRecipeBuilder()
                .ingredient(Blocks.REDSTONE_ROSE.value())
                .result(new ItemStack(net.minecraft.world.item.Items.RED_DYE, 2))
                .processingTime(60)
                .unlockedBy("has_redstone_rose", has(Blocks.REDSTONE_ROSE.value()))
                .save(recipeConsumer, ppId("redstone_rose_to_red_dye"));

        SimpleCookingRecipeBuilder.blasting(
                Ingredient.of(Items.REMNANT_DEBRIS.value()),
                RecipeCategory.MISC,
                new ItemStack(Items.NETHERITE_REMNANT.value()),
                2.0F,
                50
        ).unlockedBy("has_remnant_debris", has(Items.REMNANT_DEBRIS.value()))
                .save(recipeConsumer, "remnant_debris_to_netherite_remnant_blasting");
        SimpleCookingRecipeBuilder.smelting(
                Ingredient.of(Items.REMNANT_DEBRIS.value()),
                RecipeCategory.MISC,
                new ItemStack(Items.NETHERITE_REMNANT.value()),
                2.0F,
                100
        ).unlockedBy("has_remnant_debris", has(Items.REMNANT_DEBRIS.value()))
                .save(recipeConsumer, "remnant_debris_to_netherite_remnant_smelting");

        SimpleCookingRecipeBuilder.blasting(
                Ingredient.of(Items.DEEPSLATE_REMNANT_DEBRIS.value()),
                RecipeCategory.MISC,
                new ItemStack(Items.NETHERITE_REMNANT.value()),
                2.0F,
                50
        ).unlockedBy("has_deepslate_remnant_debris", has(Items.DEEPSLATE_REMNANT_DEBRIS.value()))
                .save(recipeConsumer, "deepslate_remnant_debris_to_netherite_remnant_blasting");
        SimpleCookingRecipeBuilder.smelting(
                Ingredient.of(Items.DEEPSLATE_REMNANT_DEBRIS.value()),
                RecipeCategory.MISC,
                new ItemStack(Items.NETHERITE_REMNANT.value()),
                2.0F,
                100
        ).unlockedBy("has_deepslate_remnant_debris", has(Items.DEEPSLATE_REMNANT_DEBRIS.value()))
                .save(recipeConsumer, "deepslate_remnant_debris_to_netherite_remnant_smelting");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, net.minecraft.world.item.Items.NETHERITE_INGOT)
                .requires(net.minecraft.world.item.Items.NETHERITE_SCRAP, 2)
                .requires(Items.NETHERITE_REMNANT.value(), 1)
                .requires(net.minecraft.world.item.Items.GOLD_INGOT, 4)
                .group("netherite_ingot")
                .unlockedBy("has_netherite_remnant", has(Items.NETHERITE_REMNANT.value()))
                .save(recipeConsumer);
    }
}
