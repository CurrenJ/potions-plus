package grill24.potionsplus.mixin;

import com.mojang.serialization.Codec;
import grill24.potionsplus.block.OreFlowerBlock;
import grill24.potionsplus.core.Blocks;
import grill24.potionsplus.core.PotionsPlus;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.BulkSectionAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.OreFeature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.BitSet;
import java.util.Iterator;
import java.util.Optional;

@Mixin(OreFeature.class)
public abstract class OreFeatureMixin extends Feature<OreConfiguration> {
    public OreFeatureMixin(Codec<OreConfiguration> oreConfigurationCodec) {
        super(oreConfigurationCodec);
    }

    @Inject(method = "doPlace", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/chunk/LevelChunkSection;setBlockState(IIILnet/minecraft/world/level/block/state/BlockState;Z)Lnet/minecraft/world/level/block/state/BlockState;"), locals = LocalCapture.CAPTURE_FAILSOFT)
    public void onSetBlockState(WorldGenLevel level, RandomSource random, OreConfiguration config, double minX, double maxX, double minZ, double maxZ, double minY, double maxY, int x, int y, int z, int width, int height, CallbackInfoReturnable<Boolean> cir, int i, BitSet bitset, BlockPos.MutableBlockPos blockpos$mutableblockpos, int j, double[] adouble, BulkSectionAccess bulksectionaccess, int j4, double d9, double d11, double d13, double d15, int k4, int l, int i1, int j1, int k1, int l1, int i2, double d5, int j2, double d6, int k2, double d7, int l2, LevelChunkSection levelchunksection, int i3, int j3, int k3, BlockState blockstate, Iterator var57, OreConfiguration.TargetBlockState oreconfiguration$targetblockstate) {
        potions_plus$onPlaceOre(level, random, blockpos$mutableblockpos, bulksectionaccess, levelchunksection, i3, j3, k3, oreconfiguration$targetblockstate.state);
    }

    @Unique
    private static void potions_plus$onPlaceOre(WorldGenLevel level, RandomSource random, BlockPos.MutableBlockPos blockpos$mutableblockpos, BulkSectionAccess bulksectionaccess, LevelChunkSection levelchunksection, int x, int y, int z, BlockState blockstate) {
        if (bulksectionaccess == null || blockpos$mutableblockpos == null || random == null) {
            PotionsPlus.LOGGER.warn("OreFeatureMixin: Null field(s) detected, falling back to original method");
            return;
        }

        for (OreFlowerBlock block : Blocks.ORE_FLOWER_BLOCKS) {
            if (block.mayPlaceOn(blockstate)) {
                BlockPos abovePos = new BlockPos(blockpos$mutableblockpos.getX(), blockpos$mutableblockpos.getY() + 1, blockpos$mutableblockpos.getZ());
                BlockState above = bulksectionaccess.getBlockState(abovePos);
                if (above.isAir() && random.nextFloat() < block.getGenerationChance()) {
                    potions_plus$tryPlaceBlock(level, bulksectionaccess, block, abovePos);
                }
            }
        }

        if (blockstate.is(net.minecraft.world.level.block.Blocks.NETHER_QUARTZ_ORE)) {
            if (random.nextFloat() < 0.1F) {
                Optional<BlockState> quartzState = potions_plus$tryPlaceBlock(level, bulksectionaccess, Blocks.SULFURIC_NETHER_QUARTZ_ORE.value(), blockpos$mutableblockpos);
                quartzState.ifPresent(blockState -> levelchunksection.setBlockState(x, y, z, blockState, false));
            }
        }
    }

    @Unique
    private static Optional<BlockState> potions_plus$tryPlaceBlock(WorldGenLevel level, BulkSectionAccess bulkSectionAccess, Block block, BlockPos pos) {
        if (level.ensureCanWrite(pos)) {
            int x = SectionPos.sectionRelative(pos.getX());
            int y = SectionPos.sectionRelative(pos.getY());
            int z = SectionPos.sectionRelative(pos.getZ());
            LevelChunkSection section = bulkSectionAccess.getSection(pos);

            if (section != null) {
                return Optional.of(section.setBlockState(x, y, z, block.defaultBlockState(), false));
            }
        }

        return Optional.empty();
    }
}
