package grill24.potionsplus.core.seededrecipe;

import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import grill24.potionsplus.utility.PUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Arrays;

public class PpIngredient {
    public Ingredient[] ingredients;

    protected PpIngredient(Ingredient[] ingredients) {
        this.ingredients = ingredients;
    }

    // Hashcode and equals methods
    @Override
    public int hashCode() {
        StringBuilder result = new StringBuilder();
        for (Ingredient ingredient : ingredients) {
            result.append(PUtil.getNameOrVerbosePotionName(ingredient.getItems()[0]));
        }
        return result.toString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof PpIngredient)) {
            return false;
        }
        PpIngredient other = (PpIngredient) obj;
        if (ingredients.length != other.ingredients.length) {
            return false;
        }
        for (int i = 0; i < ingredients.length; i++) {
            if (!toJsonOrThrow(ingredients[i]).equals(toJsonOrThrow(other.ingredients[i]))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("[");
        for (Ingredient ingredient : ingredients) {
            result.append(" ");
            result.append(PUtil.getNameOrVerbosePotionName(ingredient.getItems()[0]));
        }
        result.append(" ]");
        return result.toString();
    }

    public boolean contains(Ingredient ingredient) {
        for (Ingredient i : ingredients) {
            if (toJsonOrThrow(i).equals(toJsonOrThrow(ingredient))) {
                return true;
            }
        }
        return false;
    }

    private static JsonElement toJsonOrThrow(Ingredient ingredient) {
        DataResult<JsonElement> result = Ingredient.CODEC.encodeStart(JsonOps.INSTANCE, ingredient);
        return result.result().orElseThrow();
    }

    public ItemStack getItemStack() {
        assert ingredients.length == 1 : "Tried to get item stack, but there are multiple.";
        return ingredients[0].getItems()[0];
    }

    public static PpIngredient of(ItemStack... stacks) {
        return new PpIngredient(Arrays.stream(stacks).map(Ingredient::of).toArray(Ingredient[]::new));
    }

    public static PpIngredient of(Ingredient... ingredients) {
        return new PpIngredient(ingredients);
    }
}
