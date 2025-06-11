package grill24.potionsplus.mixin;

import com.mojang.serialization.Codec;
import grill24.potionsplus.block.PotionsPlusOreBlock;
import grill24.potionsplus.core.Biomes;
import grill24.potionsplus.core.blocks.OreBlocks;
import grill24.potionsplus.utility.registration.RuntimeTextureVariantModelGenerator;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.util.Lazy;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.MonsterRoomFeature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Predicate;

@Mixin(MonsterRoomFeature.class)
public abstract class MonsterRoomFeatureMixin extends Feature<NoneFeatureConfiguration> {
    @Unique
    private static final Lazy<WeightedStateProvider> potions_plus$SPAWNABLE = Lazy.of(() -> new WeightedStateProvider(WeightedList.<BlockState>builder()
            .add(potions_plus$getMossyOre(OreBlocks.STONEY_COAL_ORE), 1)
            .add(potions_plus$getMossyOre(OreBlocks.STONEY_COPPER_ORE), 1)
            .add(potions_plus$getMossyOre(OreBlocks.STONEY_IRON_ORE), 1)
            .add(potions_plus$getMossyOre(OreBlocks.STONEY_GOLD_ORE), 1)
            .add(potions_plus$getMossyOre(OreBlocks.STONEY_LAPIS_ORE), 1)
            .add(potions_plus$getMossyOre(OreBlocks.STONEY_REDSTONE_ORE), 1)
            .add(potions_plus$getMossyOre(OreBlocks.STONEY_DIAMOND_ORE), 1)
            .add(potions_plus$getMossyOre(OreBlocks.STONEY_EMERALD_ORE), 1)
    ));

    @Unique
    private static BlockState potions_plus$getMossyOre(Holder<Block> blockHolder) {
        Block block = blockHolder.value();
        return RuntimeTextureVariantModelGenerator.getTextureVariantBlockState(block, new ItemStack(Blocks.MOSSY_COBBLESTONE), block.defaultBlockState(), PotionsPlusOreBlock.TEXTURE);
    }
    
    public MonsterRoomFeatureMixin(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Redirect(method = "place", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/feature/MonsterRoomFeature;safeSetBlock(Lnet/minecraft/world/level/WorldGenLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Ljava/util/function/Predicate;)V"))
    private void place(MonsterRoomFeature instance, WorldGenLevel worldGenLevel, BlockPos pos, BlockState blockState, Predicate predicate) {
        if (blockState.is(Blocks.MOSSY_COBBLESTONE)) {
            if (worldGenLevel.getRandom().nextInt(5) == 0) {
                super.safeSetBlock(worldGenLevel, pos, potions_plus$SPAWNABLE.get().getState(worldGenLevel.getRandom(), BlockPos.ZERO), predicate);
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
