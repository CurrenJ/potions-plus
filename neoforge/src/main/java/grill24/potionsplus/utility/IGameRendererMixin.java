package grill24.potionsplus.utility;

import net.minecraft.world.item.ItemStack;

public interface IGameRendererMixin {
    void potions_plus$displayItemActivation(ItemStack stack, ItemActivationAnimationEnum animation);
}
