package grill24.potionsplus.data;

import grill24.potionsplus.block.*;
import grill24.potionsplus.core.Blocks;
import grill24.potionsplus.core.Items;
import grill24.potionsplus.utility.ModInfo;
import grill24.potionsplus.utility.PUtil;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.VariantBlockStateBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;
import oshi.util.tuples.Pair;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

import static grill24.potionsplus.utility.Utility.mc;
import static grill24.potionsplus.utility.Utility.ppId;
import static net.minecraft.data.models.model.ModelLocationUtils.getModelLocation;
import static grill24.potionsplus.core.Items.DYNAMIC_ICON_INDEX_PROPERTY_NAME;

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

        registerPotionEffectIcons();
        registerGenericIcons();

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
                new String[][]{
                        new String[]{"block/hanging_fern_upper", "block/hanging_fern_middle", "block/hanging_fern_lower"},
                        new String[]{"block/hanging_fern_upper_blooming", "block/hanging_fern_middle_blooming", "block/hanging_fern_lower_blooming"},
                },
                hangingPlantTextureOrientation,
                "block/hanging_fern_upper_blooming");
        registerVersatilePlantBlock(Blocks.COWLICK_VINE,
                new String[]{"block/cowlick_vine_0", "block/cowlick_vine_1", "block/cowlick_vine_2", "block/cowlick_vine_3", "block/cowlick_vine_tail"},
                hangingPlantTextureOrientation,
                "block/cowlick_vine_tail");
        registerBloomingPlantBlock(Blocks.DROOPY_VINE,
                new String[][]{
                        new String[]{"block/droopy_vine_0", "block/droopy_vine_1", "block/droopy_vine_2"},
                        new String[]{"block/droopy_vine_blooming_0", "block/droopy_vine_blooming_1", "block/droopy_vine_blooming_2"},
                },
                hangingPlantTextureOrientation,
                "block/droopy_vine_blooming_2");
        registerBloomingPlantBlock(Blocks.SURVIVOR_STICK,
                new String[][]{
                        new String[]{"block/survivor_stick_0", "block/survivor_stick_1"},
                        new String[]{"block/survivor_stick_blooming_0", "block/survivor_stick_blooming_1"},
                },
                hangingPlantTextureOrientation,
                "block/survivor_stick_blooming_1");

        registerItem(Items.WREATH.value());

        registerItem(Items.NETHERITE_REMNANT.value());
        registerItem(Items.RAW_URANIUM.value());
        registerItem(Items.URANIUM_INGOT.value());
        registerItem(Items.SULFUR_SHARD.value());
        registerItem(Items.SULFURIC_ACID.value());
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

    private void registerPotionEffectIcons() {
        ItemModelBuilder imb = null;
        for (MobEffect mobEffect : PUtil.getAllMobEffects()) {
            ResourceLocation registryName = BuiltInRegistries.MOB_EFFECT.getKey(mobEffect);
            if (imb == null) {
                imb = itemModels().getBuilder("potion_effect_icon")
                        .parent(models().getExistingFile(mcLoc("item/generated")))
                        .texture("layer0", registryName.getNamespace() + ":mob_effect/" + registryName.getPath());
            }

            // Each potion effect icon is a separate item model
            // That is referenced in the overrides of the item we make in "imb"
            String name = "potion_effect_icon_" + registryName.getPath();
            itemModels().singleTexture(name, mc("item/generated"), "layer0", ResourceLocation.fromNamespaceAndPath(registryName.getNamespace(), "mob_effect/" + registryName.getPath()));

            // Add override to main model
            float f = (grill24.potionsplus.core.potion.MobEffects.POTION_ICON_INDEX_MAP.get().get(registryName) - 1) / 64F;
            imb = imb.override().predicate(ppId(DYNAMIC_ICON_INDEX_PROPERTY_NAME), f).model(itemModels().getExistingFile(ppId(name))).end();
        }
    }

    private void registerGenericIcons() {
        ItemModelBuilder imb = null;

        for (ResourceLocation rl : grill24.potionsplus.core.Items.GENERIC_ICON_RESOURCE_LOCATIONS) {
            if (imb == null) {
                imb = itemModels().getBuilder("generic_icon")
                        .parent(models().getExistingFile(mcLoc("item/generated")))
                        .texture("layer0", rl.getNamespace() + ":item/" + rl.getPath());
            }

            // Each generic icon is a separate item model
            // That is referenced in the overrides of the item we make in "imb"
            String name = "generic_icon_" + rl.getPath();
            try {
                itemModels().singleTexture(name, mc("item/generated"), "layer0", ResourceLocation.fromNamespaceAndPath(rl.getNamespace(), "item/" + rl.getPath()));
            } catch (IllegalArgumentException e) {
                // If the texture doesn't exist, try looking for a particle texture
                // Hacky
                itemModels().singleTexture(name, mc("item/generated"), "layer0", ResourceLocation.fromNamespaceAndPath(rl.getNamespace(), "particle/" + rl.getPath()));
            }

            // Add override to main model
            float f = Items.GENERIC_ICON_RESOURCE_LOCATIONS.indexOf(rl) / 64F;
            imb = imb.override().predicate(ppId(DYNAMIC_ICON_INDEX_PROPERTY_NAME), f).model(itemModels().getExistingFile(ppId(name))).end();
        }
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

    private void registerItemFromParent(Item item, ResourceLocation parent) {
        ResourceLocation modelLocation = getModelLocation(item);
        itemModels().getBuilder(modelLocation.getPath())
                .parent(models().getExistingFile(parent));
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

    private void registerVersatilePlantBlock(DeferredHolder<Block, VersatilePlantBlock> holder, String[] textures, Function<Direction, Pair<Integer, Integer>> texRotationFunction, String itemTexture) {
        VersatilePlantBlock block = holder.value();

        String name = holder.getKey().location().getPath();

        // This loop generates a model for every texture used in the pattern
        Set<String> usedModels = new HashSet<>();
        for (Integer textureIndex : block.getUsedTextures()) {
            String modelName = getVersatilePlantModelName(name, textureIndex);

            // Ensure we don't duplicate textures, since textures can be repeated in the pattern. Only need one model per texture.
            if (!usedModels.contains(modelName)) {
                usedModels.add(modelName);

                String tex = textures[textureIndex];
                models().withExistingParent(modelName, mcLoc("block/cross"))
                        .texture("cross", tex);
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
    private void registerBloomingPlantBlock(DeferredHolder<Block, BloomingPlantBlock> holder, String[][] textures, Function<Direction, Pair<Integer, Integer>> texRotationFunction, String itemTexture) {
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

                    String tex = textures[blooming][textureIndex];
                    models().withExistingParent(modelName, mcLoc("block/cross"))
                            .texture("cross", tex);
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
