package grill24.potionsplus.core.blocks;

import grill24.potionsplus.block.*;
import grill24.potionsplus.core.Items;
import grill24.potionsplus.utility.registration.RegistrationUtility;
import grill24.potionsplus.utility.registration.RecipeGeneratorUtility;
import grill24.potionsplus.utility.registration.block.BlockModelUtility;
import grill24.potionsplus.utility.registration.block.FaceAttachedBlockModelGenerator;
import grill24.potionsplus.utility.registration.block.SimpleBlockBuilder;
import grill24.potionsplus.utility.registration.item.ItemModelUtility;
import grill24.potionsplus.utility.registration.item.SimpleItemBuilder;
import net.minecraft.core.Holder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

import java.util.function.BiFunction;
import java.util.function.Supplier;

import static grill24.potionsplus.utility.Utility.mc;
import static grill24.potionsplus.utility.Utility.ppId;

public class DecorationBlocks {
    public static Holder<Block> COOBLESTONE, ICICLE;
    public static Holder<Block> UNSTABLE_BLOCK, UNSTABLE_MOLTEN_DEEPSLATE, UNSTABLE_DEEPSLATE, UNSTABLE_MOLTEN_BLACKSTONE, UNSTABLE_BLACKSTONE;
    public static Holder<Block> LAVA_GEYSER, DECORATIVE_FIRE;
    public static Holder<Block> GROWING_MOSSY_COBBLESTONE, GROWING_MOSSY_STONE_BRICKS;
    public static Holder<Block> GROWING_MOSSY_COBBLESTONE_SLAB, GROWING_MOSSY_COBBLESTONE_STAIRS;
    public static Holder<Block> GROWING_MOSSY_STONE_BRICK_SLAB, GROWING_MOSSY_STONE_BRICK_STAIRS;


    public static void init(BiFunction<String, Supplier<Block>, Holder<Block>> registerBlock, BiFunction<String, Supplier<Item>, Holder<Item>> registerItem) {
        COOBLESTONE = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("cooblestone")
                .blockFactory(CooblestoneBlock::new)
                .properties(() -> BlockBehaviour.Properties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(1.5F, 6.0F).lightLevel(state -> 10))
        ).getHolder();
        Items.registerBlockItem(COOBLESTONE, registerItem);

