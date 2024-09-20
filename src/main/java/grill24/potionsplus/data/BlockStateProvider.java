package grill24.potionsplus.data;

import grill24.potionsplus.block.ClotheslineBlock;
import grill24.potionsplus.block.ClotheslinePart;
import grill24.potionsplus.block.ParticleEmitterBlock;
import grill24.potionsplus.core.Blocks;
import grill24.potionsplus.core.Items;
import grill24.potionsplus.utility.ModInfo;
import grill24.potionsplus.utility.PUtil;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;

import static net.minecraft.data.models.model.ModelLocationUtils.getModelLocation;
import static net.minecraft.data.models.model.TextureMapping.getBlockTexture;
import static grill24.potionsplus.core.Items.DYNAMIC_ICON_INDEX_PROPERTY_NAME;

public class BlockStateProvider extends net.minecraftforge.client.model.generators.BlockStateProvider {
    public BlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, ModInfo.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        // ----- Blocks -----

        // Particle Emitter
        registerParticleEmitter();

        // Herbalist's Lectern
        registerBlockWithModel(Blocks.HERBALISTS_LECTERN.get());
        registerHorizontalDirectionalBlock(Blocks.ABYSSAL_TROVE.get());
        registerHorizontalDirectionalBlock(Blocks.SANGUINE_ALTAR.get());

        registerClothesline();
        registerFlowerBlock(Blocks.IRON_OXIDE_DAISY.get());
        registerFlowerBlock(Blocks.COPPER_CHRYSANTHEMUM.get());
        registerFlowerBlock(Blocks.LAPIS_LILAC.get());
        registerFlowerBlock(Blocks.DIAMOUR.get());
        registerFlowerBlock(Blocks.GOLDEN_CUBENSIS.get());
        registerFlowerBlock(Blocks.REDSTONE_ROSE.get());
        registerFlowerBlock(Blocks.BLACK_COALLA_LILY.get());

        registerCubeAll(Blocks.DENSE_DIAMOND_ORE.get());
        registerCubeAll(Blocks.DEEPSLATE_DENSE_DIAMOND_ORE.get());
        registerCubeAll(Blocks.COOBLESTONE.get());
        registerCubeAll(Blocks.UNSTABLE_BLOCK.get());
        registerCubeAll(Blocks.UNSTABLE_DEEPSLATE.get(), new ResourceLocation("block/cobbled_deepslate"));
        registerCubeAll(Blocks.UNSTABLE_MOLTEN_DEEPSLATE.get());
        registerCubeAll(Blocks.UNSTABLE_BLACKSTONE.get(), new ResourceLocation("block/blackstone"));
        registerCubeAll(Blocks.UNSTABLE_MOLTEN_BLACKSTONE.get());
        registerFaceAttachedHorizontalDirectionalBlock(Blocks.LAVA_GEYSER.get());
        registerItemFromParent(Blocks.LAVA_GEYSER.get().asItem(), block(ForgeRegistries.BLOCKS.getKey(Blocks.LAVA_GEYSER.get())));
        registerItem(Blocks.DECORATIVE_FIRE.get().asItem(), mcLoc("block/fire_0"));

        registerCubeAll(Blocks.SANDY_COPPER_ORE.get());
        registerCubeAll(Blocks.SANDY_IRON_ORE.get());
        registerCubeAll(Blocks.SANDY_GOLD_ORE.get());
        registerCubeAll(Blocks.SANDY_DIAMOND_ORE.get());
        registerCubeAll(Blocks.SANDY_REDSTONE_ORE.get());
        registerCubeAll(Blocks.SANDY_LAPIS_ORE.get());
        registerCubeAll(Blocks.SANDY_COAL_ORE.get());
        registerCubeAll(Blocks.SANDY_EMERALD_ORE.get());

