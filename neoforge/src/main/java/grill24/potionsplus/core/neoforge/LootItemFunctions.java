package grill24.potionsplus.core.neoforge;

import grill24.potionsplus.utility.ModInfo;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.neoforged.neoforge.registries.DeferredRegister;

public class LootItemFunctions {
    public static final DeferredRegister<LootItemFunctionType<?>> LOOT_ITEM_FUNCTIONS = DeferredRegister.create(Registries.LOOT_FUNCTION_TYPE, ModInfo.MOD_ID);
}
