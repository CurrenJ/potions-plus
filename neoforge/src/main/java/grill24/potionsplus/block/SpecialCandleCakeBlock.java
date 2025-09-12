package grill24.potionsplus.block;

import grill24.potionsplus.entity.PrimedSpecialCake;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CandleCakeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class SpecialCandleCakeBlock extends CandleCakeBlock {
    
    public SpecialCandleCakeBlock(Properties properties) {
        super(Blocks.CANDLE, properties);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack item, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        // Check if player is using flint and steel or fire charge to light the candle
        if (item.is(Items.FLINT_AND_STEEL) || item.is(Items.FIRE_CHARGE)) {
            if (!state.getValue(LIT)) {
                if (!level.isClientSide) {
                    // Instead of lighting the candle, explode!
                    explodeAsCake(level, pos);
                    
                    // Damage the flint and steel if it's not in creative mode
                    if (item.is(Items.FLINT_AND_STEEL) && !player.getAbilities().instabuild) {
                        item.hurtAndBreak(1, player, player.getEquipmentSlotForItem(item));
                    }
                }
                return level.isClientSide ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
            }
        }
        
        return super.useItemOn(item, state, level, pos, player, hand, hitResult);
    }
    
    private void explodeAsCake(Level level, BlockPos pos) {
        if (!level.isClientSide) {
            // Remove the block
            level.removeBlock(pos, false);
            
            // Create and spawn the primed special cake entity
            PrimedSpecialCake primedCake = new PrimedSpecialCake(level, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, null);
            level.addFreshEntity(primedCake);
            
            level.playSound(null, pos, SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
    }
}