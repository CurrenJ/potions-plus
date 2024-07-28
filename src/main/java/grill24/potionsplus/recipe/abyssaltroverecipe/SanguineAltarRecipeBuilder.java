package grill24.potionsplus.recipe.abyssaltroverecipe;

import grill24.potionsplus.core.Recipes;
import grill24.potionsplus.recipe.ShapelessProcessingRecipeBuilder;
import grill24.potionsplus.utility.ModInfo;
import grill24.potionsplus.utility.PUtil;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;

public class SanguineAltarRecipeBuilder extends ShapelessProcessingRecipeBuilder<SanguineAltarRecipe, SanguineAltarRecipeBuilder> {
    @Override
    public SanguineAltarRecipe build(ResourceLocation resourceLocation) {
        return new SanguineAltarRecipe(resourceLocation, group, ingredients, result, processingTime);
    }

    public SanguineAltarRecipe build() {
        StringBuilder name = new StringBuilder();
        for (Ingredient ingredient : ingredients) {
            name.append(PUtil.getNameOrVerbosePotionName(ingredient.getItems()[0])).append("_");
        }
        name.append("to_");
        name.append(PUtil.getNameOrVerbosePotionName(result));

        return new SanguineAltarRecipe(new ResourceLocation(ModInfo.MOD_ID, name.toString()), group, ingredients, result, processingTime);
    }

    @Override
    protected SanguineAltarRecipeBuilder self() {
        return this;
    }

    @Override
    protected FinishedRecipe getFinishedRecipe(ResourceLocation resourceLocation) {
        return new Result<>(
                resourceLocation,
                new ResourceLocation(resourceLocation.getNamespace(), "recipes/" + this.result.getItem().getItemCategory().getRecipeFolderName() + "/" + resourceLocation.getPath()),
                this,
                Recipes.SANGUINE_ALTAR_RECIPE_SERIALIZER.get()
        );
    }
}
