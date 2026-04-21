package grill24.potionsplus.mixin;

import grill24.potionsplus.event.ServerPlayerHeldItemChangedEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Inventory.class)
public abstract class InventoryMixin {
    @Shadow
    public int selected;
    @Shadow
    @Final
    public Player player;

    @Shadow
    public abstract ItemStack getItem(int index);

    @Unique
    private int potions_plus$lastSelectedSlot = -1;
    @Unique
    private ItemStack potions_plus$lastHeldItem = ItemStack.EMPTY;

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        int selectedSlot = this.selected;
        ItemStack heldItem = this.getItem(selectedSlot);

        if (selectedSlot != potions_plus$lastSelectedSlot || !ItemStack.isSameItemSameComponents(potions_plus$lastHeldItem, heldItem)) {
            if (this.player instanceof ServerPlayer serverPlayer) {
                NeoForge.EVENT_BUS.post(new ServerPlayerHeldItemChangedEvent(serverPlayer.getServer(), serverPlayer, potions_plus$lastHeldItem, heldItem));
            }

            potions_plus$lastSelectedSlot = selectedSlot;
        }

        potions_plus$lastHeldItem = this.getItem(selectedSlot);
    }

}
