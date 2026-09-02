package grill24.potionsplus.loot;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import grill24.potionsplus.core.LootItemConditions;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * A LootItemCondition that checks whether any checked entity's current biome is in a given set.
 */
public record IsInBiomeCondition(ImmutableSet<ResourceKey<Biome>> biomes) implements LootItemCondition {
    public static final MapCodec<IsInBiomeCondition> CODEC = RecordCodecBuilder.mapCodec(codecBuilder -> codecBuilder.group(
            ResourceKey.codec(Registries.BIOME).listOf()
                    .xmap(ImmutableSet::copyOf, ArrayList::new)
                    .fieldOf("biomes").forGetter(IsInBiomeCondition::biomes)).apply(codecBuilder, IsInBiomeCondition::new)
    );

    public IsInBiomeCondition {
        // Sort the biomes to ensure consistent serialization - otherwise the order may change between runs
        biomes = biomes.stream().sorted().collect(ImmutableSet.toImmutableSet());
    }

    @Override
    public @NotNull LootItemConditionType getType() {
        return LootItemConditions.IS_IN_BIOME.value();
    }

    @Override
    public @NotNull Set<LootContextParam<?>> getReferencedContextParams() {
        return ImmutableSet.of(LootContextParams.THIS_ENTITY, LootContextParams.ATTACKING_ENTITY);
    }

    public boolean test(LootContext context) {
        List<Entity> entitiesToCheck = new ArrayList<>();

        Optional.ofNullable(context.getParamOrNull(LootContextParams.THIS_ENTITY))
                .ifPresent(entitiesToCheck::add);
        Optional.ofNullable(context.getParamOrNull(LootContextParams.ATTACKING_ENTITY))
                .ifPresent(entitiesToCheck::add);

        Level level = context.getLevel();
        for (Entity entity : entitiesToCheck) {
            Optional<ResourceKey<Biome>> currentBiome = level.getBiome(entity.blockPosition()).unwrapKey();
            if (currentBiome.isPresent() && biomes.contains(currentBiome.get())) {
                return true;
            }
        }

        return false;
    }

    @SafeVarargs
    public static Builder isInBiome(ResourceKey<Biome>... key) {
        return () -> new IsInBiomeCondition(ImmutableSet.copyOf(Set.of(key)));
    }
}
