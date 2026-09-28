package grill24.potionsplus.core.forge;

import com.mojang.serialization.MapCodec;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraftforge.registries.DeferredRegister;

public class LootItemFunctions {
    public static final DeferredRegister<MapCodec<? extends LootItemFunction>> LOOT_ITEM_FUNCTIONS =
            DeferredRegister.create(Registries.LOOT_FUNCTION_TYPE, ModInfo.MOD_ID);
}
