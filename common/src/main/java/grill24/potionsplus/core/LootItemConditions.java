package grill24.potionsplus.core;

import com.mojang.serialization.MapCodec;
import grill24.potionsplus.loot.HasPlayerAbilityCondition;
import grill24.potionsplus.loot.IsInBiomeCondition;
import grill24.potionsplus.loot.IsInBiomeTagCondition;
import grill24.potionsplus.loot.LootItemBlockTagCondition;
import net.minecraft.core.Holder;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public class LootItemConditions {
    public static Holder<MapCodec<? extends LootItemCondition>> HAS_PLAYER_ABILITY;
    public static Holder<MapCodec<? extends LootItemCondition>> LOOT_ITEM_BLOCK_TAG;
    public static Holder<MapCodec<? extends LootItemCondition>> IS_IN_BIOME;
    public static Holder<MapCodec<? extends LootItemCondition>> IS_IN_BIOME_TAG;

    public static void init(BiFunction<String, Supplier<MapCodec<? extends LootItemCondition>>, Holder<MapCodec<? extends LootItemCondition>>> register) {
        HAS_PLAYER_ABILITY = register.apply("has_player_ability", () -> HasPlayerAbilityCondition.MAP_CODEC);
        LOOT_ITEM_BLOCK_TAG = register.apply("loot_item_block_tag", () -> LootItemBlockTagCondition.MAP_CODEC);
        IS_IN_BIOME = register.apply("is_in_biome", () -> IsInBiomeCondition.MAP_CODEC);
        IS_IN_BIOME_TAG = register.apply("is_in_biome_tag", () -> IsInBiomeTagCondition.MAP_CODEC);
    }
}
