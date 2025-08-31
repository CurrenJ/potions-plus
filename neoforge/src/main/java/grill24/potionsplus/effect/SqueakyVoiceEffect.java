package grill24.potionsplus.effect;

import grill24.potionsplus.core.Translations;
import grill24.potionsplus.event.AnimatedItemTooltipEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.List;

public class SqueakyVoiceEffect extends MobEffect implements IEffectTooltipDetails {
    public SqueakyVoiceEffect(MobEffectCategory mobEffectCategory, int color) {
        super(mobEffectCategory, color);
    }

    // This effect's functionality will be handled through sound event listeners
    // that modify the pitch of player sounds
    
    public static float getPitchModifier(int amplifier) {
        switch (amplifier) {
            case 0: return 1.3f;  // Slightly higher pitch
            case 1: return 1.6f;  // Higher pitch
            case 2: return 2.0f;  // Very high pitch (squeaky)
            default: return 2.5f; // Extremely high pitch
        }
    }
    
    public static boolean shouldAffectSound(int amplifier) {
        switch (amplifier) {
            case 0: return true;  // Only player sounds
            case 1: return true;  // Player + some interaction sounds
            case 2: return true;  // Player + interaction + footstep sounds
            default: return true; // All sounds from player
        }
    }

    @Override
    public AnimatedItemTooltipEvent.TooltipLines getTooltipDetails(MobEffectInstance effectInstance) {
        float pitchMod = getPitchModifier(effectInstance.getAmplifier());
        String pitchText = String.format("%.1fx", pitchMod);
        
        Component pitchComponent = Component.literal(pitchText).withStyle(ChatFormatting.YELLOW);
        
        List<Component> text = List.of(
                Component.translatable("effect.potionsplus.squeaky_voice.tooltip_1").withStyle(ChatFormatting.LIGHT_PURPLE),
                pitchComponent,
                Component.translatable("effect.potionsplus.squeaky_voice.tooltip_2").withStyle(ChatFormatting.LIGHT_PURPLE));
        
        return createTooltipLine(text);
    }
}