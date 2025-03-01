package grill24.potionsplus.data;

import grill24.potionsplus.block.*;
import grill24.potionsplus.core.Blocks;
import grill24.potionsplus.core.Items;
import grill24.potionsplus.item.IItemModelGenerator;
import grill24.potionsplus.item.ItemOverrideUtility;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.VariantBlockStateBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;
import oshi.util.tuples.Pair;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static grill24.potionsplus.utility.Utility.mc;
import static grill24.potionsplus.utility.Utility.ppId;
import static net.minecraft.data.models.model.ModelLocationUtils.getModelLocation;

public class BlockStateProvider extends net.neoforged.neoforge.client.model.generators.BlockStateProvider {
    public BlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, ModInfo.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        // ----- Blocks -----

        // Particle Emitter
        registerParticleEmitter();

        // Herbalist's Lectern
        registerBlockWithModel(Blocks.HERBALISTS_LECTERN.value());
        registerHorizontalDirectionalBlock(Blocks.ABYSSAL_TROVE.value());
        registerHorizontalDirectionalBlock(Blocks.SANGUINE_ALTAR.value());

        // Potion Beacon
        registerBlockWithModel(Blocks.POTION_BEACON.value());

        // Skill Journals
        registerHorizontalDirectionalBlock(Blocks.SKILL_JOURNALS.value());
        registerItemFromParent(Blocks.SKILL_JOURNALS.value().asItem(), ResourceLocation.fromNamespaceAndPath(ModInfo.MOD_ID, "block/skill_journals"));

        registerClothesline();
        registerFlowerBlock(Blocks.IRON_OXIDE_DAISY.value());
        registerFlowerBlock(Blocks.COPPER_CHRYSANTHEMUM.value());
        registerFlowerBlock(Blocks.LAPIS_LILAC.value());
        registerFlowerBlock(Blocks.DIAMOUR.value());
        registerFlowerBlock(Blocks.GOLDEN_CUBENSIS.value());
        registerFlowerBlock(Blocks.REDSTONE_ROSE.value());
        registerFlowerBlock(Blocks.BLACK_COALLA_LILY.value());

        registerCubeAll(Blocks.DENSE_DIAMOND_ORE.value());
        registerCubeAll(Blocks.DEEPSLATE_DENSE_DIAMOND_ORE.value());
        registerCubeAll(Blocks.COOBLESTONE.value());
        registerCubeAll(Blocks.UNSTABLE_BLOCK.value());
        registerCubeAll(Blocks.UNSTABLE_DEEPSLATE.value(), mc("block/cobbled_deepslate"));
        registerCubeAll(Blocks.UNSTABLE_MOLTEN_DEEPSLATE.value());
        registerCubeAll(Blocks.UNSTABLE_BLACKSTONE.value(), mc("block/blackstone"));
        registerCubeAll(Blocks.UNSTABLE_MOLTEN_BLACKSTONE.value());
        registerFaceAttachedHorizontalDirectionalBlock(Blocks.LAVA_GEYSER.value());
        registerItemFromParent(Blocks.LAVA_GEYSER.value().asItem(), block(BuiltInRegistries.BLOCK.getKey(Blocks.LAVA_GEYSER.value())));
        registerItem(Blocks.DECORATIVE_FIRE.value().asItem(), mcLoc("block/fire_0"));

        registerCubeAll(Blocks.SANDY_COPPER_ORE.value());
        registerCubeAll(Blocks.SANDY_IRON_ORE.value());
        registerCubeAll(Blocks.SANDY_GOLD_ORE.value());
        registerCubeAll(Blocks.SANDY_DIAMOND_ORE.value());
        registerCubeAll(Blocks.SANDY_REDSTONE_ORE.value());
        registerCubeAll(Blocks.SANDY_LAPIS_ORE.value());
        registerCubeAll(Blocks.SANDY_COAL_ORE.value());
        registerCubeAll(Blocks.SANDY_EMERALD_ORE.value());

