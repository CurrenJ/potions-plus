package grill24.potionsplus.core.neoforge.blocks;

import grill24.potionsplus.block.*;
import grill24.potionsplus.core.neoforge.Items;
import grill24.potionsplus.utility.registration.RecipeGeneratorUtility;
import grill24.potionsplus.utility.registration.neoforge.RegistrationUtility;
import grill24.potionsplus.utility.registration.block.BlockModelUtility;
import grill24.potionsplus.utility.registration.block.SimpleBlockBuilder;
import net.minecraft.core.Holder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.MapColor;

import java.util.function.BiFunction;
import java.util.function.Supplier;

import static grill24.potionsplus.data.neoforge.RecipeProvider.has;

public class DecorationBlocks {
    public static Holder<Block> GROWING_MOSSY_COBBLESTONE, GROWING_MOSSY_STONE_BRICKS;
    public static Holder<Block> GROWING_MOSSY_COBBLESTONE_SLAB, GROWING_MOSSY_COBBLESTONE_STAIRS;
    public static Holder<Block> GROWING_MOSSY_STONE_BRICK_SLAB, GROWING_MOSSY_STONE_BRICK_STAIRS;

    public static void init(BiFunction<String, Supplier<Block>, Holder<Block>> registerBlock, BiFunction<String, Supplier<Item>, Holder<Item>> registerItem) {
        GROWING_MOSSY_COBBLESTONE = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("growing_mossy_cobblestone")
                .blockFactory(prop -> new GrowableMossyBlock(prop.mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(2.0F, 6.0F).randomTicks(), Blocks.MOSSY_COBBLESTONE))
                .modelGenerator(holder -> new BlockModelUtility.BlockFamilyModelGenerator<>(holder, family -> family
                        .slab(GROWING_MOSSY_COBBLESTONE_SLAB.value())
                        .stairs(GROWING_MOSSY_COBBLESTONE_STAIRS.value())))
        ).getHolder();
        Items.registerBlockItem(GROWING_MOSSY_COBBLESTONE, registerItem);

        GROWING_MOSSY_STONE_BRICKS = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("growing_mossy_stone_bricks")
                .blockFactory(prop -> new GrowableMossyBlock(prop.mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(1.5F, 6.0F).randomTicks(), Blocks.MOSSY_STONE_BRICKS))
                .modelGenerator(holder -> new BlockModelUtility.BlockFamilyModelGenerator<>(holder, family -> family
                        .slab(GROWING_MOSSY_STONE_BRICK_SLAB.value())
                        .stairs(GROWING_MOSSY_STONE_BRICK_STAIRS.value())))
        ).getHolder();
        Items.registerBlockItem(GROWING_MOSSY_STONE_BRICKS, registerItem);

        GROWING_MOSSY_COBBLESTONE_SLAB = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("growing_mossy_cobblestone_slab")
                .blockFactory(prop -> new GrowableMossySlab(prop.mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(2.0F, 6.0F).randomTicks(), Blocks.MOSSY_COBBLESTONE_SLAB))
                .modelGenerator(null) // Family model generator handles this in the parent block
                .recipeGenerator(holder -> new RecipeGeneratorUtility.RecipeGenerator<>(holder,
                        h -> ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, h.value(), 6)
                                .pattern("XXX")
                                .define('X', GROWING_MOSSY_COBBLESTONE.value())
                                .unlockedBy("has_growing_mossy_cobblestone", has(GROWING_MOSSY_COBBLESTONE.value()))))
        ).getHolder();
        Items.registerBlockItem(GROWING_MOSSY_COBBLESTONE_SLAB, registerItem);

        GROWING_MOSSY_COBBLESTONE_STAIRS = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("growing_mossy_cobblestone_stairs")
                .blockFactory(prop -> new GrowableMossyStairs(() -> Blocks.COBBLESTONE.defaultBlockState(), prop.mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(2.0F, 6.0F).randomTicks(), Blocks.MOSSY_COBBLESTONE_STAIRS))
                .modelGenerator(null) // Family model generator handles this in the parent block
                .recipeGenerator(holder -> new RecipeGeneratorUtility.RecipeGenerator<>(holder,
                        h -> ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, h.value(), 4)
                                .pattern("X  ")
                                .pattern("XX ")
                                .pattern("XXX")
                                .define('X', GROWING_MOSSY_COBBLESTONE.value())
                                .unlockedBy("has_growing_mossy_cobblestone", has(GROWING_MOSSY_COBBLESTONE.value()))))
        ).getHolder();
        Items.registerBlockItem(GROWING_MOSSY_COBBLESTONE_STAIRS, registerItem);

        GROWING_MOSSY_STONE_BRICK_SLAB = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("growing_mossy_stone_brick_slab")
                .blockFactory(prop -> new GrowableMossySlab(prop.mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(1.5F, 6.0F).randomTicks(), Blocks.MOSSY_STONE_BRICK_SLAB))
                .modelGenerator(null) // Family model generator handles this in the parent block
                .recipeGenerator(holder -> new RecipeGeneratorUtility.RecipeGenerator<>(holder,
                        h -> ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, h.value(), 6)
                                .pattern("XXX")
                                .define('X', GROWING_MOSSY_STONE_BRICKS.value())
                                .unlockedBy("has_growing_mossy_stone_bricks", has(GROWING_MOSSY_STONE_BRICKS.value()))))
        ).getHolder();
        Items.registerBlockItem(GROWING_MOSSY_STONE_BRICK_SLAB, registerItem);

        GROWING_MOSSY_STONE_BRICK_STAIRS = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("growing_mossy_stone_brick_stairs")
                .blockFactory(prop -> new GrowableMossyStairs(() -> Blocks.STONE_BRICKS.defaultBlockState(), prop.mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(1.5F, 6.0F).randomTicks(), Blocks.MOSSY_STONE_BRICK_STAIRS))
                .modelGenerator(null) // Family model generator handles this in the parent block
                .recipeGenerator(holder -> new RecipeGeneratorUtility.RecipeGenerator<>(holder,
                        h -> ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, h.value(), 4)
                                .pattern("X  ")
                                .pattern("XX ")
                                .pattern("XXX")
                                .define('X', GROWING_MOSSY_STONE_BRICKS.value())
                                .unlockedBy("has_growing_mossy_stone_bricks", has(GROWING_MOSSY_STONE_BRICKS.value()))))
        ).getHolder();
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
