package grill24.potionsplus.core.fabric.blocks;

import grill24.potionsplus.block.ClotheslineBlock;
import grill24.potionsplus.block.PotionBeaconBlock;
import grill24.potionsplus.block.PrecisionDispenserBlock;
import grill24.potionsplus.core.fabric.Items;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * PRECISION_DISPENSER, CLOTHESLINE and POTION_BEACON are portable to Fabric: their BE classes and
 * (as of Phase 11a) Block classes live in common/. The other three BE-block classes
 * (BrewingCauldronBlock, HerbalistsLecternBlock, SanguineAltarBlock, AbyssalTroveBlock) are still
 * neoforge-only, deeply coupled to the {@code DynamicIconItems}/{@code RecipesRegistrar} registration
 * DSL, and are deferred to a later phase alongside their BEs.
 *
 * <p>Clothesline/PotionBeacon are registered here with the plain
 * {@code registerBlock.apply(name, supplier)} path (no model/recipe/loot generation) rather than the
 * neoforge-only {@code RegistrationUtility}/{@code SimpleBlockBuilder} DSL - Decision 5 keeps NeoForge
 * as datagen source of truth and shares its output via {@code commonDatagen}, so Fabric/Forge never
 * need to generate their own models/recipes/loot at runtime.
 */
public class BlockEntityBlocks {
    public static Holder<Block> PRECISION_DISPENSER;
    public static Holder<Block> CLOTHESLINE;
    public static Holder<Block> POTION_BEACON;

    public static void init(BiFunction<String, Supplier<Block>, Holder<Block>> registerBlock, BiFunction<String, Supplier<Item>, Holder<Item>> registerItem) {
        PRECISION_DISPENSER = registerBlock.apply("precision_dispenser",
                () -> new PrecisionDispenserBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(3.5F).sound(SoundType.METAL)));
        Items.registerBlockItemWithAutoModel(() -> PRECISION_DISPENSER, registerItem);

        CLOTHESLINE = registerBlock.apply("clothesline",
                () -> new ClotheslineBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).noOcclusion().strength(0.8F).sound(SoundType.WOOD)));
        Items.registerBlockItem(CLOTHESLINE, registerItem); // No auto item model - NeoForge datagen generates a custom one (ClotheslineBlockModelGenerator).

        POTION_BEACON = registerBlock.apply("potion_beacon",
                () -> new PotionBeaconBlock(BlockBehaviour.Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.BEACON).mapColor(MapColor.WOOD).requiresCorrectToolForDrops().strength(2.5F).sound(SoundType.WOOD)));
        Items.registerBlockItemWithAutoModel(() -> POTION_BEACON, registerItem);

        // Populate common stubs
        grill24.potionsplus.core.blocks.BlockEntityBlocks.PRECISION_DISPENSER = PRECISION_DISPENSER;
        grill24.potionsplus.core.blocks.BlockEntityBlocks.CLOTHESLINE = CLOTHESLINE;
        grill24.potionsplus.core.blocks.BlockEntityBlocks.POTION_BEACON = POTION_BEACON;
    }
}