        ICICLE = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("icicle")
                .blockFactory(prop -> new IcicleBlock(prop
                        .mapColor(MapColor.ICE)
                        .requiresCorrectToolForDrops()
                        .strength(0.5F)
                        .noOcclusion()
                        .randomTicks()
                        .strength(1.5F, 3.0F)
                        .sound(SoundType.GLASS)
                        .dynamicShape()))
                .modelGenerator(null) // Hand-made model
        ).getHolder();
        RegistrationUtility.register(registerItem, SimpleItemBuilder.createSimple("icicle")
                .itemFactory(prop -> new BlockItem(ICICLE.value(), prop.useBlockDescriptionPrefix()))
                .modelGenerator(h -> new ItemModelUtility.ItemFromModelFileGenerator<>(h, ppId("item/icicle"))));

        UNSTABLE_BLOCK = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("unstable_block")
                .blockFactory(prop -> new UnstableBlock(prop.mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(1.5F, 6.0F)))
        ).getHolder();
        Items.registerBlockItem(UNSTABLE_BLOCK, registerItem);

        UNSTABLE_MOLTEN_DEEPSLATE = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("unstable_molten_deepslate")
                .blockFactory(prop -> new UnstableBlock(prop.mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(1.5F, 6.0F)))
        ).getHolder();
        Items.registerBlockItem(UNSTABLE_MOLTEN_DEEPSLATE, registerItem);

        UNSTABLE_DEEPSLATE = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("unstable_deepslate")
                .blockFactory(prop -> new UnstableBlock(prop.mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(1.5F, 6.0F)))
                .modelGenerator(holder -> new BlockModelUtility.CubeAllBlockModelGenerator<>(holder, mc("block/cobbled_deepslate"), true, true, false))
        ).getHolder();
        Items.registerBlockItem(UNSTABLE_DEEPSLATE, registerItem);

        UNSTABLE_MOLTEN_BLACKSTONE = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("unstable_molten_blackstone")
                .blockFactory(prop -> new UnstableBlock(prop.mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(1.5F, 6.0F)))
        ).getHolder();
        Items.registerBlockItem(UNSTABLE_MOLTEN_BLACKSTONE, registerItem);

        UNSTABLE_BLACKSTONE = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("unstable_blackstone")
                .blockFactory(prop -> new UnstableBlock(prop.mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(1.5F, 6.0F)))
                .modelGenerator(holder -> new BlockModelUtility.CubeAllBlockModelGenerator<>(holder, mc("block/blackstone"), true, true, false))
        ).getHolder();
        Items.registerBlockItem(UNSTABLE_BLACKSTONE, registerItem);

        LAVA_GEYSER = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("lava_geyser")
                .blockFactory(prop -> new GeyserBlock(prop.mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(1.5F, 6.0F).lightLevel((p_152605_) -> 7).sound(SoundType.WOOL).noOcclusion()))
                .modelGenerator(FaceAttachedBlockModelGenerator::new)
        ).getHolder();
        Items.registerBlockItem(LAVA_GEYSER, registerItem);

        DECORATIVE_FIRE = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("decorative_fire")
                .blockFactory(prop -> new DecorativeFireBlock(prop.mapColor(MapColor.FIRE).noCollission().instabreak().lightLevel((state) -> 15).sound(SoundType.WOOL)))
                .lootGenerator(null) // Hand-made loot table
                .modelGenerator(null) // Hand-made model
        ).getHolder();
        Items.registerBlockItemWithTexture(DECORATIVE_FIRE, registerItem, mc("block/fire_0"));

        GROWING_MOSSY_COBBLESTONE = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("growing_mossy_cobblestone")
                .blockFactory(prop -> new GrowableMossyBlock(prop.mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(2.0F, 6.0F).randomTicks(), Blocks.MOSSY_COBBLESTONE))
                .modelGenerator(holder -> new BlockModelUtility.CubeAllBlockModelGenerator<>(holder, ppId("block/growing_mossy_cobblestone"), true, true, false))
        ).getHolder();
        Items.registerBlockItem(GROWING_MOSSY_COBBLESTONE, registerItem);

        GROWING_MOSSY_STONE_BRICKS = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("growing_mossy_stone_bricks")
                .blockFactory(prop -> new GrowableMossyBlock(prop.mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(1.5F, 6.0F).randomTicks(), Blocks.MOSSY_STONE_BRICKS))
                .modelGenerator(holder -> new BlockModelUtility.CubeAllBlockModelGenerator<>(holder, ppId("block/growing_mossy_stone_bricks"), true, true, false))
        ).getHolder();
        Items.registerBlockItem(GROWING_MOSSY_STONE_BRICKS, registerItem);

        GROWING_MOSSY_COBBLESTONE_SLAB = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("growing_mossy_cobblestone_slab")
                .blockFactory(prop -> new GrowableMossySlab(prop.mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(2.0F, 6.0F).randomTicks(), Blocks.MOSSY_COBBLESTONE_SLAB))
                .modelGenerator(null) // Use default slab models
                .recipeGenerator(holder -> new RecipeGeneratorUtility.RecipeGenerator<>(holder, 
                    (provider, h) -> provider.shaped(RecipeCategory.BUILDING_BLOCKS, h.value(), 6)
                        .pattern("XXX")
                        .define('X', DecorationBlocks.GROWING_MOSSY_COBBLESTONE.value())
                        .unlockedBy("has_growing_mossy_cobblestone", provider.has(DecorationBlocks.GROWING_MOSSY_COBBLESTONE.value()))))
        ).getHolder();
        Items.registerBlockItem(GROWING_MOSSY_COBBLESTONE_SLAB, registerItem);

        GROWING_MOSSY_COBBLESTONE_STAIRS = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("growing_mossy_cobblestone_stairs")
                .blockFactory(prop -> new GrowableMossyStairs(() -> Blocks.COBBLESTONE.defaultBlockState(), prop.mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(2.0F, 6.0F).randomTicks(), Blocks.MOSSY_COBBLESTONE_STAIRS))
                .modelGenerator(null) // Use default stair models
                .recipeGenerator(holder -> new RecipeGeneratorUtility.RecipeGenerator<>(holder, 
                    (provider, h) -> provider.shaped(RecipeCategory.BUILDING_BLOCKS, h.value(), 4)
                        .pattern("X  ")
                        .pattern("XX ")
                        .pattern("XXX")
                        .define('X', DecorationBlocks.GROWING_MOSSY_COBBLESTONE.value())
                        .unlockedBy("has_growing_mossy_cobblestone", provider.has(DecorationBlocks.GROWING_MOSSY_COBBLESTONE.value()))))
        ).getHolder();
        Items.registerBlockItem(GROWING_MOSSY_COBBLESTONE_STAIRS, registerItem);

        GROWING_MOSSY_STONE_BRICK_SLAB = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("growing_mossy_stone_brick_slab")
                .blockFactory(prop -> new GrowableMossySlab(prop.mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(1.5F, 6.0F).randomTicks(), Blocks.MOSSY_STONE_BRICK_SLAB))
                .modelGenerator(null) // Use default slab models
                .recipeGenerator(holder -> new RecipeGeneratorUtility.RecipeGenerator<>(holder, 
                    (provider, h) -> provider.shaped(RecipeCategory.BUILDING_BLOCKS, h.value(), 6)
                        .pattern("XXX")
                        .define('X', DecorationBlocks.GROWING_MOSSY_STONE_BRICKS.value())
                        .unlockedBy("has_growing_mossy_stone_bricks", provider.has(DecorationBlocks.GROWING_MOSSY_STONE_BRICKS.value()))))
        ).getHolder();
        Items.registerBlockItem(GROWING_MOSSY_STONE_BRICK_SLAB, registerItem);

        GROWING_MOSSY_STONE_BRICK_STAIRS = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("growing_mossy_stone_brick_stairs")
                .blockFactory(prop -> new GrowableMossyStairs(() -> Blocks.STONE_BRICKS.defaultBlockState(), prop.mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(1.5F, 6.0F).randomTicks(), Blocks.MOSSY_STONE_BRICK_STAIRS))
                .modelGenerator(null) // Use default stair models
                .recipeGenerator(holder -> new RecipeGeneratorUtility.RecipeGenerator<>(holder, 
                    (provider, h) -> provider.shaped(RecipeCategory.BUILDING_BLOCKS, h.value(), 4)
                        .pattern("X  ")
                        .pattern("XX ")
                        .pattern("XXX")
                        .define('X', DecorationBlocks.GROWING_MOSSY_STONE_BRICKS.value())
                        .unlockedBy("has_growing_mossy_stone_bricks", provider.has(DecorationBlocks.GROWING_MOSSY_STONE_BRICKS.value()))))
        ).getHolder();
        Items.registerBlockItem(GROWING_MOSSY_STONE_BRICK_STAIRS, registerItem);
    }

}
