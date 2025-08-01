package grill24.potionsplus.core.blocks;

import grill24.potionsplus.block.*;
import grill24.potionsplus.core.Tags;
import grill24.potionsplus.core.items.OreItems;
import grill24.potionsplus.utility.registration.RecipeGeneratorUtility;
import grill24.potionsplus.utility.registration.RegistrationUtility;
import grill24.potionsplus.utility.registration.RuntimeTextureVariantModelGenerator;
import grill24.potionsplus.utility.registration.block.*;
import grill24.potionsplus.utility.registration.item.ItemModelUtility;
import grill24.potionsplus.utility.registration.item.SimpleItemBuilder;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import static grill24.potionsplus.utility.Utility.mc;
import static grill24.potionsplus.utility.Utility.ppId;

public class BlockEntityBlocks {
    public static Holder<Block> BREWING_CAULDRON;
    public static Holder<Block> PARTICLE_EMITTER;
    public static Holder<Block> HERBALISTS_LECTERN;
    public static Holder<Block> SANGUINE_ALTAR;
    public static Holder<Block> ABYSSAL_TROVE;
    public static Holder<Block> PRECISION_DISPENSER;
    public static Holder<Block> CLOTHESLINE;
    public static Holder<Block> POTION_BEACON;
    public static Holder<Block> SKILL_JOURNALS;
    public static Holder<Block> FISHING_LEADERBOARDS;
    public static Holder<Block> FISH_TANK;

    public static List<Holder<Block>> FISH_TANK_BLOCKS;
    public static Map<Integer, Holder<Block>> FISH_TANK_FRAME_BLOCKS;
    public static Map<Integer, Holder<Block>> FISH_TANK_SAND_BLOCKS;

    public static Holder<Block> SMALL_FILTER_HOPPER, LARGE_FILTER_HOPPER, HUGE_FILTER_HOPPER;

    public static void init(BiFunction<String, Supplier<Block>, Holder<Block>> registerBlock, BiFunction<String, Supplier<Item>, Holder<Item>> registerItem) {
        BREWING_CAULDRON = RegistrationUtility.register(
                registerBlock,
                SimpleBlockBuilder.createSimple("brewing_cauldron")
                        .blockFactory(BrewingCauldronBlock::new)
                        .properties(() -> BlockBehaviour.Properties.ofFullCopy(Blocks.WATER_CAULDRON))
                        .modelGenerator((blockGetter) ->
                                new BlockModelUtility.FromModelFileBlockStateGenerator<>(blockGetter, mc("block/water_cauldron_full"), true, false))
        ).getHolder();
        grill24.potionsplus.core.Items.registerBlockItemWithTexture(BREWING_CAULDRON, registerItem, ppId("item/brewing_cauldron"));

        PARTICLE_EMITTER = RegistrationUtility.register(
                registerBlock,
                SimpleBlockBuilder.createSimple("particle_emitter")
                        .blockFactory(ParticleEmitterBlock::new)
                        .properties(() -> BlockBehaviour.Properties.of().mapColor(MapColor.METAL).requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.METAL))
                        .modelGenerator(ParticleEmitterBlockModelGenerator::new) // Custom model generator for ParticleEmitterBlock
                        .recipeGenerator(holder -> new RecipeGeneratorUtility.RecipeGenerator<>(holder,
                                (recipeProvider, h) ->
                                        recipeProvider.shaped(RecipeCategory.REDSTONE, h.value())
                                                .pattern("III")
                                                .pattern("IXI")
                                                .pattern("III")
                                                .define('I', Items.IRON_INGOT)
                                                .define('X', Items.SPORE_BLOSSOM)
                                                .unlockedBy("has_iron_ingot", recipeProvider.has(Items.SPORE_BLOSSOM))))
        ).getHolder();
        grill24.potionsplus.core.Items.registerBlockItem(PARTICLE_EMITTER, registerItem); // Doesn't generate an item model because we generate it in ParticleEmitterBlockModelGenerator

