package grill24.potionsplus.effect;

import grill24.potionsplus.core.Translations;
import grill24.potionsplus.event.AnimatedItemTooltipEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.InstantenousMobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent;

import javax.annotation.Nullable;
import java.util.List;

public class TeleportationEffect extends InstantenousMobEffect implements IEffectTooltipDetails {
    public TeleportationEffect(MobEffectCategory mobEffectCategory, int color) {
        super(mobEffectCategory, color);
    }

    @Override
    public boolean applyEffectTick(ServerLevel serverLevel, LivingEntity livingEntity, int amplifier) {
        teleport(livingEntity, amplifier);
        return true;
    }

    @Override
    public void applyInstantenousEffect(ServerLevel serverLevel, @Nullable Entity source, @Nullable Entity indirectSource, LivingEntity livingEntity, int amplifier, double health) {
        teleport(livingEntity, amplifier);
    }

    private static void teleport(LivingEntity livingEntity, int amplifier) {
        if (livingEntity.level().isClientSide) {
            return;
        }

        // Teleport to top of world
        double d0 = livingEntity.getX();
        double d1 = livingEntity.getY();
        double d2 = livingEntity.getZ();

        final int stepLength = getStepLength(amplifier);
        for (int i = 0; i < 16; ++i) {
            // Taken from ChorusFruitItem
            double d3 = livingEntity.getX() + (livingEntity.getRandom().nextDouble() - 0.5D) * stepLength;
            double d4 = Mth.clamp(livingEntity.getY() + (double) (livingEntity.getRandom().nextInt(stepLength) - 8), livingEntity.level().getMinY(), livingEntity.level().getMaxY() + ((ServerLevel) livingEntity.level()).getLogicalHeight() - 1);
            double d5 = livingEntity.getZ() + (livingEntity.getRandom().nextDouble() - 0.5D) * stepLength;
            if (livingEntity.isPassenger()) {
                livingEntity.stopRiding();
            }

            EntityTeleportEvent.ItemConsumption event = EventHooks.onItemConsumptionTeleport(livingEntity, new ItemStack(Items.CHORUS_FRUIT), d3, d4, d5);
            if (livingEntity.randomTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ(), true)) {
                SoundEvent soundevent = livingEntity instanceof Fox ? SoundEvents.FOX_TELEPORT : SoundEvents.CHORUS_FRUIT_TELEPORT;
                livingEntity.level().playSound(null, d0, d1, d2, soundevent, SoundSource.PLAYERS, 1.0F, 1.0F);
                livingEntity.playSound(soundevent, 1.0F, 1.0F);
                break;
            }
        }
    }

    private static int getStepLength(int amplifier) {
        return 32 * (amplifier + 1);
    }


    @Override
    public AnimatedItemTooltipEvent.TooltipLines getTooltipDetails(MobEffectInstance effectInstance) {
        Component range = Component.literal(String.valueOf(getStepLength(effectInstance.getAmplifier()))).withStyle(ChatFormatting.GREEN);

        List<Component> text = List.of(
                Component.translatable(Translations.EFFECT_POTIONSPLUS_TELEPORTATION_TOOLTIP_1).withStyle(ChatFormatting.LIGHT_PURPLE),
                range,
                Component.translatable(Translations.EFFECT_POTIONSPLUS_TELEPORTATION_TOOLTIP_2).withStyle(ChatFormatting.LIGHT_PURPLE));

        return createTooltipLine(text);
    }
}
