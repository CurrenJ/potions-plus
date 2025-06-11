package grill24.potionsplus.utility.registration;

import grill24.potionsplus.data.RecipeProvider;
import net.minecraft.data.recipes.RecipeOutput;

public interface IRecipeGenerator<T> extends IDataGenerator<T> {
    void generate(RecipeProvider provider, RecipeOutput output);
}