        registerCubeAll(Blocks.MOSSY_COPPER_ORE.value());
        registerCubeAll(Blocks.MOSSY_IRON_ORE.value());
        registerCubeAll(Blocks.MOSSY_GOLD_ORE.value());
        registerCubeAll(Blocks.MOSSY_DIAMOND_ORE.value());
        registerCubeAll(Blocks.MOSSY_REDSTONE_ORE.value());
        registerCubeAll(Blocks.MOSSY_LAPIS_ORE.value());
        registerCubeAll(Blocks.MOSSY_COAL_ORE.value());
        registerCubeAll(Blocks.MOSSY_EMERALD_ORE.value());

        registerCubeAll(Blocks.DEEPSLATE_REMNANT_DEBRIS.value());
        registerCubeAll(Blocks.REMNANT_DEBRIS.value());

        registerUraniumOre(Blocks.URANIUM_ORE.value());
        registerUraniumOre(Blocks.DEEPSLATE_URANIUM_ORE.value());
        registerUraniumOre(Blocks.SANDY_URANIUM_ORE.value());
        registerUraniumOre(Blocks.MOSSY_URANIUM_ORE.value());
        registerCubeAll(Blocks.URANIUM_BLOCK.value());
        registerCubeAll(Blocks.URANIUM_GLASS.value());

        registerCubeAll(Blocks.SULFURIC_NETHER_QUARTZ_ORE.value());

        // ----- Items -----

        for (IItemModelGenerator modelGenerator : Items.ITEM_MODEL_GENERATORS) {
            modelGenerator.generate(this);
        }

        registerItem(Items.MOSS.value());
        registerItem(Items.SALT.value());
        registerItem(Items.WORMROOT.value());
        registerItem(Items.ROTTEN_WORMROOT.value());
        registerItem(Items.LUNAR_BERRIES.value());

        registerItem(Blocks.IRON_OXIDE_DAISY.value().asItem(), "block/iron_oxide_daisy");
        registerItem(Blocks.COPPER_CHRYSANTHEMUM.value().asItem(), "block/copper_chrysanthemum");
        registerItem(Blocks.LAPIS_LILAC.value().asItem(), "block/lapis_lilac");
        registerItem(Blocks.DIAMOUR.value().asItem(), "block/diamour");
        registerItem(Blocks.GOLDEN_CUBENSIS.value().asItem(), "block/golden_cubensis");
        registerItem(Blocks.REDSTONE_ROSE.value().asItem(), "block/redstone_rose");
        registerItem(Blocks.BLACK_COALLA_LILY.value().asItem(), "block/black_coalla_lily");

        BiConsumer<String, ResourceLocation> crossModelGenerator = (modelName, resource) ->
                models().withExistingParent(modelName, mcLoc("block/cross")).texture("cross", resource);
        Function<Direction, Pair<Integer, Integer>> hangingPlantTextureOrientation = (facing) -> {
            int xRotOffset = switch (facing) {
                case UP -> 180;
                case DOWN -> 0;
                default -> 90;
            };
            int yRotOffset = switch (facing) {
                case NORTH -> 180;
                case EAST -> 270;
                case SOUTH -> 0;
                case WEST -> 90;
                default -> 0;
            };
            return new Pair<>(xRotOffset, yRotOffset);
        };
        registerBloomingPlantBlock(Blocks.HANGING_FERN,
                new ResourceLocation[][]{
                        new ResourceLocation[]{ppId("block/hanging_fern_upper"), ppId("block/hanging_fern_middle"), ppId("block/hanging_fern_lower")},
                        new ResourceLocation[]{ppId("block/hanging_fern_upper_blooming"), ppId("block/hanging_fern_middle_blooming"), ppId("block/hanging_fern_lower_blooming")},
                },
                hangingPlantTextureOrientation,
                ppId("block/hanging_fern_upper_blooming"),
                crossModelGenerator);
        registerVersatilePlantBlock(Blocks.COWLICK_VINE,
                new ResourceLocation[]{ppId("block/cowlick_vine_0"), ppId("block/cowlick_vine_1"), ppId("block/cowlick_vine_2"), ppId("block/cowlick_vine_3"), ppId("block/cowlick_vine_tail")},
                hangingPlantTextureOrientation,
                ppId("block/cowlick_vine_tail"),
                crossModelGenerator);
        registerBloomingPlantBlock(Blocks.DROOPY_VINE,
                new ResourceLocation[][]{
                        new ResourceLocation[]{ppId("block/droopy_vine_0"), ppId("block/droopy_vine_1"), ppId("block/droopy_vine_2")},
                        new ResourceLocation[]{ppId("block/droopy_vine_blooming_0"), ppId("block/droopy_vine_blooming_1"), ppId("block/droopy_vine_blooming_2")},
                },
                hangingPlantTextureOrientation,
                ppId("block/droopy_vine_blooming_2"),
                crossModelGenerator);
        registerBloomingPlantBlock(Blocks.SURVIVOR_STICK,
                new ResourceLocation[][]{
                        new ResourceLocation[]{ppId("block/survivor_stick_0"), ppId("block/survivor_stick_1")},
                        new ResourceLocation[]{ppId("block/survivor_stick_blooming_0"), ppId("block/survivor_stick_blooming_1")},
                },
                hangingPlantTextureOrientation,
                ppId("block/survivor_stick_blooming_1"),
                crossModelGenerator);

