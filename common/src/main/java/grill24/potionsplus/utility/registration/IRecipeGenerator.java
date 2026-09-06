package grill24.potionsplus.utility.registration;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;

public interface IRecipeGenerator<T> extends IDataGenerator<T> {
    void generate(RecipeProvider provider, RecipeOutput output);
}
