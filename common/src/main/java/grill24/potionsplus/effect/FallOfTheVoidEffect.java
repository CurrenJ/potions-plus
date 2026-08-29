package grill24.potionsplus.effect;

import grill24.potionsplus.core.Translations;
import grill24.potionsplus.core.potion.MobEffects;
import grill24.potionsplus.event.AnimatedItemTooltipEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class FallOfTheVoidEffect extends MobEffect implements IEffectTooltipDetails {
    public FallOfTheVoidEffect(MobEffectCategory mobEffectCategory, int color) {
        super(mobEffectCategory, color);
    }

    /**
     * Returns the new damage value to apply, or the original damage if no change.
     */
    public static float onLivingEntityDamage(LivingEntity livingEntity, DamageSource source, float originalDamage) {
        if (source == livingEntity.damageSources().fellOutOfWorld()) {
            if (livingEntity.hasEffect(MobEffects.FALL_OF_THE_VOID)) {
                Vec3 blockPos = livingEntity.position().with(Direction.Axis.Y, livingEntity.level().getMaxY());
                livingEntity.teleportTo(blockPos.x, blockPos.y, blockPos.z);

                livingEntity.removeEffect(MobEffects.FALL_OF_THE_VOID);
                livingEntity.addEffect(new MobEffectInstance(net.minecraft.world.effect.MobEffects.SLOW_FALLING, 900, 0));

                return 0f;
            }
        }
        return originalDamage;
    }

    @Override
    public AnimatedItemTooltipEvent.TooltipLines getTooltipDetails(MobEffectInstance effectInstance) {
        MutableComponent tooltip = Component.translatable(Translations.EFFECT_POTIONSPLUS_FALL_OF_THE_VOID_TOOLTIP).withStyle(ChatFormatting.LIGHT_PURPLE);
        return createTooltipLine(tooltip);
    }
}
