package grill24.potionsplus.effect;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class ShepherdsSerenadeEffect extends MobEffect {
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
}
