package grill24.potionsplus.event;

import grill24.potionsplus.core.potion.MobEffects;
import grill24.potionsplus.effect.MidasTouchEffect;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;

@EventBusSubscriber(modid = ModInfo.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class MidasTouchEventHandler {
    
    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (event.getPlayer() instanceof Player player && player.hasEffect(MobEffects.MIDAS_TOUCH)) {
            handleMidasTouch(player, event.getPos(), event.getState());
        }
    }
    
    @SubscribeEvent  
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (event.getEntity() instanceof Player player && player.hasEffect(MobEffects.MIDAS_TOUCH)) {
            handleMidasTouch(player, event.getPos(), event.getPlacedBlock());
        }
    }
    
    private static void handleMidasTouch(Player player, BlockPos centerPos, BlockState centerState) {
        if (player.level() instanceof ServerLevel serverLevel) {
            int amplifier = player.getEffect(MobEffects.MIDAS_TOUCH).getAmplifier();
            int radius = MidasTouchEffect.getTransformRadius(amplifier);
            
            // Transform blocks in radius around the interacted block
            for (int x = -radius; x <= radius; x++) {
                for (int y = -radius; y <= radius; y++) {
                    for (int z = -radius; z <= radius; z++) {
                        BlockPos pos = centerPos.offset(x, y, z);
                        BlockState state = serverLevel.getBlockState(pos);
                        
                        if (MidasTouchEffect.canTransformBlock(state)) {
                            MidasTouchEffect.transformBlock(serverLevel, pos, state, amplifier);
                        }
                    }
                }
            }
        }
    }
}