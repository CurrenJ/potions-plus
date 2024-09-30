package grill24.potionsplus.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import grill24.potionsplus.utility.PUtil;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public abstract class ShapelessProcessingRecipe implements Recipe<RecipeInput> {
    protected final RecipeCategory category;
    protected final String group;
    protected final ItemStack result;
    protected final Ingredient[] ingredients;
    protected final int processingTime;

    public ShapelessProcessingRecipe(RecipeCategory category, String group, Ingredient[] ingredients, ItemStack result, int processingTime) {
        this.category = category;
        this.group = group;
        this.ingredients = ingredients;
        this.result = result;
        this.processingTime = processingTime;
    }

    @Override
    public boolean matches(RecipeInput recipeInput, Level level) {
        boolean hasAllIngredients = true;
        for (Ingredient ingredient : this.ingredients) {
            boolean hasIngredient = false;
            for (int i = 0; i < recipeInput.size(); i++) {
                ItemStack itemStack = recipeInput.getItem(i);
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
    public ItemStack assemble(RecipeInput container, HolderLookup.Provider registryAccess) {
        return this.result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int p_43999_, int p_44000_) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider access) {
        return this.result.copy();
    }

    public ItemStack getResultItem() {
        return this.result.copy();
    }

    public int getProcessingTime() {
        return this.processingTime;
    }

    public @NotNull NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> nonnulllist = NonNullList.create();
        nonnulllist.addAll(List.of(this.ingredients));
        return nonnulllist;
    }

    public RecipeCategory getCategory() {
        return this.category;
    }
}