        registerCubeAll(Blocks.MOSSY_COPPER_ORE.get());
        registerCubeAll(Blocks.MOSSY_IRON_ORE.get());
        registerCubeAll(Blocks.MOSSY_GOLD_ORE.get());
        registerCubeAll(Blocks.MOSSY_DIAMOND_ORE.get());
        registerCubeAll(Blocks.MOSSY_REDSTONE_ORE.get());
        registerCubeAll(Blocks.MOSSY_LAPIS_ORE.get());
        registerCubeAll(Blocks.MOSSY_COAL_ORE.get());
        registerCubeAll(Blocks.MOSSY_EMERALD_ORE.get());

        // ----- Items -----

        registerPotionEffectIcons();
        registerGenericIcons();

        registerItem(Items.MOSS.get());
        registerItem(Items.SALT.get());
        registerItem(Items.WORMROOT.get());
        registerItem(Items.ROTTEN_WORMROOT.get());
        registerItem(Items.LUNAR_BERRIES.get());
        registerItem(Blocks.IRON_OXIDE_DAISY.get().asItem(), "block");
        registerItem(Blocks.COPPER_CHRYSANTHEMUM.get().asItem(), "block");
        registerItem(Blocks.LAPIS_LILAC.get().asItem(), "block");
        registerItem(Blocks.DIAMOUR.get().asItem(), "block");
        registerItem(Blocks.GOLDEN_CUBENSIS.get().asItem(), "block");
        registerItem(Blocks.REDSTONE_ROSE.get().asItem(), "block");
        registerItem(Blocks.BLACK_COALLA_LILY.get().asItem(), "block");
        registerItem(Items.WREATH.get());
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
            ResourceLocation registryName = ForgeRegistries.MOB_EFFECTS.getKey(mobEffect);
            if (imb == null) {
                imb = itemModels().getBuilder("potion_effect_icon")
                        .parent(models().getExistingFile(mcLoc("item/generated")))
                        .texture("layer0", registryName.getNamespace() + ":mob_effect/" + registryName.getPath());
            }

            // Each potion effect icon is a separate item model
            // That is referenced in the overrides of the item we make in "imb"
            String name = "potion_effect_icon_" + registryName.getPath();
            itemModels().singleTexture(name, new ResourceLocation("item/generated"), "layer0", new ResourceLocation(registryName.getNamespace() + ":mob_effect/" + registryName.getPath()));

