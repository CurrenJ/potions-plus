package grill24.potionsplus.worldgen.feature;

import com.mojang.serialization.Codec;
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
    public static WeightedStateProvider FILL_SAMPLER = new WeightedStateProvider(SimpleWeightedRandomList.<BlockState>builder()
            .add(grill24.potionsplus.core.Blocks.UNSTABLE_BLACKSTONE.get().defaultBlockState().setValue(UnstableBlock.PRIMED, true), 2)
            .add(grill24.potionsplus.core.Blocks.UNSTABLE_DEEPSLATE.get().defaultBlockState().setValue(UnstableBlock.PRIMED, true), 2)
            .add(grill24.potionsplus.core.Blocks.UNSTABLE_MOLTEN_BLACKSTONE.get().defaultBlockState().setValue(UnstableBlock.PRIMED, true), 1)
            .add(grill24.potionsplus.core.Blocks.UNSTABLE_MOLTEN_DEEPSLATE.get().defaultBlockState().setValue(UnstableBlock.PRIMED, true), 1)
    );

    public VolcanicFissureFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        double radiusX = context.random().nextDouble() * 10.0 + 5, radiusY = context.random().nextDouble() * 7.0D + 2.0D, radiusZ = context.random().nextDouble() * 1.5D + 1.0D;
        double azimuthalAngle = context.random().nextDouble(Math.PI * 2);
        boolean[] shape = WorldGenUtil.generateRotatedEllipsoid(context.random(), radiusX, radiusY, radiusZ, azimuthalAngle, 0);


        int width = (int) Math.ceil(2 * radiusX);
        int height = (int) Math.ceil(2 * radiusY);
        int depth = (int) Math.ceil(2 * radiusZ);

        // Check validity
        AtomicBoolean valid = new AtomicBoolean(true);
        WorldGenUtil.iterateEllipsoidShape(shape, width, height, depth, (blockPos -> {
            if(blockPos.getY() < height / 3) {
                if(context.level().getBlockState(blockPos.offset(context.origin()).offset(-radiusX, -radiusY, -radiusY)).isAir()) {
                    valid.set(false);
                }
            }
        }));
        if(!valid.get()) {
            return false;
        }

        BlockState bottomLayer = Blocks.LAVA.defaultBlockState();
        WorldGenUtil.iterateEllipsoidShape(shape, width, height, depth, (p -> {
            BlockPos pos = p.offset(context.origin()).offset(-radiusX, -radiusY, -radiusY);
            BlockState cavity = FILL_SAMPLER.getState(context.random(), pos);

            if(!context.level().getBlockState(pos).isAir()) {
            if(p.getY() <= height / 2) {
                if(p.getY() < height / 4) {
                    context.level().setBlock(pos, bottomLayer, 2 | 16);
                } else {
                    context.level().setBlock(p.offset(context.origin()).offset(-radiusX, -radiusY, -radiusY), cavity, 2 | 16);
                }
            }
            }
        }));

        System.out.println("Volcanic fissure generated at " + context.origin());

        return false;
    }
}
