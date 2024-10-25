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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.SoulSandBlock;
import org.checkerframework.checker.units.qual.C;

import java.util.List;

public class CropCollectorEffect extends MobEffect implements ITickingAreaTooltipDetails {
    public CropCollectorEffect(MobEffectCategory mobEffectCategory, int color) {
        super(mobEffectCategory, color);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        // 20 / 2^amplifier
        int j = getTickInterval(amplifier); // lv 0 = 20, lv 1 = 10, lv 2 = 5, lv 3 = 2
        if (j > 0) {
            return duration % j == 0;
        } else {
            return false;
        }
    }

    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        final int radius = getRadius(amplifier);
        if (livingEntity.level() instanceof ServerLevel serverLevel) {
            RandomSource random = livingEntity.getRandom();
            BlockPos origin = livingEntity.blockPosition();
            // If on farm-land or soul sand, origin is the block above
            if (livingEntity.level().getBlockState(origin).getBlock() instanceof FarmBlock || livingEntity.level().getBlockState(origin).getBlock() instanceof SoulSandBlock) {
                origin = origin.above();
            }

            BlockPos pos = Utility.randomBlockPosInBox(origin, radius, 0, radius, random);
            Block block = livingEntity.level().getBlockState(pos).getBlock();
            if (block instanceof CropBlock cropBlock && cropBlock.isMaxAge(livingEntity.level().getBlockState(pos))) {
                serverLevel.destroyBlock(pos, true, livingEntity);
            }
        }
        return true;
    }

    public int getTickInterval(int amplifier) {
        return 20 >> amplifier;
    }

    public int getRadius(int amplifier) {
        return 1 + amplifier;
    }

    @Override
    public Component getVerb() {
        return Component.translatable("effect.potionsplus.ticking_area.crop_collector").withStyle(ChatFormatting.GREEN);
    }
}
