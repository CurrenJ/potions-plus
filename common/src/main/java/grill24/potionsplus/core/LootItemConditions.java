package grill24.potionsplus.core;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class LootItemConditions {
    public static Holder<MapCodec<? extends LootItemCondition>> HAS_PLAYER_ABILITY;
    public static Holder<MapCodec<? extends LootItemCondition>> LOOT_ITEM_BLOCK_TAG;
    public static Holder<MapCodec<? extends LootItemCondition>> IS_IN_BIOME;
    public static Holder<MapCodec<? extends LootItemCondition>> IS_IN_BIOME_TAG;
}