        HERBALISTS_LECTERN = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("herbalists_lectern")
                        .blockFactory(HerbalistsLecternBlock::new)
                        .properties(() -> BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(2.5F).sound(SoundType.WOOD))
                        .modelGenerator(holder -> new BlockModelUtility.FromModelFileBlockStateGenerator<>(holder, ppId("block/herbalists_lectern")))
                        .recipeGenerator(holder -> new RecipeGeneratorUtility.RecipeGenerator<>(holder,
                                (recipeProvider, h) ->
                                        recipeProvider.shaped(RecipeCategory.BREWING, h.value())
                                                .pattern("WWW")
                                                .pattern("S S")
                                                .pattern("DDD")
                                                .define('W', ItemTags.PLANKS)
                                                .define('S', Items.STICK)
                                                .define('D', Items.DEEPSLATE_BRICK_SLAB)
                                                .unlockedBy("has_potion", recipeProvider.has(Items.POTION)))))
                .getHolder();
        grill24.potionsplus.core.Items.registerBlockItem(HERBALISTS_LECTERN, registerItem);

        SANGUINE_ALTAR = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("sanguine_altar")
                        .blockFactory(SanguineAltarBlock::new)
                        .properties(() -> BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE).strength(2.5F).requiresCorrectToolForDrops().sound(SoundType.STONE))
                        .modelGenerator(HorizontalDirectionalBlockModelGenerator::new)
                        .recipeGenerator(holder -> new RecipeGeneratorUtility.RecipeGenerator<>(holder,
                                (recipeProvider, h) ->
                                        recipeProvider.shaped(RecipeCategory.BREWING, h.value())
                                                .pattern("AEA")
                                                .pattern("ESE")
                                                .pattern("AEA")
                                                .define('E', Items.END_ROD)
                                                .define('A', Items.AMETHYST_SHARD)
                                                .define('S', Items.SOUL_SAND)
                                                .unlockedBy("has_potion", recipeProvider.has(Items.POTION)))))
                .getHolder();
        grill24.potionsplus.core.Items.registerBlockItemWithAutoModel(() -> SANGUINE_ALTAR, registerItem);

        ABYSSAL_TROVE = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("abyssal_trove")
                        .blockFactory(AbyssalTroveBlock::new)
                        .properties(() -> BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).strength(5.0F, 6.0F).sound(SoundType.SOUL_SAND))
                        .modelGenerator(HorizontalDirectionalBlockModelGenerator::new)
                        .recipeGenerator(holder -> new RecipeGeneratorUtility.RecipeGenerator<>(holder,
                                (recipeProvider, h) ->
                                        recipeProvider.shaped(RecipeCategory.BREWING, h.value())
                                                .pattern("OSO")
                                                .pattern("SAS")
                                                .pattern("OSO")
                                                .define('O', Items.SOUL_SOIL)
                                                .define('A', Items.AMETHYST_BLOCK)
                                                .define('S', Items.SOUL_SAND)
                                                .unlockedBy("has_potion", recipeProvider.has(Items.POTION)))))
                .getHolder();
        grill24.potionsplus.core.Items.registerBlockItemWithAutoModel(() -> ABYSSAL_TROVE, registerItem);

        PRECISION_DISPENSER = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("precision_dispenser")
                        .blockFactory(PrecisionDispenserBlock::new)
                        .properties(() -> BlockBehaviour.Properties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(3.5F).sound(SoundType.METAL))
                        .modelGenerator(null)
                        .recipeGenerator(holder -> new RecipeGeneratorUtility.RecipeGenerator<>(holder,
                                (recipeProvider, h) ->
                                        recipeProvider.shapeless(RecipeCategory.REDSTONE, h.value())
                                                .requires(net.minecraft.world.item.Items.DISPENSER)
                                                .requires(net.minecraft.world.item.Items.SPYGLASS)
                                                .unlockedBy("has_dispenser", recipeProvider.has(net.minecraft.world.item.Items.DISPENSER)))))
                .getHolder();
        grill24.potionsplus.core.Items.registerBlockItemWithAutoModel(() -> PRECISION_DISPENSER, registerItem);


        CLOTHESLINE = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("clothesline")
                        .blockFactory(ClotheslineBlock::new)
                        .properties(() -> BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).noOcclusion().strength(0.8F).sound(SoundType.WOOD))
                        .lootGenerator(null) // Hand-made custom loot table json
                        .modelGenerator(h -> new ClotheslineBlockModelGenerator<>(h)))
                .getHolder();
        grill24.potionsplus.core.Items.registerBlockItem(CLOTHESLINE, registerItem); // Doesn't generate an item model because we generate it in ClotheslineBlockModelGenerator

        POTION_BEACON = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("potion_beacon")
                .blockFactory(PotionBeaconBlock::new)
                .properties(() -> BlockBehaviour.Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.BEACON).mapColor(MapColor.WOOD).requiresCorrectToolForDrops().strength(2.5F).sound(SoundType.WOOD))
                .modelGenerator(holder -> new BlockModelUtility.FromModelFileBlockStateGenerator<>(holder, ppId("block/potion_beacon")))
                .recipeGenerator(holder -> new RecipeGeneratorUtility.RecipeGenerator<>(holder,
                        ppId("potion_beacon"),
                        (recipeProvider, h) ->
                                recipeProvider.shaped(RecipeCategory.BREWING, h.value())
                                        .pattern("GGG")
                                        .pattern("GNG")
                                        .pattern("OOO")
                                        .define('G', OreBlocks.URANIUM_GLASS.value())
                                        .define('N', Items.NETHER_STAR)
                                        .define('O', Items.OBSIDIAN)
                                        .unlockedBy("has_uranium_glass", recipeProvider.has(OreBlocks.URANIUM_GLASS.value()))))
                .recipeGenerator(holder -> new RecipeGeneratorUtility.RecipeGenerator<>(holder,
                        ppId("potion_beacon_alternate"),
                        (recipeProvider, h) ->
                                recipeProvider.shaped(RecipeCategory.BREWING, h.value())
                                        .pattern("GGG")
                                        .pattern("GBG")
                                        .pattern("OOO")
                                        .define('G', OreBlocks.URANIUM_GLASS.value())
                                        .define('B', Items.BEACON)
                                        .define('O', Items.OBSIDIAN)
                                        .unlockedBy("has_uranium_glass", recipeProvider.has(OreBlocks.URANIUM_GLASS.value()))))
        ).getHolder();
        grill24.potionsplus.core.Items.registerBlockItem(POTION_BEACON, registerItem);

        SKILL_JOURNALS = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("skill_journals")
                .blockFactory(SkillJournalsBlock::new)
                .properties(() -> BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(2.5F).sound(SoundType.WOOD).noOcclusion())
                .modelGenerator(HorizontalDirectionalBlockModelGenerator::new)
                .recipeGenerator(holder -> new RecipeGeneratorUtility.RecipeGenerator<>(holder,
                        (recipeProvider, h) ->
                                recipeProvider.shapeless(RecipeCategory.MISC, BlockEntityBlocks.SKILL_JOURNALS.value().asItem())
                                        .requires(net.minecraft.world.item.Items.BOOK)
                                        .requires(net.minecraft.world.item.Items.BOOK)
                                        .requires(net.minecraft.world.item.Items.BOOK)
                                        .unlockedBy("has_book", recipeProvider.has(net.minecraft.world.item.Items.BOOK))))
        ).getHolder();
        grill24.potionsplus.core.Items.registerBlockItemWithAutoModel(() -> SKILL_JOURNALS, registerItem);

        FISHING_LEADERBOARDS = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("fishing_leaderboards")
                .blockFactory(FishingLeaderboardsBlock::new)
                .properties(() -> BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(2.5F).sound(SoundType.WOOD).noOcclusion())
                .modelGenerator(HorizontalDirectionalBlockModelGenerator::new)
                .recipeGenerator(holder -> new RecipeGeneratorUtility.RecipeGenerator<>(holder,
                        (recipeProvider, h) ->
                                recipeProvider.shapeless(RecipeCategory.MISC, h.value())
                                        .requires(net.minecraft.world.item.Items.BOOK)
                                        .requires(ItemTags.FISHES)
                                        .group("fishing_leaderboards")
                                        .unlockedBy("has_book", recipeProvider.has(net.minecraft.world.item.Items.BOOK))))
        ).getHolder();
        grill24.potionsplus.core.Items.registerBlockItemWithAutoModel(() -> FISHING_LEADERBOARDS, registerItem);

        SMALL_FILTER_HOPPER = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("small_filter_hopper")
                        .blockFactory(SmallFilterHopperBlock::new)
                        .properties(() -> BlockBehaviour.Properties.ofFullCopy(Blocks.HOPPER))
                        .modelGenerator(null) // Hand-made custom model json
                        .recipeGenerator(holder -> new RecipeGeneratorUtility.RecipeGenerator<>(holder,
                                (recipeProvider, h) ->
                                        recipeProvider.shaped(RecipeCategory.REDSTONE, h.value())
                                                .define('H', Items.HOPPER)
                                                .define('C', Items.COMPARATOR)
                                                .define('D', Items.REDSTONE)
                                                .define('T', Items.REDSTONE_TORCH)
                                                .define('S', Items.STONE)
                                                .pattern("HCD")
                                                .pattern("HTS")
                                                .unlockedBy("has_hopper", recipeProvider.has(Items.HOPPER)))))
                .getHolder();
        RegistrationUtility.register(registerItem, SimpleItemBuilder.createSimple("small_filter_hopper")
                .itemFactory(prop -> new BlockItem(SMALL_FILTER_HOPPER.value(), prop.useBlockDescriptionPrefix()))
                .modelGenerator(h -> new ItemModelUtility.ItemFromModelFileGenerator<>(h, ppId("item/small_filter_hopper"))));

        LARGE_FILTER_HOPPER = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("large_filter_hopper")
                        .blockFactory(LargeFilterHopperBlock::new)
                        .properties(() -> BlockBehaviour.Properties.ofFullCopy(Blocks.HOPPER))
                        .modelGenerator(null) // Hand-made custom model json
                        .recipeGenerator(holder -> new RecipeGeneratorUtility.RecipeGenerator<>(holder,
                                (recipeProvider, h) ->
                                        recipeProvider.shaped(RecipeCategory.REDSTONE, h.value())
                                                .define('H', SMALL_FILTER_HOPPER.value())
                                                .define('D', Items.DIAMOND)
                                                .define('G', Items.GOLD_INGOT)
                                                .pattern("D")
                                                .pattern("H")
                                                .pattern("G")
                                                .unlockedBy("has_hopper", recipeProvider.has(SMALL_FILTER_HOPPER.value())))))
                .getHolder();
        RegistrationUtility.register(registerItem, SimpleItemBuilder.createSimple("large_filter_hopper")
                .itemFactory(prop -> new BlockItem(LARGE_FILTER_HOPPER.value(), prop.useBlockDescriptionPrefix()))
                .modelGenerator(h -> new ItemModelUtility.ItemFromModelFileGenerator<>(h, ppId("item/large_filter_hopper"))));

        HUGE_FILTER_HOPPER = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple("huge_filter_hopper")
                        .blockFactory(HugeFilterHopperBlock::new)
                        .properties(() -> BlockBehaviour.Properties.ofFullCopy(Blocks.HOPPER))
                        .modelGenerator(null) // Hand-made custom model json
                        .recipeGenerator(holder -> new RecipeGeneratorUtility.RecipeGenerator<>(holder,
                                (recipeProvider, h) ->
                                        recipeProvider.shaped(RecipeCategory.REDSTONE, h.value())
                                                .define('H', LARGE_FILTER_HOPPER.value())
                                                .define('U', OreItems.URANIUM_INGOT.value())
                                                .define('D', Items.DIAMOND)
                                                .pattern("U")
                                                .pattern("H")
                                                .pattern("D")
                                                .unlockedBy("has_hopper", recipeProvider.has(LARGE_FILTER_HOPPER.value())))))
                .getHolder();
        RegistrationUtility.register(registerItem, SimpleItemBuilder.createSimple("huge_filter_hopper")
                .itemFactory(prop -> new BlockItem(HUGE_FILTER_HOPPER.value(), prop.useBlockDescriptionPrefix()))
                .modelGenerator(h -> new ItemModelUtility.ItemFromModelFileGenerator<>(h, ppId("item/huge_filter_hopper"))));

        FISH_TANK_BLOCKS = new ArrayList<>();
        FISH_TANK_FRAME_BLOCKS = new HashMap<>();
        FISH_TANK_SAND_BLOCKS = new HashMap<>();

        FISH_TANK = registerFishTank("fish_tank", Component.translatable("tooltip.potionsplus.fish_tank"),
                ppId("block/invisible_fish_tank"), ppId("item/fish_tank"), ItemTags.PLANKS, registerBlock, registerItem);
        FISH_TANK_BLOCKS.add(FISH_TANK);

        registerFishTankMultiPartFrameSubBlock("fish_tank_frame", Tags.Blocks.FISH_TANK_FRAME, registerBlock, registerItem);
        registerFishTankMultiPartSandSubBlock("fish_tank_sand", Tags.Blocks.FISH_TANK_SAND, registerBlock, registerItem);
    }

    private static Map<Integer, Holder<Block>> registerFishTankMultiPartFrameSubBlock(String name, TagKey<Block> frameBlocks, BiFunction<String, Supplier<Block>, Holder<Block>> registerBlock, BiFunction<String, Supplier<Item>, Holder<Item>> registerItem) {
        return forAllFishTankPartModels(ppId("block/" + name), (model, id) -> {
            Holder<Block> blockHolderResult = registerFishTankFrameSubBlock(model.getPath(), model, frameBlocks, registerBlock, registerItem);
            FISH_TANK_FRAME_BLOCKS.put(id, blockHolderResult);
            return blockHolderResult;
        });
    }

    private static Map<Integer, Holder<Block>> registerFishTankMultiPartSandSubBlock(String name, TagKey<Block> frameBlocks, BiFunction<String, Supplier<Block>, Holder<Block>> registerBlock, BiFunction<String, Supplier<Item>, Holder<Item>> registerItem) {
        return forAllFishTankPartModels(ppId("block/" + name), (model, id) -> {
            Holder<Block> blockHolderResult = registerFishTankSandSubBlock(model.getPath(), model, frameBlocks, registerBlock, registerItem);
            FISH_TANK_SAND_BLOCKS.put(id, blockHolderResult);
            return blockHolderResult;
        });
    }

    private static Holder<Block> registerFishTankFrameSubBlock(String name, ResourceLocation baseModel, TagKey<Block> frameBlocks, BiFunction<String, Supplier<Block>, Holder<Block>> registerBlock, BiFunction<String, Supplier<Item>, Holder<Item>> registerItem) {
        Holder<Block> blockHolderResult = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple(name)
                .properties(() -> BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS).noOcclusion())
                .blockFactory(FishTankFrameBlock::new)
                .modelGenerator(p -> new BlockModelUtility.FromModelFileBlockStateGenerator<>(p, baseModel))
                .renderType(BlockBuilder.RenderType.TRANSLUCENT)
                .runtimeModelGenerator(holder -> new RuntimeTextureVariantModelGenerator(holder, baseModel,
                        RuntimeTextureVariantModelGenerator.PropertyTexVariant.fromTag(FishTankFrameBlock.FRAME_VARIANT, frameBlocks, "frame")
                ))
        ).getHolder();
        grill24.potionsplus.core.Items.registerBlockItem(blockHolderResult, registerItem);

        return blockHolderResult;
    }

    private static Holder<Block> registerFishTankSandSubBlock(String name, ResourceLocation baseModel, TagKey<Block> frameBlocks, BiFunction<String, Supplier<Block>, Holder<Block>> registerBlock, BiFunction<String, Supplier<Item>, Holder<Item>> registerItem) {
        Holder<Block> blockHolderResult = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple(name)
                .properties(() -> BlockBehaviour.Properties.ofFullCopy(Blocks.SAND).noOcclusion())
                .blockFactory(FishTankSandBlock::new)
                .modelGenerator(p -> new BlockModelUtility.FromModelFileBlockStateGenerator<>(p, baseModel))
                .renderType(BlockBuilder.RenderType.TRANSLUCENT)
                .runtimeModelGenerator(holder -> new RuntimeTextureVariantModelGenerator(holder, baseModel,
                        RuntimeTextureVariantModelGenerator.PropertyTexVariant.fromTag(FishTankSandBlock.SAND_VARIANT, frameBlocks, "sand")
                ))
        ).getHolder();
        grill24.potionsplus.core.Items.registerBlockItem(blockHolderResult, registerItem);

        return blockHolderResult;
    }

    private static Holder<Block> registerFishTank(String name, Component tooltip, ResourceLocation baseModel, ResourceLocation itemModel, TagKey<Item> recipeItem, BiFunction<String, Supplier<Block>, Holder<Block>> registerBlock, BiFunction<String, Supplier<Item>, Holder<Item>> registerItem) {
        Holder<Block> blockHolderResult = RegistrationUtility.register(registerBlock, SimpleBlockBuilder.createSimple(name)
                .properties(() -> BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS).noOcclusion())
                .blockFactory(p -> new FishTankBlock(p, tooltip))
                .modelGenerator(p -> new BlockModelUtility.FromModelFileBlockStateGenerator<>(p, baseModel, true, false))
                .recipeGenerator(holder -> new RecipeGeneratorUtility.RecipeGenerator<>(holder,
                        (recipeProvider, h) ->
                                recipeProvider.shaped(RecipeCategory.BUILDING_BLOCKS, h.value())
                                        .pattern("RGR")
                                        .pattern("GBG")
                                        .pattern("RGR")
                                        .define('G', Blocks.GLASS)
                                        .define('R', recipeItem)
                                        .define('B', Items.WATER_BUCKET)
                                        .unlockedBy("was_water_bucket", recipeProvider.has(Items.WATER_BUCKET))))
                .renderType(BlockBuilder.RenderType.TRANSLUCENT)
        ).getHolder();
        grill24.potionsplus.core.Items.registerBlockItemWithParentModel(() -> blockHolderResult, registerItem, itemModel);

        return blockHolderResult;
    }

    public static Block[] toArray(List<Holder<Block>> blocks) {
        return blocks.stream().map(Holder::value).toArray(Block[]::new);
    }

    public static ResourceLocation getFishTankPartModel(ResourceLocation name, Map<Direction, Boolean> faces) {
        int modelIndex = getFishTankPartId(faces);
        return ResourceLocation.fromNamespaceAndPath(name.getNamespace(), name.getPath() + "_" + Integer.toBinaryString(modelIndex));
    }

    private static Map<Integer, Holder<Block>> forAllFishTankPartModels(ResourceLocation name, BiFunction<ResourceLocation, Integer, Holder<Block>> registerer) {
        HashMap<Integer, Holder<Block>> result = new HashMap<>();
        for (int i = 0; i < 64; i++) {
            Map<Direction, Boolean> faces = Map.of(
                    Direction.UP, (i & 0b000001) != 0,
                    Direction.DOWN, (i & 0b000010) != 0,
                    Direction.NORTH, (i & 0b000100) != 0,
                    Direction.SOUTH, (i & 0b001000) != 0,
                    Direction.EAST, (i & 0b010000) != 0,
                    Direction.WEST, (i & 0b100000) != 0
            );

            result.put(i, registerer.apply(getFishTankPartModel(name, faces), getFishTankPartId(faces)));
        }

        return result;
    }

    public static int getFishTankPartId(Map<Direction, Boolean> faces) {
        int modelIndex = 0;
        for (Map.Entry<Direction, Boolean> entry : faces.entrySet()) {
            if (entry.getValue()) {
                modelIndex |= (1 << entry.getKey().ordinal());
            }
        }
        return modelIndex;
    }
}
