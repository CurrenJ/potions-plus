package grill24.potionsplus.data;

import grill24.potionsplus.block.ParticleEmitterBlock;
import grill24.potionsplus.core.Blocks;
import grill24.potionsplus.core.Items;
import grill24.potionsplus.utility.ModInfo;
import grill24.potionsplus.utility.PUtil;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.Objects;

import static grill24.potionsplus.core.Items.DYNAMIC_ICON_INDEX_PROPERTY_NAME;

public class BlockStateProvider extends net.minecraftforge.client.model.generators.BlockStateProvider {
    public BlockStateProvider(DataGenerator gen, String modid, ExistingFileHelper exFileHelper) {
        super(gen, modid, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        // Brewing Cauldron
//        fromParent(grill24.potionsplus.core.Blocks.BREWING_CAULDRON.get(), net.minecraft.world.level.block.Blocks.CAULDRON);


        // Particle Emitter
        registerParticleEmitter();

        // Herbalist's Lectern
        registerBlockWithModel(Blocks.HERBALISTS_LECTERN.get());
        registerHorizontalDirectionalBlock(Blocks.ABYSSAL_TROVE.get());
        registerHorizontalDirectionalBlock(Blocks.SANGUINE_ALTAR.get());

        registerPotionEffectIcons();
        registerGenericIcons();

        registerItem(Items.MOSS.get());
        registerItem(Items.SALT.get());
        registerItem(Items.WORMROOT.get());
        registerItem(Items.ROTTEN_WORMROOT.get());
        registerItem(Items.LUNAR_BERRIES.get());
    }

    private void registerPotionEffectIcons(MobEffect... mobEffects) {
        ItemModelBuilder imb = null;
        for (MobEffect mobEffect : PUtil.getAllMobEffects()) {
            if (imb == null) {
                imb = itemModels().getBuilder("potion_effect_icon")
                        .parent(models().getExistingFile(mcLoc("item/generated")))
                        .texture("layer0", mobEffect.getRegistryName().getNamespace() + ":mob_effect/" + mobEffect.getRegistryName().getPath());
            }

            // Each potion effect icon is a separate item model
            // That is referenced in the overrides of the item we make in "imb"
            String name = "potion_effect_icon_" + Objects.requireNonNull(mobEffect.getRegistryName()).getPath();
            itemModels().singleTexture(name, new ResourceLocation("item/generated"), "layer0", new ResourceLocation(mobEffect.getRegistryName().getNamespace() + ":mob_effect/" + mobEffect.getRegistryName().getPath()));

            // Add override to main model
            float f = (grill24.potionsplus.core.potion.MobEffects.POTION_ICON_INDEX_MAP.get().get(mobEffect.getRegistryName()) - 1) / 64F;
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
        itemModels().getBuilder(Objects.requireNonNull(item.getRegistryName()).getPath())
                .parent(models().getExistingFile(mcLoc("item/generated")))
                .texture("layer0", "item/" + item.getRegistryName().getPath());
    }

    private void registerItem(Item item, String... folders) {
        itemModels().getBuilder(Objects.requireNonNull(item.getRegistryName()).getPath())
                .parent(models().getExistingFile(mcLoc("item/generated")))
                .texture("layer0", String.join("/", folders) + "/" + item.getRegistryName().getPath());
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

    private void registerHorizontalDirectionalBlock(Block block) {
        registerHorizontalDirectionalBlock(block, block.getRegistryName());
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

    private void registerBlockWithModel(Block block, ResourceLocation resourceLocation) {
        getVariantBuilder(block).forAllStates(state -> ConfiguredModel.builder()
                .modelFile(models().getExistingFile(resourceLocation))
                .build());
    }

    private void registerBlockWithModel(Block block) {
        registerBlockWithModel(block, block.getRegistryName());
    }

    private void fromParent(Block block, Block parent, boolean doBlockModels, boolean doItemModels, boolean doBlockStates) {
        if (doBlockModels) {
            models().withExistingParent(Objects.requireNonNull(block.getRegistryName()).getPath(), mcLoc(Objects.requireNonNull(parent.getRegistryName()).getPath()));
        }
        if (doItemModels) {
            itemModels().withExistingParent(Objects.requireNonNull(block.getRegistryName()).getPath(), mcLoc(Objects.requireNonNull(parent.getRegistryName()).getPath()));
        }
        if (doBlockStates) {
            getVariantBuilder(block).forAllStates(state -> {
                return ConfiguredModel.builder()
                        .modelFile(models().getExistingFile(mcLoc(Objects.requireNonNull(parent.getRegistryName()).getPath())))
                        .build();
            });
        }
    }

    private void fromParent(Block block, Block parent) {
        fromParent(block, parent, true, true, true);
    }

    private ResourceLocation mcBlock(Block block) {
        return mcLoc(Objects.requireNonNull(block.getRegistryName()).getPath());
    }
}
