package grill24.potionsplus.mixin;

import com.mojang.serialization.Codec;
import grill24.potionsplus.core.Biomes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.neoforged.neoforge.common.Tags;
import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.MonsterRoomFeature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MonsterRoomFeature.class)
public abstract class MonsterRoomFeatureMixin extends Feature<NoneFeatureConfiguration> {
    public MonsterRoomFeatureMixin(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
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
