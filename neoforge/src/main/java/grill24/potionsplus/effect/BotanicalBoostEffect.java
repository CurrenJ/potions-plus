package grill24.potionsplus.effect;

import grill24.potionsplus.utility.Utility;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.SoulSandBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class BotanicalBoostEffect extends MobEffect implements ITickingAreaTooltipDetails {
    public BotanicalBoostEffect(MobEffectCategory mobEffectCategory, int color) {
        super(mobEffectCategory, color);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        int j = getTickInterval(amplifier); // lv 0 = 10, lv 1 = 5, lv 2 = 2, lv 3 = 1
        if (j > 0) {
            return duration % j == 0;
        } else {
            return true;
        }
    }

    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        if (livingEntity.level() instanceof ServerLevel serverLevel) {
            final int radius = getRadius(amplifier);
            RandomSource random = livingEntity.getRandom();
            BlockPos origin = livingEntity.blockPosition();
            // If on farm-land or soul sand, origin is the block above
            if (livingEntity.level().getBlockState(origin).getBlock() instanceof FarmBlock || livingEntity.level().getBlockState(origin).getBlock() instanceof SoulSandBlock) {
                origin = origin.above();
            }

            BlockPos pos = Utility.randomBlockPosInBox(origin, radius, 0, radius, random);
            BlockState blockState = serverLevel.getBlockState(pos);
            if (blockState.isRandomlyTicking()) {
                for (int i = 0; i < amplifier + 1; i++) {
                    blockState.randomTick(serverLevel, pos, random);
                }
            }
        }
        return true;
    }

    @Override
    public int getTickInterval(int amplifier) {
        return 10 >> amplifier;
    }

    @Override
    public int getRadius(int amplifier) {
        return 1 + amplifier;
    }

    @Override
    public Component getVerb() {
        return Component.translatable("effect.potionsplus.ticking_area.botanical_boost");
    }
}
