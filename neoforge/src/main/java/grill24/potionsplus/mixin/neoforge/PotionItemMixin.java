package grill24.potionsplus.mixin.neoforge;

import grill24.potionsplus.persistence.SavedData;
import grill24.potionsplus.platform.Platform;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.level.Level;
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
        int drinkTime = Platform.getPotionDrinkTimeTicks();
        info.setReturnValue(drinkTime);
    }

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void use(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        int cooldownTime = Platform.getPotionDrinkCooldownTimeTicks();
        long lastUseTime = SavedData.instance.getLastPotionUseTime(player);
        if (lastUseTime != -1 && (level.getGameTime() - lastUseTime) < cooldownTime) {
            cir.setReturnValue(InteractionResultHolder.fail(player.getItemInHand(hand)));
        }
    }

    @Inject(method = "finishUsingItem", at = @At("HEAD"))
    private void finishUsingItem(ItemStack stack, Level level, LivingEntity entityLiving, CallbackInfoReturnable<ItemStack> cir) {
        if (entityLiving instanceof Player player) {
            SavedData.instance.setLastPotionUseTime(player, level.getGameTime());
        }
    }
}
