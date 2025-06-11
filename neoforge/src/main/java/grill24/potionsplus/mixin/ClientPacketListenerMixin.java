package grill24.potionsplus.mixin;

import grill24.potionsplus.core.items.HatItems;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin {
    @Inject(method = "findTotem", at = @At("TAIL"), cancellable = true)
    private static void findTotem(Player player, CallbackInfoReturnable<ItemStack> cir) {
        if (player.getItemBySlot(EquipmentSlot.HEAD).is(HatItems.WREATH.value())) {
            cir.setReturnValue(new ItemStack(HatItems.WREATH.value()));
        }
    }
}
