package grill24.potionsplus.mixin;

import grill24.potionsplus.core.Blocks;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.server.Bootstrap;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Bootstrap.class)
public abstract class BootstrapMixin {

    @Inject(method = "bootStrap()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/cauldron/CauldronInteraction;bootStrap()V"))
    private static void bootStrap(CallbackInfo ci) {
        potions_plus$bootStrapCauldronInteractions();
    }

    @Unique
    private static void potions_plus$bootStrapCauldronInteractions() {
        // Add custom cauldron interactions here
        CauldronInteraction.WATER.put(Items.BLAZE_POWDER, (blockState, level, blockPos, player, interactionHand, stack) -> {
            player.getItemInHand(interactionHand).shrink(1);
            level.setBlockAndUpdate(blockPos, Blocks.BREWING_CAULDRON.get().defaultBlockState());
            level.playSound(player, blockPos, SoundEvents.MOOSHROOM_CONVERT, player.getSoundSource(), 1.0F, 1.0F);
            return InteractionResult.CONSUME;
        });
    }
}