        // Pair<Parent Model, Pair<Texture Location Vine, Texture Location >>
        BiConsumer<String, Pair<ResourceLocation, Pair<ResourceLocation, ResourceLocation>>> lumoseedModelGenerator = (modelName, resources) ->
                models().withExistingParent(modelName, resources.getA()).texture("vine", resources.getB().getA()).texture("sack", resources.getB().getB());
        this.<Pair<ResourceLocation, Pair<ResourceLocation, ResourceLocation>>>registerBloomingPlantBlock(Blocks.LUMOSEED_SACKS,
                new Pair[][] {
                        new Pair[]{new Pair<>(ppId("block/lumoseed_sack"), new Pair<>(mc("block/moss_block"), mc("block/moss_block"))), new Pair(ppId("block/lumoseed_sack_tail"), new Pair<>(mc("block/moss_block"), mc("block/moss_block")))},
                        new Pair[]{new Pair<>(ppId("block/lumoseed_sack"), new Pair<>(mc("block/moss_block"), mc("block/glowstone"))), new Pair(ppId("block/lumoseed_sack_tail"), new Pair<>(mc("block/moss_block"), mc("block/glowstone")))}
                },
                hangingPlantTextureOrientation,
                mc("item/coal"),
                lumoseedModelGenerator);

        Function<Direction, Pair<Integer, Integer>> uprightPlantTextureOrientation = (facing) -> {
            int xRotOffset = switch (facing) {
                case UP -> 0;
                case DOWN -> 180;
                default -> 90;
            };
            int yRotOffset = switch (facing) {
                case NORTH -> 0;
                case EAST -> 90;
                case SOUTH -> 180;
                case WEST -> 270;
                default -> 0;
            };
            return new Pair<>(xRotOffset, yRotOffset);
        };
        registerVersatilePlantBlock(Blocks.DANDELION_VERSATILE,
                new ResourceLocation[] {mc("block/dandelion")},
                uprightPlantTextureOrientation,
                mc("block/dandelion"),
                crossModelGenerator);
        registerVersatilePlantBlock(Blocks.TORCHFLOWER_VERSATILE,
                new ResourceLocation[] {mc("block/torchflower")},
                uprightPlantTextureOrientation,
                mc("block/torchflower"),
                crossModelGenerator);
        registerVersatilePlantBlock(Blocks.POPPY_VERSATILE,
                new ResourceLocation[] {mc("block/poppy")},
                uprightPlantTextureOrientation,
                mc("block/poppy"),
                crossModelGenerator);
        registerVersatilePlantBlock(Blocks.BLUE_ORCHID_VERSATILE,
                new ResourceLocation[] {mc("block/blue_orchid")},
                uprightPlantTextureOrientation,
                mc("block/blue_orchid"),
                crossModelGenerator);
        registerVersatilePlantBlock(Blocks.ALLIUM_VERSATILE,
                new ResourceLocation[] {mc("block/allium")},
                uprightPlantTextureOrientation,
                mc("block/allium"),
                crossModelGenerator);
        registerVersatilePlantBlock(Blocks.AZURE_BLUET_VERSATILE,
                new ResourceLocation[] {mc("block/azure_bluet")},
                uprightPlantTextureOrientation,
                mc("block/azure_bluet"),
                crossModelGenerator);
        registerVersatilePlantBlock(Blocks.RED_TULIP_VERSATILE,
                new ResourceLocation[] {mc("block/red_tulip")},
                uprightPlantTextureOrientation,
                mc("block/red_tulip"),
                crossModelGenerator);
        registerVersatilePlantBlock(Blocks.ORANGE_TULIP_VERSATILE,
                new ResourceLocation[] {mc("block/orange_tulip")},
                uprightPlantTextureOrientation,
                mc("block/orange_tulip"),
                crossModelGenerator);
        registerVersatilePlantBlock(Blocks.WHITE_TULIP_VERSATILE,
                new ResourceLocation[] {mc("block/white_tulip")},
                uprightPlantTextureOrientation,
                mc("block/white_tulip"),
                crossModelGenerator);
        registerVersatilePlantBlock(Blocks.PINK_TULIP_VERSATILE,
                new ResourceLocation[] {mc("block/pink_tulip")},
                uprightPlantTextureOrientation,
                mc("block/pink_tulip"),
                crossModelGenerator);
        registerVersatilePlantBlock(Blocks.OXEYE_DAISY_VERSATILE,
                new ResourceLocation[] {mc("block/oxeye_daisy")},
                uprightPlantTextureOrientation,
                mc("block/oxeye_daisy"),
                crossModelGenerator);
        registerVersatilePlantBlock(Blocks.CORNFLOWER_VERSATILE,
                new ResourceLocation[] {mc("block/cornflower")},
                uprightPlantTextureOrientation,
                mc("block/cornflower"),
                crossModelGenerator);
        registerVersatilePlantBlock(Blocks.WITHER_ROSE_VERSATILE,
                new ResourceLocation[] {mc("block/wither_rose")},
                uprightPlantTextureOrientation,
                mc("block/wither_rose"),
                crossModelGenerator);
        registerVersatilePlantBlock(Blocks.LILY_OF_THE_VALLEY_VERSATILE,
                new ResourceLocation[] {mc("block/lily_of_the_valley")},
                uprightPlantTextureOrientation,
                mc("block/lily_of_the_valley"),
                crossModelGenerator);
        registerVersatilePlantBlock(Blocks.BROWN_MUSHROOM_VERSATILE,
                new ResourceLocation[] {mc("block/brown_mushroom")},
                uprightPlantTextureOrientation,
                mc("block/brown_mushroom"),
                crossModelGenerator);
        registerVersatilePlantBlock(Blocks.RED_MUSHROOM_VERSATILE,
                new ResourceLocation[] {mc("block/red_mushroom")},
                uprightPlantTextureOrientation,
                mc("block/red_mushroom"),
                crossModelGenerator);

