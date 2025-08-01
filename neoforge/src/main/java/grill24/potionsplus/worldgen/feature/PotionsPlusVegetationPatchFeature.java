package grill24.potionsplus.worldgen.feature;

import com.mojang.serialization.Codec;
import grill24.potionsplus.block.PotionsPlusOreBlock;
import grill24.potionsplus.core.blocks.OreBlocks;
import grill24.potionsplus.mixin.OreFeatureMixin;
import grill24.potionsplus.utility.registration.RuntimeTextureVariantModelGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.neoforged.neoforge.common.Tags;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

public class PotionsPlusVegetationPatchFeature extends Feature<PotionsPlusVegetationPatchConfiguration> {
    public PotionsPlusVegetationPatchFeature(Codec<PotionsPlusVegetationPatchConfiguration> codec) {
        super(codec);
    }

    public boolean place(FeaturePlaceContext<PotionsPlusVegetationPatchConfiguration> p_160612_) {
        WorldGenLevel worldgenlevel = p_160612_.level();
        PotionsPlusVegetationPatchConfiguration vegetationpatchconfiguration = p_160612_.config();
        RandomSource random = p_160612_.random();
        BlockPos blockpos = p_160612_.origin();
        Predicate<BlockState> predicate = (p_204782_) -> {
            return p_204782_.is(vegetationpatchconfiguration.replaceable);
        };
        int i = vegetationpatchconfiguration.xzRadius.sample(random) + 1;
        int j = vegetationpatchconfiguration.xzRadius.sample(random) + 1;
        Set<BlockPos> set = this.placeGroundPatch(worldgenlevel, vegetationpatchconfiguration, random, blockpos, predicate, i, j);
        this.distributeVegetation(p_160612_, worldgenlevel, vegetationpatchconfiguration, random, set, i, j);
        return !set.isEmpty();
    }

    protected Set<BlockPos> placeGroundPatch(WorldGenLevel p_160597_, PotionsPlusVegetationPatchConfiguration configuration, RandomSource p_160599_, BlockPos p_160600_, Predicate<BlockState> p_160601_, int xRadius, int zRadius) {
        BlockPos.MutableBlockPos blockpos$mutableblockpos = p_160600_.mutable();
        BlockPos.MutableBlockPos blockpos$mutableblockpos1 = blockpos$mutableblockpos.mutable();
        Direction facingOpposite = configuration.facing.getOpposite();
        Direction facing = configuration.facing;
        Set<BlockPos> set = new HashSet<>();

        for (int i = -xRadius; i <= xRadius; ++i) {
            boolean flag = i == -xRadius || i == xRadius;

            for (int j = -zRadius; j <= zRadius; ++j) {
                boolean flag1 = j == -zRadius || j == zRadius;
                boolean flag2 = flag || flag1;
                boolean flag3 = flag && flag1;
                boolean flag4 = flag2 && !flag3;
                if (!flag3 && (!flag4 || configuration.extraEdgeColumnChance != 0.0F && !(p_160599_.nextFloat() > configuration.extraEdgeColumnChance))) {
//                    blockpos$mutableblockpos.setWithOffset(p_160600_, i, 0, j);
                    // UP = (i, 0, j) AXISY
                    // DOWN = (i, 0, j) AXISY
                    // NORTH = (i, j, 0) AXISZ
                    // SOUTH = (i, j, 0) AXISZ
                    // EAST = (0, i, j) AXISX
                    // WEST = (0, i, j) AXISX
                    Direction.Axis axis = configuration.facing.getAxis();
                    blockpos$mutableblockpos.setWithOffset(p_160600_,
                            axis == Direction.Axis.X ? 0 : i,
                            axis == Direction.Axis.Y ? 0 : (axis == Direction.Axis.Z ? j : i),
                            axis == Direction.Axis.Z ? 0 : j);

                    for (int k = 0; p_160597_.isStateAtPosition(blockpos$mutableblockpos, (state) -> !state.isCollisionShapeFullBlock(p_160597_, blockpos$mutableblockpos)) && k < configuration.verticalRange; ++k) {
                        blockpos$mutableblockpos.move(facingOpposite);
                    }

                    for (int i1 = 0; p_160597_.isStateAtPosition(blockpos$mutableblockpos, (p_204784_) -> p_204784_.isCollisionShapeFullBlock(p_160597_, blockpos$mutableblockpos)) && i1 < configuration.verticalRange; ++i1) {
                        blockpos$mutableblockpos.move(facing);
                    }

                    blockpos$mutableblockpos1.setWithOffset(blockpos$mutableblockpos, facingOpposite);
                    BlockState blockstate = p_160597_.getBlockState(blockpos$mutableblockpos1);
                    if (!p_160597_.getBlockState(blockpos$mutableblockpos).isCollisionShapeFullBlock(p_160597_, blockpos$mutableblockpos) && blockstate.isFaceSturdy(p_160597_, blockpos$mutableblockpos1, facing)) {
                        int l = configuration.depth.sample(p_160599_) + (configuration.extraBottomBlockChance > 0.0F && p_160599_.nextFloat() < configuration.extraBottomBlockChance ? 1 : 0);
                        BlockPos blockpos = blockpos$mutableblockpos1.immutable();
                        boolean flag5 = this.placeGround(p_160597_, configuration, p_160601_, p_160599_, blockpos$mutableblockpos1, l);
                        if (flag5) {
                            set.add(blockpos);
                        }
                    }
                }
            }
        }

        return set;
    }

    protected void distributeVegetation(FeaturePlaceContext<PotionsPlusVegetationPatchConfiguration> p_160614_, WorldGenLevel p_160615_, PotionsPlusVegetationPatchConfiguration p_160616_, RandomSource p_160617_, Set<BlockPos> p_160618_, int p_160619_, int p_160620_) {
        for (BlockPos blockpos : p_160618_) {
            if (p_160616_.vegetationChance > 0.0F && p_160617_.nextFloat() < p_160616_.vegetationChance) {
                this.placeVegetation(p_160615_, p_160616_, p_160614_.chunkGenerator(), p_160617_, blockpos);
            }
        }

    }

    protected boolean placeVegetation(WorldGenLevel p_160592_, PotionsPlusVegetationPatchConfiguration p_160593_, ChunkGenerator p_160594_, RandomSource p_160595_, BlockPos p_160596_) {
        return p_160593_.vegetationFeature.value().place(p_160592_, p_160594_, p_160595_, p_160596_.relative(p_160593_.facing));
    }

    protected boolean placeGround(WorldGenLevel level, PotionsPlusVegetationPatchConfiguration p_160606_, Predicate<BlockState> canReplace, RandomSource random, BlockPos.MutableBlockPos pos, int p_160610_) {
        for (int i = 0; i < p_160610_; ++i) {
            BlockState ground = p_160606_.groundState.getState(random, pos);
            BlockState existing = level.getBlockState(pos);
            if (!ground.is(existing.getBlock())) {
                if (!canReplace.test(existing)) {
                    return i != 0;
                }

                // Don't replace ores with ground
                if (existing.is(Tags.Blocks.ORES)) {
                    Optional<BlockState> state = OreBlocks.tryGetRuntimeOreVariant(existing, ground);
                    if (state.isPresent()) {
                        ground = state.get();
                    }
                }

                level.setBlock(pos, ground, 2 | 16);
                pos.move(p_160606_.facing.getOpposite());
            }
        }

        return true;
    }
}
