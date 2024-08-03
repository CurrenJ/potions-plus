package grill24.potionsplus.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;

public class SlipNSlideEffect extends MobEffect {
    public SlipNSlideEffect(MobEffectCategory mobEffectCategory, int color) {
        super(mobEffectCategory, color);
    }

    public static float getFriction(int amplifier) {
        return switch (amplifier) {
            case 0 -> 0.91f;
            case 1 -> 0.93f;
            case 2 -> 0.95f;
            default -> 0.6f;
        };
    }
}