        BiConsumer<String, ResourceLocation> parentedModelGenerator = (modelName, parentModel) ->
                models().withExistingParent(modelName, parentModel);
        registerVersatilePlantBlock(Blocks.SUNFLOWER_VERSATILE,
                new ResourceLocation[] {mc("block/sunflower_bottom"), mc("block/sunflower_top")},
                uprightPlantTextureOrientation,
                mc("block/sunflower_front"),
                parentedModelGenerator);
        registerVersatilePlantBlock(Blocks.LILAC_VERSATILE,
                new ResourceLocation[] {mc("block/lilac_bottom"), mc("block/lilac_top")},
                uprightPlantTextureOrientation,
                mc("block/lilac_top"),
                parentedModelGenerator);
        registerVersatilePlantBlock(Blocks.ROSE_BUSH_VERSATILE,
                new ResourceLocation[] {mc("block/rose_bush_bottom"), mc("block/rose_bush_top")},
                uprightPlantTextureOrientation,
                mc("block/rose_bush_top"),
                parentedModelGenerator);
        registerVersatilePlantBlock(Blocks.PEONY_VERSATILE,
                new ResourceLocation[] {mc("block/peony_bottom"), mc("block/peony_top")},
                uprightPlantTextureOrientation,
                mc("block/peony_top"),
                parentedModelGenerator);

