package grill24.potionsplus.core.seededrecipe;

import grill24.potionsplus.utility.PUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class PpMultiIngredient extends PpIngredient {
    protected PpMultiIngredient(List<ItemStack> stacks) {
        // Sort the ingredients by their name for consistent ordering
        super(stacks.stream()
                .sorted(Comparator.comparing(PUtil::getNameOrVerbosePotionName))
                .toList());
    }

    public static PpMultiIngredient of(ItemStack... stacks) {
        return new PpMultiIngredient(Arrays.stream(stacks).toList());
    }

    public static PpMultiIngredient of(List<ItemStack> stacks) {
        return new PpMultiIngredient(stacks);
    }

    public List<PpIngredient> split() {
        return this.matchStacks.stream().map(PpIngredient::new).toList();
    }

    public int size() {
        return this.matchStacks.size();
    }
}
