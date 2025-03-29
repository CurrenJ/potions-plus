package grill24.potionsplus.loot;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import grill24.potionsplus.core.LootItemConditions;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * A LootItemCondition that checks the {@linkplain LootContextParams#TOOL tool} against an {@link ItemPredicate}.
 */
public record IsInBiomeTagCondition(TagKey<Biome> biomeTag) implements LootItemCondition {
    public static final MapCodec<IsInBiomeTagCondition> CODEC = RecordCodecBuilder.mapCodec(codecBuilder -> codecBuilder.group(
           TagKey.codec(Registries.BIOME).fieldOf("biomeTag").forGetter(IsInBiomeTagCondition::biomeTag)).apply(codecBuilder, IsInBiomeTagCondition::new)
    );

    @Override
    public LootItemConditionType getType() {
        return LootItemConditions.IS_IN_BIOME_TAG.value();
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return ImmutableSet.of(LootContextParams.THIS_ENTITY, LootContextParams.ATTACKING_ENTITY);
    }

    public boolean test(LootContext context) {
        List<Entity> entitiesToCheck = new ArrayList<>();

        Optional.ofNullable(context.getParamOrNull(LootContextParams.THIS_ENTITY))
                .ifPresent(entitiesToCheck::add);
        Optional.ofNullable(context.getParamOrNull(LootContextParams.ATTACKING_ENTITY))
                .ifPresent(entitiesToCheck::add);

        Level level = context.getLevel();
        Iterable<Holder<Biome>> biomes = level.registryAccess().registryOrThrow(Registries.BIOME).getTagOrEmpty(biomeTag);
        for (Holder<Biome> biome : biomes) {
            for (Entity entity : entitiesToCheck) {
                Optional<ResourceKey<Biome>> currentBiome = Optional.ofNullable(level.getBiome(entity.blockPosition()).getKey());
                if (currentBiome.isPresent() && biome.is(currentBiome.get())) {
                    return true;
                }
            }
        }

        return false;
    }

    public static Builder isInBiomeTag(TagKey<Biome> key) {
        return () -> new IsInBiomeTagCondition(key);
    }
}
