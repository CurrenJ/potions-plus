package grill24.potionsplus.core;

import grill24.potionsplus.utility.ModInfo;
import grill24.potionsplus.worldgen.OffsetPlacement;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class PlacementModifierTypes {
    public static final DeferredRegister<PlacementModifierType<?>> PLACEMENT_MODIFIER_TYPES = DeferredRegister.create(Registries.PLACEMENT_MODIFIER_TYPE, ModInfo.MOD_ID);

    public static final DeferredHolder<PlacementModifierType<?>, PlacementModifierType<OffsetPlacement>> OFFSET = PLACEMENT_MODIFIER_TYPES.register("offset", () -> () -> OffsetPlacement.CODEC);
}
