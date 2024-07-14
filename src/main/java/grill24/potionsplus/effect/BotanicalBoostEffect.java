package grill24.potionsplus.effect;

import grill24.potionsplus.utility.Utility;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.SoulSandBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Random;

public class BotanicalBoostEffect extends MobEffect {
    public BotanicalBoostEffect(MobEffectCategory mobEffectCategory, int color) {
        super(mobEffectCategory, color);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        // 20 / 2^amplifier
        int j = 10 >> amplifier;
        if (j > 0) {
            return duration % j == 0;
        } else {
            return true;
        }
    }

    @Override
    public void applyEffectTick(LivingEntity livingEntity, int amplifier) {
        if (livingEntity.level instanceof ServerLevel serverLevel) {
            final int radius = 1 + 2 * amplifier;
            Random random = livingEntity.getRandom();
            BlockPos origin = livingEntity.blockPosition();
            // If on farm-land or soul sand, origin is the block above
            if (livingEntity.level.getBlockState(origin).getBlock() instanceof FarmBlock || livingEntity.level.getBlockState(origin).getBlock() instanceof SoulSandBlock) {
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
    }
}
