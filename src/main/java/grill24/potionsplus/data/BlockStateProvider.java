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

        registerPotionEffectIcons(MobEffects.ABSORPTION, MobEffects.BAD_OMEN, MobEffects.BLINDNESS, MobEffects.CONDUIT_POWER, MobEffects.DOLPHINS_GRACE, MobEffects.FIRE_RESISTANCE, MobEffects.GLOWING, MobEffects.HEALTH_BOOST, MobEffects.HERO_OF_THE_VILLAGE, MobEffects.HUNGER, MobEffects.INVISIBILITY, MobEffects.JUMP, MobEffects.LEVITATION, MobEffects.LUCK, MobEffects.NIGHT_VISION, MobEffects.POISON, MobEffects.REGENERATION, MobEffects.SATURATION, MobEffects.SLOW_FALLING, MobEffects.UNLUCK, MobEffects.WATER_BREATHING, MobEffects.WEAKNESS, MobEffects.WITHER);
        registerGenericIcons();
    }

    private static class ResourceLocationTrimNamespace extends ResourceLocation {
        public ResourceLocationTrimNamespace(String path) {
            super("", path);
        }

        @Override
        public String toString() {
            return path;
        }
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
            float f = (grill24.potionsplus.core.MobEffects.POTION_ICON_INDEX_MAP.get().get(mobEffect.getRegistryName()) - 1) / 64F;
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
            itemModels().singleTexture(name, new ResourceLocation("item/generated"), "layer0", new ResourceLocation(rl.getNamespace() + ":item/" + rl.getPath()));

            // Add override to main model
            float f = Items.GENERIC_ICON_RESOURCE_LOCATIONS.indexOf(rl) / 64F;
            imb = imb.override().predicate(new ResourceLocation(ModInfo.MOD_ID, DYNAMIC_ICON_INDEX_PROPERTY_NAME), f).model(itemModels().getExistingFile(new ResourceLocation(ModInfo.MOD_ID, name))).end();
        }
    }

    private void registerItem(Item item) {
        itemModels().getBuilder(Objects.requireNonNull(item.getRegistryName()).getPath())
                .parent(models().getExistingFile(mcLoc("item/generated")))
                .texture("layer0", "mob_effect/" + item.getRegistryName().getPath());
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
