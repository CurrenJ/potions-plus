package grill24.potionsplus.core;

import grill24.potionsplus.loot.IsInBiomeCondition;
import grill24.potionsplus.loot.IsInBiomeTagCondition;
import grill24.potionsplus.loot.LootItemBlockTagCondition;
import net.minecraft.core.Holder;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * Loader-agnostic loot item condition hub. The {@code DeferredRegister} lives in the loader module,
 * which calls {@link #init} to register the three condition types wrapped in their
 * {@link LootItemConditionType}s. See docs/multi-loader-expansion.md Phase 4.
 */
public class LootItemConditions {
    public static Holder<LootItemConditionType> LOOT_ITEM_BLOCK_TAG;
    public static Holder<LootItemConditionType> IS_IN_BIOME;
    public static Holder<LootItemConditionType> IS_IN_BIOME_TAG;

    public static void init(BiFunction<String, Supplier<LootItemConditionType>, Holder<LootItemConditionType>> register) {
        LOOT_ITEM_BLOCK_TAG = register.apply("loot_item_block_tag", () -> new LootItemConditionType(LootItemBlockTagCondition.CODEC));
        IS_IN_BIOME = register.apply("is_in_biome", () -> new LootItemConditionType(IsInBiomeCondition.CODEC));
        IS_IN_BIOME_TAG = register.apply("is_in_biome_tag", () -> new LootItemConditionType(IsInBiomeTagCondition.CODEC));
    }
}
