package grill24.potionsplus.effect;

import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.List;

public interface ITickingTooltipDetails extends IEffectTooltipDetails {
    int getTickInterval(int amplifier);
}
