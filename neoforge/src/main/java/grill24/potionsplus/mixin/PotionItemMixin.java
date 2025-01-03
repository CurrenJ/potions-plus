package grill24.potionsplus.mixin;

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

    @Inject(method = "getUseDuration", at = @At("RETURN"), cancellable = true)
    private void getUseDuration(CallbackInfoReturnable<Integer> info) {
        info.setReturnValue(16);
    }
}
