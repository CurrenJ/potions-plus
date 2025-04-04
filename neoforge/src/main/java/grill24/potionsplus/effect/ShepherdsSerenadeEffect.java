package grill24.potionsplus.effect;

import grill24.potionsplus.core.Translations;
import grill24.potionsplus.event.AnimatedItemTooltipEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.List;

public class ShepherdsSerenadeEffect extends MobEffect implements IEffectTooltipDetails {
    public ShepherdsSerenadeEffect(MobEffectCategory mobEffectCategory, int color) {
        super(mobEffectCategory, color);
    }

    @Override
    public Component getDisplayName() {
        String name = Minecraft.getInstance().player.getName().getContents().toString();
        if(name.equals("grill24") || name.equals("ThatDinoGuy")) {
            return Component.literal("Milkboat's Melody");
        }

        return super.getDisplayName();
    }

    @Override
    public AnimatedItemTooltipEvent.TooltipLines getTooltipDetails(MobEffectInstance effectInstance) {
        List<Component> text = List.of(Component.translatable(Translations.EFFECT_POTIONSPLUS_SHEPHERDS_SERENADE_TOOLTIP).withStyle(ChatFormatting.LIGHT_PURPLE));

        return createTooltipLine(text);
    }
}
