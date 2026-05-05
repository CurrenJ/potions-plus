package grill24.potionsplus.core.neoforge;

import com.mojang.serialization.MapCodec;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.neoforged.neoforge.registries.DeferredRegister;

public class LootItemFunctions {
    public static final DeferredRegister<MapCodec<? extends LootItemFunction>> LOOT_ITEM_FUNCTIONS =
            DeferredRegister.create(Registries.LOOT_FUNCTION_TYPE, ModInfo.MOD_ID);
}
