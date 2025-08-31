package grill24.potionsplus.effect;

import grill24.potionsplus.core.Translations;
import grill24.potionsplus.event.AnimatedItemTooltipEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public class DizzySpellsEffect extends MobEffect implements IEffectTooltipDetails {
    public DizzySpellsEffect(MobEffectCategory mobEffectCategory, int color) {
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
        if (livingEntity instanceof ServerPlayer player) {
            // Random chance to cause a dizzy spell
            if (livingEntity.getRandom().nextFloat() < getDizzyChance(amplifier)) {
                // Random rotation amount
                float yawChange = (livingEntity.getRandom().nextFloat() - 0.5f) * getMaxRotation(amplifier);
                float pitchChange = (livingEntity.getRandom().nextFloat() - 0.5f) * (getMaxRotation(amplifier) * 0.5f);
                
                // Send rotation packet to client - this would need to be implemented
                // For now, we'll apply it directly to the player
                float newYaw = player.getYRot() + yawChange;
                float newPitch = Math.max(-90, Math.min(90, player.getXRot() + pitchChange));
                
                player.setYRot(newYaw);
                player.setXRot(newPitch);
                player.setYHeadRot(newYaw);
                
                // Sync to client
                player.connection.teleport(player.getX(), player.getY(), player.getZ(), newYaw, newPitch);
            }
        }
        return true;
    }

    public int getTickInterval(int amplifier) {
        // How often dizzy spells can occur
        switch (amplifier) {
            case 0: return 120; // Every 6 seconds
            case 1: return 80;  // Every 4 seconds
            case 2: return 60;  // Every 3 seconds
            default: return 40; // Every 2 seconds
        }
    }
    
    public float getDizzyChance(int amplifier) {
        // Chance of dizzy spell occurring on each interval
        switch (amplifier) {
            case 0: return 0.3f; // 30% chance
            case 1: return 0.5f; // 50% chance
            case 2: return 0.7f; // 70% chance
            default: return 0.9f; // 90% chance
        }
    }
    
    public float getMaxRotation(int amplifier) {
        // Maximum rotation in degrees
        switch (amplifier) {
            case 0: return 30.0f;  // Small dizzy spells
            case 1: return 60.0f;  // Medium dizzy spells
            case 2: return 120.0f; // Large dizzy spells
            default: return 180.0f; // Extreme dizzy spells
        }
    }

    @Override
    public AnimatedItemTooltipEvent.TooltipLines getTooltipDetails(MobEffectInstance effectInstance) {
        float chance = getDizzyChance(effectInstance.getAmplifier()) * 100;
        float interval = getTickInterval(effectInstance.getAmplifier()) / 20.0f;
        float maxRot = getMaxRotation(effectInstance.getAmplifier());
        
        Component chanceComponent = Component.literal(String.format("%.0f%%", chance)).withStyle(ChatFormatting.RED);
        Component intervalComponent = Component.literal(String.format("%.1fs", interval)).withStyle(ChatFormatting.YELLOW);
        Component rotationComponent = Component.literal(String.format("%.0f°", maxRot)).withStyle(ChatFormatting.GOLD);
        
        List<Component> text = List.of(
                Component.translatable("effect.potionsplus.dizzy_spells.tooltip_1").withStyle(ChatFormatting.LIGHT_PURPLE),
                chanceComponent,
                Component.translatable("effect.potionsplus.dizzy_spells.tooltip_2").withStyle(ChatFormatting.LIGHT_PURPLE),
                intervalComponent,
                Component.translatable("effect.potionsplus.dizzy_spells.tooltip_3").withStyle(ChatFormatting.LIGHT_PURPLE),
                rotationComponent,
                Component.translatable("effect.potionsplus.dizzy_spells.tooltip_4").withStyle(ChatFormatting.LIGHT_PURPLE));
        
        return createTooltipLine(text);
    }
}