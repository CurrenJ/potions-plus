package grill24.potionsplus.core;

import grill24.potionsplus.worldgen.OffsetPlacement;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

public class PlacementModifierTypes {
    public static PlacementModifierType<OffsetPlacement> OFFSET = () -> OffsetPlacement.CODEC;
}
