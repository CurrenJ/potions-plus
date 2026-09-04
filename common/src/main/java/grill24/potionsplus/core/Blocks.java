package grill24.potionsplus.core;

import grill24.potionsplus.blockentity.AbyssalTroveBlockEntity;
import grill24.potionsplus.blockentity.BrewingCauldronBlockEntity;
import grill24.potionsplus.blockentity.ClotheslineBlockEntity;
import grill24.potionsplus.blockentity.HerbalistsLecternBlockEntity;
import grill24.potionsplus.blockentity.PotionBeaconBlockEntity;
import grill24.potionsplus.blockentity.SanguineAltarBlockEntity;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.entity.BlockEntityType;

/**
 * Loader-agnostic {@link BlockEntityType} holder hub, populated by each loader's own
 * {@code core.<loader>.Blocks} at registration time (mirrors how {@code core.blocks.BlockEntityBlocks}
 * holds the {@code Block} side of the same six block entities). See docs/multi-loader-expansion.md
 * Phase 11a.
 *
 * <p>All six BE classes (and their {@code Block} classes) are now in {@code common/}, so every field
 * here is typed to its concrete BE class. {@code SanguineAltarBlockEntity} was the last holdout: its
 * two sync packets ({@code ClientboundSanguineAltarConversionProgressPacket}/
 * {@code ...StatePacket}) moved to {@code common/network/} alongside it.
 */
public class Blocks {
    public static Holder<BlockEntityType<BrewingCauldronBlockEntity>> BREWING_CAULDRON_BLOCK_ENTITY;
    public static Holder<BlockEntityType<HerbalistsLecternBlockEntity>> HERBALISTS_LECTERN_BLOCK_ENTITY;
    public static Holder<BlockEntityType<SanguineAltarBlockEntity>> SANGUINE_ALTAR_BLOCK_ENTITY;
    public static Holder<BlockEntityType<AbyssalTroveBlockEntity>> ABYSSAL_TROVE_BLOCK_ENTITY;
    public static Holder<BlockEntityType<ClotheslineBlockEntity>> CLOTHESLINE_BLOCK_ENTITY;
    public static Holder<BlockEntityType<PotionBeaconBlockEntity>> POTION_BEACON_BLOCK_ENTITY;
}
