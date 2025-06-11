package grill24.potionsplus.mixin;

import grill24.potionsplus.config.PotionsPlusConfig;
import grill24.potionsplus.core.DataAttachments;
import grill24.potionsplus.effect.LastPotionUsePlayerData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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

    // TODO: Set use duration through Consume data component. Do this by modifying output of BrewingCauldron
//    @Inject(method = "getUseDuration", at = @At("RETURN"), cancellable = true)
//    private void getUseDuration(CallbackInfoReturnable<Integer> info) {
//        int drinkTime = PotionsPlusConfig.CONFIG.potionDrinkTimeTicks.getAsInt();
//        info.setReturnValue(drinkTime);
//    }

//    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
//    private void use(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
//        int cooldownTime = PotionsPlusConfig.CONFIG.potionDrinkCooldownTimeTicks.getAsInt();
//        long lastUseTime = player.getData(DataAttachments.LAST_POTION_USE_PLAYER_DATA).timestamp();
//        if (lastUseTime != -1 && (level.getGameTime() - lastUseTime) < cooldownTime) {
//            cir.setReturnValue(InteractionResult.FAIL);
//        }
//    }

    // TODO: Set cooldown using Minecraft's data component for it. Do this by modifying output of BrewingCauldron
//    @Inject(method = "finishUsingItem", at = @At("HEAD"))
//    private void finishUsingItem(ItemStack stack, Level level, LivingEntity entityLiving, CallbackInfoReturnable<ItemStack> cir) {
//        entityLiving.setData(DataAttachments.LAST_POTION_USE_PLAYER_DATA, new LastPotionUsePlayerData(level.getGameTime()));
//    }
}
