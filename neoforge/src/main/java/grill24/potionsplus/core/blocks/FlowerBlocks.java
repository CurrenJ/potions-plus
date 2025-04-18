package grill24.potionsplus.core.blocks;

import grill24.potionsplus.block.*;
import grill24.potionsplus.core.Blocks;
import grill24.potionsplus.core.Items;
import grill24.potionsplus.core.potion.MobEffects;
import grill24.potionsplus.utility.registration.RegistrationUtility;
import grill24.potionsplus.utility.registration.block.*;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredHolder;
import oshi.util.tuples.Pair;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import static grill24.potionsplus.utility.Utility.mc;
import static grill24.potionsplus.utility.Utility.ppId;

public class FlowerBlocks {
    public static Holder<Block> LUNAR_BERRY_BUSH;
    public static Holder<Block> COWLICK_VINE, HANGING_FERN, DROOPY_VINE, SURVIVOR_STICK, LUMOSEED_SACKS;

    public static Holder<Block> DANDELION_VERSATILE, TORCHFLOWER_VERSATILE, POPPY_VERSATILE, BLUE_ORCHID_VERSATILE,
            ALLIUM_VERSATILE, AZURE_BLUET_VERSATILE, RED_TULIP_VERSATILE, ORANGE_TULIP_VERSATILE,
            WHITE_TULIP_VERSATILE, PINK_TULIP_VERSATILE, OXEYE_DAISY_VERSATILE, CORNFLOWER_VERSATILE,
            WITHER_ROSE_VERSATILE, LILY_OF_THE_VALLEY_VERSATILE, BROWN_MUSHROOM_VERSATILE, RED_MUSHROOM_VERSATILE;
    public static Holder<Block> SUNFLOWER_VERSATILE, LILAC_VERSATILE, ROSE_BUSH_VERSATILE, PEONY_VERSATILE, TALL_GRASS_VERSATILE, LARGE_FERN_VERSATILE, PITCHER_PLANT_VERSATILE;

    public static Holder<Block> IRON_OXIDE_DAISY, COPPER_CHRYSANTHEMUM, LAPIS_LILAC, DIAMOUR, GOLDEN_CUBENSIS, REDSTONE_ROSE, BLACK_COALLA_LILY;

