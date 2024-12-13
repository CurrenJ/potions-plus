package grill24.potionsplus.effect;

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
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent;

import javax.annotation.Nullable;
import java.util.List;

public class TeleportationEffect extends InstantenousMobEffect implements IEffectTooltipDetails{
    public TeleportationEffect(MobEffectCategory mobEffectCategory, int color) {
        super(mobEffectCategory, color);
    }

    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        teleport(livingEntity, amplifier);
        return true;
    }

    @Override
    public void applyInstantenousEffect(@Nullable Entity source, @Nullable Entity indirectSource, LivingEntity livingEntity, int amplifier, double health) {
        teleport(livingEntity, amplifier);
    }

    private static void teleport(LivingEntity livingEntity, int amplifier) {
        if(livingEntity.level().isClientSide) {
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
            double d4 = Mth.clamp(livingEntity.getY() + (double) (livingEntity.getRandom().nextInt(stepLength) - 8), (double) livingEntity.level().getMinBuildHeight(), (double) (livingEntity.level().getMinBuildHeight() + ((ServerLevel) livingEntity.level()).getLogicalHeight() - 1));
            double d5 = livingEntity.getZ() + (livingEntity.getRandom().nextDouble() - 0.5D) * stepLength;
            if (livingEntity.isPassenger()) {
                livingEntity.stopRiding();
            }

            EntityTeleportEvent.ChorusFruit event = EventHooks.onChorusFruitTeleport(livingEntity, d3, d4, d5);
            if (livingEntity.randomTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ(), true)) {
                SoundEvent soundevent = livingEntity instanceof Fox ? SoundEvents.FOX_TELEPORT : SoundEvents.CHORUS_FRUIT_TELEPORT;
                livingEntity.level().playSound((Player) null, d0, d1, d2, soundevent, SoundSource.PLAYERS, 1.0F, 1.0F);
                livingEntity.playSound(soundevent, 1.0F, 1.0F);
                break;
            }
        }
    }

    private static int getStepLength(int amplifier) {
        return 32 * (amplifier + 1);
    }


    @Override
    public List<Component> getTooltipDetails(MobEffectInstance effectInstance) {
        Component range = Component.literal(String.valueOf(getStepLength(effectInstance.getAmplifier()))).withStyle(ChatFormatting.GREEN);

        return List.of(
                Component.translatable("effect.potionsplus.teleportation.tooltip_1").withStyle(ChatFormatting.LIGHT_PURPLE),
                range,
                Component.translatable("effect.potionsplus.teleportation.tooltip_2").withStyle(ChatFormatting.LIGHT_PURPLE));
    }
}
