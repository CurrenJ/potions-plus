package grill24.potionsplus.data;

import grill24.potionsplus.core.Items;
import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipe;
import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipeBuilder;
import grill24.potionsplus.recipe.clotheslinerecipe.ClotheslineRecipeBuilder;
import grill24.potionsplus.utility.ModInfo;
import grill24.potionsplus.utility.PUtil;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
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

        ShapedRecipeBuilder.shaped(Items.WREATH.get())
                .pattern("LBL")
                .pattern("BTB")
                .pattern("LBL")
                .define('L', ItemTags.LEAVES)
                .define('B', net.minecraft.world.item.Items.BONE)
                .define('T', net.minecraft.world.item.Items.TOTEM_OF_UNDYING)
                .unlockedBy("has_bone", has(net.minecraft.world.item.Items.BONE))
                .save(recipeConsumer);

        ShapedRecipeBuilder.shaped(Items.WREATH.get())
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
                net.minecraft.world.item.alchemy.Potions.WATER, PUtil.PotionType.POTION,
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
                .save(recipeConsumer, new ResourceLocation(ModInfo.MOD_ID, "rotten_flesh_to_leather"));

        new ClotheslineRecipeBuilder()
                .ingredients(Ingredient.of(net.minecraft.world.item.Items.BONE))
                .result(new ItemStack(net.minecraft.world.item.Items.BONE_MEAL, 4))
                .processingTime(600)
                .unlockedBy("has_bone", has(net.minecraft.world.item.Items.BONE))
                .save(recipeConsumer, new ResourceLocation(ModInfo.MOD_ID, "bone_to_bone_meal"));

        new ClotheslineRecipeBuilder()
                .ingredient(net.minecraft.world.item.Items.SOUL_SAND)
                .result(net.minecraft.world.item.Items.SAND)
                .processingTime(200)
                .unlockedBy("has_soul_sand", has(net.minecraft.world.item.Items.SOUL_SAND))
                .save(recipeConsumer, new ResourceLocation(ModInfo.MOD_ID, "soul_sand_to_sand"));

        new ClotheslineRecipeBuilder()
                .ingredient(net.minecraft.world.item.Items.LAVA_BUCKET)
                .result(net.minecraft.world.item.Items.OBSIDIAN)
                .processingTime(2400)
                .unlockedBy("has_lava_bucket", has(net.minecraft.world.item.Items.LAVA_BUCKET))
                .save(recipeConsumer, new ResourceLocation(ModInfo.MOD_ID, "lava_bucket_to_obsidian"));

        new ClotheslineRecipeBuilder()
                .ingredient(net.minecraft.world.item.Items.WET_SPONGE)
                .result(net.minecraft.world.item.Items.SPONGE)
                .processingTime(300)
                .unlockedBy("has_wet_sponge", has(net.minecraft.world.item.Items.WET_SPONGE))
                .save(recipeConsumer, new ResourceLocation(ModInfo.MOD_ID, "wet_sponge_to_sponge"));

        new ClotheslineRecipeBuilder()
                .ingredient(net.minecraft.world.item.Items.POTION)
                .result(net.minecraft.world.item.Items.GLASS_BOTTLE)
                .processingTime(100)
                .unlockedBy("has_potion", has(net.minecraft.world.item.Items.POTION))
                .save(recipeConsumer, new ResourceLocation(ModInfo.MOD_ID, "potion_to_glass_bottle"));

        new ClotheslineRecipeBuilder()
                .ingredient(net.minecraft.world.item.Items.WATER_BUCKET)
                .result(net.minecraft.world.item.Items.BUCKET)
                .processingTime(100)
                .unlockedBy("has_water_bucket", has(net.minecraft.world.item.Items.WATER_BUCKET))
                .save(recipeConsumer, new ResourceLocation(ModInfo.MOD_ID, "water_bucket_to_bucket"));

        new ClotheslineRecipeBuilder()
                .ingredient(Items.IRON_OXIDE_DAISY.get())
                .result(new ItemStack(net.minecraft.world.item.Items.GRAY_DYE, 2))
                .processingTime(60)
                .unlockedBy("has_iron_oxide_daisy", has(Items.IRON_OXIDE_DAISY.get()))
                .save(recipeConsumer, new ResourceLocation(ModInfo.MOD_ID, "iron_oxide_daisy_to_gray_dye"));

        new ClotheslineRecipeBuilder()
                .ingredient(Items.COPPER_CHRYSANTHEMUM.get())
                .result(new ItemStack(net.minecraft.world.item.Items.ORANGE_DYE, 2))
                .processingTime(60)
                .unlockedBy("has_copper_chrysanthemum", has(Items.COPPER_CHRYSANTHEMUM.get()))
                .save(recipeConsumer, new ResourceLocation(ModInfo.MOD_ID, "copper_chrysanthemum_to_orange_dye"));

        new ClotheslineRecipeBuilder()
                .ingredient(Items.GOLDEN_CUBENSIS.get())
                .result(new ItemStack(net.minecraft.world.item.Items.YELLOW_DYE, 2))
                .processingTime(60)
                .unlockedBy("has_golden_cubensis", has(Items.GOLDEN_CUBENSIS.get()))
                .save(recipeConsumer, new ResourceLocation(ModInfo.MOD_ID, "golden_cubensis_to_yellow_dye"));

        new ClotheslineRecipeBuilder()
                .ingredient(Items.LAPIS_LILAC.get())
                .result(new ItemStack(net.minecraft.world.item.Items.BLUE_DYE, 2))
                .processingTime(60)
                .unlockedBy("has_lapis_lilac", has(Items.LAPIS_LILAC.get()))
                .save(recipeConsumer, new ResourceLocation(ModInfo.MOD_ID, "lapis_lilac_to_blue_dye"));

        new ClotheslineRecipeBuilder()
                .ingredient(Items.DIAMOUR.get())
                .result(new ItemStack(net.minecraft.world.item.Items.LIGHT_BLUE_DYE, 2))
                .processingTime(60)
                .unlockedBy("has_diamour", has(Items.DIAMOUR.get()))
                .save(recipeConsumer, new ResourceLocation(ModInfo.MOD_ID, "diamour_to_light_blue_dye"));

        new ClotheslineRecipeBuilder()
                .ingredient(Items.BLACK_COALLA_LILY.get())
                .result(new ItemStack(net.minecraft.world.item.Items.BLACK_DYE, 2))
                .processingTime(60)
                .unlockedBy("has_black_coalla_lily", has(Items.BLACK_COALLA_LILY.get()))
                .save(recipeConsumer, new ResourceLocation(ModInfo.MOD_ID, "black_coalla_lily_to_black_dye"));

        new ClotheslineRecipeBuilder()
                .ingredient(Items.REDSTONE_ROSE.get())
                .result(new ItemStack(net.minecraft.world.item.Items.RED_DYE, 2))
                .processingTime(60)
                .unlockedBy("has_redstone_rose", has(Items.REDSTONE_ROSE.get()))
                .save(recipeConsumer, new ResourceLocation(ModInfo.MOD_ID, "redstone_rose_to_red_dye"));
    }

    private void buildBrewingCauldronRecipes(Consumer<FinishedRecipe> recipeConsumer, List<BrewingCauldronRecipe> recipes) {
        for (BrewingCauldronRecipe recipe : recipes) {
            buildBrewingCauldronRecipe(recipeConsumer, recipe);
        }
    }

    private void buildBrewingCauldronRecipe(Consumer<FinishedRecipe> recipeConsumer, BrewingCauldronRecipe recipe) {
        new BrewingCauldronRecipeBuilder(recipe)
                .unlockedBy("has_potion", has(net.minecraft.world.item.Items.POTION))
                .save(recipeConsumer, recipe.getId());
    }
}
