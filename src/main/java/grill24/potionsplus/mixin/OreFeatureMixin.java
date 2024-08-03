package grill24.potionsplus.mixin;

import com.mojang.serialization.Codec;
import grill24.potionsplus.core.Blocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.WorldGenLevel;
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
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;
import java.util.function.Function;

@Mixin(OreFeature.class)
public abstract class OreFeatureMixin extends Feature<OreConfiguration> {
    public OreFeatureMixin(Codec<OreConfiguration> oreConfigurationCodec) {
        super(oreConfigurationCodec);
    }

    @Unique
    private static WorldGenLevel potions_plus$worldGenLevel;
    @Unique
    private static Random potions_plus$random;
    @Unique
    private static BulkSectionAccess potions_plus$bulkSectionAccess;
    @Unique
    private static BlockPos.MutableBlockPos potions_plus$mutable;

    @Inject(method = "doPlace", at = @At("HEAD"))
    public void doPlaceInject(WorldGenLevel worldGenLevel, Random random, OreConfiguration p_66535_, double p_66536_, double p_66537_, double p_66538_, double p_66539_, double p_66540_, double p_66541_, int p_66542_, int p_66543_, int p_66544_, int p_66545_, int p_66546_, CallbackInfoReturnable<Boolean> cir) {
        potions_plus$worldGenLevel = worldGenLevel;
        potions_plus$random = random;
    }

    @Redirect(method = "doPlace", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/chunk/BulkSectionAccess;getSection(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/chunk/LevelChunkSection;"))
    public LevelChunkSection getSectionRedirect(BulkSectionAccess instance, BlockPos blockPos) {
        potions_plus$bulkSectionAccess = instance;
        return potions_plus$bulkSectionAccess.getSection(blockPos);
    }

    @Redirect(method = "doPlace", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/feature/OreFeature;canPlaceOre(Lnet/minecraft/world/level/block/state/BlockState;Ljava/util/function/Function;Ljava/util/Random;Lnet/minecraft/world/level/levelgen/feature/configurations/OreConfiguration;Lnet/minecraft/world/level/levelgen/feature/configurations/OreConfiguration$TargetBlockState;Lnet/minecraft/core/BlockPos$MutableBlockPos;)Z"))
    public boolean canPlaceOreRedirect(BlockState p_160170_, Function<BlockPos, BlockState> p_160171_, Random p_160172_, OreConfiguration p_160173_, OreConfiguration.TargetBlockState p_160174_, BlockPos.MutableBlockPos mutableBlockPos) {
        potions_plus$mutable = mutableBlockPos;
        return OreFeature.canPlaceOre(p_160170_, p_160171_, p_160172_, p_160173_, p_160174_, mutableBlockPos);
    }

    @Redirect(method = "doPlace", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/chunk/LevelChunkSection;setBlockState(IIILnet/minecraft/world/level/block/state/BlockState;Z)Lnet/minecraft/world/level/block/state/BlockState;"))
    public BlockState setBlockStateRedirect(LevelChunkSection instance, int p_62992_, int p_62993_, int p_62994_, BlockState blockState, boolean p_62996_) {
        grill24.potionsplus.core.Blocks.ORE_FLOWER_BLOCKS.forEach(
                block -> {
                    if (block.mayPlaceOn(blockState)) {
                        BlockPos abovePos = new BlockPos(potions_plus$mutable.getX(), potions_plus$mutable.getY() + 1, potions_plus$mutable.getZ());
                        BlockState above = potions_plus$bulkSectionAccess.getBlockState(abovePos);
                        if (above.isAir() && potions_plus$random.nextFloat() < block.getGenerationChance() && potions_plus$worldGenLevel.ensureCanWrite(abovePos)) {
                            System.out.println("Placed " + block + " at " + abovePos);

                            int x = SectionPos.sectionRelative(potions_plus$mutable.getX());
                            int y = SectionPos.sectionRelative(potions_plus$mutable.getY() + 1);
                            int z = SectionPos.sectionRelative(potions_plus$mutable.getZ());
                            LevelChunkSection section = potions_plus$bulkSectionAccess.getSection(potions_plus$mutable);

                            if (section != null) {
                                section.setBlockState(x, y, z, block.defaultBlockState(), false);
                            }
                        }
                    }
                }
        );

        if (blockState.is(Blocks.DENSE_DIAMOND_ORE.get())) {
            System.out.println("Placed dense diamond ore at " + potions_plus$mutable);
        }

        return instance.setBlockState(p_62992_, p_62993_, p_62994_, blockState, false);
    }
}