            // Add override to main model
            float f = (grill24.potionsplus.core.potion.MobEffects.POTION_ICON_INDEX_MAP.get().get(registryName) - 1) / 64F;
            imb = imb.override().predicate(new ResourceLocation(ModInfo.MOD_ID, DYNAMIC_ICON_INDEX_PROPERTY_NAME), f).model(itemModels().getExistingFile(new ResourceLocation(ModInfo.MOD_ID, name))).end();
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
                itemModels().singleTexture(name, new ResourceLocation("item/generated"), "layer0", new ResourceLocation(rl.getNamespace() + ":item/" + rl.getPath()));
            } catch (IllegalArgumentException e) {
                // If the texture doesn't exist, try looking for a particle texture
                // Hacky
                itemModels().singleTexture(name, new ResourceLocation("item/generated"), "layer0", new ResourceLocation(rl.getNamespace() + ":particle/" + rl.getPath()));
            }

            // Add override to main model
            float f = Items.GENERIC_ICON_RESOURCE_LOCATIONS.indexOf(rl) / 64F;
            imb = imb.override().predicate(new ResourceLocation(ModInfo.MOD_ID, DYNAMIC_ICON_INDEX_PROPERTY_NAME), f).model(itemModels().getExistingFile(new ResourceLocation(ModInfo.MOD_ID, name))).end();
        }
    }

    private void registerItem(Item item) {
        ResourceLocation modelLocation = getModelLocation(item);
        itemModels().getBuilder(modelLocation.getPath())
                .parent(models().getExistingFile(mcLoc("item/generated")))
                .texture("layer0", modelLocation);
    }

    private void registerItem(Item item, String... texturePath) {
        ResourceLocation resourceLocation = ForgeRegistries.ITEMS.getKey(item);
        itemModels().getBuilder(resourceLocation.getPath())
                .parent(models().getExistingFile(mcLoc("item/generated")))
                .texture("layer0", String.join("/", texturePath) + "/" + resourceLocation.getPath());
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
            Block blockForModel = ParticleEmitterBlock.PARTICLE_EMITTER_CONFIGURATIONS[i].blockModel;
            blockForModel = blockForModel == null ? net.minecraft.world.level.block.Blocks.IRON_BLOCK : blockForModel;

            getVariantBuilder(Blocks.PARTICLE_EMITTER.get())
                    .partialState().with(ParticleEmitterBlock.PARTICLE_TYPE, i).modelForState()
                    .modelFile(models().getExistingFile(mcBlock(blockForModel)))
                    .addModel();
        }

        simpleBlockItem(Blocks.PARTICLE_EMITTER.get(), models().getExistingFile(mcLoc("block/iron_block")));
    }

    private void registerClothesline() {
        getVariantBuilder(Blocks.CLOTHESLINE.get())
                .partialState().with(ClotheslineBlock.PART, ClotheslinePart.LEFT).modelForState()
                .modelFile(models().getExistingFile(mcLoc("oak_fence_post")))
                .addModel()
                .partialState().with(ClotheslineBlock.PART, ClotheslinePart.RIGHT).modelForState()
                .modelFile(models().getExistingFile(mcLoc("oak_fence_post")))
                .addModel();

        itemModels().getBuilder(Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(Blocks.CLOTHESLINE.get())).getPath())
                .parent(models().getExistingFile(mcLoc("block/oak_fence_post")));
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
                    .modelFile(models().getExistingFile(ForgeRegistries.BLOCKS.getKey(block)))
                    .rotationY(yRot)
                    .rotationX(xRot)
                    .uvLock(true)
                    .build();
        });
    }

    private void registerHorizontalDirectionalBlock(Block block) {
        registerHorizontalDirectionalBlock(block, ForgeRegistries.BLOCKS.getKey(block));
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
        models().withExistingParent(Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(block)).getPath(), mcLoc("block/cross"))
                .texture("cross", texture);

        getVariantBuilder(block).forAllStates(state -> ConfiguredModel.builder()
                .modelFile(models().getExistingFile(ForgeRegistries.BLOCKS.getKey(block)))
                .build());
    }

    private void registerFlowerBlock(Block block) {
        registerFlowerBlock(block, "block/" + Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(block)).getPath());
    }

    private void registerBlockWithModel(Block block, ResourceLocation resourceLocation) {
        getVariantBuilder(block).forAllStates(state -> ConfiguredModel.builder()
                .modelFile(models().getExistingFile(resourceLocation))
                .build());
    }

    private void registerBlockWithModel(Block block) {
        registerBlockWithModel(block, ForgeRegistries.BLOCKS.getKey(block));
    }

    private void fromParent(Block block, Block parent, boolean doBlockModels, boolean doItemModels, boolean doBlockStates) {
        if (doBlockModels) {
            models().withExistingParent(Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(block)).getPath(), mcLoc(Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(parent)).getPath()));
        }
        if (doItemModels) {
            itemModels().withExistingParent(Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(block)).getPath(), mcLoc(Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(parent)).getPath()));
        }
        if (doBlockStates) {
            getVariantBuilder(block).forAllStates(state -> {
                return ConfiguredModel.builder()
                        .modelFile(models().getExistingFile(mcLoc(Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(parent)).getPath())))
                        .build();
            });
        }
    }

    private void fromParent(Block block, Block parent) {
        fromParent(block, parent, true, true, true);
    }

    private ResourceLocation mcBlock(Block block) {
        return mcLoc(Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(block)).getPath());
    }

    private ResourceLocation block(ResourceLocation resourceLocation) {
        return new ResourceLocation(ModInfo.MOD_ID, "block/" + resourceLocation.getPath());
    }

    private ResourceLocation item(ResourceLocation resourceLocation) {
        return new ResourceLocation(ModInfo.MOD_ID, "item/" + resourceLocation.getPath());
    }
}
