package grill24.potionsplus.core;

import com.mojang.serialization.MapCodec;
import grill24.potionsplus.loot.HasPlayerAbilityCondition;
import grill24.potionsplus.loot.IsInBiomeCondition;
import grill24.potionsplus.loot.IsInBiomeTagCondition;
import grill24.potionsplus.loot.LootItemBlockTagCondition;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.registries.DeferredRegister;

public class LootItemConditions {
    public static final DeferredRegister<MapCodec<? extends LootItemCondition>> LOOT_ITEM_CONDITIONS =
            DeferredRegister.create(BuiltInRegistries.LOOT_CONDITION_TYPE, ModInfo.MOD_ID);

    public static final Holder<MapCodec<? extends LootItemCondition>> HAS_PLAYER_ABILITY =
            register("has_player_ability", HasPlayerAbilityCondition.MAP_CODEC);
    public static final Holder<MapCodec<? extends LootItemCondition>> LOOT_ITEM_BLOCK_TAG =
            register("loot_item_block_tag", LootItemBlockTagCondition.MAP_CODEC);
    public static final Holder<MapCodec<? extends LootItemCondition>> IS_IN_BIOME =
            register("is_in_biome", IsInBiomeCondition.MAP_CODEC);
    public static final Holder<MapCodec<? extends LootItemCondition>> IS_IN_BIOME_TAG =
            register("is_in_biome_tag", IsInBiomeTagCondition.MAP_CODEC);

    private static Holder<MapCodec<? extends LootItemCondition>> register(String name, MapCodec<? extends LootItemCondition> codec) {
        return LOOT_ITEM_CONDITIONS.register(name, () -> codec);
    }
}
