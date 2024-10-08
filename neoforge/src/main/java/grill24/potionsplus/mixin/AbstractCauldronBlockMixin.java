package grill24.potionsplus.mixin;

import grill24.potionsplus.core.Blocks;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(AbstractCauldronBlock.class)
public abstract class AbstractCauldronBlockMixin extends Block {
    public AbstractCauldronBlockMixin(Properties properties) {
        super(properties);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(Properties properties, CauldronInteraction.InteractionMap interactions, CallbackInfo ci) {
        CauldronInteraction.EMPTY.map().put(Items.CAULDRON, (blockState, level, blockPos, player, interactionHand, stack) -> {
            player.getItemInHand(interactionHand).shrink(1);
            level.setBlockAndUpdate(blockPos, Blocks.BREWING_CAULDRON.value().defaultBlockState());
            level.playSound(player, blockPos, SoundEvents.MOOSHROOM_CONVERT, player.getSoundSource(), 1.0F, 1.0F);
            return ItemInteractionResult.CONSUME;
        });
    }
}
