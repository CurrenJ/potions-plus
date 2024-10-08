package grill24.potionsplus.core;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;
import net.neoforged.neoforge.registries.DeferredRegister;

import static grill24.potionsplus.utility.Utility.ppId;

public class LootTables {
    public static final ResourceKey<LootTable> ARID_CAVE_SUSPICIOUS_SAND = ResourceKey.create(Registries.LOOT_TABLE, ppId("arid_cave_suspicious_sand"));
}
