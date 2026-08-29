package grill24.potionsplus.effect;

import grill24.potionsplus.alchemy.EffectScaling;
import grill24.potionsplus.utility.Utility;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.FarmlandBlock;
import net.minecraft.world.level.block.SoulSandBlock;
import net.minecraft.world.level.block.state.BlockState;

public class BotanicalBoostEffect extends MobEffect implements ITickingAreaTooltipDetails {
    public BotanicalBoostEffect(MobEffectCategory mobEffectCategory, int color) {
        super(mobEffectCategory, color);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % getTickInterval(amplifier) == 0;
    }

    @Override
    public boolean applyEffectTick(ServerLevel serverLevel, LivingEntity livingEntity, int amplifier) {
        final int radius = getRadius(amplifier);
        RandomSource random = livingEntity.getRandom();
        BlockPos origin = livingEntity.blockPosition();
        // If on farm-land or soul sand, origin is the block above
        if (livingEntity.level().getBlockState(origin).getBlock() instanceof FarmlandBlock || livingEntity.level().getBlockState(origin).getBlock() instanceof SoulSandBlock) {
            origin = origin.above();
        }

        BlockPos pos = Utility.randomBlockPosInBox(origin, radius, 0, radius, random);
        BlockState blockState = serverLevel.getBlockState(pos);
        if (blockState.isRandomlyTicking()) {
            for (int i = 0; i < EffectScaling.clampAmplifier(amplifier) + 1; i++) {
                blockState.randomTick(serverLevel, pos, random);
            }
        }
        return true;
    }

    @Override
    public int getTickInterval(int amplifier) {
        return EffectScaling.tickInterval(10, amplifier);
    }

    @Override
    public int getRadius(int amplifier) {
        return 1 + EffectScaling.clampAmplifier(amplifier);
    }

    @Override
    public Component getVerb() {
        return Component.translatable("effect.potionsplus.ticking_area.botanical_boost");
    }
}
