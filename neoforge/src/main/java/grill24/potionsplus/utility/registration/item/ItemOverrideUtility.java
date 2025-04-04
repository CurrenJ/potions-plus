package grill24.potionsplus.utility.registration.item;

import grill24.potionsplus.item.EdibleChoiceItem;
import grill24.potionsplus.utility.PUtil;
import grill24.potionsplus.utility.registration.IModelGenerator;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static grill24.potionsplus.utility.Utility.mc;
import static grill24.potionsplus.utility.Utility.ppId;

public class ItemOverrideUtility {
    public abstract static class ItemOverrideModelGenerator<T extends Item> implements IModelGenerator<Item> {
        protected ResourceLocation overridePropertyId;
        private final Supplier<Holder<Item>> itemGetter;

        public ItemOverrideModelGenerator(Supplier<Holder<Item>> itemGetter, ResourceLocation overridePropertyId) {
            this.overridePropertyId = overridePropertyId;
            this.itemGetter = itemGetter;
        }

        ResourceLocation getOverridePropertyId() {
            return overridePropertyId;
        }

        @Override
        public Holder<Item> getHolder() {
            return itemGetter.get();
        }
    }

    public static class EdibleChoiceItemOverrideModelData extends ItemOverrideModelGenerator<EdibleChoiceItem> {
        private final ResourceLocation layer0;
        private final List<ResourceLocation> layer1;
        private final Map<ResourceLocation, Integer> textureIndexMap;

        public EdibleChoiceItemOverrideModelData(Supplier<Holder<Item>> itemGetter, ResourceLocation overridePropertyId, ResourceLocation layer0, List<ResourceLocation> layer1) {
            super(itemGetter, overridePropertyId);
            this.layer0 = layer0;
            this.layer1 = layer1;
            this.layer1.addFirst(layer0);
            this.textureIndexMap = new HashMap<>();
            for (int i = 0; i < layer1.size(); i++) {
                textureIndexMap.put(this.layer1.get(i), i);
            }
        }

        private int getIndex(ResourceLocation textureLocation) {
            return textureIndexMap.getOrDefault(textureLocation, 0);
        }

        public float getOverrideValue(ResourceLocation textureLocation) {
            return getIndex(textureLocation) / 64F;
        }

        public ResourceLocation getRandomFlag(RandomSource randomSource) {
            return layer1.get(randomSource.nextInt(layer1.size()-1)+1);
        }

        @Override
        public void generate(BlockStateProvider provider) {
            ItemModelBuilder imb = null;

            Holder<? extends Item> item = getHolder();
            for (ResourceLocation layer1Texture : layer1) {
                ItemModelProvider itemModels = provider.itemModels();
                if (imb == null) {
                    imb = itemModels.getBuilder(item.getKey().location().getPath())
                            .parent(provider.models().getExistingFile(mc("item/generated")))
                            .texture("layer0", layer0);
                }

                // Each generic icon is a separate item model
                // That is referenced in the overrides of the item we make in "imb"
                String str = layer1Texture.getPath();
                String name = item.getKey().location().getPath() + "_" + str.substring(str.lastIndexOf('/') + 1);
                itemModels.getBuilder(name)
                        .parent(provider.models().getExistingFile(mc("item/generated")))
                        .texture("layer0", layer0)
                        .texture("layer1", layer1Texture);

                // Add override to main model
                float f = getOverrideValue(layer1Texture);
                imb = imb.override().predicate(getOverridePropertyId(), f).model(itemModels.getExistingFile(ppId(name))).end();
            }
        }
    }

    public static class PotionEffectIconOverrideModelData extends ItemOverrideModelGenerator<Item> {
        public PotionEffectIconOverrideModelData(Supplier<Holder<Item>> itemSupplier, ResourceLocation overridePropertyId) {
            super(itemSupplier, overridePropertyId);
        }

        @Override
        public void generate(BlockStateProvider provider) {
            ItemModelProvider itemModels = provider.itemModels();
            ItemModelBuilder imb = null;

            Holder<Item> item = getHolder();
            for (MobEffect mobEffect : PUtil.getAllMobEffects()) {
                ResourceLocation registryName = BuiltInRegistries.MOB_EFFECT.getKey(mobEffect);
                if (imb == null) {
                    imb = itemModels.getBuilder(item.getKey().location().getPath())
                            .parent(provider.models().getExistingFile(mc("item/generated")))
                            .texture("layer0", registryName.getNamespace() + ":mob_effect/" + registryName.getPath());
                }

                // Each potion effect icon is a separate item model
                // That is referenced in the overrides of the item we make in "imb"
                String name = "potion_effect_icon_" + registryName.getPath();
                itemModels.singleTexture(name, mc("item/generated"), "layer0", ResourceLocation.fromNamespaceAndPath(registryName.getNamespace(), "mob_effect/" + registryName.getPath()));

                // Add override to main model
                float f = (grill24.potionsplus.core.potion.MobEffects.POTION_ICON_INDEX_MAP.get().get(registryName) - 1) / 64F;
                imb = imb.override().predicate(getOverridePropertyId(), f).model(itemModels.getExistingFile(ppId(name))).end();
            }
        }
    }

    public static class DynamicItemOverrideModelData extends ItemOverrideModelGenerator<Item> {
        private final List<ResourceLocation> textureLocations;
        private final Map<ResourceLocation, Integer> textureIndexMap;

        public DynamicItemOverrideModelData(Supplier<Holder<Item>> itemSupplier, ResourceLocation overridePropertyId, List<ResourceLocation> textureLocations) {
            super(itemSupplier, overridePropertyId);
            this.textureLocations = textureLocations;
            this.textureIndexMap = new HashMap<>();
            for (int i = 0; i < textureLocations.size(); i++) {
                textureIndexMap.put(textureLocations.get(i), i);
            }
        }

        public int getIndex(ResourceLocation textureLocation) {
            return textureIndexMap.getOrDefault(textureLocation, 0);
        }

        public int getItemStackCountForTexture(ResourceLocation textureLocation) {
            return getIndex(textureLocation) + 1;
        }

        public ItemStack getItemStackForTexture(ItemLike itemLike, ResourceLocation textureLocation) {
            return new ItemStack(itemLike, getItemStackCountForTexture(textureLocation));
        }

        @Override
        public void generate(BlockStateProvider provider) {
            ItemModelBuilder imb = null;

            Holder<Item> item = getHolder();
            for (ResourceLocation rl : textureLocations) {
                ItemModelProvider itemModels = provider.itemModels();
                if (imb == null) {
                    imb = itemModels.getBuilder(item.getKey().location().getPath())
                            .parent(provider.models().getExistingFile(mc("item/generated")))
                            .texture("layer0", rl);
                }


                // Each generic icon is a separate item model
                // That is referenced in the overrides of the item we make in "imb"
                String str = rl.getPath();
                String name = "generic_icon_" + str.substring(str.lastIndexOf('/') + 1);
                itemModels.singleTexture(name, mc("item/generated"), "layer0", rl);

                // Add override to main model
                float f = getIndex(rl) / 64F;
                imb = imb.override().predicate(getOverridePropertyId(), f).model(itemModels.getExistingFile(ppId(name))).end();
            }
        }

        @Override
        public ResourceLocation getOverridePropertyId() {
            return overridePropertyId;
        }
    }
}
