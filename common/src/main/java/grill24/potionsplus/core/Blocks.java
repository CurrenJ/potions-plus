package grill24.potionsplus.core;

import grill24.potionsplus.blockentity.ClotheslineBlockEntity;
import grill24.potionsplus.blockentity.PotionBeaconBlockEntity;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.entity.BlockEntityType;

/**
 * Loader-agnostic {@link BlockEntityType} holder hub, populated by each loader's own
 * {@code core.<loader>.Blocks} at registration time (mirrors how {@code core.blocks.BlockEntityBlocks}
 * holds the {@code Block} side of the same six block entities). See docs/multi-loader-expansion.md
 * Phase 11a.
 *
 * <p>Only {@code CLOTHESLINE_BLOCK_ENTITY} and {@code POTION_BEACON_BLOCK_ENTITY} are typed to their
 * concrete BE class: those two BE classes were ported to {@code common/} in Phase 11a. The other four
 * ({@code BrewingCauldronBlockEntity}, {@code HerbalistsLecternBlockEntity},
 * {@code SanguineAltarBlockEntity}, {@code AbyssalTroveBlockEntity}) are still neoforge-only (blocked
 * on the {@code DynamicIconItems}/{@code RecipesRegistrar} DSL - Phase 11a steps 3/4), so common code
 * can only see them as {@code BlockEntityType<?>}.
 */
public class Blocks {
    public static Holder<BlockEntityType<?>> BREWING_CAULDRON_BLOCK_ENTITY;
    public static Holder<BlockEntityType<?>> HERBALISTS_LECTERN_BLOCK_ENTITY;
    public static Holder<BlockEntityType<?>> SANGUINE_ALTAR_BLOCK_ENTITY;
    public static Holder<BlockEntityType<?>> ABYSSAL_TROVE_BLOCK_ENTITY;
    public static Holder<BlockEntityType<ClotheslineBlockEntity>> CLOTHESLINE_BLOCK_ENTITY;
    public static Holder<BlockEntityType<PotionBeaconBlockEntity>> POTION_BEACON_BLOCK_ENTITY;
}
