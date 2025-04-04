package grill24.potionsplus.data;

import grill24.potionsplus.core.blocks.FlowerBlocks;
import grill24.potionsplus.core.items.OreItems;
import grill24.potionsplus.core.potion.MobEffects;
import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipe;
import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipeBuilder;
import grill24.potionsplus.recipe.clotheslinerecipe.ClotheslineRecipeBuilder;
import grill24.potionsplus.utility.ModInfo;
import grill24.potionsplus.utility.PUtil;
import grill24.potionsplus.utility.registration.RegistrationUtility;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static grill24.potionsplus.utility.Utility.ppId;

public class RecipeProvider extends net.minecraft.data.recipes.RecipeProvider {
    public RecipeProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registryAccess) {
        super(packOutput, registryAccess);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeConsumer) {
        RegistrationUtility.generateRecipes(ModInfo.MOD_ID, this, recipeConsumer);

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
                .result(new ItemStack(OreItems.SULFURIC_ACID))
                .ingredients(PUtil.createPotionItemStack(net.minecraft.world.item.alchemy.Potions.WATER, PUtil.PotionType.POTION), new ItemStack(OreItems.SULFUR_SHARD))
                .processingTime(40)
                .unlockedBy("has_sulfur_shard", has(OreItems.SULFUR_SHARD.value()))
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
                .ingredients(PUtil.createPotionItemStack(grill24.potionsplus.core.potion.Potions.ANY_POTION, PUtil.PotionType.POTION), new ItemStack(OreItems.URANIUM_INGOT.value()))
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
                .ingredient(FlowerBlocks.IRON_OXIDE_DAISY.value())
                .result(new ItemStack(net.minecraft.world.item.Items.RAW_IRON, 3))
                .processingTime(60)
                .unlockedBy("has_iron_oxide_daisy", has(FlowerBlocks.IRON_OXIDE_DAISY.value()))
                .save(recipeConsumer, ppId("iron_oxide_daisy_to_raw_iron"));

        new ClotheslineRecipeBuilder()
                .ingredient(FlowerBlocks.COPPER_CHRYSANTHEMUM.value())
                .result(new ItemStack(net.minecraft.world.item.Items.RAW_COPPER, 6))
                .processingTime(60)
                .unlockedBy("has_copper_chrysanthemum", has(FlowerBlocks.COPPER_CHRYSANTHEMUM.value()))
                .save(recipeConsumer, ppId("copper_chrysanthemum_to_raw_copper"));

        new ClotheslineRecipeBuilder()
                .ingredient(FlowerBlocks.GOLDEN_CUBENSIS.value())
                .result(new ItemStack(net.minecraft.world.item.Items.RAW_GOLD, 3))
                .processingTime(60)
                .unlockedBy("has_golden_cubensis", has(FlowerBlocks.GOLDEN_CUBENSIS.value()))
                .save(recipeConsumer, ppId("golden_cubensis_to_raw_gold"));

        new ClotheslineRecipeBuilder()
                .ingredient(FlowerBlocks.LAPIS_LILAC.value())
                .result(new ItemStack(net.minecraft.world.item.Items.LAPIS_BLOCK, 1))
                .processingTime(60)
                .unlockedBy("has_lapis_lilac", has(FlowerBlocks.LAPIS_LILAC.value()))
                .save(recipeConsumer, ppId("lapis_lilac_to_lapis_block"));

        new ClotheslineRecipeBuilder()
                .ingredient(FlowerBlocks.DIAMOUR.value())
                .result(new ItemStack(net.minecraft.world.item.Items.DIAMOND, 2))
                .processingTime(60)
                .unlockedBy("has_diamour", has(FlowerBlocks.DIAMOUR.value()))
                .save(recipeConsumer, ppId("diamour_to_diamonds"));

        new ClotheslineRecipeBuilder()
                .ingredient(FlowerBlocks.BLACK_COALLA_LILY.value())
                .result(new ItemStack(net.minecraft.world.item.Items.COAL_BLOCK, 1))
                .processingTime(60)
                .unlockedBy("has_black_coalla_lily", has(FlowerBlocks.BLACK_COALLA_LILY.value()))
                .save(recipeConsumer, ppId("black_coalla_lily_to_coal_block"));

        new ClotheslineRecipeBuilder()
                .ingredient(FlowerBlocks.REDSTONE_ROSE.value())
                .result(new ItemStack(net.minecraft.world.item.Items.REDSTONE_BLOCK, 1))
                .processingTime(60)
                .unlockedBy("has_redstone_rose", has(FlowerBlocks.REDSTONE_ROSE.value()))
                .save(recipeConsumer, ppId("redstone_rose_to_redstone_block"));
    }

    public static @NotNull Criterion<InventoryChangeTrigger.TriggerInstance> has(@NotNull ItemLike itemLike) {
        return net.minecraft.data.recipes.RecipeProvider.has(itemLike);
    }

    // has KeyTag
    public static @NotNull Criterion<InventoryChangeTrigger.TriggerInstance> has(@NotNull TagKey<Item> keyTag) {
        return net.minecraft.data.recipes.RecipeProvider.has(keyTag);
    }
}
