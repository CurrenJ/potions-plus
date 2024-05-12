package grill24.potionsplus.data;

import grill24.potionsplus.core.Items;
import grill24.potionsplus.core.Potions;
import grill24.potionsplus.utility.ModInfo;
import grill24.potionsplus.utility.PUtil;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.data.ExistingFileHelper;

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
        brewingCauldronPotionModifierForAllContainers(recipeConsumer, 0.1F, 100, "has_potion",
                net.minecraft.world.item.alchemy.Potions.AWKWARD,
                Potions.EXPLODING.get(),
                Ingredient.of(net.minecraft.world.item.Items.GUNPOWDER),
                Ingredient.of(net.minecraft.world.item.Items.SUGAR));

        // Geode Grace
        brewingCauldronPotionModifierForAllContainers(recipeConsumer, 0.1F, 100, "has_potion",
                net.minecraft.world.item.alchemy.Potions.AWKWARD,
                Potions.GEODE_GRACE.get(),
                Ingredient.of(net.minecraft.world.item.Items.GOLDEN_APPLE),
                Ingredient.of(net.minecraft.world.item.Items.TUFF));

        // Fall of the Void
        brewingCauldronPotionModifierForAllContainers(recipeConsumer, 0.1F, 100, "has_potion",
                net.minecraft.world.item.alchemy.Potions.AWKWARD,
                Potions.FALL_OF_THE_VOID.get(),
                Ingredient.of(net.minecraft.world.item.Items.ENDER_PEARL),
                Ingredient.of(net.minecraft.world.item.Items.PHANTOM_MEMBRANE),
                Ingredient.of(net.minecraft.world.item.Items.FEATHER));

        // Magnetic
        brewingCauldronPotionModifierForAllContainers(recipeConsumer, 0.1F, 100, "has_potion",
                net.minecraft.world.item.alchemy.Potions.AWKWARD,
                Potions.MAGNETIC.get(),
                Ingredient.of(net.minecraft.world.item.Items.IRON_INGOT),
                Ingredient.of(net.minecraft.world.item.Items.COPPER_INGOT));

        // Teleportation
        brewingCauldronPotionModifierForAllContainers(recipeConsumer, 0.1F, 100, "has_potion",
                net.minecraft.world.item.alchemy.Potions.AWKWARD,
                Potions.TELEPORTATION.get(),
                Ingredient.of(net.minecraft.world.item.Items.CHORUS_FRUIT));

        // Levitation
        brewingCauldronPotionModifierForAllContainers(recipeConsumer, 0.1F, 100, "has_potion", net.minecraft.world.item.alchemy.Potions.SLOW_FALLING,
                Potions.LEVITATION.get(), Ingredient.of(net.minecraft.world.item.Items.FERMENTED_SPIDER_EYE));
        brewingCauldronPotionModifierForAllContainers(recipeConsumer, 0.1F, 100, "has_potion", net.minecraft.world.item.alchemy.Potions.LONG_SLOW_FALLING,
                Potions.LONG_LEVITATION.get(), Ingredient.of(net.minecraft.world.item.Items.FERMENTED_SPIDER_EYE));
        brewingCauldronPotionModifierForAllContainers(recipeConsumer, 0.1F, 100, "has_potion", Potions.LEVITATION.get(),
                Potions.LONG_LEVITATION.get(), Ingredient.of(net.minecraft.world.item.Items.REDSTONE));

        // Water Bottle
        brewingCauldronRecipe(recipeConsumer, 0.1F, 30, "has_potion", net.minecraft.world.item.alchemy.Potions.WATER, PUtil.PotionType.POTION,
                Ingredient.of(net.minecraft.world.item.Items.GLASS_BOTTLE));

        // Water Bucket
        brewingCauldronRecipe(recipeConsumer, 0.1F, 40, "has_potion",
                new ItemStack(net.minecraft.world.item.Items.WATER_BUCKET),
                Ingredient.of(net.minecraft.world.item.Items.BUCKET));

        // Clay
        brewingCauldronRecipe(recipeConsumer, 0.1F, 50, "has_potion",
                new ItemStack(net.minecraft.world.item.Items.CLAY_BALL),
                Ingredient.of(net.minecraft.world.item.Items.SAND),
                Ingredient.of(net.minecraft.world.item.Items.GRAVEL));

        // Mushroom Conversions
        brewingCauldronRecipe(recipeConsumer, 0.1F, 80, "has_potion",
                new ItemStack(net.minecraft.world.item.Items.BROWN_MUSHROOM),
                Ingredient.of(net.minecraft.world.item.Items.RED_MUSHROOM),
                Ingredient.of(net.minecraft.world.item.Items.FERMENTED_SPIDER_EYE));
        brewingCauldronRecipe(recipeConsumer, 0.1F, 80, "has_potion",
                new ItemStack(net.minecraft.world.item.Items.RED_MUSHROOM),
                Ingredient.of(net.minecraft.world.item.Items.BROWN_MUSHROOM),
                Ingredient.of(net.minecraft.world.item.Items.FERMENTED_SPIDER_EYE));

        // Grass
        brewingCauldronRecipe(recipeConsumer, 0.1F, 40, "has_potion",
                new ItemStack(net.minecraft.world.item.Items.GRASS_BLOCK),
                Ingredient.of(net.minecraft.world.item.Items.DIRT),
                Ingredient.of(net.minecraft.world.item.Items.MOSS_BLOCK));

        // Instant Health
        brewingCauldronPotionModifierForAllContainers(recipeConsumer, 0.1F, 200, "has_potion",
                net.minecraft.world.item.alchemy.Potions.AWKWARD,
                net.minecraft.world.item.alchemy.Potions.STRONG_HEALING,
                Ingredient.of(net.minecraft.world.item.Items.GOLDEN_CARROT),
                Ingredient.of(net.minecraft.world.item.Items.HONEY_BOTTLE),
                Ingredient.of(net.minecraft.world.item.Items.GLOW_BERRIES));
    }

    private void brewingCauldronRecipe(Consumer<FinishedRecipe> recipeConsumer, float experience, int processingTime, String advancementNameIngredient, ItemStack result, Ingredient... ingredients) {
        StringBuilder name = new StringBuilder();
        for (Ingredient ingredient : ingredients) {
            name.append(PUtil.getNameOrVerbosePotionName(ingredient.getItems()[0])).append("_");
        }
        name.append("to_");
        name.append(PUtil.getNameOrVerbosePotionName(result));

        BrewingCauldronRecipeBuilder.brewing(ingredients, result, experience, processingTime)
                .unlockedBy("has_potion", has(net.minecraft.world.item.Items.POTION))
                .save(recipeConsumer, new net.minecraft.resources.ResourceLocation(ModInfo.MOD_ID, name.toString()));
    }

    private void brewingCauldronRecipe(Consumer<FinishedRecipe> recipeConsumer, float experience, int processingTime, String advancementNameIngredient, Potion potion, PUtil.PotionType potionType, Ingredient... ingredients) {
        brewingCauldronRecipe(recipeConsumer, experience, processingTime, advancementNameIngredient, PUtil.createPotionItemStack(potion, potionType), ingredients);
    }

    private void brewingCauldronRecipe(Consumer<FinishedRecipe> recipeConsumer, float experience, int baseProcessingTime, String advancementNameIngredient, Potion inputPotion, Potion outputPotion, PUtil.PotionType potionType, Ingredient... nonPotionIngredients) {
        ItemStack inputPotionItemStack = PUtil.createPotionItemStack(inputPotion, potionType);
        Ingredient[] allIngredients = new Ingredient[nonPotionIngredients.length + 1];
        System.arraycopy(nonPotionIngredients, 0, allIngredients, 0, nonPotionIngredients.length);
        allIngredients[allIngredients.length - 1] = Ingredient.of(inputPotionItemStack);

        int processingTime = PUtil.getProcessingTime(baseProcessingTime, inputPotionItemStack, PUtil.createPotionItemStack(outputPotion, potionType), nonPotionIngredients.length);
        brewingCauldronRecipe(recipeConsumer, experience, processingTime, advancementNameIngredient, outputPotion, potionType, allIngredients);
    }

    /*
     * This method creates a recipe for a potion modifier that applies to all potion containers. This includes potion, splash potion, and lingering potion.
     */
    private void brewingCauldronPotionModifierForAllContainers(Consumer<FinishedRecipe> recipeConsumer, float experience, int processingTime, String advancementNameIngredient, Potion inputPotion, Potion outputPotion, Ingredient... nonPotionIngredients) {
        // The below calls handle all (container -> same container type) potion recipes
        // Container transformation recipes (potion -> splash... etc) are handled in the runtime recipe generation in RecipeManagerMixin.java
        brewingCauldronRecipe(recipeConsumer, experience, processingTime, advancementNameIngredient, inputPotion, outputPotion, PUtil.PotionType.POTION, nonPotionIngredients);
        brewingCauldronRecipe(recipeConsumer, experience, processingTime, advancementNameIngredient, inputPotion, outputPotion, PUtil.PotionType.SPLASH_POTION, nonPotionIngredients);
        brewingCauldronRecipe(recipeConsumer, experience, processingTime, advancementNameIngredient, inputPotion, outputPotion, PUtil.PotionType.LINGERING_POTION, nonPotionIngredients);
    }
}
