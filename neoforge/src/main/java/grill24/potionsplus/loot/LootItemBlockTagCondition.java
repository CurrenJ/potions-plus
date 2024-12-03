package grill24.potionsplus.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import grill24.potionsplus.core.LootItemConditions;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

import java.util.Set;

/**
 * A LootItemCondition that checks whether the {@linkplain LootContextParams#BLOCK_STATE block state} matches a given Block and {@link StatePropertiesPredicate}.
 */
public record LootItemBlockTagCondition(TagKey<Block> block) implements LootItemCondition {
    public static final MapCodec<LootItemBlockTagCondition> CODEC = RecordCodecBuilder.<LootItemBlockTagCondition>mapCodec(
            p_344716_ -> p_344716_.group(
                        TagKey.codec(Registries.BLOCK).fieldOf("blockTag").forGetter(LootItemBlockTagCondition::block)
                    )
                    .apply(p_344716_, LootItemBlockTagCondition::new)
        );

    @Override
    public LootItemConditionType getType() {
        return LootItemConditions.LOOT_ITEM_BLOCK_TAG.value();
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return Set.of(LootContextParams.BLOCK_STATE);
    }

    public boolean test(LootContext context) {
        BlockState blockstate = context.getParamOrNull(LootContextParams.BLOCK_STATE);
        return blockstate != null && blockstate.is(this.block);
    }

    public static LootItemBlockTagCondition.Builder tag(TagKey<Block> block) {
        return new LootItemBlockTagCondition.Builder(block);
    }

    public static class Builder implements LootItemCondition.Builder {
        private final TagKey<Block> block;

        public Builder(TagKey<Block> block) {
            this.block = block;
        }

        @Override
        public LootItemCondition build() {
            return new LootItemBlockTagCondition(this.block);
        }
    }
}
