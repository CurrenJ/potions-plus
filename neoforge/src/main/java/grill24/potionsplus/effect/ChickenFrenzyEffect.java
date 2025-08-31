package grill24.potionsplus.effect;

import grill24.potionsplus.core.Translations;
import grill24.potionsplus.event.AnimatedItemTooltipEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;

import java.util.List;

public class ChickenFrenzyEffect extends MobEffect implements IEffectTooltipDetails {
    public ChickenFrenzyEffect(MobEffectCategory mobEffectCategory, int color) {
        super(mobEffectCategory, color);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        int j = getTickInterval(amplifier);
        if (j > 0) {
            return duration % j == 0;
        } else {
            return true;
        }
    }

    @Override
    public boolean applyEffectTick(ServerLevel serverLevel, LivingEntity livingEntity, int amplifier) {
        if (livingEntity.level().isClientSide()) {
            return true;
        }

        // Spawn chickens around the player
        int spawnCount = Math.min(1 + amplifier, 3); // Max 3 chickens per tick
        
        for (int i = 0; i < spawnCount; i++) {
            // Find a random position around the player
            double range = 3.0 + amplifier * 2.0;
            double x = livingEntity.getX() + (livingEntity.getRandom().nextDouble() - 0.5) * range;
            double z = livingEntity.getZ() + (livingEntity.getRandom().nextDouble() - 0.5) * range;
            double y = livingEntity.getY();
            
            BlockPos spawnPos = new BlockPos((int) x, (int) y, (int) z);
            
            // Make sure there's a solid block below and air above
            if (serverLevel.getBlockState(spawnPos.below()).isSolid() && 
                serverLevel.getBlockState(spawnPos).isAir() && 
                serverLevel.getBlockState(spawnPos.above()).isAir()) {
                
                Chicken chicken = new Chicken(EntityType.CHICKEN, serverLevel);
                if (chicken != null) {
                    chicken.setPos(x, y, z);
                    
                    serverLevel.addFreshEntity(chicken);
                    
                    serverLevel.addFreshEntity(chicken);
                    
                    // Play chicken sound
                    serverLevel.playSound(null, spawnPos, SoundEvents.CHICKEN_AMBIENT, 
                                        SoundSource.NEUTRAL, 0.6F, 
                                        1.0F + (livingEntity.getRandom().nextFloat() - 0.5F) * 0.4F);
                }
            }
        }
        
        return true;
    }

    public int getTickInterval(int amplifier) {
        // Spawn chickens every 3-5 seconds depending on amplifier
        switch (amplifier) {
            case 0: return 100; // 5 seconds
            case 1: return 80;  // 4 seconds
            case 2: return 60;  // 3 seconds
            default: return 40; // 2 seconds
        }
    }

    @Override
    public AnimatedItemTooltipEvent.TooltipLines getTooltipDetails(MobEffectInstance effectInstance) {
        int interval = getTickInterval(effectInstance.getAmplifier());
        float seconds = interval / 20.0F;
        
        Component intervalComponent = Component.literal(String.format("%.1f", seconds)).withStyle(ChatFormatting.YELLOW);
        Component spawnCountComponent = Component.literal(String.valueOf(Math.min(1 + effectInstance.getAmplifier(), 3))).withStyle(ChatFormatting.GREEN);
        
        List<Component> text = List.of(
                Component.translatable("effect.potionsplus.chicken_frenzy.tooltip_1").withStyle(ChatFormatting.LIGHT_PURPLE),
                spawnCountComponent,
                Component.translatable("effect.potionsplus.chicken_frenzy.tooltip_2").withStyle(ChatFormatting.LIGHT_PURPLE),
                intervalComponent,
                Component.translatable("effect.potionsplus.chicken_frenzy.tooltip_3").withStyle(ChatFormatting.LIGHT_PURPLE));
        
        return createTooltipLine(text);
    }
}