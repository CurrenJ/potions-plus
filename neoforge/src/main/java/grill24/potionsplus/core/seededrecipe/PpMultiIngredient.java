package grill24.potionsplus.core.seededrecipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Arrays;

public class PpMultiIngredient extends PpIngredient {
    protected PpMultiIngredient(Ingredient[] ingredients) {
        super(ingredients);
    }

    public static PpMultiIngredient of(ItemStack... stacks) {
        return new PpMultiIngredient(Arrays.stream(stacks).map(Ingredient::of).toArray(Ingredient[]::new));
    }

    public static PpMultiIngredient of(Ingredient... ingredients) {
        return new PpMultiIngredient(ingredients);
    }
}
