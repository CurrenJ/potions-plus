package grill24.potionsplus.effect;

import grill24.potionsplus.core.Translations;
import grill24.potionsplus.event.AnimatedItemTooltipEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MidasTouchEffect extends MobEffect implements IEffectTooltipDetails {
    // Keep track of transformed blocks and their original states
    public static final Map<BlockPos, ScheduledBlockRestore> transformedBlocks = new HashMap<>();
    
    public static class ScheduledBlockRestore {
        public final BlockState originalState;
        public final long restoreTime;
        
        public ScheduledBlockRestore(BlockState originalState, long restoreTime) {
            this.originalState = originalState;
            this.restoreTime = restoreTime;
        }
    }
    
    public MidasTouchEffect(MobEffectCategory mobEffectCategory, int color) {
        super(mobEffectCategory, color);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        // Check every second for block restoration
        return duration % 20 == 0;
    }

    @Override
    public boolean applyEffectTick(ServerLevel serverLevel, LivingEntity livingEntity, int amplifier) {
        // Check for blocks that need to be restored
        long currentTime = serverLevel.getGameTime();
        transformedBlocks.entrySet().removeIf(entry -> {
            if (currentTime >= entry.getValue().restoreTime) {
                BlockPos pos = entry.getKey();
                if (serverLevel.getBlockState(pos).is(Blocks.GOLD_BLOCK)) {
                    serverLevel.setBlock(pos, entry.getValue().originalState, 3);
                    serverLevel.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ITEM_BREAK, SoundSource.BLOCKS, 0.5F, 1.2F);
                }
                return true;
            }
            return false;
        });
        
        return true;
    }
    
    public static boolean canTransformBlock(BlockState blockState) {
        // Only transform certain block types to avoid breaking important blocks
        return blockState.is(Blocks.STONE) || 
               blockState.is(Blocks.COBBLESTONE) ||
               blockState.is(Blocks.DIRT) ||
               blockState.is(Blocks.GRASS_BLOCK) ||
               blockState.is(Blocks.SAND) ||
               blockState.is(Blocks.GRAVEL) ||
               blockState.is(Blocks.CLAY) ||
               blockState.is(Blocks.SANDSTONE) ||
               blockState.is(Blocks.DIORITE) ||
               blockState.is(Blocks.GRANITE) ||
               blockState.is(Blocks.ANDESITE);
    }
    
    public static void transformBlock(ServerLevel level, BlockPos pos, BlockState originalState, int amplifier) {
        if (canTransformBlock(originalState) && !transformedBlocks.containsKey(pos)) {
            level.setBlock(pos, Blocks.GOLD_BLOCK.defaultBlockState(), 3);
            level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.8F, 1.5F);
            
            // Schedule restoration based on amplifier
            long restoreDelay = getTransformDuration(amplifier);
            transformedBlocks.put(pos, new ScheduledBlockRestore(originalState, level.getGameTime() + restoreDelay));
        }
    }
    
    public static int getTransformDuration(int amplifier) {
        // How long blocks stay as gold (in ticks)
        switch (amplifier) {
            case 0: return 200;  // 10 seconds
            case 1: return 400;  // 20 seconds
            case 2: return 600;  // 30 seconds
            default: return 800; // 40 seconds
        }
    }
    
    public static int getTransformRadius(int amplifier) {
        // Radius around touched block to transform
        return Math.min(1 + amplifier, 3);
    }

    @Override
    public AnimatedItemTooltipEvent.TooltipLines getTooltipDetails(MobEffectInstance effectInstance) {
        int duration = getTransformDuration(effectInstance.getAmplifier()) / 20;
        int radius = getTransformRadius(effectInstance.getAmplifier());
        
        Component durationComponent = Component.literal(String.valueOf(duration) + "s").withStyle(ChatFormatting.YELLOW);
        Component radiusComponent = Component.literal(String.valueOf(radius)).withStyle(ChatFormatting.GOLD);
        
        List<Component> text = List.of(
                Component.translatable("effect.potionsplus.midas_touch.tooltip_1").withStyle(ChatFormatting.LIGHT_PURPLE),
                radiusComponent,
                Component.translatable("effect.potionsplus.midas_touch.tooltip_2").withStyle(ChatFormatting.LIGHT_PURPLE),
                durationComponent,
                Component.translatable("effect.potionsplus.midas_touch.tooltip_3").withStyle(ChatFormatting.LIGHT_PURPLE));
        
        return createTooltipLine(text);
    }
}