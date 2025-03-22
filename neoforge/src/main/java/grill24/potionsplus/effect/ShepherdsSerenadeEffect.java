package grill24.potionsplus.effect;

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
    public List<Component> getTooltipDetails(MobEffectInstance effectInstance) {
        return List.of(Component.translatable("effect.potionsplus.shepherds_serenade.tooltip").withStyle(ChatFormatting.LIGHT_PURPLE));
    }
}
