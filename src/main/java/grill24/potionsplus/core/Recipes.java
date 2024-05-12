package grill24.potionsplus.core;

import grill24.potionsplus.recipe.BrewingCauldronRecipe;
import grill24.potionsplus.recipe.BrewingCauldronRecipeSerializer;
import grill24.potionsplus.utility.ModInfo;
import grill24.potionsplus.utility.PUtil;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = ModInfo.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Recipes {
    // Recipe Serializers
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registry.RECIPE_SERIALIZER_REGISTRY, ModInfo.MOD_ID);
    public static final RegistryObject<BrewingCauldronRecipeSerializer<BrewingCauldronRecipe>> BREWING_CAULDRON_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register("brewing_cauldron_recipe", () -> new BrewingCauldronRecipeSerializer<>(BrewingCauldronRecipe::new, 200));


    // Recipe Types
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registry.RECIPE_TYPE_REGISTRY, ModInfo.MOD_ID);
    public static final RegistryObject<RecipeType<grill24.potionsplus.recipe.BrewingCauldronRecipe>> BREWING_CAULDRON_RECIPE = RECIPE_TYPES.register("brewing_cauldron_recipe", () -> new RecipeType<>() {
    });


    @SubscribeEvent
    public static void onLoadComplete(final FMLLoadCompleteEvent event) {

    }

    public static Map<ResourceLocation, Recipe<?>> getAdditionalRuntimeRecipes(RecipeType<?> recipeType) {
        if (recipeType == BREWING_CAULDRON_RECIPE.get()) {
            return getRuntimeBrewingRecipes();
        }
        return new HashMap<>();
    }

    private static Map<ResourceLocation, Recipe<?>> getRuntimeBrewingRecipes() {
        Map<ResourceLocation, Recipe<?>> recipes = new HashMap<>();

        for (PUtil.PotionType inputPotionContainer : PUtil.PotionType.values()) {
            ForgeRegistries.POTIONS.getValues().forEach(potion -> {
                ForgeRegistries.ITEMS.getValues().forEach(item -> {
                    ItemStack input = PUtil.createPotionItemStack(potion, inputPotionContainer);
                    ItemStack ingredient = new ItemStack(item);
                    ItemStack output = BrewingRecipeRegistry.getOutput(input, ingredient);

                    if (!output.isEmpty()) {
                        addRuntimeBrewingRecipes(recipes, input, output, ingredient);
                    }
                });
            });
        }
        return recipes;
    }

    private static void addRuntimeBrewingRecipes(Map<ResourceLocation, Recipe<?>> recipes, ItemStack input, ItemStack output, ItemStack ingredient) {
        Ingredient[] ingredients = new Ingredient[]{Ingredient.of(input), Ingredient.of(ingredient)};
        String resourceName = PUtil.getNameOrVerbosePotionName(input);
        resourceName += "_" + PUtil.getNameOrVerbosePotionName(ingredient);
        resourceName += "_" + PUtil.getNameOrVerbosePotionName(output);

        BrewingCauldronRecipe recipe = new BrewingCauldronRecipe(new ResourceLocation(ModInfo.MOD_ID, resourceName), "", ingredients, output, 0.1F, PUtil.getProcessingTime(100, input, output, 1));

        recipes.put(recipe.getId(), recipe);
    }

}
