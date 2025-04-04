package grill24.potionsplus.mixin;

import com.mojang.serialization.Codec;
import grill24.potionsplus.core.Biomes;
import grill24.potionsplus.core.blocks.OreBlocks;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.util.Lazy;
import net.minecraft.core.BlockPos;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.MonsterRoomFeature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Predicate;

@Mixin(MonsterRoomFeature.class)
public abstract class MonsterRoomFeatureMixin extends Feature<NoneFeatureConfiguration> {
    private static final Lazy<WeightedStateProvider> SPAWNABLE = Lazy.of(() -> new WeightedStateProvider(SimpleWeightedRandomList .<BlockState>builder()
            .add(OreBlocks.MOSSY_COAL_ORE.value().defaultBlockState(), 1)
            .add(OreBlocks.MOSSY_COPPER_ORE.value().defaultBlockState(), 1)
            .add(OreBlocks.MOSSY_IRON_ORE.value().defaultBlockState(), 1)
            .add(OreBlocks.MOSSY_GOLD_ORE.value().defaultBlockState(), 2)
            .add(OreBlocks.MOSSY_LAPIS_ORE.value().defaultBlockState(), 2)
            .add(OreBlocks.MOSSY_REDSTONE_ORE.value().defaultBlockState(), 2)
            .add(OreBlocks.MOSSY_DIAMOND_ORE.value().defaultBlockState(), 5)
            .add(OreBlocks.MOSSY_EMERALD_ORE.value().defaultBlockState(), 5)
    ));
    
    public MonsterRoomFeatureMixin(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Redirect(method = "place", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/feature/MonsterRoomFeature;safeSetBlock(Lnet/minecraft/world/level/WorldGenLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Ljava/util/function/Predicate;)V"))
    private void place(MonsterRoomFeature instance, WorldGenLevel worldGenLevel, BlockPos pos, BlockState blockState, Predicate predicate) {
        if (blockState.is(Blocks.MOSSY_COBBLESTONE)) {
            if (worldGenLevel.getRandom().nextInt(5) == 0) {
                super.safeSetBlock(worldGenLevel, pos, SPAWNABLE.get().getState(worldGenLevel.getRandom(), BlockPos.ZERO), predicate);
            } else {
                super.safeSetBlock(worldGenLevel, pos, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), predicate);
            }
        } else {
            super.safeSetBlock(worldGenLevel, pos, blockState, predicate);
        }
    }

    @Redirect(method = "place", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/SpawnerBlockEntity;setEntityId(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/util/RandomSource;)V"))
    private void setEntityId(SpawnerBlockEntity spawnerBlockEntity, EntityType<?> entityType, RandomSource random, FeaturePlaceContext<NoneFeatureConfiguration> context) {
        Level level = context.level().getLevel();
        Holder<Biome> biome = level.getBiome(spawnerBlockEntity.getBlockPos());
        if (biome.is(Tags.Biomes.IS_COLD) || biome.is(Tags.Biomes.IS_COLD_OVERWORLD) && entityType == EntityType.SKELETON) {
            spawnerBlockEntity.setEntityId(EntityType.STRAY, level.getRandom());
        } else if (biome.is(Tags.Biomes.IS_DRY) || biome.is(Tags.Biomes.IS_DRY_OVERWORLD) && entityType == EntityType.ZOMBIE) {
            spawnerBlockEntity.setEntityId(EntityType.HUSK, level.getRandom());
        } else if (biome.is(Biomes.VOLCANIC_CAVE_KEY) && entityType == EntityType.SKELETON) {
            spawnerBlockEntity.setEntityId(EntityType.WITHER_SKELETON, level.getRandom());
        } else {
            spawnerBlockEntity.setEntityId(entityType, level.getRandom());
        }
    }
}
