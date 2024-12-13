package grill24.potionsplus.mixin;

import grill24.potionsplus.core.Advancements;
import grill24.potionsplus.core.Blocks;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.server.Bootstrap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
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
        CauldronInteraction.WATER.map().put(Items.BLAZE_POWDER, (blockState, level, blockPos, player, interactionHand, stack) -> potions_plus$convert(player, interactionHand, blockPos, level, stack));
        CauldronInteraction.EMPTY.map().put(Items.BLAZE_POWDER, (blockState, level, blockPos, player, interactionHand, stack) -> potions_plus$convert(player, interactionHand, blockPos, level, stack));
    }

    @Unique
    private static ItemInteractionResult potions_plus$convert(Player player, InteractionHand interactionHand, BlockPos blockPos, Level level, ItemStack stack) {
        player.getItemInHand(interactionHand).shrink(1);
        level.setBlockAndUpdate(blockPos, Blocks.BREWING_CAULDRON.value().defaultBlockState());
        level.playSound(player, blockPos, SoundEvents.MOOSHROOM_CONVERT, player.getSoundSource(), 1.0F, 1.0F);
        if (player instanceof ServerPlayer serverplayer) {
            Advancements.BREWING_CAULDRON_CREATION.value().trigger(serverplayer, Blocks.BREWING_CAULDRON.value().defaultBlockState());
        }
        return ItemInteractionResult.CONSUME;
    }
}
