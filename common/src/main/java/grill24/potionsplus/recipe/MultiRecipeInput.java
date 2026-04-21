package grill24.potionsplus.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

import java.util.List;

public record MultiRecipeInput(List<ItemStack> inputs) implements RecipeInput {
    @Override
    public ItemStack getItem(int index) {
        if (index < 0 || index >= inputs.size()) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + inputs.size());
        }
        return inputs.get(index);
    }

    @Override
    public int size() {
        return inputs.size();
    }
}