        registerVersatilePlantBlock(Blocks.TALL_GRASS_VERSATILE,
                new ResourceLocation[] {mc("block/tall_grass_bottom"), mc("block/tall_grass_top")},
                uprightPlantTextureOrientation,
                mc("block/tall_grass_top"),
                parentedModelGenerator);
        registerVersatilePlantBlock(Blocks.LARGE_FERN_VERSATILE,
                new ResourceLocation[] {mc("block/large_fern_bottom"), mc("block/large_fern_top")},
                uprightPlantTextureOrientation,
                mc("block/large_fern_top"),
                parentedModelGenerator);
        registerVersatilePlantBlock(Blocks.PITCHER_PLANT_VERSATILE,
                new ResourceLocation[] {mc("block/pitcher_plant_bottom"), mc("block/pitcher_plant_top")},
                uprightPlantTextureOrientation,
                mc("item/pitcher_plant"),
                parentedModelGenerator);



        registerItem(Items.WREATH.value());

        registerItem(Items.NETHERITE_REMNANT.value());
        registerItem(Items.RAW_URANIUM.value());
        registerItem(Items.URANIUM_INGOT.value());
        registerItem(Items.SULFUR_SHARD.value());
        registerItem(Items.SULFURIC_ACID.value());
        registerItemFromParentWithTextureOverride(Items.COPPER_FISHING_ROD.value(), mc("item/handheld_rod"), ppId("item/copper_fishing_rod"));

