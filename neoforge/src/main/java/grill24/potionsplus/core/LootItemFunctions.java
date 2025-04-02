package grill24.potionsplus.core;

import grill24.potionsplus.function.SetFishSizeFunction;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class LootItemFunctions {
    public static final DeferredRegister<LootItemFunctionType<?>> LOOT_ITEM_FUNCTIONS = DeferredRegister.create(Registries.LOOT_FUNCTION_TYPE, ModInfo.MOD_ID);

    public static final DeferredHolder<LootItemFunctionType<?>, LootItemFunctionType<SetFishSizeFunction>> SET_FISH_SIZE = LOOT_ITEM_FUNCTIONS.register("set_fish_size", () -> new LootItemFunctionType<>(SetFishSizeFunction.CODEC));
}
