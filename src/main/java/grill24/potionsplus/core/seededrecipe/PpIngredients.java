package grill24.potionsplus.core.seededrecipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class PpIngredients {
    public Ingredient[] ingredients;
    public PpIngredients(Ingredient[] ingredients) {
        this.ingredients = ingredients;
    }

    public PpIngredients(Ingredient ingredient) {
        this.ingredients = new Ingredient[]{ingredient};
    }

    public PpIngredients(ItemStack itemStack) {
        this.ingredients = new Ingredient[]{Ingredient.of(itemStack)};
    }

    // Hashcode and equals methods
    @Override
    public int hashCode() {
        StringBuilder result = new StringBuilder();
        for (Ingredient ingredient : ingredients) {
            result.append(ingredient.toJson().toString());
        }
        return result.toString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof PpIngredients)) {
            return false;
        }
        PpIngredients other = (PpIngredients) obj;
        if (ingredients.length != other.ingredients.length) {
            return false;
        }
        for (int i = 0; i < ingredients.length; i++) {
            if (!ingredients[i].toJson().equals(other.ingredients[i].toJson())) {
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
            result.append(ingredient.getItems()[0]);
        }
        result.append(" ]");
        return result.toString();
    }

    public boolean contains(Ingredient ingredient) {
        for (Ingredient i : ingredients) {
            if (i.toJson().equals(ingredient.toJson())) {
                return true;
            }
        }
        return false;
    }
}
