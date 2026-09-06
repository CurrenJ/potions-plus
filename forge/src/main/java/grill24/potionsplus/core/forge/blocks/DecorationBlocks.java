package grill24.potionsplus.core.forge.blocks;

import grill24.potionsplus.block.GrowableMossyBlock;
import grill24.potionsplus.block.GrowableMossySlab;
import grill24.potionsplus.block.GrowableMossyStairs;
import grill24.potionsplus.core.forge.Items;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public class DecorationBlocks {
    public static Holder<Block> GROWING_MOSSY_COBBLESTONE, GROWING_MOSSY_STONE_BRICKS;
    public static Holder<Block> GROWING_MOSSY_COBBLESTONE_SLAB, GROWING_MOSSY_COBBLESTONE_STAIRS;
    public static Holder<Block> GROWING_MOSSY_STONE_BRICK_SLAB, GROWING_MOSSY_STONE_BRICK_STAIRS;

    public static void init(BiFunction<String, Supplier<Block>, Holder<Block>> registerBlock, BiFunction<String, Supplier<Item>, Holder<Item>> registerItem) {
        GROWING_MOSSY_COBBLESTONE = registerBlock.apply("growing_mossy_cobblestone",
                () -> new GrowableMossyBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(2.0F, 6.0F).randomTicks(), Blocks.MOSSY_COBBLESTONE));
        Items.registerBlockItem(GROWING_MOSSY_COBBLESTONE, registerItem);

        GROWING_MOSSY_STONE_BRICKS = registerBlock.apply("growing_mossy_stone_bricks",
                () -> new GrowableMossyBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(1.5F, 6.0F).randomTicks(), Blocks.MOSSY_STONE_BRICKS));
        Items.registerBlockItem(GROWING_MOSSY_STONE_BRICKS, registerItem);

        GROWING_MOSSY_COBBLESTONE_SLAB = registerBlock.apply("growing_mossy_cobblestone_slab",
                () -> new GrowableMossySlab(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(2.0F, 6.0F).randomTicks(), Blocks.MOSSY_COBBLESTONE_SLAB));
        Items.registerBlockItem(GROWING_MOSSY_COBBLESTONE_SLAB, registerItem);

        GROWING_MOSSY_COBBLESTONE_STAIRS = registerBlock.apply("growing_mossy_cobblestone_stairs",
                () -> new GrowableMossyStairs(() -> Blocks.COBBLESTONE.defaultBlockState(), BlockBehaviour.Properties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(2.0F, 6.0F).randomTicks(), Blocks.MOSSY_COBBLESTONE_STAIRS));
        Items.registerBlockItem(GROWING_MOSSY_COBBLESTONE_STAIRS, registerItem);

        GROWING_MOSSY_STONE_BRICK_SLAB = registerBlock.apply("growing_mossy_stone_brick_slab",
                () -> new GrowableMossySlab(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(1.5F, 6.0F).randomTicks(), Blocks.MOSSY_STONE_BRICK_SLAB));
        Items.registerBlockItem(GROWING_MOSSY_STONE_BRICK_SLAB, registerItem);

        GROWING_MOSSY_STONE_BRICK_STAIRS = registerBlock.apply("growing_mossy_stone_brick_stairs",
                () -> new GrowableMossyStairs(() -> Blocks.STONE_BRICKS.defaultBlockState(), BlockBehaviour.Properties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(1.5F, 6.0F).randomTicks(), Blocks.MOSSY_STONE_BRICK_STAIRS));
        Items.registerBlockItem(GROWING_MOSSY_STONE_BRICK_STAIRS, registerItem);

        // Populate common stubs
        grill24.potionsplus.core.blocks.DecorationBlocks.GROWING_MOSSY_COBBLESTONE = GROWING_MOSSY_COBBLESTONE;
        grill24.potionsplus.core.blocks.DecorationBlocks.GROWING_MOSSY_STONE_BRICKS = GROWING_MOSSY_STONE_BRICKS;
        grill24.potionsplus.core.blocks.DecorationBlocks.GROWING_MOSSY_COBBLESTONE_SLAB = GROWING_MOSSY_COBBLESTONE_SLAB;
        grill24.potionsplus.core.blocks.DecorationBlocks.GROWING_MOSSY_COBBLESTONE_STAIRS = GROWING_MOSSY_COBBLESTONE_STAIRS;
        grill24.potionsplus.core.blocks.DecorationBlocks.GROWING_MOSSY_STONE_BRICK_SLAB = GROWING_MOSSY_STONE_BRICK_SLAB;
        grill24.potionsplus.core.blocks.DecorationBlocks.GROWING_MOSSY_STONE_BRICK_STAIRS = GROWING_MOSSY_STONE_BRICK_STAIRS;
    }
}
