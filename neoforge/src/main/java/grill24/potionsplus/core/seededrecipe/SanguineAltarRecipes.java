package grill24.potionsplus.core.seededrecipe;

import grill24.potionsplus.core.PotionsPlus;
import grill24.potionsplus.core.Recipes;
import grill24.potionsplus.recipe.abyssaltroverecipe.SanguineAltarRecipe;
import grill24.potionsplus.recipe.abyssaltroverecipe.SanguineAltarRecipeBuilder;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.*;

public class SanguineAltarRecipes {

    // ----- Sangune Altar Recipes -----

    public static List<RecipeHolder<?>> generateAllSanguineAltarRecipes(long seed) {
        List<RecipeHolder<?>> sanguineAltarRecipes = new ArrayList<>();

        Random random = new Random(seed);
        final int processingTime = 100;

        // TODO: Remove tier
        Set<PpIngredient>[] tieredIngredients = new Set[1];
        tieredIngredients[0] = new HashSet<>(Recipes.ALL_BCR_RECIPES_ANALYSIS.getUniqueIngredients());

        for (Set<PpIngredient> ingredients : tieredIngredients) {
            List<PpIngredient> tierIngredients = new ArrayList<>(ingredients);
            PpIngredient firstIngredient = null;
            PpIngredient lastIngredient = null;

            while (!tierIngredients.isEmpty()) {
                PpIngredient nextIngredient = tierIngredients.remove(random.nextInt(tierIngredients.size()));
                if (lastIngredient != null) {
                    SanguineAltarRecipeBuilder builder = new SanguineAltarRecipeBuilder().ingredients(lastIngredient).result(nextIngredient.getItemStack()).processingTime(processingTime);
                    sanguineAltarRecipes.add(builder.build());
                } else {
                    firstIngredient = nextIngredient;
                }
                lastIngredient = nextIngredient;
            }

            if (firstIngredient != null) {
                SanguineAltarRecipeBuilder builder = new SanguineAltarRecipeBuilder().ingredients(lastIngredient).result(firstIngredient.getItemStack()).processingTime(processingTime);
                sanguineAltarRecipes.add(builder.build());
            }
        }

        return sanguineAltarRecipes;
    }

}
