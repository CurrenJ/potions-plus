package grill24.potionsplus.utility;

import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;

import java.util.List;

public record ItemStacksTooltip(List<ItemStack> items, String recipeId) implements TooltipComponent {
}