        Holder<Item>[][] blockHatItems = new Holder[][]{
                Items.COAL_ORE_HATS,
                Items.COPPER_ORE_HATS,
                Items.IRON_ORE_HATS,
                Items.GOLD_ORE_HATS,
                Items.DIAMOND_ORE_HATS,
                Items.EMERALD_ORE_HATS
        };
        ResourceLocation[] blockHatTextures = new ResourceLocation[] { mc("block/coal_ore"), mc("block/copper_ore"), mc("block/iron_ore"), mc("block/gold_ore"), mc("block/diamond_ore"), mc("block/emerald_ore") };
        registerAllBlockHatVariantsForItem(Items.BLOCK_HAT_MODELS, blockHatTextures, blockHatItems);
    }

    private void registerAllBlockHatVariantsForItem(ResourceLocation[] parentModels, ResourceLocation[] textures, Holder<Item>[][] blockHatItems) {
        for (int t = 0; t < textures.length; t++) {
            ResourceLocation texture = textures[t];
            for (int m = 0; m < parentModels.length; m++) {
                Holder<Item> itemForTexture = blockHatItems[t][m];
                ResourceLocation resourceLocation = parentModels[m];
                registerBlockHatItem(itemForTexture, resourceLocation, texture);
            }
        }
    }

    private void registerBlockHatItem(Holder<Item> item, ResourceLocation parentModel, ResourceLocation texture) {
        ResourceLocation modelLocation = getModelLocation(item.value());
        itemModels().getBuilder(modelLocation.getPath())
                .parent(models().getExistingFile(parentModel))
                .texture("0", texture);
    }

    private void registerCubeAll(Block block) {
        ResourceLocation modelLocation = getModelLocation(block);
        models().cubeAll(modelLocation.getPath(), modelLocation);
        getVariantBuilder(block).forAllStates(state -> ConfiguredModel.builder()
                .modelFile(models().getExistingFile(modelLocation))
                .build());
        simpleBlockItem(block, models().getExistingFile(modelLocation));
    }

    private void registerCubeAll(Block block, ResourceLocation texture) {
        ResourceLocation modelLocation = getModelLocation(block);
        models().cubeAll(Objects.requireNonNull(modelLocation).getPath(), texture);
        getVariantBuilder(block).forAllStates(state -> ConfiguredModel.builder()
                .modelFile(models().getExistingFile(modelLocation))
                .build());
        simpleBlockItem(block, models().getExistingFile(modelLocation));
    }

    private void registerItem(Item item) {
        ResourceLocation modelLocation = getModelLocation(item);
        itemModels().getBuilder(modelLocation.getPath())
                .parent(models().getExistingFile(mcLoc("item/generated")))
                .texture("layer0", modelLocation);
    }

    private void registerItem(Item item, String texture) {
        ResourceLocation resourceLocation = BuiltInRegistries.ITEM.getKey(item);
        itemModels().getBuilder(resourceLocation.getPath())
                .parent(models().getExistingFile(mcLoc("item/generated")))
                .texture("layer0", texture);
    }

    private void registerItem(Item item, ResourceLocation texture) {
        ResourceLocation modelLocation = getModelLocation(item);
        itemModels().getBuilder(modelLocation.getPath())
                .parent(models().getExistingFile(mcLoc("item/generated")))
                .texture("layer0", texture);
    }

    private void registerItem(Item item, ResourceLocation textureLayer0, ResourceLocation textureLayer1) {
        ResourceLocation modelLocation = getModelLocation(item);
        itemModels().getBuilder(modelLocation.getPath())
                .parent(models().getExistingFile(mcLoc("item/generated")))
                .texture("layer0", textureLayer0)
                .texture("layer1", textureLayer1);
    }

    private void registerItemFromParent(Item item, ResourceLocation parent) {
        ResourceLocation modelLocation = getModelLocation(item);
        itemModels().getBuilder(modelLocation.getPath())
                .parent(models().getExistingFile(parent));
    }

    private void registerItemFromParentWithTextureOverride(Item item, ResourceLocation parent, ResourceLocation textureOverride) {
        ResourceLocation modelLocation = getModelLocation(item);
        itemModels().getBuilder(modelLocation.getPath())
                .parent(models().getExistingFile(parent))
                .texture("layer0", textureOverride);
    }

    private void registerParticleEmitter() {
        for (int i = 0; i < ParticleEmitterBlock.PARTICLE_EMITTER_CONFIGURATIONS.length; i++) {
            Block blockForModel = ParticleEmitterBlock.PARTICLE_EMITTER_CONFIGURATIONS[i].get().blockModel;
            blockForModel = blockForModel == null ? net.minecraft.world.level.block.Blocks.IRON_BLOCK : blockForModel;

            getVariantBuilder(Blocks.PARTICLE_EMITTER.value())
                    .partialState().with(ParticleEmitterBlock.PARTICLE_TYPE, i).modelForState()
                    .modelFile(models().getExistingFile(mcBlock(blockForModel)))
                    .addModel();
        }

        simpleBlockItem(Blocks.PARTICLE_EMITTER.value(), models().getExistingFile(mcLoc("block/iron_block")));
    }

    private void registerUraniumOre(Block block) {
        VariantBlockStateBuilder builder = getVariantBuilder(block);
        for (UraniumOreBlock.UraniumState state : UraniumOreBlock.UraniumState.values()) {
            ResourceLocation textureLocation = ppId("block/" + BuiltInRegistries.BLOCK.getKey(block).getPath() + "_"+ state.getSerializedName());
            ResourceLocation model = ppId(Objects.requireNonNull(BuiltInRegistries.BLOCK.getKey(block)).getPath() + "_"+ state.getSerializedName());
            models().cubeAll(model.getPath(), textureLocation);

            builder.partialState().with(UraniumOreBlock.URANIUM_STATE, state).modelForState()
                    .modelFile(models().getExistingFile(model))
                    .addModel();

            simpleBlockItem(block, models().getExistingFile(model));
        }
    }

    private void registerClothesline() {
        getVariantBuilder(Blocks.CLOTHESLINE.value())
                .partialState().with(ClotheslineBlock.PART, ClotheslinePart.LEFT).modelForState()
                .modelFile(models().getExistingFile(mcLoc("oak_fence_post")))
                .addModel()
                .partialState().with(ClotheslineBlock.PART, ClotheslinePart.RIGHT).modelForState()
                .modelFile(models().getExistingFile(mcLoc("oak_fence_post")))
                .addModel();

        itemModels().getBuilder(Objects.requireNonNull(BuiltInRegistries.BLOCK.getKey(Blocks.CLOTHESLINE.value())).getPath())
                .parent(models().getExistingFile(ppId("block/clothesline_inventory")));
    }

    private void registerFaceAttachedHorizontalDirectionalBlock(Block block) {
        getVariantBuilder(block).forAllStates(state -> {
            int yRot = switch (state.getValue(net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock.FACING)) {
                case NORTH -> 0;
                case EAST -> 90;
                case SOUTH -> 180;
                case WEST -> 270;
                default -> 0;
            };
            int xRot = switch (state.getValue(net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock.FACE)) {
                case FLOOR -> 0;
                case WALL -> 90;
                case CEILING -> 180;
                default -> 0;
            };
            return ConfiguredModel.builder()
                    .modelFile(models().getExistingFile(BuiltInRegistries.BLOCK.getKey(block)))
                    .rotationY(yRot)
                    .rotationX(xRot)
                    .uvLock(true)
                    .build();
        });
    }

    private void registerHorizontalDirectionalBlock(Block block) {
        registerHorizontalDirectionalBlock(block, BuiltInRegistries.BLOCK.getKey(block));
    }

    private void registerHorizontalDirectionalBlock(Block block, ResourceLocation model) {
        getVariantBuilder(block).forAllStates(state -> {
            int yRot = switch (state.getValue(net.minecraft.world.level.block.HorizontalDirectionalBlock.FACING)) {
                case NORTH -> 180;
                case EAST -> 270;
                case SOUTH -> 0;
                case WEST -> 90;
                default -> 0;
            };
            return ConfiguredModel.builder()
                    .modelFile(models().getExistingFile(model))
                    .rotationY(yRot)
                    .build();
        });
    }

    private void registerFlowerBlock(Block block, String texture) {
        models().withExistingParent(Objects.requireNonNull(BuiltInRegistries.BLOCK.getKey(block)).getPath(), mcLoc("block/cross"))
                .texture("cross", texture);

        getVariantBuilder(block).forAllStates(state -> ConfiguredModel.builder()
                .modelFile(models().getExistingFile(BuiltInRegistries.BLOCK.getKey(block)))
                .build());
    }

    private void registerFlowerBlock(Block block) {
        registerFlowerBlock(block, "block/" + Objects.requireNonNull(BuiltInRegistries.BLOCK.getKey(block)).getPath());
    }

    private <T> void registerVersatilePlantBlock(DeferredHolder<Block, VersatilePlantBlock> holder, T[] resources, Function<Direction, Pair<Integer, Integer>> texRotationFunction, ResourceLocation itemTexture, BiConsumer<String, T> modelRegisterer) {
        VersatilePlantBlock block = holder.value();

        String name = holder.getKey().location().getPath();

        // This loop generates a model for every texture used in the pattern
        Set<String> usedModels = new HashSet<>();
        for (Integer textureIndex : block.getUsedTextures()) {
            String modelName = getVersatilePlantModelName(name, textureIndex);

            // Ensure we don't duplicate textures, since textures can be repeated in the pattern. Only need one model per texture.
            if (!usedModels.contains(modelName)) {
                usedModels.add(modelName);

                T resource = resources[textureIndex];
                modelRegisterer.accept(modelName, resource);
            }
        }

        getVariantBuilder(block).forAllStatesExcept(state -> {
            Direction facing = state.getValue(VersatilePlantBlock.FACING);
            int textureIndex = state.getValue(VersatilePlantBlock.TEXTURE_INDEX);
            String modelName = getVersatilePlantModelName(name, textureIndex);

            if (usedModels.contains(modelName)) {
                Pair<Integer, Integer> texRot = texRotationFunction.apply(facing);
                int xRotOffset = texRot.getA();
                int yRotOffset = texRot.getB();

                return ConfiguredModel.builder()
                        .rotationX(xRotOffset)
                        .rotationY(yRotOffset)
                        .modelFile(models().getExistingFile(ppId(modelName)))
                        .build();
            }
            return ConfiguredModel.builder().modelFile(models().getExistingFile(mc("block/air"))).build();
        }, VersatilePlantBlock.SEGMENT);

        registerItem(block.asItem(), itemTexture);
    }

    private String getVersatilePlantModelName(String name, int textureIndex) {
        if (textureIndex >= 0) {
            return name + "_t" + textureIndex;
        }

        return name + "_t0";
    }

    private String getBloomingPlantModelName(String name, int blooming, int textureIndex) {
        if (blooming >= 0 && textureIndex >= 0) {
            return name + "_b" + blooming + "_t" + textureIndex;
        }

        return name + "_b0_t0";
    }

    /***
     *
     * @param holder
     * @param textures Textures like this: ({"block/hanging_fern_base", "block/hanging_fern_tip"}, {"block/hanging_fern_base_blooming", "block/hanging_fern_tip_blooming"})
     * @param texRotationFunction
     * @param itemTexture
     */
    private <T> void registerBloomingPlantBlock(DeferredHolder<Block, BloomingPlantBlock> holder, T[][] textures, Function<Direction, Pair<Integer, Integer>> texRotationFunction, ResourceLocation itemTexture, BiConsumer<String, T> modelGenerator) {
        BloomingPlantBlock block = holder.value();

        String name = holder.getKey().location().getPath();

        // This loop generates a model for every texture used in the pattern
        Set<String> usedModels = new HashSet<>();
        for (int blooming = 0; blooming <= block.getMaxBlooming(); blooming++) {
            for (Integer textureIndex : block.getUsedTextures()) {
                String modelName = getBloomingPlantModelName(name, blooming, textureIndex);

                // Ensure we don't duplicate textures, since textures can be repeated in the pattern. Only need one model per texture.
                if (!usedModels.contains(modelName)) {
                    usedModels.add(modelName);

                    T tex = textures[blooming][textureIndex];
                    modelGenerator.accept(modelName, tex);
                }
            }
        }

        getVariantBuilder(block).forAllStatesExcept(state -> {
            int blooming = state.getValue(BloomingPlantBlock.BLOOMING);
            if (blooming <= block.getMaxBlooming()) {
                Direction facing = state.getValue(VersatilePlantBlock.FACING);
                int textureIndex = state.getValue(VersatilePlantBlock.TEXTURE_INDEX);
                String modelName = getBloomingPlantModelName(name, blooming, textureIndex);

                if (usedModels.contains(modelName)) {
                    Pair<Integer, Integer> texRot = texRotationFunction.apply(facing);
                    int xRotOffset = texRot.getA();
                    int yRotOffset = texRot.getB();

                    return ConfiguredModel.builder()
                            .rotationX(xRotOffset)
                            .rotationY(yRotOffset)
                            .modelFile(models().getExistingFile(ppId(modelName)))
                            .build();
                }
            }

            return ConfiguredModel.builder().modelFile(models().getExistingFile(mc("block/air"))).build();
        },  VersatilePlantBlock.SEGMENT);

        registerItem(block.asItem(), itemTexture);
    }

    private void registerBlockWithModel(Block block, ResourceLocation resourceLocation) {
        getVariantBuilder(block).forAllStates(state -> ConfiguredModel.builder()
                .modelFile(models().getExistingFile(resourceLocation))
                .build());
    }

    private void registerBlockWithModel(Block block) {
        registerBlockWithModel(block, BuiltInRegistries.BLOCK.getKey(block));
    }

    private void fromParent(Block block, Block parent, boolean doBlockModels, boolean doItemModels, boolean doBlockStates) {
        if (doBlockModels) {
            models().withExistingParent(Objects.requireNonNull(BuiltInRegistries.BLOCK.getKey(block)).getPath(), mcLoc(Objects.requireNonNull(BuiltInRegistries.BLOCK.getKey(parent)).getPath()));
        }
        if (doItemModels) {
            itemModels().withExistingParent(Objects.requireNonNull(BuiltInRegistries.BLOCK.getKey(block)).getPath(), mcLoc(Objects.requireNonNull(BuiltInRegistries.BLOCK.getKey(parent)).getPath()));
        }
        if (doBlockStates) {
            getVariantBuilder(block).forAllStates(state -> {
                return ConfiguredModel.builder()
                        .modelFile(models().getExistingFile(mcLoc(Objects.requireNonNull(BuiltInRegistries.BLOCK.getKey(parent)).getPath())))
                        .build();
            });
        }
    }

    private void fromParent(Block block, Block parent) {
        fromParent(block, parent, true, true, true);
    }

    private ResourceLocation mcBlock(Block block) {
        return mcLoc(Objects.requireNonNull(BuiltInRegistries.BLOCK.getKey(block)).getPath());
    }

    private ResourceLocation block(ResourceLocation resourceLocation) {
        return ppId("block/" + resourceLocation.getPath());
    }

    private ResourceLocation item(ResourceLocation resourceLocation) {
        return ppId("item/" + resourceLocation.getPath());
    }
}
