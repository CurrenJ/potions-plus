package grill24.potionsplus.worldgen.feature;

import com.mojang.serialization.Codec;
import grill24.potionsplus.core.PotionsPlus;
import grill24.potionsplus.core.blocks.DecorationBlocks;
import net.neoforged.neoforge.common.util.Lazy;
import grill24.potionsplus.block.UnstableBlock;
import grill24.potionsplus.utility.WorldGenUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;

import java.util.concurrent.atomic.AtomicBoolean;

public class VolcanicFissureFeature extends Feature<NoneFeatureConfiguration> {
    public static Lazy<WeightedStateProvider> FILL_SAMPLER = Lazy.of(() -> new WeightedStateProvider(SimpleWeightedRandomList.<BlockState>builder()
            .add(DecorationBlocks.UNSTABLE_BLACKSTONE.value().defaultBlockState().setValue(UnstableBlock.PRIMED, true), 2)
            .add(DecorationBlocks.UNSTABLE_DEEPSLATE.value().defaultBlockState().setValue(UnstableBlock.PRIMED, true), 2)
            .add(DecorationBlocks.UNSTABLE_MOLTEN_BLACKSTONE.value().defaultBlockState().setValue(UnstableBlock.PRIMED, true), 1)
            .add(DecorationBlocks.UNSTABLE_MOLTEN_DEEPSLATE.value().defaultBlockState().setValue(UnstableBlock.PRIMED, true), 1)
    ));

    public VolcanicFissureFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        double radiusX = context.random().nextDouble() * 10.0 + 5, radiusY = context.random().nextDouble() * 7.0D + 2.0D, radiusZ = context.random().nextDouble() * 1.5D + 1.0D;
        double azimuthalAngle = context.random().nextDouble() * Math.PI * 2;
        boolean[] shape = WorldGenUtil.generateRotatedEllipsoid(context.random(), radiusX, radiusY, radiusZ, azimuthalAngle, 0);


        int width = (int) Math.ceil(2 * radiusX);
        int height = (int) Math.ceil(2 * radiusY);
        int depth = (int) Math.ceil(2 * radiusZ);

        // Check validity
        AtomicBoolean valid = new AtomicBoolean(true);
        WorldGenUtil.iterateEllipsoidShape(shape, width, height, depth, (blockPos -> {
            if(blockPos.getY() < height / 3) {
                if(context.level().getBlockState(blockPos.offset(context.origin()).offset((int) -radiusX, (int) -radiusY, (int) -radiusY)).isAir()) {
                    valid.set(false);
                }
            }
        }));
        if(!valid.get()) {
            return false;
        }

        BlockState bottomLayer = Blocks.LAVA.defaultBlockState();
        WorldGenUtil.iterateEllipsoidShape(shape, width, height, depth, (p -> {
            BlockPos pos = p.offset(context.origin()).offset((int) -radiusX, (int) -radiusY, (int) -radiusY);
            BlockState cavity = FILL_SAMPLER.get().getState(context.random(), pos);

            if(!context.level().getBlockState(pos).isAir()) {
            if(p.getY() <= height / 2) {
                if(p.getY() < height / 4) {
                    context.level().setBlock(pos, bottomLayer, 2 | 16);
                } else {
                    context.level().setBlock(p.offset(context.origin()).offset((int) -radiusX, (int) -radiusY, (int) -radiusY), cavity, 2 | 16);
                }
            }
            }
        }));

        if(PotionsPlus.Debug.DEBUG) {
            PotionsPlus.LOGGER.info("Volcanic fissure generated at {}", context.origin());
        }

        return false;
    }
}
