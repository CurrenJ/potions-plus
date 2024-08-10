package grill24.potionsplus.worldgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.VegetationPatchFeature;
import net.minecraft.world.level.levelgen.feature.configurations.VegetationPatchConfiguration;

import java.util.Random;
import java.util.function.Predicate;

public class NoUpdateVegetationPatchFeature extends VegetationPatchFeature {
    public NoUpdateVegetationPatchFeature(Codec<VegetationPatchConfiguration> codec) {
        super(codec);
    }

    @Override
    protected boolean placeGround(WorldGenLevel p_160605_, VegetationPatchConfiguration p_160606_, Predicate<BlockState> p_160607_, Random p_160608_, BlockPos.MutableBlockPos p_160609_, int p_160610_) {
        for(int i = 0; i < p_160610_; ++i) {
            BlockState blockstate = p_160606_.groundState.getState(p_160608_, p_160609_);
            BlockState blockstate1 = p_160605_.getBlockState(p_160609_);
            if (!blockstate.is(blockstate1.getBlock())) {
                if (!p_160607_.test(blockstate1)) {
                    return i != 0;
                }

                p_160605_.setBlock(p_160609_, blockstate, 2 | 16);
                p_160609_.move(p_160606_.surface.getDirection());
            }
        }

        return true;
    }
}
