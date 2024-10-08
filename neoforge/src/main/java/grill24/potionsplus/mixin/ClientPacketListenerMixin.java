package grill24.potionsplus.mixin;

import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin {
    @Inject(method = "findTotem", at = @At("TAIL"), cancellable = true)
    private static void findTotem(Player player, CallbackInfoReturnable<ItemStack> cir) {
        for (ItemStack itemStack : player.getArmorSlots()) {
            if(itemStack.is(grill24.potionsplus.core.Items.WREATH.value())) {
                cir.setReturnValue(new ItemStack(grill24.potionsplus.core.Items.WREATH.value()));
            }
        }
    }
}
