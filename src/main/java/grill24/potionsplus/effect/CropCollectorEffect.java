package grill24.potionsplus.effect;

import grill24.potionsplus.core.MobEffects;
import grill24.potionsplus.utility.Utility;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.SoulSandBlock;

import java.util.Random;

public class CropCollectorEffect extends MobEffect {
    public CropCollectorEffect(MobEffectCategory mobEffectCategory, int color) {
        super(mobEffectCategory, color);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        // 20 / 2^amplifier
        int j = 20 >> amplifier;
        if (j > 0) {
            return duration % j == 0;
        } else {
            return true;
        }
    }

    @Override
    public void applyEffectTick(LivingEntity livingEntity, int amplifier) {
        final int radius = 1 + amplifier;
        if(livingEntity.level instanceof ServerLevel serverLevel) {
            Random random = livingEntity.getRandom();
            BlockPos origin = livingEntity.blockPosition();
            // If on farm-land or soul sand, origin is the block above
            if(livingEntity.level.getBlockState(origin).getBlock() instanceof FarmBlock || livingEntity.level.getBlockState(origin).getBlock() instanceof SoulSandBlock) {
                origin = origin.above();
            }

            BlockPos pos = Utility.randomBlockPosInBox(origin, radius, 0, radius, random);
            Block block = livingEntity.level.getBlockState(pos).getBlock();
            if (block instanceof CropBlock cropBlock && cropBlock.isMaxAge(livingEntity.level.getBlockState(pos))) {
                serverLevel.destroyBlock(pos, true, livingEntity);
            }
        }
    }
}
