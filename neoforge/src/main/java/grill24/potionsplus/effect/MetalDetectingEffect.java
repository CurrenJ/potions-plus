package grill24.potionsplus.effect;

import grill24.potionsplus.core.Sounds;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MetalDetectingEffect extends MobEffect implements ITickingAreaTooltipDetails {
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
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        // 50 / 2^amplifier
        int j = getTickInterval(amplifier);
        if (j > 0) {
            return duration % j == 0;
        } else {
            return false;
        }
    }

    public int getRadius(int amplifier) {
        return (amplifier + 1) * 4;
    }

    @Override
    public int getTickInterval(int amplifier) {
        return 400 >> amplifier;
    }

    @Override
    public Component getVerb() {
        return null;
    }

    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        final BlockPos pos = livingEntity.blockPosition();

        List<BlockPos> detectedBlockPos = new ArrayList<>();
        Set<Block> detectedBlocks = new HashSet<>();
        final int radius = getRadius(amplifier);
        // x, y, z
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
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
                livingEntity.level().playSound(null, soundPos, Sounds.PING_0.value(), SoundSource.BLOCKS, volume, 1.0F);
            }

            if (detectedBlocks.contains(Blocks.IRON_ORE) || detectedBlocks.contains(Blocks.DEEPSLATE_IRON_ORE)) {
                livingEntity.level().playSound(null, soundPos, Sounds.PING_1.value(), SoundSource.BLOCKS, volume, 1.0F);
            }

            if (detectedBlocks.contains(Blocks.GOLD_ORE) ||
                    detectedBlocks.contains(Blocks.DEEPSLATE_GOLD_ORE) ||
                    detectedBlocks.contains(Blocks.NETHER_GOLD_ORE) ||
                    detectedBlocks.contains(Blocks.GILDED_BLACKSTONE)) {
                livingEntity.level().playSound(null, soundPos, Sounds.PING_2.value(), SoundSource.BLOCKS, volume, 1.0F);

                if (detectedBlocks.contains(Blocks.ANCIENT_DEBRIS)) {
                    livingEntity.level().playSound(null, soundPos, Sounds.PING_3.value(), SoundSource.BLOCKS, volume, 1.0F);
                }


            }
        }
        return true;
    }

    @Override
    public List<Component> getTooltipDetails(MobEffectInstance effectInstance) {
        int range = getRadius(effectInstance.getAmplifier()) * 2 + 1;
        MutableComponent area = Component.literal(range + "x" + range).withStyle(ChatFormatting.GREEN);
        Component tickInterval = Component.literal(String.valueOf(getTickInterval(effectInstance.getAmplifier()))).withStyle(ChatFormatting.GREEN);

        return List.of(
                Component.translatable("effect.potionsplus.metal_detecting.tooltip_1").withStyle(ChatFormatting.LIGHT_PURPLE),
                area,
                Component.translatable("effect.potionsplus.metal_detecting.tooltip_2").withStyle(ChatFormatting.LIGHT_PURPLE),
                tickInterval,
                Component.translatable("effect.potionsplus.metal_detecting.tooltip_3").withStyle(ChatFormatting.LIGHT_PURPLE));
    }
}
