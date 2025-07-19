package grill24.potionsplus.mixin;

import com.mojang.serialization.Codec;
import grill24.potionsplus.block.OreFlowerBlock;
import grill24.potionsplus.block.PotionsPlusOreBlock;
import grill24.potionsplus.core.PotionsPlus;
import grill24.potionsplus.core.Tags;
import grill24.potionsplus.core.blocks.OreBlocks;
import grill24.potionsplus.utility.registration.RuntimeTextureVariantModelGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
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

    @Inject(method = "doPlace", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/level/chunk/LevelChunkSection;setBlockState(IIILnet/minecraft/world/level/block/state/BlockState;Z)Lnet/minecraft/world/level/block/state/BlockState;"), locals = LocalCapture.CAPTURE_FAILSOFT)
    public void onSetBlockState(WorldGenLevel level, RandomSource random, OreConfiguration config, double minX, double maxX, double minZ, double maxZ, double minY, double maxY, int x, int y, int z, int width, int height, CallbackInfoReturnable<Boolean> cir, int i, BitSet bitset, BlockPos.MutableBlockPos blockpos$mutableblockpos, int j, double[] adouble, BulkSectionAccess bulksectionaccess, int j4, double d9, double d11, double d13, double d15, int k4, int l, int i1, int j1, int k1, int l1, int i2, double d5, int j2, double d6, int k2, double d7, int l2, LevelChunkSection levelchunksection, int i3, int j3, int k3, BlockState blockstate, Iterator var57, OreConfiguration.TargetBlockState oreconfiguration$targetblockstate) {
        potions_plus$onPlaceOre(level, random, blockpos$mutableblockpos, bulksectionaccess, levelchunksection, i3, j3, k3, blockstate, oreconfiguration$targetblockstate.state);
    }

    @Unique
    private static void potions_plus$onPlaceOre(WorldGenLevel level, RandomSource random, BlockPos.MutableBlockPos blockpos$mutableblockpos, BulkSectionAccess bulksectionaccess, LevelChunkSection levelchunksection, int x, int y, int z, BlockState replacing, BlockState placing) {
        if (bulksectionaccess == null || blockpos$mutableblockpos == null || random == null) {
            PotionsPlus.LOGGER.warn("OreFeatureMixin: Null field(s) detected, falling back to original method");
            return;
        }

        // Replace a fraction of nether quartz ore with sulfuric nether quartz ore
        if (placing.is(net.minecraft.world.level.block.Blocks.NETHER_QUARTZ_ORE)) {
            if (random.nextFloat() < 0.1F) {
                potions_plus$tryPlaceBlock(level, bulksectionaccess, OreBlocks.SULFURIC_NETHER_QUARTZ_ORE.value(), blockpos$mutableblockpos);
            }
        }

        if (replacing.is(Tags.Blocks.STONEY_ORE_REPLACEABLE)) {
            potions_plus$tryPlaceOreVariant(level, blockpos$mutableblockpos, bulksectionaccess, replacing, placing, BlockTags.COAL_ORES, OreBlocks.STONEY_COAL_ORE.value());
            potions_plus$tryPlaceOreVariant(level, blockpos$mutableblockpos, bulksectionaccess, replacing, placing, BlockTags.COPPER_ORES, OreBlocks.STONEY_COPPER_ORE.value());
            potions_plus$tryPlaceOreVariant(level, blockpos$mutableblockpos, bulksectionaccess, replacing, placing, BlockTags.IRON_ORES, OreBlocks.STONEY_IRON_ORE.value());
            potions_plus$tryPlaceOreVariant(level, blockpos$mutableblockpos, bulksectionaccess, replacing, placing, BlockTags.GOLD_ORES, OreBlocks.STONEY_GOLD_ORE.value());
            potions_plus$tryPlaceOreVariant(level, blockpos$mutableblockpos, bulksectionaccess, replacing, placing, BlockTags.REDSTONE_ORES, OreBlocks.STONEY_REDSTONE_ORE.value());
            potions_plus$tryPlaceOreVariant(level, blockpos$mutableblockpos, bulksectionaccess, replacing, placing, BlockTags.LAPIS_ORES, OreBlocks.STONEY_LAPIS_ORE.value());
            potions_plus$tryPlaceOreVariant(level, blockpos$mutableblockpos, bulksectionaccess, replacing, placing, BlockTags.DIAMOND_ORES, OreBlocks.STONEY_DIAMOND_ORE.value());
            potions_plus$tryPlaceOreVariant(level, blockpos$mutableblockpos, bulksectionaccess, replacing, placing, BlockTags.EMERALD_ORES, OreBlocks.STONEY_EMERALD_ORE.value());
            potions_plus$tryPlaceOreVariant(level, blockpos$mutableblockpos, bulksectionaccess, replacing, placing, Tags.Blocks.ORES_URANIUM, OreBlocks.STONEY_URANIUM_ORE.value());
        }

        if (replacing.is(Tags.Blocks.SANDY_ORE_REPLACEABLE)) {
            // Sandy ores can replace sand on the surface, which is not ideal.
            // So, make most attempts to place exposed ores on the surface fail.
            float failureChance = 0;
            if (potions_plus$isOnSurface(blockpos$mutableblockpos, level)) {
                failureChance = 0.95F;
            }
            float randomValue = random.nextFloat();
            if (randomValue < failureChance) {
                potions_plus$tryPlaceBlockState(level, bulksectionaccess, replacing, blockpos$mutableblockpos);
                return;
            } else {
                potions_plus$tryPlaceOreVariant(level, blockpos$mutableblockpos, bulksectionaccess, replacing, placing, BlockTags.COAL_ORES, OreBlocks.SANDY_COAL_ORE.value());
                potions_plus$tryPlaceOreVariant(level, blockpos$mutableblockpos, bulksectionaccess, replacing, placing, BlockTags.COPPER_ORES, OreBlocks.SANDY_COPPER_ORE.value());
                potions_plus$tryPlaceOreVariant(level, blockpos$mutableblockpos, bulksectionaccess, replacing, placing, BlockTags.IRON_ORES, OreBlocks.SANDY_IRON_ORE.value());
                potions_plus$tryPlaceOreVariant(level, blockpos$mutableblockpos, bulksectionaccess, replacing, placing, BlockTags.GOLD_ORES, OreBlocks.SANDY_GOLD_ORE.value());
                potions_plus$tryPlaceOreVariant(level, blockpos$mutableblockpos, bulksectionaccess, replacing, placing, BlockTags.REDSTONE_ORES, OreBlocks.SANDY_REDSTONE_ORE.value());
                potions_plus$tryPlaceOreVariant(level, blockpos$mutableblockpos, bulksectionaccess, replacing, placing, BlockTags.LAPIS_ORES, OreBlocks.SANDY_LAPIS_ORE.value());
                potions_plus$tryPlaceOreVariant(level, blockpos$mutableblockpos, bulksectionaccess, replacing, placing, BlockTags.DIAMOND_ORES, OreBlocks.SANDY_DIAMOND_ORE.value());
                potions_plus$tryPlaceOreVariant(level, blockpos$mutableblockpos, bulksectionaccess, replacing, placing, BlockTags.EMERALD_ORES, OreBlocks.SANDY_EMERALD_ORE.value());
                potions_plus$tryPlaceOreVariant(level, blockpos$mutableblockpos, bulksectionaccess, replacing, placing, Tags.Blocks.ORES_URANIUM, OreBlocks.SANDY_URANIUM_ORE.value());
            }
        }

        // Place ore flowers atop ore blocks
        BuiltInRegistries.BLOCK.getOrThrow(Tags.Blocks.ORE_FLOWERS).forEach(block -> {
            if (block.value() instanceof OreFlowerBlock oreFlowerBlock && oreFlowerBlock.mayPlaceOn(placing)) {
                BlockPos abovePos = new BlockPos(blockpos$mutableblockpos.getX(), blockpos$mutableblockpos.getY() + 1, blockpos$mutableblockpos.getZ());
                BlockState above = bulksectionaccess.getBlockState(abovePos);
                if (above.isAir() && random.nextFloat() < oreFlowerBlock.getGenerationChance()) {
                    potions_plus$tryPlaceBlock(level, bulksectionaccess, oreFlowerBlock, abovePos);
                }
            }
        });
    }

    /**
     * Check if a block position can see the sky. Water does not block this.
     *
     * @param blockpos$mutableblockpos
     * @param level
     * @return
     */
    @Unique
    private static boolean potions_plus$isOnSurface(BlockPos.MutableBlockPos blockpos$mutableblockpos, WorldGenLevel level) {
        // Check upward every 8 blocks to build limit. All must be air
        int minBuildHeight = level.getMinY();
        int maxBuildHeight = level.getMaxY();

        BlockPos.MutableBlockPos mutable = blockpos$mutableblockpos.mutable();
        mutable.setY(mutable.getY() + 1);
        while (mutable.getY() < maxBuildHeight && mutable.getY() > minBuildHeight) {
            if (!level.getBlockState(mutable).isAir() && !level.isFluidAtPosition(mutable, state -> state.is(FluidTags.WATER))) {
                return false;
            }
            mutable.setY(mutable.getY() + 8);
        }
        return true;
    }

    @Unique
    private static void potions_plus$tryPlaceOreVariant(WorldGenLevel level, BlockPos.MutableBlockPos blockpos$mutableblockpos, BulkSectionAccess bulksectionaccess, BlockState replacing, BlockState placing, TagKey<Block> targetOre, Block variantOreBlock) {
        if (placing.is(targetOre)) {
            BlockState ore = RuntimeTextureVariantModelGenerator.getTextureVariantBlockState(variantOreBlock,
                    new ItemStack(replacing.getBlock()), variantOreBlock.defaultBlockState(), PotionsPlusOreBlock.TEXTURE);
            potions_plus$tryPlaceBlockState(level, bulksectionaccess, ore, blockpos$mutableblockpos);
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

    @Unique
    private static Optional<BlockState> potions_plus$tryPlaceBlockState(WorldGenLevel level, BulkSectionAccess bulkSectionAccess, BlockState blockstate, BlockPos pos) {
        if (level.ensureCanWrite(pos)) {
            int x = SectionPos.sectionRelative(pos.getX());
            int y = SectionPos.sectionRelative(pos.getY());
            int z = SectionPos.sectionRelative(pos.getZ());
            LevelChunkSection section = bulkSectionAccess.getSection(pos);

            if (section != null) {
                return Optional.of(section.setBlockState(x, y, z, blockstate, false));
            }
        }

        return Optional.empty();
    }
}
