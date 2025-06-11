package grill24.potionsplus.mixin;

import grill24.potionsplus.core.DataComponents;
import grill24.potionsplus.skill.reward.OwnerDataComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Consumable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Consumable.class)
public class ConsumableMixin {
    /**
     * Redirects the canEat method to check if the player is the owner of the choice item.
     * See {@link grill24.potionsplus.skill.reward.EdibleRewardGranterDataComponent} and {@link OwnerDataComponent}
     */
    @Inject(method = "canConsume", at = @At(value = "HEAD"), cancellable = true)
    private void potions_plus$canEat(LivingEntity entity, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (entity instanceof Player player) {
            if (stack.has(DataComponents.CHOICE_ITEM) && stack.has(DataComponents.OWNER)) {
                OwnerDataComponent ownerData = stack.get(DataComponents.OWNER);
                if (ownerData != null) {
                    cir.setReturnValue(ownerData.isOwner(player));
                }
            }
        }
    }
}
