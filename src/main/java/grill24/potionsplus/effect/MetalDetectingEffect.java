package grill24.potionsplus.effect;

import grill24.potionsplus.core.Sounds;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MetalDetectingEffect extends MobEffect {
    private static final Set<Block> DETECTED_BLOCKS = Set.of(
            Blocks.COPPER_ORE,
            Blocks.IRON_ORE,
            Blocks.GOLD_ORE,
            Blocks.ANCIENT_DEBRIS
    );


    private static SoundInstance ping0;
    private static SoundInstance ping1;
    private static SoundInstance ping2;
    private static SoundInstance ping3;

    public MetalDetectingEffect(MobEffectCategory mobEffectCategory, int color) {
        super(mobEffectCategory, color);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        // 50 / 2^amplifier
        int j = 400 >> amplifier;
        if (j > 0) {
            return duration % j == 0;
        } else {
            return true;
        }
    }

    @Override
    public void applyEffectTick(LivingEntity livingEntity, int amplifier) {
        final BlockPos pos = livingEntity.blockPosition();

        List<BlockPos> detectedBlockPos = new ArrayList<>();
        Set<Block> detectedBlocks = new HashSet<>();
        final int range = (amplifier + 1) * 4;
        // x, y, z
        for (int x = -range; x <= range; x++) {
            for (int y = -range; y <= range; y++) {
                for (int z = -range; z <= range; z++) {
                    final BlockPos blockPos = pos.offset(x, y, z);
                    final Block block = livingEntity.level().getBlockState(blockPos).getBlock();
                    if (DETECTED_BLOCKS.contains(block)) {
                        detectedBlockPos.add(blockPos);
                        detectedBlocks.add(block);
                    }
                }
            }
        }

        final float volume = 0.75F;
        if (!detectedBlockPos.isEmpty() && !livingEntity.level().isClientSide) {
            BlockPos soundPos = detectedBlockPos.get(livingEntity.getRandom().nextInt(0, detectedBlockPos.size()));

            if (detectedBlocks.contains(Blocks.COPPER_ORE) || detectedBlocks.contains(Blocks.DEEPSLATE_COPPER_ORE)) {
                livingEntity.level().playSound(null, soundPos, Sounds.PING_0.get(), SoundSource.BLOCKS, volume, 1.0F);
            }

            if (detectedBlocks.contains(Blocks.IRON_ORE) || detectedBlocks.contains(Blocks.DEEPSLATE_IRON_ORE)) {
                livingEntity.level().playSound(null, soundPos, Sounds.PING_1.get(), SoundSource.BLOCKS, volume, 1.0F);
            }

            if (detectedBlocks.contains(Blocks.GOLD_ORE) ||
                    detectedBlocks.contains(Blocks.DEEPSLATE_GOLD_ORE) ||
                    detectedBlocks.contains(Blocks.NETHER_GOLD_ORE) ||
                    detectedBlocks.contains(Blocks.GILDED_BLACKSTONE)) {
                livingEntity.level().playSound(null, soundPos, Sounds.PING_2.get(), SoundSource.BLOCKS, volume, 1.0F);

                if (detectedBlocks.contains(Blocks.ANCIENT_DEBRIS)) {
                    livingEntity.level().playSound(null, soundPos, Sounds.PING_3.get(), SoundSource.BLOCKS, volume, 1.0F);
                }


            }
        }
    }
}
