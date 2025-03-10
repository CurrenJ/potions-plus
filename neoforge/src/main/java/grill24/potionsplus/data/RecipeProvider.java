package grill24.potionsplus.data;

import grill24.potionsplus.core.Blocks;
import grill24.potionsplus.core.Items;
import grill24.potionsplus.core.Tags;
import grill24.potionsplus.core.potion.MobEffects;
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
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;

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
                .save(recipeConsumer, ppId("particle_emitter"));

        ShapedRecipeBuilder.shaped(RecipeCategory.BREWING, Blocks.HERBALISTS_LECTERN.value())
                .pattern("WWW")
                .pattern("S S")
                .pattern("DDD")
                .define('W', ItemTags.PLANKS)
                .define('S', net.minecraft.world.item.Items.STICK)
                .define('D', net.minecraft.world.item.Items.DEEPSLATE_BRICK_SLAB)
                .unlockedBy("has_potion", has(net.minecraft.world.item.Items.POTION))
                .save(recipeConsumer, ppId("herbalists_lectern"));

        ShapedRecipeBuilder.shaped(RecipeCategory.BREWING, Blocks.SANGUINE_ALTAR.value())
                .pattern("AEA")
                .pattern("ESE")
                .pattern("AEA")
                .define('E', net.minecraft.world.item.Items.END_ROD)
                .define('A', net.minecraft.world.item.Items.AMETHYST_SHARD)
                .define('S', net.minecraft.world.item.Items.SOUL_SAND)
                .unlockedBy("has_potion", has(net.minecraft.world.item.Items.POTION))
                .save(recipeConsumer, ppId("sanguine_altar"));

        ShapedRecipeBuilder.shaped(RecipeCategory.BREWING, Blocks.ABYSSAL_TROVE.value())
                .pattern("OSO")
                .pattern("SAS")
                .pattern("OSO")
                .define('O', net.minecraft.world.item.Items.SOUL_SOIL)
                .define('A', net.minecraft.world.item.Items.AMETHYST_BLOCK)
                .define('S', net.minecraft.world.item.Items.SOUL_SAND)
                .unlockedBy("has_potion", has(net.minecraft.world.item.Items.POTION))
                .save(recipeConsumer, ppId("abyssal_trove"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, Blocks.PRECISION_DISPENSER.value())
                .requires(net.minecraft.world.item.Items.DISPENSER)
                .requires(net.minecraft.world.item.Items.SPYGLASS)
                .unlockedBy("has_dispenser", has(net.minecraft.world.item.Items.DISPENSER))
                .save(recipeConsumer, ppId("precision_dispenser"));

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, Items.WREATH.value())
                .pattern("LBL")
                .pattern("BTB")
                .pattern("LBL")
                .define('L', ItemTags.LEAVES)
                .define('B', net.minecraft.world.item.Items.BONE)
                .define('T', net.minecraft.world.item.Items.TOTEM_OF_UNDYING)
                .unlockedBy("has_bone", has(net.minecraft.world.item.Items.BONE))
                .save(recipeConsumer, ppId("wreath"));

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, Items.WREATH.value())
                .pattern("BLB")
                .pattern("LTL")
                .pattern("BLB")
                .define('L', ItemTags.LEAVES)
                .define('B', net.minecraft.world.item.Items.BONE)
                .define('T', net.minecraft.world.item.Items.TOTEM_OF_UNDYING)
                .unlockedBy("has_bone", has(net.minecraft.world.item.Items.BONE))
                .save(recipeConsumer, ppId("wreath_alternate"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, Blocks.URANIUM_GLASS.value())
                .requires(net.minecraft.world.item.Items.GLASS)
                .requires(Items.URANIUM_INGOT.value())
                .unlockedBy("has_uranium_ingot", has(Items.URANIUM_INGOT.value()))
                .save(recipeConsumer, ppId("uranium_glass"));

        ShapedRecipeBuilder.shaped(RecipeCategory.BREWING, Blocks.POTION_BEACON.value())
                .pattern("GGG")
                .pattern("GNG")
                .pattern("OOO")
                .define('G', Blocks.URANIUM_GLASS.value())
                .define('N', net.minecraft.world.item.Items.NETHER_STAR)
                .define('O', net.minecraft.world.item.Items.OBSIDIAN)
                .unlockedBy("has_uranium_glass", has(Blocks.URANIUM_GLASS.value()))
                .save(recipeConsumer, ppId("potion_beacon"));

        ShapedRecipeBuilder.shaped(RecipeCategory.BREWING, Blocks.POTION_BEACON.value())
                .pattern("GGG")
                .pattern("GBG")
                .pattern("OOO")
                .define('G', Blocks.URANIUM_GLASS.value())
                .define('B', net.minecraft.world.item.Items.BEACON)
                .define('O', net.minecraft.world.item.Items.OBSIDIAN)
                .unlockedBy("has_uranium_glass", has(Blocks.URANIUM_GLASS.value()))
                .save(recipeConsumer, ppId("potion_beacon_alternate"));

        // ----- Static Brewing Cauldron Recipes -----

        // Water Bottle
        new BrewingCauldronRecipeBuilder()
                .result(PUtil.createPotionItemStack(net.minecraft.world.item.alchemy.Potions.WATER, PUtil.PotionType.POTION))
                .ingredients(net.minecraft.world.item.Items.GLASS_BOTTLE)
                .processingTime(30)
                .unlockedBy("has_potion", has(net.minecraft.world.item.Items.POTION))
                .save(recipeConsumer, ppId("water_bottle"));

        // Water Bucket
        new BrewingCauldronRecipeBuilder()
                .result(new ItemStack(net.minecraft.world.item.Items.BUCKET))
                .ingredients(net.minecraft.world.item.Items.WATER_BUCKET)
                .processingTime(40)
                .unlockedBy("has_potion", has(net.minecraft.world.item.Items.POTION))
                .save(recipeConsumer, ppId("water_bucket"));

        // Obsidian
        new BrewingCauldronRecipeBuilder()
                .result(new ItemStack(net.minecraft.world.item.Items.OBSIDIAN))
                .ingredients(net.minecraft.world.item.Items.LAVA_BUCKET)
                .processingTime(200)
                .unlockedBy("has_lava_bucket", has(net.minecraft.world.item.Items.LAVA_BUCKET))
                .save(recipeConsumer, ppId("obsidian"));

        // Clay
        new BrewingCauldronRecipeBuilder()
                .result(new ItemStack(net.minecraft.world.item.Items.CLAY_BALL))
                .ingredients(net.minecraft.world.item.Items.SAND, net.minecraft.world.item.Items.GRAVEL)
                .processingTime(50)
                .unlockedBy("has_potion", has(net.minecraft.world.item.Items.POTION))
                .save(recipeConsumer, ppId("clay"));

        // Mushroom Conversions
        new BrewingCauldronRecipeBuilder()
                .result(new ItemStack(net.minecraft.world.item.Items.BROWN_MUSHROOM))
                .ingredients(net.minecraft.world.item.Items.RED_MUSHROOM, net.minecraft.world.item.Items.FERMENTED_SPIDER_EYE)
                .processingTime(20)
                .unlockedBy("has_potion", has(net.minecraft.world.item.Items.POTION))
                .save(recipeConsumer, ppId("mushroom_conversions_r2b"));
        new BrewingCauldronRecipeBuilder()
                .result(new ItemStack(net.minecraft.world.item.Items.RED_MUSHROOM))
                .ingredients(net.minecraft.world.item.Items.BROWN_MUSHROOM, net.minecraft.world.item.Items.FERMENTED_SPIDER_EYE)
                .processingTime(20)
                .unlockedBy("has_potion", has(net.minecraft.world.item.Items.POTION))
                .save(recipeConsumer, ppId("mushroom_conversions_b2r"));

        // Grass
        new BrewingCauldronRecipeBuilder()
                .result(new ItemStack(net.minecraft.world.item.Items.GRASS_BLOCK))
                .ingredients(net.minecraft.world.item.Items.DIRT, net.minecraft.world.item.Items.MOSS_BLOCK)
                .processingTime(40)
                .unlockedBy("has_potion", has(net.minecraft.world.item.Items.POTION))
                .save(recipeConsumer, ppId("grass_block"));

        // Sulfuric Acid
        new BrewingCauldronRecipeBuilder()
                .result(new ItemStack(Items.SULFURIC_ACID))
                .ingredients(PUtil.createPotionItemStack(net.minecraft.world.item.alchemy.Potions.WATER, PUtil.PotionType.POTION), new ItemStack(Items.SULFUR_SHARD))
                .processingTime(40)
                .unlockedBy("has_sulfur_shard", has(Items.SULFUR_SHARD.value()))
                .save(recipeConsumer, ppId("sulfuric_acid"));

        // All Potions Duration Increase [5 Seconds] [Recipe is constant in all worlds]
        List<BrewingCauldronRecipe.PotionMatchingCriteria> upgradePotionMatchingCriteria = List.of(BrewingCauldronRecipe.PotionMatchingCriteria.IGNORE_POTION_CONTAINER, BrewingCauldronRecipe.PotionMatchingCriteria.IGNORE_POTION_EFFECTS_MIN_1_EFFECT);
        new BrewingCauldronRecipeBuilder()
                .result(PUtil.createPotionItemStack(grill24.potionsplus.core.potion.Potions.ANY_POTION, PUtil.PotionType.POTION))
                .ingredients(PUtil.createPotionItemStack(grill24.potionsplus.core.potion.Potions.ANY_POTION, PUtil.PotionType.POTION), new ItemStack(net.minecraft.world.item.Items.QUARTZ))
                .processingTime(30)
                .durationToAdd(100)
                .potionMatchingCriteria(upgradePotionMatchingCriteria)
                .isSeededRuntimeRecipe()
                .unlockedBy("has_potion", has(net.minecraft.world.item.Items.POTION))
                .save(recipeConsumer, ppId("all_potions_duration_increase"));

        // Amplification Increase [1 level] [Recipe is constant in all worlds]
        new BrewingCauldronRecipeBuilder()
                .result(PUtil.createPotionItemStack(grill24.potionsplus.core.potion.Potions.ANY_POTION, PUtil.PotionType.POTION))
                .ingredients(PUtil.createPotionItemStack(grill24.potionsplus.core.potion.Potions.ANY_POTION, PUtil.PotionType.POTION), new ItemStack(Items.URANIUM_INGOT.value()))
                .processingTime(30)
                .amplifierToAdd(1)
                .potionMatchingCriteria(upgradePotionMatchingCriteria)
                .isSeededRuntimeRecipe()
                .unlockedBy("has_potion", has(net.minecraft.world.item.Items.POTION))
                .save(recipeConsumer, ppId("amplification_testing"));

        // Merge Potions
        ItemStack mergedPotionResult = new ItemStack(net.minecraft.world.item.Items.POTION);
        PUtil.setCustomEffects(mergedPotionResult, List.of(new MobEffectInstance(MobEffects.ANY_POTION), new MobEffectInstance(MobEffects.ANY_OTHER_POTION)));
        new BrewingCauldronRecipeBuilder()
                .result(mergedPotionResult)
                .ingredients(PUtil.createPotionItemStack(grill24.potionsplus.core.potion.Potions.ANY_POTION, PUtil.PotionType.POTION), PUtil.createPotionItemStack(grill24.potionsplus.core.potion.Potions.ANY_OTHER_POTION, PUtil.PotionType.POTION))
                .processingTime(30)
                .experienceRequired(10F)
                .potionMatchingCriteria(List.of(BrewingCauldronRecipe.PotionMatchingCriteria.NEVER_MATCH))
                .unlockedBy("has_potion", has(net.minecraft.world.item.Items.POTION))
                .save(recipeConsumer, ppId("merge_potions"));

        // Splash Potion
        new BrewingCauldronRecipeBuilder()
                .result(PUtil.createPotionItemStack(grill24.potionsplus.core.potion.Potions.ANY_POTION, PUtil.PotionType.SPLASH_POTION))
                .ingredients(PUtil.createPotionItemStack(grill24.potionsplus.core.potion.Potions.ANY_POTION, PUtil.PotionType.POTION), new ItemStack(net.minecraft.world.item.Items.GUNPOWDER))
                .processingTime(30)
                .potionMatchingCriteria(List.of(BrewingCauldronRecipe.PotionMatchingCriteria.IGNORE_POTION_EFFECTS))
                .unlockedBy("has_potion", has(net.minecraft.world.item.Items.POTION))
                .save(recipeConsumer, ppId("splash_potion"));

        // Lingering Potion
        new BrewingCauldronRecipeBuilder()
                .result(PUtil.createPotionItemStack(grill24.potionsplus.core.potion.Potions.ANY_POTION, PUtil.PotionType.LINGERING_POTION))
                .ingredients(PUtil.createPotionItemStack(grill24.potionsplus.core.potion.Potions.ANY_POTION, PUtil.PotionType.SPLASH_POTION), new ItemStack(net.minecraft.world.item.Items.DRAGON_BREATH))
                .processingTime(30)
                .potionMatchingCriteria(List.of(BrewingCauldronRecipe.PotionMatchingCriteria.IGNORE_POTION_EFFECTS))
                .unlockedBy("has_potion", has(net.minecraft.world.item.Items.POTION))
                .save(recipeConsumer, ppId("lingering_potion"));

        // Tipped Arrow
        new BrewingCauldronRecipeBuilder()
                .result(PUtil.createPotionItemStack(grill24.potionsplus.core.potion.Potions.ANY_POTION, PUtil.PotionType.TIPPED_ARROW))
                .ingredients(PUtil.createPotionItemStack(grill24.potionsplus.core.potion.Potions.ANY_POTION, PUtil.PotionType.POTION), new ItemStack(net.minecraft.world.item.Items.ARROW))
                .processingTime(30)
                .potionMatchingCriteria(List.of(BrewingCauldronRecipe.PotionMatchingCriteria.IGNORE_POTION_EFFECTS))
                .unlockedBy("has_potion", has(net.minecraft.world.item.Items.POTION))
                .save(recipeConsumer, ppId("tipped_arrow"));


        // ----- Clothesline Recipes -----

        new ClotheslineRecipeBuilder()
                .ingredients(net.minecraft.world.item.Items.ROTTEN_FLESH)
                .result(new ItemStack(net.minecraft.world.item.Items.LEATHER))
                .processingTime(100)
                .unlockedBy("has_rotten_flesh", has(net.minecraft.world.item.Items.ROTTEN_FLESH))
                .save(recipeConsumer, ppId("rotten_flesh_to_leather"));

        new ClotheslineRecipeBuilder()
                .ingredients(net.minecraft.world.item.Items.BONE)
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
                Ingredient.of(Tags.Items.REMNANT_DEBRIS),
                RecipeCategory.MISC,
                new ItemStack(Items.NETHERITE_REMNANT.value()),
                2.0F,
                50
        ).unlockedBy("has_remnant_debris", has(Items.REMNANT_DEBRIS.value()))
                .save(recipeConsumer, ppId("remnant_debris_to_netherite_remnant_blasting"));
        SimpleCookingRecipeBuilder.smelting(
                Ingredient.of(Tags.Items.REMNANT_DEBRIS),
                RecipeCategory.MISC,
                new ItemStack(Items.NETHERITE_REMNANT.value()),
                2.0F,
                100
        ).unlockedBy("has_remnant_debris", has(Items.REMNANT_DEBRIS.value()))
                .save(recipeConsumer, ppId("remnant_debris_to_netherite_remnant_smelting"));

        SimpleCookingRecipeBuilder.blasting(
                Ingredient.of(Tags.Items.URANIUM_ORE),
                RecipeCategory.MISC,
                new ItemStack(Items.URANIUM_INGOT.value()),
                2.0F,
                50
        ).unlockedBy("has_uranium_ore", has(Blocks.URANIUM_ORE.value()))
                .save(recipeConsumer, ppId("uranium_ore_to_uranium_ingot_blasting"));
        SimpleCookingRecipeBuilder.smelting(
                Ingredient.of(Tags.Items.URANIUM_ORE),
                RecipeCategory.MISC,
                new ItemStack(Items.URANIUM_INGOT.value()),
                2.0F,
                100
        ).unlockedBy("has_uranium_ore", has(Blocks.URANIUM_ORE.value()))
                .save(recipeConsumer, ppId("uranium_ore_to_uranium_ingot_smelting"));

        SimpleCookingRecipeBuilder.blasting(
                Ingredient.of(Items.RAW_URANIUM.value()),
                RecipeCategory.MISC,
                new ItemStack(Items.URANIUM_INGOT.value()),
                2.0F,
                50
        ).unlockedBy("has_raw_uranium", has(Items.RAW_URANIUM.value()))
                .save(recipeConsumer, ppId("raw_uranium_to_uranium_ingot_blasting"));
        SimpleCookingRecipeBuilder.smelting(
                Ingredient.of(Items.RAW_URANIUM.value()),
                RecipeCategory.MISC,
                new ItemStack(Items.URANIUM_INGOT.value()),
                2.0F,
                100
        ).unlockedBy("has_raw_uranium", has(Items.RAW_URANIUM.value()))
                .save(recipeConsumer, ppId("raw_uranium_to_uranium_ingot_smelting"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, net.minecraft.world.item.Items.NETHERITE_INGOT)
                .requires(net.minecraft.world.item.Items.NETHERITE_SCRAP, 2)
                .requires(Items.NETHERITE_REMNANT.value(), 1)
                .requires(net.minecraft.world.item.Items.GOLD_INGOT, 4)
                .group("netherite_ingot")
                .unlockedBy("has_netherite_remnant", has(Items.NETHERITE_REMNANT.value()))
                .save(recipeConsumer, ppId("netherite_ingot"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Blocks.URANIUM_BLOCK.value())
                .pattern("UUU")
                .pattern("UUU")
                .pattern("UUU")
                .define('U', Items.URANIUM_INGOT.value())
                .unlockedBy("has_uranium_ingot", has(Items.URANIUM_INGOT.value()))
                .save(recipeConsumer, ppId("uranium_block"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Blocks.SKILL_JOURNALS.value().asItem())
                .requires(net.minecraft.world.item.Items.BOOK)
                .requires(net.minecraft.world.item.Items.BOOK)
                .requires(net.minecraft.world.item.Items.BOOK)
                .unlockedBy("has_book", has(net.minecraft.world.item.Items.BOOK))
                .save(recipeConsumer, ppId("skill_journals"));

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, Items.COPPER_FISHING_ROD.value())
                .define('C', net.minecraft.world.item.Items.COPPER_INGOT)
                .define('S', net.minecraft.world.item.Items.STICK)
                .define('F', net.minecraft.world.item.Items.STRING)
                .pattern("  C")
                .pattern(" CF")
                .pattern("S F")
                .unlockedBy("has_copper_ingot", has(net.minecraft.world.item.Items.COPPER_INGOT))
                .save(recipeConsumer, ppId("copper_fishing_rod"));
    }
}