    public static void init(BiFunction<String, Supplier<Block>, Holder<Block>> registerBlock, BiFunction<String, Supplier<Item>, Holder<Item>> registerItem) {
        LUNAR_BERRY_BUSH = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("lunar_berry_bush")
                .blockFactory(prop -> new LunarBerryBushBlock(BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.SWEET_BERRY_BUSH).noOcclusion().lightLevel(LunarBerryBushBlock.LIGHT_EMISSION)))
                .lootGenerator(null) // Hand-made loot tables
                .modelGenerator(null) // Hand-made model
                .renderType(BlockBuilder.RenderType.CUTOUT)
        ).getHolder();
        Items.registerBlockItem(LUNAR_BERRY_BUSH, registerItem);

        IRON_OXIDE_DAISY = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("iron_oxide_daisy")
                .blockFactory(prop -> new OreFlowerBlock(MobEffects.MAGNETIC, 200, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().instabreak().sound(SoundType.GRASS), false, null,
                        state -> state.is(net.minecraft.world.level.block.Blocks.IRON_ORE) || state.is(net.minecraft.world.level.block.Blocks.DEEPSLATE_IRON_ORE),
                        0.15f))
                .modelGenerator(SimpleCrossBlockModelGenerator::new)
                .renderType(BlockBuilder.RenderType.CUTOUT)
        ).getHolder();
        Items.registerBlockItemWithTexture(IRON_OXIDE_DAISY, registerItem, ppId("block/iron_oxide_daisy"));

        COPPER_CHRYSANTHEMUM = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("copper_chrysanthemum")
                .blockFactory(prop -> new OreFlowerBlock(MobEffects.FORTUITOUS_FATE, 200, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().instabreak().sound(SoundType.GRASS), false, null,
                        state -> state.is(net.minecraft.world.level.block.Blocks.COPPER_ORE) || state.is(net.minecraft.world.level.block.Blocks.DEEPSLATE_COPPER_ORE),
                        0.15f))
                .modelGenerator(SimpleCrossBlockModelGenerator::new)
                .renderType(BlockBuilder.RenderType.CUTOUT)
        ).getHolder();
        Items.registerBlockItemWithTexture(COPPER_CHRYSANTHEMUM, registerItem, ppId("block/copper_chrysanthemum"));

        LAPIS_LILAC = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("lapis_lilac")
                .blockFactory(prop -> new OreFlowerBlock(MobEffects.LOOTING, 200, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().instabreak().sound(SoundType.GRASS), false, null,
                        state -> state.is(net.minecraft.world.level.block.Blocks.LAPIS_ORE) || state.is(net.minecraft.world.level.block.Blocks.DEEPSLATE_LAPIS_ORE),
                        0.3f))
                .modelGenerator(SimpleCrossBlockModelGenerator::new)
                .renderType(BlockBuilder.RenderType.CUTOUT)
        ).getHolder();
        Items.registerBlockItemWithTexture(LAPIS_LILAC, registerItem, ppId("block/lapis_lilac"));

        DIAMOUR = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("diamour")
                .blockFactory(prop -> new OreFlowerBlock(MobEffects.TELEPORTATION, 200, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().instabreak().sound(SoundType.GRASS), false, null,
                        state -> state.is(net.minecraft.world.level.block.Blocks.DIAMOND_ORE) || state.is(net.minecraft.world.level.block.Blocks.DEEPSLATE_DIAMOND_ORE),
                        0.15f))
                .modelGenerator(SimpleCrossBlockModelGenerator::new)
                .renderType(BlockBuilder.RenderType.CUTOUT)
        ).getHolder();
        Items.registerBlockItemWithTexture(DIAMOUR, registerItem, ppId("block/diamour"));

        GOLDEN_CUBENSIS = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("golden_cubensis")
                .blockFactory(prop -> new OreFlowerBlock(MobEffects.GEODE_GRACE, 200, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().instabreak().sound(SoundType.GRASS), false, null,
                        state -> state.is(net.minecraft.world.level.block.Blocks.GOLD_ORE) || state.is(net.minecraft.world.level.block.Blocks.DEEPSLATE_GOLD_ORE),
                        0.2f))
                .modelGenerator(SimpleCrossBlockModelGenerator::new)
                .renderType(BlockBuilder.RenderType.CUTOUT)
        ).getHolder();
        Items.registerBlockItemWithTexture(GOLDEN_CUBENSIS, registerItem, ppId("block/golden_cubensis"));

        REDSTONE_ROSE = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("redstone_rose")
                .blockFactory(prop -> new OreFlowerBlock(MobEffects.REACH_FOR_THE_STARS, 200, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().instabreak().sound(SoundType.GRASS), false, null,
                        state -> state.is(net.minecraft.world.level.block.Blocks.REDSTONE_ORE) || state.is(net.minecraft.world.level.block.Blocks.DEEPSLATE_REDSTONE_ORE),
                        0.1f))
                .modelGenerator(SimpleCrossBlockModelGenerator::new)
                .renderType(BlockBuilder.RenderType.CUTOUT)
        ).getHolder();
        Items.registerBlockItemWithTexture(REDSTONE_ROSE, registerItem, ppId("block/redstone_rose"));

        BLACK_COALLA_LILY = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("black_coalla_lily")
                .blockFactory(prop -> new OreFlowerBlock(net.minecraft.world.effect.MobEffects.FIRE_RESISTANCE, 200, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().instabreak().sound(SoundType.GRASS), false, null,
                        state -> state.is(net.minecraft.world.level.block.Blocks.COAL_ORE) || state.is(net.minecraft.world.level.block.Blocks.DEEPSLATE_COAL_ORE),
                        0.1f))
                .modelGenerator(SimpleCrossBlockModelGenerator::new)
                .renderType(BlockBuilder.RenderType.CUTOUT)
        ).getHolder();
        Items.registerBlockItemWithTexture(BLACK_COALLA_LILY, registerItem, ppId("block/black_coalla_lily"));

        COWLICK_VINE = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("cowlick_vine")
                .blockFactory(prop -> new VersatilePlantBlock(BlockBehaviour.Properties.of()
                        .mapColor(MapColor.PLANT)
                        .noCollission()
                        .instabreak()
                        .sound(SoundType.GRASS)
                        .ignitedByLava()
                        .pushReaction(PushReaction.DESTROY),
                        new VersatilePlantBlock.VersatilePlantConfig(true, false, 0, 9,
                                new VersatilePlantBlockTexturePattern(List.of(0, 1), List.of(2, 3), List.of(4), true))))
                .modelGenerator(holder -> new VersatilePlantBlockModelGenerator<>(holder,
                        new ResourceLocation[]{
                                ppId("block/cowlick_vine_0"), ppId("block/cowlick_vine_1"),
                                ppId("block/cowlick_vine_2"), ppId("block/cowlick_vine_3"),
                                ppId("block/cowlick_vine_tail")},
                        VersatilePlantBlockModelGenerator.HANGING_PLANT_TEX_ORIENTATION,
                        ppId("block/cowlick_vine_tail"),
                        VersatilePlantBlockModelGenerator.CROSS_MODEL_GENERATOR
                ))
                .renderType(BlockBuilder.RenderType.CUTOUT)
        ).getHolder();
        Items.registerBlockItem(COWLICK_VINE, registerItem);

        HANGING_FERN = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("hanging_fern")
                .blockFactory(prop -> new BloomingPlantBlock(BlockBehaviour.Properties.of()
                        .mapColor(MapColor.PLANT)
                        .noCollission()
                        .instabreak()
                        .sound(SoundType.GRASS)
                        .ignitedByLava()
                        .pushReaction(PushReaction.DESTROY),
                        new VersatilePlantBlock.VersatilePlantConfig(true, false, 1, 5,
                                new VersatilePlantBlockTexturePattern(List.of(0), List.of(1), List.of(2), false)), 1))
                .modelGenerator(holder -> new BloomingVersatilePlantBlockModelGenerator<>(holder,
                        new ResourceLocation[][]{
                                new ResourceLocation[]{ppId("block/hanging_fern_upper"), ppId("block/hanging_fern_middle"), ppId("block/hanging_fern_lower")},
                                new ResourceLocation[]{ppId("block/hanging_fern_upper_blooming"), ppId("block/hanging_fern_middle_blooming"), ppId("block/hanging_fern_lower_blooming")}},
                        VersatilePlantBlockModelGenerator.HANGING_PLANT_TEX_ORIENTATION,
                        ppId("block/hanging_fern_upper_blooming"),
                        VersatilePlantBlockModelGenerator.CROSS_MODEL_GENERATOR
                ))
                .renderType(BlockBuilder.RenderType.CUTOUT)
        ).getHolder();
        Items.registerBlockItem(HANGING_FERN, registerItem);

        DROOPY_VINE = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("droopy_vine")
                .blockFactory(prop -> new BloomingPlantBlock(BlockBehaviour.Properties.of()
                        .mapColor(MapColor.PLANT)
                        .noCollission()
                        .instabreak()
                        .sound(SoundType.GRASS)
                        .ignitedByLava()
                        .pushReaction(PushReaction.DESTROY),
                        new VersatilePlantBlock.VersatilePlantConfig(true, false, 0, 7,
                                new VersatilePlantBlockTexturePattern(List.of(), List.of(0, 1), List.of(2), false)), 1))
                .modelGenerator(holder -> new BloomingVersatilePlantBlockModelGenerator<>(holder,
                        new ResourceLocation[][]{
                                new ResourceLocation[]{ppId("block/droopy_vine_0"), ppId("block/droopy_vine_1"), ppId("block/droopy_vine_2")},
                                new ResourceLocation[]{ppId("block/droopy_vine_blooming_0"), ppId("block/droopy_vine_blooming_1"), ppId("block/droopy_vine_blooming_2")},
                        },
                        VersatilePlantBlockModelGenerator.HANGING_PLANT_TEX_ORIENTATION,
                        ppId("block/droopy_vine_blooming_2"),
                        VersatilePlantBlockModelGenerator.CROSS_MODEL_GENERATOR
                ))
                .renderType(BlockBuilder.RenderType.CUTOUT)
        ).getHolder();
        Items.registerBlockItem(DROOPY_VINE, registerItem);

        SURVIVOR_STICK = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("survivor_stick")
                .blockFactory(prop -> new BloomingPlantBlock(BlockBehaviour.Properties.of()
                        .mapColor(MapColor.PLANT)
                        .noCollission()
                        .instabreak()
                        .sound(SoundType.GRASS)
                        .ignitedByLava()
                        .pushReaction(PushReaction.DESTROY),
                        new VersatilePlantBlock.VersatilePlantConfig(true, false, 1, 6,
                                new VersatilePlantBlockTexturePattern(List.of(), List.of(0), List.of(1), false)), 1))
                .modelGenerator(holder -> new BloomingVersatilePlantBlockModelGenerator<>(holder,
                        new ResourceLocation[][]{
                                new ResourceLocation[]{ppId("block/survivor_stick_0"), ppId("block/survivor_stick_1")},
                                new ResourceLocation[]{ppId("block/survivor_stick_blooming_0"), ppId("block/survivor_stick_blooming_1")},
                        },
                        VersatilePlantBlockModelGenerator.HANGING_PLANT_TEX_ORIENTATION,
                        ppId("block/survivor_stick_blooming_1"),
                        VersatilePlantBlockModelGenerator.CROSS_MODEL_GENERATOR
                ))
                .renderType(BlockBuilder.RenderType.CUTOUT)
        ).getHolder();
        Items.registerBlockItem(SURVIVOR_STICK, registerItem);

        LUMOSEED_SACKS = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("lumoseed_sacks")
                .blockFactory(prop -> new BloomingPlantBlock(BlockBehaviour.Properties.of()
                        .mapColor(MapColor.PLANT)
                        .noCollission()
                        .instabreak()
                        .sound(SoundType.GRASS)
                        .ignitedByLava()
                        .pushReaction(PushReaction.DESTROY),
                        new VersatilePlantBlock.VersatilePlantConfig(true, false, 0, 6,
                                new VersatilePlantBlockTexturePattern(List.of(), List.of(0), List.of(1), false)), 1))
                .modelGenerator(holder -> new BloomingVersatilePlantBlockModelGenerator<>(holder,
                        new Pair[][]{
                                new Pair[]{new Pair<>(ppId("block/lumoseed_sack"), new Pair<>(mc("block/moss_block"), mc("block/moss_block"))), new Pair(ppId("block/lumoseed_sack_tail"), new Pair<>(mc("block/moss_block"), mc("block/moss_block")))},
                                new Pair[]{new Pair<>(ppId("block/lumoseed_sack"), new Pair<>(mc("block/moss_block"), mc("block/glowstone"))), new Pair(ppId("block/lumoseed_sack_tail"), new Pair<>(mc("block/moss_block"), mc("block/glowstone")))}
                        },
                        VersatilePlantBlockModelGenerator.HANGING_PLANT_TEX_ORIENTATION,
                        mc("item/coal"),
                        VersatilePlantBlockModelGenerator.LUMOSEED_MODEL_GENERATOR
                ))
                .renderType(BlockBuilder.RenderType.SOLID)
        ).getHolder();
        Items.registerBlockItem(LUMOSEED_SACKS, registerItem);

        DANDELION_VERSATILE = RegistrationUtility.register(registerBlock, SimpleVersatilePlantBlockBuilder.create("dandelion_versatile",
                new ResourceLocation[]{mc("block/dandelion")},
                VersatilePlantBlockModelGenerator.UPRIGHT_PLANT_TEX_ORIENTATION,
                mc("block/dandelion"),
                VersatilePlantBlockModelGenerator.PARENTED_MODEL_GENERATOR)
        ).getHolder();
        Items.registerBlockItem(DANDELION_VERSATILE, registerItem);

        TORCHFLOWER_VERSATILE = RegistrationUtility.register(registerBlock, SimpleVersatilePlantBlockBuilder.create("torchflower_versatile",
                new ResourceLocation[]{mc("block/torchflower")},
                VersatilePlantBlockModelGenerator.UPRIGHT_PLANT_TEX_ORIENTATION,
                mc("block/torchflower"),
                VersatilePlantBlockModelGenerator.PARENTED_MODEL_GENERATOR)
        ).getHolder();
        Items.registerBlockItem(TORCHFLOWER_VERSATILE, registerItem);

        POPPY_VERSATILE = RegistrationUtility.register(registerBlock, SimpleVersatilePlantBlockBuilder.create("poppy_versatile",
                new ResourceLocation[]{mc("block/poppy")},
                VersatilePlantBlockModelGenerator.UPRIGHT_PLANT_TEX_ORIENTATION,
                mc("block/poppy"),
                VersatilePlantBlockModelGenerator.PARENTED_MODEL_GENERATOR)
        ).getHolder();
        Items.registerBlockItem(POPPY_VERSATILE, registerItem);

        BLUE_ORCHID_VERSATILE = RegistrationUtility.register(registerBlock, SimpleVersatilePlantBlockBuilder.create("blue_orchid_versatile",
                new ResourceLocation[]{mc("block/blue_orchid")},
                VersatilePlantBlockModelGenerator.UPRIGHT_PLANT_TEX_ORIENTATION,
                mc("block/blue_orchid"),
                VersatilePlantBlockModelGenerator.PARENTED_MODEL_GENERATOR)
        ).getHolder();
        Items.registerBlockItem(BLUE_ORCHID_VERSATILE, registerItem);

        ALLIUM_VERSATILE = RegistrationUtility.register(registerBlock, SimpleVersatilePlantBlockBuilder.create("allium_versatile",
                new ResourceLocation[]{mc("block/allium")},
                VersatilePlantBlockModelGenerator.UPRIGHT_PLANT_TEX_ORIENTATION,
                mc("block/allium"),
                VersatilePlantBlockModelGenerator.PARENTED_MODEL_GENERATOR)
        ).getHolder();
        Items.registerBlockItem(ALLIUM_VERSATILE, registerItem);

        AZURE_BLUET_VERSATILE = RegistrationUtility.register(registerBlock, SimpleVersatilePlantBlockBuilder.create("azure_bluet_versatile",
                new ResourceLocation[]{mc("block/azure_bluet")},
                VersatilePlantBlockModelGenerator.UPRIGHT_PLANT_TEX_ORIENTATION,
                mc("block/azure_bluet"),
                VersatilePlantBlockModelGenerator.PARENTED_MODEL_GENERATOR)
        ).getHolder();
        Items.registerBlockItem(AZURE_BLUET_VERSATILE, registerItem);

        RED_TULIP_VERSATILE = RegistrationUtility.register(registerBlock, SimpleVersatilePlantBlockBuilder.create("red_tulip_versatile",
                new ResourceLocation[]{mc("block/red_tulip")},
                VersatilePlantBlockModelGenerator.UPRIGHT_PLANT_TEX_ORIENTATION,
                mc("block/red_tulip"),
                VersatilePlantBlockModelGenerator.PARENTED_MODEL_GENERATOR)
        ).getHolder();
        Items.registerBlockItem(RED_TULIP_VERSATILE, registerItem);

        ORANGE_TULIP_VERSATILE = RegistrationUtility.register(registerBlock, SimpleVersatilePlantBlockBuilder.create("orange_tulip_versatile",
                new ResourceLocation[]{mc("block/orange_tulip")},
                VersatilePlantBlockModelGenerator.UPRIGHT_PLANT_TEX_ORIENTATION,
                mc("block/orange_tulip"),
                VersatilePlantBlockModelGenerator.PARENTED_MODEL_GENERATOR)
        ).getHolder();
        Items.registerBlockItem(ORANGE_TULIP_VERSATILE, registerItem);

        WHITE_TULIP_VERSATILE = RegistrationUtility.register(registerBlock, SimpleVersatilePlantBlockBuilder.create("white_tulip_versatile",
                new ResourceLocation[]{mc("block/white_tulip")},
                VersatilePlantBlockModelGenerator.UPRIGHT_PLANT_TEX_ORIENTATION,
                mc("block/white_tulip"),
                VersatilePlantBlockModelGenerator.PARENTED_MODEL_GENERATOR)
        ).getHolder();
        Items.registerBlockItem(WHITE_TULIP_VERSATILE, registerItem);

        PINK_TULIP_VERSATILE = RegistrationUtility.register(registerBlock, SimpleVersatilePlantBlockBuilder.create("pink_tulip_versatile",
                new ResourceLocation[]{mc("block/pink_tulip")},
                VersatilePlantBlockModelGenerator.UPRIGHT_PLANT_TEX_ORIENTATION,
                mc("block/pink_tulip"),
                VersatilePlantBlockModelGenerator.PARENTED_MODEL_GENERATOR)
        ).getHolder();
        Items.registerBlockItem(PINK_TULIP_VERSATILE, registerItem);

        OXEYE_DAISY_VERSATILE = RegistrationUtility.register(registerBlock, SimpleVersatilePlantBlockBuilder.create("oxeye_daisy_versatile",
                new ResourceLocation[]{mc("block/oxeye_daisy")},
                VersatilePlantBlockModelGenerator.UPRIGHT_PLANT_TEX_ORIENTATION,
                mc("block/oxeye_daisy"),
                VersatilePlantBlockModelGenerator.PARENTED_MODEL_GENERATOR)
        ).getHolder();
        Items.registerBlockItem(OXEYE_DAISY_VERSATILE, registerItem);

        CORNFLOWER_VERSATILE = RegistrationUtility.register(registerBlock, SimpleVersatilePlantBlockBuilder.create("cornflower_versatile",
                new ResourceLocation[]{mc("block/cornflower")},
                VersatilePlantBlockModelGenerator.UPRIGHT_PLANT_TEX_ORIENTATION,
                mc("block/cornflower"),
                VersatilePlantBlockModelGenerator.PARENTED_MODEL_GENERATOR)
        ).getHolder();
        Items.registerBlockItem(CORNFLOWER_VERSATILE, registerItem);

        WITHER_ROSE_VERSATILE = RegistrationUtility.register(registerBlock, SimpleVersatilePlantBlockBuilder.create("wither_rose_versatile",
                new ResourceLocation[]{mc("block/wither_rose")},
                VersatilePlantBlockModelGenerator.UPRIGHT_PLANT_TEX_ORIENTATION,
                mc("block/wither_rose"),
                VersatilePlantBlockModelGenerator.PARENTED_MODEL_GENERATOR)
        ).getHolder();
        Items.registerBlockItem(WITHER_ROSE_VERSATILE, registerItem);

        LILY_OF_THE_VALLEY_VERSATILE = RegistrationUtility.register(registerBlock, SimpleVersatilePlantBlockBuilder.create("lily_of_the_valley_versatile",
                new ResourceLocation[]{mc("block/lily_of_the_valley")},
                VersatilePlantBlockModelGenerator.UPRIGHT_PLANT_TEX_ORIENTATION,
                mc("block/lily_of_the_valley"),
                VersatilePlantBlockModelGenerator.PARENTED_MODEL_GENERATOR)
        ).getHolder();
        Items.registerBlockItem(LILY_OF_THE_VALLEY_VERSATILE, registerItem);

        BROWN_MUSHROOM_VERSATILE = RegistrationUtility.register(registerBlock, SimpleVersatilePlantBlockBuilder.create("brown_mushroom_versatile",
                new ResourceLocation[]{mc("block/brown_mushroom")},
                VersatilePlantBlockModelGenerator.UPRIGHT_PLANT_TEX_ORIENTATION,
                mc("block/brown_mushroom"),
                VersatilePlantBlockModelGenerator.PARENTED_MODEL_GENERATOR)
        ).getHolder();
        Items.registerBlockItem(BROWN_MUSHROOM_VERSATILE, registerItem);

        RED_MUSHROOM_VERSATILE = RegistrationUtility.register(registerBlock, SimpleVersatilePlantBlockBuilder.create("red_mushroom_versatile",
                new ResourceLocation[]{mc("block/red_mushroom")},
                VersatilePlantBlockModelGenerator.UPRIGHT_PLANT_TEX_ORIENTATION,
                mc("block/red_mushroom"),
                VersatilePlantBlockModelGenerator.PARENTED_MODEL_GENERATOR)
        ).getHolder();
        Items.registerBlockItem(RED_MUSHROOM_VERSATILE, registerItem);

        SUNFLOWER_VERSATILE = RegistrationUtility.register(registerBlock, SimpleTallVersatilePlantBlockBuilder.create("sunflower_versatile",
                true,
                new ResourceLocation[]{mc("block/sunflower_bottom"), mc("block/sunflower_top")},
                VersatilePlantBlockModelGenerator.UPRIGHT_PLANT_TEX_ORIENTATION,
                mc("block/sunflower_front"),
                VersatilePlantBlockModelGenerator.PARENTED_MODEL_GENERATOR)
        ).getHolder();
        Items.registerBlockItem(SUNFLOWER_VERSATILE, registerItem);

        LILAC_VERSATILE = RegistrationUtility.register(registerBlock, SimpleTallVersatilePlantBlockBuilder.create("lilac_versatile",
                true,
                new ResourceLocation[]{mc("block/lilac_bottom"), mc("block/lilac_top")},
                VersatilePlantBlockModelGenerator.UPRIGHT_PLANT_TEX_ORIENTATION,
                mc("block/lilac_top"),
                VersatilePlantBlockModelGenerator.PARENTED_MODEL_GENERATOR)
        ).getHolder();
        Items.registerBlockItem(LILAC_VERSATILE, registerItem);

        ROSE_BUSH_VERSATILE = RegistrationUtility.register(registerBlock, SimpleTallVersatilePlantBlockBuilder.create("rose_bush_versatile",
                true,
                new ResourceLocation[]{mc("block/rose_bush_bottom"), mc("block/rose_bush_top")},
                VersatilePlantBlockModelGenerator.UPRIGHT_PLANT_TEX_ORIENTATION,
                mc("block/rose_bush_top"),
                VersatilePlantBlockModelGenerator.PARENTED_MODEL_GENERATOR)
        ).getHolder();
        Items.registerBlockItem(ROSE_BUSH_VERSATILE, registerItem);

        PEONY_VERSATILE = RegistrationUtility.register(registerBlock, SimpleTallVersatilePlantBlockBuilder.create("peony_versatile",
                true,
                new ResourceLocation[]{mc("block/peony_bottom"), mc("block/peony_top")},
                VersatilePlantBlockModelGenerator.UPRIGHT_PLANT_TEX_ORIENTATION,
                mc("block/peony_top"),
                VersatilePlantBlockModelGenerator.PARENTED_MODEL_GENERATOR)
        ).getHolder();
        Items.registerBlockItem(PEONY_VERSATILE, registerItem);

        TALL_GRASS_VERSATILE = RegistrationUtility.register(registerBlock, SimpleTallVersatilePlantBlockBuilder.create("tall_grass_versatile",
                true,
                new ResourceLocation[]{mc("block/tall_grass_bottom"), mc("block/tall_grass_top")},
                VersatilePlantBlockModelGenerator.UPRIGHT_PLANT_TEX_ORIENTATION,
                mc("block/tall_grass_top"),
                VersatilePlantBlockModelGenerator.PARENTED_MODEL_GENERATOR)
        ).getHolder();
        Items.registerBlockItem(TALL_GRASS_VERSATILE, registerItem);

        LARGE_FERN_VERSATILE = RegistrationUtility.register(registerBlock, SimpleTallVersatilePlantBlockBuilder.create("large_fern_versatile",
                true,
                new ResourceLocation[]{mc("block/large_fern_bottom"), mc("block/large_fern_top")},
                VersatilePlantBlockModelGenerator.UPRIGHT_PLANT_TEX_ORIENTATION,
                mc("block/large_fern_top"),
                VersatilePlantBlockModelGenerator.PARENTED_MODEL_GENERATOR)
        ).getHolder();
        Items.registerBlockItem(LARGE_FERN_VERSATILE, registerItem);

        PITCHER_PLANT_VERSATILE = RegistrationUtility.register(registerBlock, SimpleTallVersatilePlantBlockBuilder.create("pitcher_plant_versatile",
                false,
                new ResourceLocation[]{mc("block/pitcher_plant_bottom"), mc("block/pitcher_plant_top")},
                VersatilePlantBlockModelGenerator.UPRIGHT_PLANT_TEX_ORIENTATION,
                mc("item/pitcher_plant"),
                VersatilePlantBlockModelGenerator.PARENTED_MODEL_GENERATOR)
        ).getHolder();
        Items.registerBlockItem(PITCHER_PLANT_VERSATILE, registerItem);
    }
}
