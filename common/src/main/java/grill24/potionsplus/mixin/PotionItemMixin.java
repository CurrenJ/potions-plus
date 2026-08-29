package grill24.potionsplus.mixin;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PotionItem.class)
public abstract class PotionItemMixin extends Item {
    public PotionItemMixin(Properties properties) {
        super(properties);
    }

    /**
     * {@link PotionItem#getName(ItemStack)} derives the name from the potion contents and only falls back to
     * {@link net.minecraft.core.component.DataComponents#ITEM_NAME} when the stack has no potion contents at all.
     * Potions brewed by this mod carry custom effects with no linked potion, so vanilla would name them
     * "item.minecraft.potion.effect.empty". Honour an explicitly set item name instead - the default component
     * every item is registered with is left to vanilla's handling.
     */
    @Inject(method = "getName", at = @At("HEAD"), cancellable = true)
    private void potions_plus$useExplicitItemName(ItemStack itemStack, CallbackInfoReturnable<Component> cir) {
        if (itemStack.hasNonDefault(DataComponents.ITEM_NAME)) {
            cir.setReturnValue(itemStack.get(DataComponents.ITEM_NAME));
        }
    }
}
