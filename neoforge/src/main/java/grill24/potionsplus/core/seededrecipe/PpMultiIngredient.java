package grill24.potionsplus.core.seededrecipe;

import grill24.potionsplus.utility.PUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class PpMultiIngredient extends PpIngredient {
    protected PpMultiIngredient(Ingredient[] ingredients) {
        // Sort the ingredients by their name for consistent ordering
        super(Arrays.stream(ingredients).sorted(Comparator.comparing(a -> PUtil.getNameOrVerbosePotionName(a.getItems()[0]))).toArray(Ingredient[]::new));
    }

    public static PpMultiIngredient of(ItemStack... stacks) {
        return new PpMultiIngredient(Arrays.stream(stacks).map(Ingredient::of).toArray(Ingredient[]::new));
    }

    public static PpMultiIngredient of(List<ItemStack> stacks) {
        return PpMultiIngredient.of(stacks.stream().toArray(ItemStack[]::new));
    }

    public static PpMultiIngredient of(Ingredient... ingredients) {
        return new PpMultiIngredient(ingredients);
    }

    public List<PpIngredient> split() {
        return Arrays.asList(this.ingredients).stream().map(PpIngredient::new).toList();
    }

    public int size() {
        return this.ingredients.length;
    }
}
