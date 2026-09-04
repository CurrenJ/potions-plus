package grill24.potionsplus.core.forge.blocks;

import grill24.potionsplus.block.AbyssalTroveBlock;
import grill24.potionsplus.block.BrewingCauldronBlock;
import grill24.potionsplus.block.ClotheslineBlock;
import grill24.potionsplus.block.HerbalistsLecternBlock;
import grill24.potionsplus.block.PotionBeaconBlock;
import grill24.potionsplus.block.PrecisionDispenserBlock;
import grill24.potionsplus.block.SanguineAltarBlock;
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
 * All six block-entity blocks (PRECISION_DISPENSER, CLOTHESLINE, POTION_BEACON, BREWING_CAULDRON,
 * HERBALISTS_LECTERN, ABYSSAL_TROVE and SANGUINE_ALTAR) are portable to Forge: their BE classes and
 * Block classes all live in common/ as of Phase 11a.
 *
 * <p>These are registered here with the plain {@code registerBlock.apply(name, supplier)} path (no
 * model/recipe/loot generation) rather than the neoforge-only
 * {@code RegistrationUtility}/{@code SimpleBlockBuilder} DSL - Decision 5 keeps NeoForge as datagen
 * source of truth and shares its output via {@code commonDatagen}, so Fabric/Forge never need to
 * generate their own models/recipes/loot at runtime.
 */
public class BlockEntityBlocks {
    public static Holder<Block> PRECISION_DISPENSER;
    public static Holder<Block> CLOTHESLINE;
    public static Holder<Block> POTION_BEACON;
    public static Holder<Block> BREWING_CAULDRON;
    public static Holder<Block> HERBALISTS_LECTERN;
    public static Holder<Block> ABYSSAL_TROVE;
    public static Holder<Block> SANGUINE_ALTAR;

    public static void init(BiFunction<String, Supplier<Block>, Holder<Block>> registerBlock, BiFunction<String, Supplier<Item>, Holder<Item>> registerItem) {
        PRECISION_DISPENSER = registerBlock.apply("precision_dispenser",
                () -> new PrecisionDispenserBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(3.5F).sound(SoundType.METAL)));
        Items.registerBlockItemWithAutoModel(() -> PRECISION_DISPENSER, registerItem);

        BREWING_CAULDRON = registerBlock.apply("brewing_cauldron",
                () -> new BrewingCauldronBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).requiresCorrectToolForDrops().strength(1.0F).noOcclusion()));
        Items.registerBlockItemWithAutoModel(() -> BREWING_CAULDRON, registerItem);

        CLOTHESLINE = registerBlock.apply("clothesline",
                () -> new ClotheslineBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).noOcclusion().strength(0.8F).sound(SoundType.WOOD)));
        Items.registerBlockItem(CLOTHESLINE, registerItem); // No auto item model - NeoForge datagen generates a custom one (ClotheslineBlockModelGenerator).

        POTION_BEACON = registerBlock.apply("potion_beacon",
                () -> new PotionBeaconBlock(BlockBehaviour.Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.BEACON).mapColor(MapColor.WOOD).requiresCorrectToolForDrops().strength(2.5F).sound(SoundType.WOOD)));
        Items.registerBlockItemWithAutoModel(() -> POTION_BEACON, registerItem);

        HERBALISTS_LECTERN = registerBlock.apply("herbalists_lectern",
                () -> new HerbalistsLecternBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(2.5F).sound(SoundType.WOOD)));
        Items.registerBlockItemWithAutoModel(() -> HERBALISTS_LECTERN, registerItem);

        ABYSSAL_TROVE = registerBlock.apply("abyssal_trove",
                () -> new AbyssalTroveBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).strength(5.0F, 6.0F).sound(SoundType.SOUL_SAND)));
        Items.registerBlockItemWithAutoModel(() -> ABYSSAL_TROVE, registerItem);

        SANGUINE_ALTAR = registerBlock.apply("sanguine_altar",
                () -> new SanguineAltarBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE).strength(2.5F).requiresCorrectToolForDrops().sound(SoundType.STONE)));
        Items.registerBlockItemWithAutoModel(() -> SANGUINE_ALTAR, registerItem);

        // Populate common stubs
        grill24.potionsplus.core.blocks.BlockEntityBlocks.PRECISION_DISPENSER = PRECISION_DISPENSER;
        grill24.potionsplus.core.blocks.BlockEntityBlocks.CLOTHESLINE = CLOTHESLINE;
        grill24.potionsplus.core.blocks.BlockEntityBlocks.POTION_BEACON = POTION_BEACON;
        grill24.potionsplus.core.blocks.BlockEntityBlocks.BREWING_CAULDRON = BREWING_CAULDRON;
        grill24.potionsplus.core.blocks.BlockEntityBlocks.HERBALISTS_LECTERN = HERBALISTS_LECTERN;
        grill24.potionsplus.core.blocks.BlockEntityBlocks.ABYSSAL_TROVE = ABYSSAL_TROVE;
        grill24.potionsplus.core.blocks.BlockEntityBlocks.SANGUINE_ALTAR = SANGUINE_ALTAR;
    }
}
