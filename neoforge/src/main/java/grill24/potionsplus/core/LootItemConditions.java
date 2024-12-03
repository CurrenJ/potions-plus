package grill24.potionsplus.core;

import com.mojang.serialization.MapCodec;
import grill24.potionsplus.loot.HasPlayerAbilityCondition;
import grill24.potionsplus.loot.LootItemBlockTagCondition;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.neoforged.neoforge.registries.DeferredRegister;

public class LootItemConditions {
    public static final DeferredRegister<LootItemConditionType> LOOT_ITEM_CONDITIONS = DeferredRegister.create(BuiltInRegistries.LOOT_CONDITION_TYPE, ModInfo.MOD_ID);

    public static final Holder<LootItemConditionType> HAS_PLAYER_ABILITY = register("has_player_ability", HasPlayerAbilityCondition.CODEC);
    public static final Holder<LootItemConditionType> LOOT_ITEM_BLOCK_TAG = register("loot_item_block_tag", LootItemBlockTagCondition.CODEC);

    private static Holder<LootItemConditionType> register(String name, MapCodec<? extends LootItemCondition> codec) {
        return LOOT_ITEM_CONDITIONS.register(name, () -> new LootItemConditionType(codec));
    }
}
