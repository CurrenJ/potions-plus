package grill24.potionsplus.core.forge.blocks;

import grill24.potionsplus.block.PrecisionDispenserBlock;
import grill24.potionsplus.core.forge.Items;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * Only PRECISION_DISPENSER is portable to Forge in Phase 4: the other six BE-block classes
 * (BrewingCauldronBlock, HerbalistsLecternBlock, SanguineAltarBlock, AbyssalTroveBlock,
 * ClotheslineBlock, PotionBeaconBlock) are neoforge-only, deeply coupled to neoforge BEs,
 * networking, persistence and renderers, and are deferred to a later phase alongside their BEs.
 */
public class BlockEntityBlocks {
    public static Holder<Block> PRECISION_DISPENSER;

    public static void init(BiFunction<String, Supplier<Block>, Holder<Block>> registerBlock, BiFunction<String, Supplier<Item>, Holder<Item>> registerItem) {
        PRECISION_DISPENSER = registerBlock.apply("precision_dispenser",
                () -> new PrecisionDispenserBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(3.5F).sound(SoundType.METAL)));
        Items.registerBlockItemWithAutoModel(() -> PRECISION_DISPENSER, registerItem);

        // Populate common stubs (only the portable one)
        grill24.potionsplus.core.blocks.BlockEntityBlocks.PRECISION_DISPENSER = PRECISION_DISPENSER;
    }
}
