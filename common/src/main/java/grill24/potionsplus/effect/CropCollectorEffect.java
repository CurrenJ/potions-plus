package grill24.potionsplus.effect;

import grill24.potionsplus.alchemy.EffectScaling;
import grill24.potionsplus.utility.Utility;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.FarmlandBlock;
import net.minecraft.world.level.block.SoulSandBlock;

public class CropCollectorEffect extends MobEffect implements ITickingAreaTooltipDetails {
    public CropCollectorEffect(MobEffectCategory mobEffectCategory, int color) {
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
        Block block = livingEntity.level().getBlockState(pos).getBlock();
        if (block instanceof CropBlock cropBlock && cropBlock.isMaxAge(livingEntity.level().getBlockState(pos))) {
            serverLevel.destroyBlock(pos, true, livingEntity);
        }
        return true;
    }

    public int getTickInterval(int amplifier) {
        return EffectScaling.tickInterval(20, amplifier);
    }

    public int getRadius(int amplifier) {
        return 1 + EffectScaling.clampAmplifier(amplifier);
    }

    @Override
    public Component getVerb() {
        return Component.translatable("effect.potionsplus.ticking_area.crop_collector").withStyle(ChatFormatting.GREEN);
    }
}
