package grill24.potionsplus.data;

import grill24.potionsplus.core.Items;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.ShapedRecipeBuilder;

import java.util.function.Consumer;

public class RecipeProvider extends net.minecraft.data.recipes.RecipeProvider {
    public RecipeProvider(DataGenerator p_125973_) {
        super(p_125973_);
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
    }
}
