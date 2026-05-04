package grill24.potionsplus.core;

import grill24.potionsplus.block.GeneticCropBlockEntity;
import grill24.potionsplus.blockentity.*;
import grill24.potionsplus.blockentity.filterhopper.HugeFilterHopperBlockEntity;
import grill24.potionsplus.blockentity.filterhopper.LargeFilterHopperBlockEntity;
import grill24.potionsplus.blockentity.filterhopper.SmallFilterHopperBlockEntity;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class Blocks {
    public static Holder<BlockEntityType<BrewingCauldronBlockEntity>> BREWING_CAULDRON_BLOCK_ENTITY;
    public static Holder<BlockEntityType<HerbalistsLecternBlockEntity>> HERBALISTS_LECTERN_BLOCK_ENTITY;
    public static Holder<BlockEntityType<SanguineAltarBlockEntity>> SANGUINE_ALTAR_BLOCK_ENTITY;
    public static Holder<BlockEntityType<AbyssalTroveBlockEntity>> ABYSSAL_TROVE_BLOCK_ENTITY;
    public static Holder<BlockEntityType<ClotheslineBlockEntity>> CLOTHESLINE_BLOCK_ENTITY;
    public static Holder<BlockEntityType<PotionBeaconBlockEntity>> POTION_BEACON_BLOCK_ENTITY;
    public static Holder<BlockEntityType<SmallFilterHopperBlockEntity>> SMALL_FILTER_HOPPER_BLOCK_ENTITY;
    public static Holder<BlockEntityType<LargeFilterHopperBlockEntity>> LARGE_FILTER_HOPPER_BLOCK_ENTITY;
    public static Holder<BlockEntityType<HugeFilterHopperBlockEntity>> HUGE_FILTER_HOPPER_BLOCK_ENTITY;
    public static Holder<BlockEntityType<GeneticCropBlockEntity>> GENETIC_CROP_BLOCK_ENTITY;
}
