package grill24.potionsplus.core.blocks;

import grill24.potionsplus.block.*;
import grill24.potionsplus.core.Items;
import grill24.potionsplus.core.items.PlantItems;
import grill24.potionsplus.core.potion.MobEffects;
import grill24.potionsplus.utility.registration.RegistrationUtility;
import grill24.potionsplus.utility.registration.block.*;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.neoforge.common.Tags;
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

    public static Holder<Block> TOMATO_PLANT, BRASSICA_OLERACEA_PLANT, CABBAGE_PLANT, KALE_PLANT, KOHLRABI_PLANT, BROCCOLLI_PLANT, CAULIFLOWER_PLANT, BRUSSELS_SPROUTS_PLANT;
    public static List<Holder<Block>> GENETIC_CROP_PLANTS;

    public static void init(BiFunction<String, Supplier<Block>, Holder<Block>> registerBlock, BiFunction<String, Supplier<Item>, Holder<Item>> registerItem) {
        LUNAR_BERRY_BUSH = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("lunar_berry_bush")
                .blockFactory(prop -> new LunarBerryBushBlock(prop.mapColor(MapColor.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.SWEET_BERRY_BUSH).noOcclusion().lightLevel(LunarBerryBushBlock.LIGHT_EMISSION)))
                .lootGenerator(null) // Hand-made loot tables
                .modelGenerator(null) // Hand-made model
                .renderType(BlockBuilder.RenderType.CUTOUT)
        ).getHolder();
        Items.registerBlockItem(LUNAR_BERRY_BUSH, registerItem);

        IRON_OXIDE_DAISY = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("iron_oxide_daisy")
                .blockFactory(prop -> new OreFlowerBlock(MobEffects.MAGNETIC, 200, prop.mapColor(MapColor.PLANT).noCollission().instabreak().sound(SoundType.GRASS), false, null,
                        state -> state.is(BlockTags.DIRT) || state.getBlock() instanceof FarmBlock,
                        state -> state.is(Tags.Blocks.ORES_IRON),
                        0.15f))
                .modelGenerator(SimpleCrossBlockModelGenerator::new)
                .renderType(BlockBuilder.RenderType.CUTOUT)
        ).getHolder();
        Items.registerBlockItemWithTexture(IRON_OXIDE_DAISY, registerItem, ppId("block/iron_oxide_daisy"));

        COPPER_CHRYSANTHEMUM = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("copper_chrysanthemum")
                .blockFactory(prop -> new OreFlowerBlock(MobEffects.FORTUITOUS_FATE, 200, prop.mapColor(MapColor.PLANT).noCollission().instabreak().sound(SoundType.GRASS), false, null,
                        state -> state.is(BlockTags.DIRT) || state.getBlock() instanceof FarmBlock,
                        state -> state.is(Tags.Blocks.ORES_COPPER),
                        0.15f))
                .modelGenerator(SimpleCrossBlockModelGenerator::new)
                .renderType(BlockBuilder.RenderType.CUTOUT)
        ).getHolder();
        Items.registerBlockItemWithTexture(COPPER_CHRYSANTHEMUM, registerItem, ppId("block/copper_chrysanthemum"));

        LAPIS_LILAC = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("lapis_lilac")
                .blockFactory(prop -> new OreFlowerBlock(MobEffects.LOOTING, 200, prop.mapColor(MapColor.PLANT).noCollission().instabreak().sound(SoundType.GRASS), false, null,
                        state -> state.is(BlockTags.DIRT) || state.getBlock() instanceof FarmBlock,
                        state -> state.is(Tags.Blocks.ORES_LAPIS),
                        0.3f))
                .modelGenerator(SimpleCrossBlockModelGenerator::new)
                .renderType(BlockBuilder.RenderType.CUTOUT)
        ).getHolder();
        Items.registerBlockItemWithTexture(LAPIS_LILAC, registerItem, ppId("block/lapis_lilac"));

        DIAMOUR = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("diamour")
                .blockFactory(prop -> new OreFlowerBlock(MobEffects.TELEPORTATION, 200, prop.mapColor(MapColor.PLANT).noCollission().instabreak().sound(SoundType.GRASS), false, null,
                        state -> state.is(BlockTags.DIRT) || state.getBlock() instanceof FarmBlock,
                        state -> state.is(Tags.Blocks.ORES_DIAMOND),
                        0.15f))
                .modelGenerator(SimpleCrossBlockModelGenerator::new)
                .renderType(BlockBuilder.RenderType.CUTOUT)
        ).getHolder();
        Items.registerBlockItemWithTexture(DIAMOUR, registerItem, ppId("block/diamour"));

        GOLDEN_CUBENSIS = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("golden_cubensis")
                .blockFactory(prop -> new OreFlowerBlock(MobEffects.GEODE_GRACE, 200, prop.mapColor(MapColor.PLANT).noCollission().instabreak().sound(SoundType.GRASS), false, null,
                        state -> state.is(BlockTags.DIRT) || state.getBlock() instanceof FarmBlock,
                        state -> state.is(Tags.Blocks.ORES_GOLD),
                        0.2f))
                .modelGenerator(SimpleCrossBlockModelGenerator::new)
                .renderType(BlockBuilder.RenderType.CUTOUT)
        ).getHolder();
        Items.registerBlockItemWithTexture(GOLDEN_CUBENSIS, registerItem, ppId("block/golden_cubensis"));

        REDSTONE_ROSE = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("redstone_rose")
                .blockFactory(prop -> new OreFlowerBlock(MobEffects.REACH_FOR_THE_STARS, 200, prop.mapColor(MapColor.PLANT).noCollission().instabreak().sound(SoundType.GRASS), false, null,
                        state -> state.is(BlockTags.DIRT) || state.getBlock() instanceof FarmBlock,
                        state -> state.is(Tags.Blocks.ORES_REDSTONE),

                        0.1f))
                .modelGenerator(SimpleCrossBlockModelGenerator::new)
                .renderType(BlockBuilder.RenderType.CUTOUT)
        ).getHolder();
        Items.registerBlockItemWithTexture(REDSTONE_ROSE, registerItem, ppId("block/redstone_rose"));

        BLACK_COALLA_LILY = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("black_coalla_lily")
                .blockFactory(prop -> new OreFlowerBlock(net.minecraft.world.effect.MobEffects.FIRE_RESISTANCE, 200, prop.mapColor(MapColor.PLANT).noCollission().instabreak().sound(SoundType.GRASS), false, null,
                        state -> state.is(BlockTags.DIRT) || state.getBlock() instanceof FarmBlock,
                        state -> state.is(Tags.Blocks.ORES_COAL),
                        0.1f))
                .modelGenerator(SimpleCrossBlockModelGenerator::new)
                .renderType(BlockBuilder.RenderType.CUTOUT)
        ).getHolder();
        Items.registerBlockItemWithTexture(BLACK_COALLA_LILY, registerItem, ppId("block/black_coalla_lily"));

        final Supplier<BlockBehaviour.Properties> versatilePlantPropertiesFactory = () -> BlockBehaviour.Properties.of()
                .mapColor(MapColor.PLANT)
                .noCollission()
                .instabreak()
                .sound(SoundType.GRASS)
                .ignitedByLava()
                .pushReaction(PushReaction.DESTROY);

        COWLICK_VINE = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("cowlick_vine")
                .properties(versatilePlantPropertiesFactory)
                .blockFactory(prop -> new VersatilePlantBlock(prop,
                        new VersatilePlantBlock.VersatilePlantConfig(true, false, 0, 9,
                                new VersatilePlantBlockTexturePattern(List.of(0, 1), List.of(2, 3), List.of(4), true))))
                .modelGenerator(holder -> new VersatilePlantBlockModelGenerator<>(holder,
                        new VersatilePlantBlockModelGenerator.ClientModelData.Builder<ResourceLocation>()
                                .resources(new ResourceLocation[]{
                                        ppId("block/cowlick_vine_0"), ppId("block/cowlick_vine_1"),
                                        ppId("block/cowlick_vine_2"), ppId("block/cowlick_vine_3"),
                                        ppId("block/cowlick_vine_tail")})
                                .texRotationFunction(VersatilePlantBlockModelGenerator.HANGING_PLANT_TEX_ORIENTATION)
                                .itemTexture(ppId("block/cowlick_vine_tail"))
                                .modelRegisterer(VersatilePlantBlockModelGenerator.CROSS_MODEL_GENERATOR)
                                .build()
                ))
                .lootGenerator(h -> new BlockLootUtility.VersatilePlantDropSelfLoot<>(LootContextParamSets.BLOCK, h))
                .renderType(BlockBuilder.RenderType.CUTOUT)
        ).getHolder();
        Items.registerBlockItem(COWLICK_VINE, registerItem);

        HANGING_FERN = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("hanging_fern")
                .properties(versatilePlantPropertiesFactory)
                .blockFactory(prop -> new BloomingPlantBlock(prop,
                        new VersatilePlantBlock.VersatilePlantConfig(true, false, 1, 5,
                                new VersatilePlantBlockTexturePattern(List.of(0), List.of(1), List.of(2), false)), 1))
                .modelGenerator(holder -> new BloomingVersatilePlantBlockModelGenerator<>(holder,
                        new BloomingVersatilePlantBlockModelGenerator.ClientModelData.Builder<ResourceLocation>()
                                .resources(new ResourceLocation[][]{
                                        new ResourceLocation[]{ppId("block/hanging_fern_upper"), ppId("block/hanging_fern_middle"), ppId("block/hanging_fern_lower")},
                                        new ResourceLocation[]{ppId("block/hanging_fern_upper_blooming"), ppId("block/hanging_fern_middle_blooming"), ppId("block/hanging_fern_lower_blooming")}})
                                .texRotationFunction(VersatilePlantBlockModelGenerator.HANGING_PLANT_TEX_ORIENTATION)
                                .itemTexture(ppId("block/hanging_fern_upper_blooming"))
                                .modelRegisterer(VersatilePlantBlockModelGenerator.CROSS_MODEL_GENERATOR)
                                .build()
                ))
                .lootGenerator(h -> new BlockLootUtility.VersatilePlantDropSelfLoot<>(LootContextParamSets.BLOCK, h))
                .renderType(BlockBuilder.RenderType.CUTOUT)
        ).getHolder();
        Items.registerBlockItem(HANGING_FERN, registerItem);

        DROOPY_VINE = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("droopy_vine")
                .properties(versatilePlantPropertiesFactory)
                .blockFactory(prop -> new BloomingPlantBlock(prop,
                        new VersatilePlantBlock.VersatilePlantConfig(true, false, 0, 7,
                                new VersatilePlantBlockTexturePattern(List.of(), List.of(0, 1), List.of(2), false)), 1))
                .modelGenerator(holder -> new BloomingVersatilePlantBlockModelGenerator<>(holder,
                        new BloomingVersatilePlantBlockModelGenerator.ClientModelData.Builder<ResourceLocation>()
                                .resources(new ResourceLocation[][]{
                                        new ResourceLocation[]{ppId("block/droopy_vine_0"), ppId("block/droopy_vine_1"), ppId("block/droopy_vine_2")},
                                        new ResourceLocation[]{ppId("block/droopy_vine_blooming_0"), ppId("block/droopy_vine_blooming_1"), ppId("block/droopy_vine_blooming_2")}})
                                .texRotationFunction(VersatilePlantBlockModelGenerator.HANGING_PLANT_TEX_ORIENTATION)
                                .itemTexture(ppId("block/droopy_vine_blooming_2"))
                                .modelRegisterer(VersatilePlantBlockModelGenerator.CROSS_MODEL_GENERATOR)
                                .build()
                ))
                .lootGenerator(h -> new BlockLootUtility.VersatilePlantDropSelfLoot<>(LootContextParamSets.BLOCK, h))
                .renderType(BlockBuilder.RenderType.CUTOUT)
        ).getHolder();
        Items.registerBlockItem(DROOPY_VINE, registerItem);

        SURVIVOR_STICK = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("survivor_stick")
                .properties(versatilePlantPropertiesFactory)
                .blockFactory(prop -> new BloomingPlantBlock(prop,
                        new VersatilePlantBlock.VersatilePlantConfig(true, false, 1, 6,
                                new VersatilePlantBlockTexturePattern(List.of(), List.of(0), List.of(1), false)), 1))
                .modelGenerator(holder -> new BloomingVersatilePlantBlockModelGenerator<>(holder,
                        new BloomingVersatilePlantBlockModelGenerator.ClientModelData.Builder<ResourceLocation>()
                                .resources(new ResourceLocation[][]{
                                        new ResourceLocation[]{ppId("block/survivor_stick_0"), ppId("block/survivor_stick_1")},
                                        new ResourceLocation[]{ppId("block/survivor_stick_blooming_0"), ppId("block/survivor_stick_blooming_1")},})
                                .texRotationFunction(VersatilePlantBlockModelGenerator.HANGING_PLANT_TEX_ORIENTATION)
                                .itemTexture(ppId("block/survivor_stick_blooming_1"))
                                .modelRegisterer(VersatilePlantBlockModelGenerator.CROSS_MODEL_GENERATOR)
                                .build()
                ))
                .lootGenerator(h -> new BlockLootUtility.VersatilePlantDropSelfLoot<>(LootContextParamSets.BLOCK, h))
                .renderType(BlockBuilder.RenderType.CUTOUT)
        ).getHolder();
        Items.registerBlockItem(SURVIVOR_STICK, registerItem);

        LUMOSEED_SACKS = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("lumoseed_sacks")
                .properties(versatilePlantPropertiesFactory)
                .blockFactory(prop -> new BloomingPlantBlock(prop,
                        new VersatilePlantBlock.VersatilePlantConfig(true, false, 0, 6,
                                new VersatilePlantBlockTexturePattern(List.of(), List.of(0), List.of(1), false)), 1))
                .modelGenerator(holder -> new BloomingVersatilePlantBlockModelGenerator<>(holder,
                        new BloomingVersatilePlantBlockModelGenerator.ClientModelData.Builder<Pair<ResourceLocation, Pair<ResourceLocation, ResourceLocation>>>()
                                .resources(new Pair[][]{
                                        new Pair[]{
                                                new Pair<>(ppId("block/lumoseed_sack"), new Pair<>(mc("block/moss_block"), mc("block/moss_block"))),
                                                new Pair(ppId("block/lumoseed_sack_tail"), new Pair<>(mc("block/moss_block"), mc("block/moss_block")))},
                                        new Pair[]{
                                                new Pair<>(ppId("block/lumoseed_sack"), new Pair<>(mc("block/moss_block"), mc("block/glowstone"))),
                                                new Pair(ppId("block/lumoseed_sack_tail"), new Pair<>(mc("block/moss_block"), mc("block/glowstone")))}
                                })
                                .texRotationFunction(VersatilePlantBlockModelGenerator.HANGING_PLANT_TEX_ORIENTATION)
                                .itemModel(ppId("block/lumoseed_sacks_b1_t0"))
                                .modelRegisterer(VersatilePlantBlockModelGenerator.LUMOSEED_MODEL_GENERATOR)
                                .build()
                ))
                .lootGenerator(h -> new BlockLootUtility.VersatilePlantDropSelfLoot<>(LootContextParamSets.BLOCK, h))
                .renderType(BlockBuilder.RenderType.SOLID)
        ).getHolder();
        Items.registerBlockItem(LUMOSEED_SACKS, registerItem);

        DANDELION_VERSATILE = RegistrationUtility.register(registerBlock, SimpleVersatilePlantBlockBuilder.create("dandelion_versatile",
                () -> new VersatilePlantBlockModelGenerator.ClientModelData.Builder<ResourceLocation>()
                        .resources(new ResourceLocation[]{mc("block/dandelion")})
                        .texRotationFunction(VersatilePlantBlockModelGenerator.UPRIGHT_PLANT_TEX_ORIENTATION)
                        .itemTexture(mc("block/dandelion"))
                        .modelRegisterer(VersatilePlantBlockModelGenerator.PARENTED_MODEL_GENERATOR)
                        .build()
        )).getHolder();
        Items.registerBlockItem(DANDELION_VERSATILE, registerItem);

        TORCHFLOWER_VERSATILE = RegistrationUtility.register(registerBlock, SimpleVersatilePlantBlockBuilder.create("torchflower_versatile",
                () -> new VersatilePlantBlockModelGenerator.ClientModelData.Builder<ResourceLocation>()
                        .resources(new ResourceLocation[]{mc("block/torchflower")})
                        .texRotationFunction(VersatilePlantBlockModelGenerator.UPRIGHT_PLANT_TEX_ORIENTATION)
                        .itemTexture(mc("block/torchflower"))
                        .modelRegisterer(VersatilePlantBlockModelGenerator.PARENTED_MODEL_GENERATOR)
                        .build()
        )).getHolder();
        Items.registerBlockItem(TORCHFLOWER_VERSATILE, registerItem);

        POPPY_VERSATILE = RegistrationUtility.register(registerBlock, SimpleVersatilePlantBlockBuilder.create("poppy_versatile",
                () -> new VersatilePlantBlockModelGenerator.ClientModelData.Builder<ResourceLocation>()
                        .resources(new ResourceLocation[]{mc("block/poppy")})
                        .texRotationFunction(VersatilePlantBlockModelGenerator.UPRIGHT_PLANT_TEX_ORIENTATION)
                        .itemTexture(mc("block/poppy"))
                        .modelRegisterer(VersatilePlantBlockModelGenerator.PARENTED_MODEL_GENERATOR)
                        .build()
        )).getHolder();
        Items.registerBlockItem(POPPY_VERSATILE, registerItem);

        BLUE_ORCHID_VERSATILE = RegistrationUtility.register(registerBlock, SimpleVersatilePlantBlockBuilder.create("blue_orchid_versatile",
                () -> new VersatilePlantBlockModelGenerator.ClientModelData.Builder<ResourceLocation>()
                        .resources(new ResourceLocation[]{mc("block/blue_orchid")})
                        .texRotationFunction(VersatilePlantBlockModelGenerator.UPRIGHT_PLANT_TEX_ORIENTATION)
                        .itemTexture(mc("block/blue_orchid"))
                        .modelRegisterer(VersatilePlantBlockModelGenerator.PARENTED_MODEL_GENERATOR)
                        .build()
        )).getHolder();
        Items.registerBlockItem(BLUE_ORCHID_VERSATILE, registerItem);

        ALLIUM_VERSATILE = RegistrationUtility.register(registerBlock, SimpleVersatilePlantBlockBuilder.create("allium_versatile",
                () -> new VersatilePlantBlockModelGenerator.ClientModelData.Builder<ResourceLocation>()
                        .resources(new ResourceLocation[]{mc("block/allium")})
                        .texRotationFunction(VersatilePlantBlockModelGenerator.UPRIGHT_PLANT_TEX_ORIENTATION)
                        .itemTexture(mc("block/allium"))
                        .modelRegisterer(VersatilePlantBlockModelGenerator.PARENTED_MODEL_GENERATOR)
                        .build()
        )).getHolder();
        Items.registerBlockItem(ALLIUM_VERSATILE, registerItem);

        AZURE_BLUET_VERSATILE = RegistrationUtility.register(registerBlock, SimpleVersatilePlantBlockBuilder.create("azure_bluet_versatile",
                () -> new VersatilePlantBlockModelGenerator.ClientModelData.Builder<ResourceLocation>()
                        .resources(new ResourceLocation[]{mc("block/azure_bluet")})
                        .texRotationFunction(VersatilePlantBlockModelGenerator.UPRIGHT_PLANT_TEX_ORIENTATION)
                        .itemTexture(mc("block/azure_bluet"))
                        .modelRegisterer(VersatilePlantBlockModelGenerator.PARENTED_MODEL_GENERATOR)
                        .build()
        )).getHolder();
        Items.registerBlockItem(AZURE_BLUET_VERSATILE, registerItem);

        RED_TULIP_VERSATILE = RegistrationUtility.register(registerBlock, SimpleVersatilePlantBlockBuilder.create("red_tulip_versatile",
                () -> new VersatilePlantBlockModelGenerator.ClientModelData.Builder<ResourceLocation>()
                        .resources(new ResourceLocation[]{mc("block/red_tulip")})
                        .texRotationFunction(VersatilePlantBlockModelGenerator.UPRIGHT_PLANT_TEX_ORIENTATION)
                        .itemTexture(mc("block/red_tulip"))
                        .modelRegisterer(VersatilePlantBlockModelGenerator.PARENTED_MODEL_GENERATOR)
                        .build()
        )).getHolder();
        Items.registerBlockItem(RED_TULIP_VERSATILE, registerItem);

        ORANGE_TULIP_VERSATILE = RegistrationUtility.register(registerBlock, SimpleVersatilePlantBlockBuilder.create("orange_tulip_versatile",
                () -> new VersatilePlantBlockModelGenerator.ClientModelData.Builder<ResourceLocation>()
                        .resources(new ResourceLocation[]{mc("block/orange_tulip")})
                        .texRotationFunction(VersatilePlantBlockModelGenerator.UPRIGHT_PLANT_TEX_ORIENTATION)
                        .itemTexture(mc("block/orange_tulip"))
                        .modelRegisterer(VersatilePlantBlockModelGenerator.PARENTED_MODEL_GENERATOR)
                        .build()
        )).getHolder();
        Items.registerBlockItem(ORANGE_TULIP_VERSATILE, registerItem);

        WHITE_TULIP_VERSATILE = RegistrationUtility.register(registerBlock, SimpleVersatilePlantBlockBuilder.create("white_tulip_versatile",
                () -> new VersatilePlantBlockModelGenerator.ClientModelData.Builder<ResourceLocation>()
                        .resources(new ResourceLocation[]{mc("block/white_tulip")})
                        .texRotationFunction(VersatilePlantBlockModelGenerator.UPRIGHT_PLANT_TEX_ORIENTATION)
                        .itemTexture(mc("block/white_tulip"))
                        .modelRegisterer(VersatilePlantBlockModelGenerator.PARENTED_MODEL_GENERATOR)
                        .build()
        )).getHolder();
        Items.registerBlockItem(WHITE_TULIP_VERSATILE, registerItem);

        PINK_TULIP_VERSATILE = RegistrationUtility.register(registerBlock, SimpleVersatilePlantBlockBuilder.create("pink_tulip_versatile",
                () -> new VersatilePlantBlockModelGenerator.ClientModelData.Builder<ResourceLocation>()
                        .resources(new ResourceLocation[]{mc("block/pink_tulip")})
                        .texRotationFunction(VersatilePlantBlockModelGenerator.UPRIGHT_PLANT_TEX_ORIENTATION)
                        .itemTexture(mc("block/pink_tulip"))
                        .modelRegisterer(VersatilePlantBlockModelGenerator.PARENTED_MODEL_GENERATOR)
                        .build()
        )).getHolder();
        Items.registerBlockItem(PINK_TULIP_VERSATILE, registerItem);

        OXEYE_DAISY_VERSATILE = RegistrationUtility.register(registerBlock, SimpleVersatilePlantBlockBuilder.create("oxeye_daisy_versatile",
                () -> new VersatilePlantBlockModelGenerator.ClientModelData.Builder<ResourceLocation>()
                        .resources(new ResourceLocation[]{mc("block/oxeye_daisy")})
                        .texRotationFunction(VersatilePlantBlockModelGenerator.UPRIGHT_PLANT_TEX_ORIENTATION)
                        .itemTexture(mc("block/oxeye_daisy"))
                        .modelRegisterer(VersatilePlantBlockModelGenerator.PARENTED_MODEL_GENERATOR)
                        .build()
        )).getHolder();
        Items.registerBlockItem(OXEYE_DAISY_VERSATILE, registerItem);

        CORNFLOWER_VERSATILE = RegistrationUtility.register(registerBlock, SimpleVersatilePlantBlockBuilder.create("cornflower_versatile",
                () -> new VersatilePlantBlockModelGenerator.ClientModelData.Builder<ResourceLocation>()
                        .resources(new ResourceLocation[]{mc("block/cornflower")})
                        .texRotationFunction(VersatilePlantBlockModelGenerator.UPRIGHT_PLANT_TEX_ORIENTATION)
                        .itemTexture(mc("block/cornflower"))
                        .modelRegisterer(VersatilePlantBlockModelGenerator.PARENTED_MODEL_GENERATOR)
                        .build()
        )).getHolder();
        Items.registerBlockItem(CORNFLOWER_VERSATILE, registerItem);

        WITHER_ROSE_VERSATILE = RegistrationUtility.register(registerBlock, SimpleVersatilePlantBlockBuilder.create("wither_rose_versatile",
                () -> new VersatilePlantBlockModelGenerator.ClientModelData.Builder<ResourceLocation>()
                        .resources(new ResourceLocation[]{mc("block/wither_rose")})
                        .texRotationFunction(VersatilePlantBlockModelGenerator.UPRIGHT_PLANT_TEX_ORIENTATION)
                        .itemTexture(mc("block/wither_rose"))
                        .modelRegisterer(VersatilePlantBlockModelGenerator.PARENTED_MODEL_GENERATOR)
                        .build()
        )).getHolder();
        Items.registerBlockItem(WITHER_ROSE_VERSATILE, registerItem);

        LILY_OF_THE_VALLEY_VERSATILE = RegistrationUtility.register(registerBlock, SimpleVersatilePlantBlockBuilder.create("lily_of_the_valley_versatile",
                () -> new VersatilePlantBlockModelGenerator.ClientModelData.Builder<ResourceLocation>()
                        .resources(new ResourceLocation[]{mc("block/lily_of_the_valley")})
                        .texRotationFunction(VersatilePlantBlockModelGenerator.UPRIGHT_PLANT_TEX_ORIENTATION)
                        .itemTexture(mc("block/lily_of_the_valley"))
                        .modelRegisterer(VersatilePlantBlockModelGenerator.PARENTED_MODEL_GENERATOR)
                        .build()
        )).getHolder();
        Items.registerBlockItem(LILY_OF_THE_VALLEY_VERSATILE, registerItem);

        BROWN_MUSHROOM_VERSATILE = RegistrationUtility.register(registerBlock, SimpleVersatilePlantBlockBuilder.create("brown_mushroom_versatile",
                () -> new VersatilePlantBlockModelGenerator.ClientModelData.Builder<ResourceLocation>()
                        .resources(new ResourceLocation[]{mc("block/brown_mushroom")})
                        .texRotationFunction(VersatilePlantBlockModelGenerator.UPRIGHT_PLANT_TEX_ORIENTATION)
                        .itemTexture(mc("block/brown_mushroom"))
                        .modelRegisterer(VersatilePlantBlockModelGenerator.PARENTED_MODEL_GENERATOR)
                        .build()
        )).getHolder();
        Items.registerBlockItem(BROWN_MUSHROOM_VERSATILE, registerItem);

        RED_MUSHROOM_VERSATILE = RegistrationUtility.register(registerBlock, SimpleVersatilePlantBlockBuilder.create("red_mushroom_versatile",
                () -> new VersatilePlantBlockModelGenerator.ClientModelData.Builder<ResourceLocation>()
                        .resources(new ResourceLocation[]{mc("block/red_mushroom")})
                        .texRotationFunction(VersatilePlantBlockModelGenerator.UPRIGHT_PLANT_TEX_ORIENTATION)
                        .itemTexture(mc("block/red_mushroom"))
                        .modelRegisterer(VersatilePlantBlockModelGenerator.PARENTED_MODEL_GENERATOR)
                        .build()
        )).getHolder();
        Items.registerBlockItem(RED_MUSHROOM_VERSATILE, registerItem);

        SUNFLOWER_VERSATILE = RegistrationUtility.register(registerBlock, SimpleTallVersatilePlantBlockBuilder.create("sunflower_versatile",
                true,
                () -> new VersatilePlantBlockModelGenerator.ClientModelData.Builder<ResourceLocation>()
                        .resources(new ResourceLocation[]{mc("block/sunflower_bottom"), mc("block/sunflower_top")})
                        .texRotationFunction(VersatilePlantBlockModelGenerator.UPRIGHT_PLANT_TEX_ORIENTATION)
                        .itemTexture(mc("block/sunflower_front"))
                        .modelRegisterer(VersatilePlantBlockModelGenerator.PARENTED_MODEL_GENERATOR)
                        .build()
        )).getHolder();
        Items.registerBlockItem(SUNFLOWER_VERSATILE, registerItem);

        LILAC_VERSATILE = RegistrationUtility.register(registerBlock, SimpleTallVersatilePlantBlockBuilder.create("lilac_versatile",
                true,
                () -> new VersatilePlantBlockModelGenerator.ClientModelData.Builder<ResourceLocation>()
                        .resources(new ResourceLocation[]{mc("block/lilac_bottom"), mc("block/lilac_top")})
                        .texRotationFunction(VersatilePlantBlockModelGenerator.UPRIGHT_PLANT_TEX_ORIENTATION)
                        .itemTexture(mc("block/lilac_top"))
                        .modelRegisterer(VersatilePlantBlockModelGenerator.PARENTED_MODEL_GENERATOR)
                        .build()
        )).getHolder();
        Items.registerBlockItem(LILAC_VERSATILE, registerItem);

        ROSE_BUSH_VERSATILE = RegistrationUtility.register(registerBlock, SimpleTallVersatilePlantBlockBuilder.create("rose_bush_versatile",
                true,
                () -> new VersatilePlantBlockModelGenerator.ClientModelData.Builder<ResourceLocation>()
                        .resources(new ResourceLocation[]{mc("block/rose_bush_bottom"), mc("block/rose_bush_top")})
                        .texRotationFunction(VersatilePlantBlockModelGenerator.UPRIGHT_PLANT_TEX_ORIENTATION)
                        .itemTexture(mc("block/rose_bush_top"))
                        .modelRegisterer(VersatilePlantBlockModelGenerator.PARENTED_MODEL_GENERATOR)
                        .build()
        )).getHolder();
        Items.registerBlockItem(ROSE_BUSH_VERSATILE, registerItem);

        PEONY_VERSATILE = RegistrationUtility.register(registerBlock, SimpleTallVersatilePlantBlockBuilder.create("peony_versatile",
                true,
                () -> new VersatilePlantBlockModelGenerator.ClientModelData.Builder<ResourceLocation>()
                        .resources(new ResourceLocation[]{mc("block/peony_bottom"), mc("block/peony_top")})
                        .texRotationFunction(VersatilePlantBlockModelGenerator.UPRIGHT_PLANT_TEX_ORIENTATION)
                        .itemTexture(mc("block/peony_top"))
                        .modelRegisterer(VersatilePlantBlockModelGenerator.PARENTED_MODEL_GENERATOR)
                        .build()
        )).getHolder();
        Items.registerBlockItem(PEONY_VERSATILE, registerItem);

        TALL_GRASS_VERSATILE = RegistrationUtility.register(registerBlock, SimpleTallVersatilePlantBlockBuilder.create("tall_grass_versatile",
                true,
                () -> new VersatilePlantBlockModelGenerator.ClientModelData.Builder<ResourceLocation>()
                        .resources(new ResourceLocation[]{mc("block/tall_grass_bottom"), mc("block/tall_grass_top")})
                        .texRotationFunction(VersatilePlantBlockModelGenerator.UPRIGHT_PLANT_TEX_ORIENTATION)
                        .itemTexture(mc("block/tall_grass_top"))
                        .modelRegisterer(VersatilePlantBlockModelGenerator.PARENTED_MODEL_GENERATOR)
                        .useTint()
                        .build()
        )).getHolder();
        Items.registerBlockItem(TALL_GRASS_VERSATILE, registerItem);

        LARGE_FERN_VERSATILE = RegistrationUtility.register(registerBlock, SimpleTallVersatilePlantBlockBuilder.create("large_fern_versatile",
                true,
                () -> new VersatilePlantBlockModelGenerator.ClientModelData.Builder<ResourceLocation>()
                        .resources(new ResourceLocation[]{mc("block/large_fern_bottom"), mc("block/large_fern_top")})
                        .texRotationFunction(VersatilePlantBlockModelGenerator.UPRIGHT_PLANT_TEX_ORIENTATION)
                        .itemTexture(mc("block/large_fern_top"))
                        .modelRegisterer(VersatilePlantBlockModelGenerator.PARENTED_MODEL_GENERATOR)
                        .useTint()
                        .build()
        )).getHolder();
        Items.registerBlockItem(LARGE_FERN_VERSATILE, registerItem);

        PITCHER_PLANT_VERSATILE = RegistrationUtility.register(registerBlock, SimpleTallVersatilePlantBlockBuilder.create("pitcher_plant_versatile",
                false,
                () -> new VersatilePlantBlockModelGenerator.ClientModelData.Builder<ResourceLocation>()
                        .resources(new ResourceLocation[]{mc("block/pitcher_plant_bottom"), mc("block/pitcher_plant_top")})
                        .texRotationFunction(VersatilePlantBlockModelGenerator.UPRIGHT_PLANT_TEX_ORIENTATION)
                        .itemTexture(mc("item/pitcher_plant"))
                        .modelRegisterer(VersatilePlantBlockModelGenerator.PARENTED_MODEL_GENERATOR)
                        .build()
        )).getHolder();
        Items.registerBlockItem(PITCHER_PLANT_VERSATILE, registerItem);

        TOMATO_PLANT = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.create("tomato_plant")
                .modelGenerator(h -> new GeneticCropBlockModelGenerator<>(h, ModelTemplates.CROP,
                        new GeneticCropBlockModelGenerator.PlantTextures(ppId("block/tomatoes_0"), ppId("block/tomatoes_pollinated_0")),
                        new GeneticCropBlockModelGenerator.PlantTextures(ppId("block/tomatoes_1"), ppId("block/tomatoes_pollinated_1")),
                        new GeneticCropBlockModelGenerator.PlantTextures(ppId("block/tomatoes_2"), ppId("block/tomatoes_pollinated_2")),
                        new GeneticCropBlockModelGenerator.PlantTextures(ppId("block/tomatoes_3"), ppId("block/tomatoes_pollinated_3")),
                        new GeneticCropBlockModelGenerator.PlantTextures(ppId("block/tomatoes_4"), ppId("block/tomatoes_pollinated_4")),
                        new GeneticCropBlockModelGenerator.PlantTextures(ppId("block/tomatoes_5"), ppId("block/tomatoes_pollinated_5"), ppId("block/tomatoes_5_ripe"))))
                .properties(() -> BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT).mapColor(MapColor.PLANT))
                .blockFactory(prop -> new GeneticCropBlock(prop, GeneticCropBlock.CropProperties.of(PlantItems.TOMATO, 25, 200, 5, 400, 0F, true)))
                .renderType(BlockBuilder.RenderType.CUTOUT)
        ).getHolder();

        BRASSICA_OLERACEA_PLANT = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.create("brassica_oleracea_plant")
                .modelGenerator(h -> new GeneticCropBlockModelGenerator<>(h, GeneticCropBlockModelGenerator.CROP_CROSS,
                        new GeneticCropBlockModelGenerator.PlantTextures(ppId("block/brassica_oleracea_0"), ppId("block/brassica_oleracea_pollinated_0"), ppId("block/brassica_oleracea_ripe_0")),
                        new GeneticCropBlockModelGenerator.PlantTextures(ppId("block/brassica_oleracea_1"), ppId("block/brassica_oleracea_pollinated_1"), ppId("block/brassica_oleracea_ripe_1")),
                        new GeneticCropBlockModelGenerator.PlantTextures(ppId("block/brassica_oleracea_2"), ppId("block/brassica_oleracea_pollinated_2"), ppId("block/brassica_oleracea_ripe_2")),
                        new GeneticCropBlockModelGenerator.PlantTextures(ppId("block/brassica_oleracea_3"), ppId("block/brassica_oleracea_pollinated_3"), ppId("block/brassica_oleracea_ripe_3")),
                        new GeneticCropBlockModelGenerator.PlantTextures(ppId("block/brassica_oleracea_4"), ppId("block/brassica_oleracea_pollinated_4"), ppId("block/brassica_oleracea_ripe_4")),
                        new GeneticCropBlockModelGenerator.PlantTextures(ppId("block/brassica_oleracea_5"), ppId("block/brassica_oleracea_pollinated_5"), ppId("block/brassica_oleracea_ripe_5")),
                        new GeneticCropBlockModelGenerator.PlantTextures(ppId("block/brassica_oleracea_6"), ppId("block/brassica_oleracea_pollinated_6"), ppId("block/brassica_oleracea_ripe_6")),
                        new GeneticCropBlockModelGenerator.PlantTextures(ppId("block/brassica_oleracea_7"), ppId("block/brassica_oleracea_pollinated_7"), ppId("block/brassica_oleracea_ripe_7")),
                        new GeneticCropBlockModelGenerator.PlantTextures(ppId("block/brassica_oleracea_8"), ppId("block/brassica_oleracea_pollinated_8"), ppId("block/brassica_oleracea_ripe_8")),
                        new GeneticCropBlockModelGenerator.PlantTextures(ppId("block/brassica_oleracea_9"), ppId("block/brassica_oleracea_pollinated_9"), ppId("block/brassica_oleracea_ripe_9"))
                ))
                .properties(() -> BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT).mapColor(MapColor.PLANT))
                .blockFactory(prop -> new GeneticCropBlock(prop, GeneticCropBlock.CropProperties.of(PlantItems.BRASSICA_OLERACEA, 25, 200, 10, 400, 0.25F)))
                .renderType(BlockBuilder.RenderType.CUTOUT)
        ).getHolder();

        CABBAGE_PLANT = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.create("cabbage_plant")
                .modelGenerator(h -> new GeneticCropBlockModelGenerator<>(h, GeneticCropBlockModelGenerator.CROP_CROSS,
                        new GeneticCropBlockModelGenerator.PlantTextures(ppId("block/tomatoes_0"), ppId("block/tomatoes_pollinated_0"))))
                .properties(() -> BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT).mapColor(MapColor.PLANT))
                .blockFactory(prop -> new GeneticCropBlock(prop, GeneticCropBlock.CropProperties.of(PlantItems.CABBAGE, 25, 200, 10, 400, 0.25F)))
                .renderType(BlockBuilder.RenderType.CUTOUT)
        ).getHolder();

        KALE_PLANT = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.create("kale_plant")
                .modelGenerator(h -> new GeneticCropBlockModelGenerator<>(h, GeneticCropBlockModelGenerator.CROP_CROSS,
                        new GeneticCropBlockModelGenerator.PlantTextures(ppId("block/tomatoes_0"), ppId("block/tomatoes_pollinated_0"))))
                .properties(() -> BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT).mapColor(MapColor.PLANT))
                .blockFactory(prop -> new GeneticCropBlock(prop, GeneticCropBlock.CropProperties.of(PlantItems.KALE, 25, 200, 10, 400, 0.25F)))
                .renderType(BlockBuilder.RenderType.CUTOUT)
        ).getHolder();

        BROCCOLLI_PLANT = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.create("broccoli_plant")
                .modelGenerator(h -> new GeneticCropBlockModelGenerator<>(h, GeneticCropBlockModelGenerator.CROP_CROSS,
                        new GeneticCropBlockModelGenerator.PlantTextures(ppId("block/tomatoes_0"), ppId("block/tomatoes_pollinated_0"))))
                .properties(() -> BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT).mapColor(MapColor.PLANT))
                .blockFactory(prop -> new GeneticCropBlock(prop, GeneticCropBlock.CropProperties.of(PlantItems.BROCCOLI, 25, 200, 10, 400, 0.25F)))
                .renderType(BlockBuilder.RenderType.CUTOUT)
        ).getHolder();

        CAULIFLOWER_PLANT = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.create("cauliflower_plant")
                .modelGenerator(h -> new GeneticCropBlockModelGenerator<>(h, GeneticCropBlockModelGenerator.CROP_CROSS,
                        new GeneticCropBlockModelGenerator.PlantTextures(ppId("block/tomatoes_0"), ppId("block/tomatoes_pollinated_0"))))
                .properties(() -> BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT).mapColor(MapColor.PLANT))
                .blockFactory(prop -> new GeneticCropBlock(prop, GeneticCropBlock.CropProperties.of(PlantItems.CAULIFLOWER, 25, 200, 10, 400, 0.25F)))
                .renderType(BlockBuilder.RenderType.CUTOUT)
        ).getHolder();

        KOHLRABI_PLANT = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.create("kohlrabi_plant")
                .modelGenerator(h -> new GeneticCropBlockModelGenerator<>(h, GeneticCropBlockModelGenerator.CROP_CROSS,
                        new GeneticCropBlockModelGenerator.PlantTextures(ppId("block/tomatoes_0"), ppId("block/tomatoes_pollinated_0"))))
                .properties(() -> BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT).mapColor(MapColor.PLANT))
                .blockFactory(prop -> new GeneticCropBlock(prop, GeneticCropBlock.CropProperties.of(PlantItems.KOHLRABI, 25, 200, 10, 400, 0.25F)))
                .renderType(BlockBuilder.RenderType.CUTOUT)
        ).getHolder();

        BRUSSELS_SPROUTS_PLANT = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.create("brussels_sprouts_plant")
                .modelGenerator(h -> new GeneticCropBlockModelGenerator<>(h, GeneticCropBlockModelGenerator.CROP_CROSS,
                        new GeneticCropBlockModelGenerator.PlantTextures(ppId("block/tomatoes_0"), ppId("block/tomatoes_pollinated_0"))))
                .properties(() -> BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT).mapColor(MapColor.PLANT))
                .blockFactory(prop -> new GeneticCropBlock(prop, GeneticCropBlock.CropProperties.of(PlantItems.BRUSSELS_SPROUTS, 25, 200, 10, 400, 0.25F)))
                .renderType(BlockBuilder.RenderType.CUTOUT)
        ).getHolder();

        GENETIC_CROP_PLANTS = List.of(
                TOMATO_PLANT,
                BRASSICA_OLERACEA_PLANT,
                CABBAGE_PLANT,
                KALE_PLANT,
                BROCCOLLI_PLANT,
                CAULIFLOWER_PLANT,
                KOHLRABI_PLANT,
                BRUSSELS_SPROUTS_PLANT
        );
    }
}
