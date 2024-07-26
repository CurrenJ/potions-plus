package grill24.potionsplus.recipe;

import com.google.gson.JsonObject;
import grill24.potionsplus.core.Recipes;
import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipe;
import grill24.potionsplus.utility.PUtil;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class ShapelessProcessingRecipe implements Recipe<Container> {
    protected final ResourceLocation id;
    protected final String group;
    protected final ItemStack result;
    protected final Ingredient[] ingredients;
    protected final int processingTime;

    public ShapelessProcessingRecipe(ResourceLocation resourceLocation, String group, Ingredient[] ingredients, ItemStack itemStack, int processingTime) {
        this.id = resourceLocation;
        this.group = group;
        this.ingredients = ingredients;
        this.result = itemStack;
        this.processingTime = processingTime;
    }

    @Override
    public boolean matches(Container container, Level level) {
        boolean hasAllIngredients = true;
        for (Ingredient ingredient : this.ingredients) {
            boolean hasIngredient = false;
            for (int i = 0; i < container.getContainerSize(); i++) {
                ItemStack itemStack = container.getItem(i);
                if (PUtil.isSameItemOrPotion(itemStack, ingredient.getItems()[0])) {
                    hasIngredient = true;
                    break;
                }
            }
            if (!hasIngredient) {
                hasAllIngredients = false;
                break;
            }
        }
        return hasAllIngredients;
    }

    @Override
    public ItemStack assemble(Container container) {
        return this.result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int p_43999_, int p_44000_) {
        return true;
    }

    @Override
    public ItemStack getResultItem() {
        return this.result;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    public int getProcessingTime() {
        return this.processingTime;
    }

    public @NotNull NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> nonnulllist = NonNullList.create();
        nonnulllist.addAll(List.of(this.ingredients));
        return nonnulllist;
    }
}
