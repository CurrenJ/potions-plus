package grill24.potionsplus.utility.registration.item;

import grill24.potionsplus.core.items.DynamicIconItems;
import grill24.potionsplus.item.EdibleChoiceItem;
import grill24.potionsplus.item.modelproperty.EdibleChoiceProperty;
import grill24.potionsplus.utility.PUtil;
import grill24.potionsplus.utility.registration.IModelGenerator;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.RangeSelectItemModel;
import net.minecraft.client.renderer.item.properties.numeric.Count;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;

import java.util.*;
import java.util.function.Supplier;

import static grill24.potionsplus.utility.Utility.mc;
import static grill24.potionsplus.utility.Utility.ppId;

public class ItemOverrideUtility {
    public abstract static class ItemOverrideModelGenerator<T extends Item> implements IModelGenerator<Item> {
        private final Supplier<Holder<Item>> itemGetter;

        public ItemOverrideModelGenerator(Supplier<Holder<Item>> itemGetter) {
            this.itemGetter = itemGetter;
        }

        @Override
        public Holder<Item> getHolder() {
            return itemGetter.get();
        }
    }

    public static class EdibleChoiceItemOverrideModelGenerator extends ItemOverrideModelGenerator<EdibleChoiceItem> {
        private final ItemOverrideCommonUtility.EdibleChoiceItemOverrideData commonData;

        public EdibleChoiceItemOverrideModelGenerator(Supplier<Holder<Item>> itemGetter, ItemOverrideCommonUtility.EdibleChoiceItemOverrideData commonData) {
            super(itemGetter);
            this.commonData = commonData;
        }

        @Override
        public void generate(BlockModelGenerators blockModelGenerators, ItemModelGenerators itemModelGenerators) {
            Holder<? extends Item> item = getHolder();

            TextureMapping fallbackItemTextureMapping = new TextureMapping().put(TextureSlot.LAYER0, new Material(commonData.getLayer0()));
            Identifier fallbackItemModelId = ppId(item.unwrapKey().orElseThrow().identifier().getPath() + "_fallback");
            Identifier fallbackItemModel = ModelTemplates.FLAT_ITEM.create(fallbackItemModelId, fallbackItemTextureMapping, blockModelGenerators.modelOutput);

            List<RangeSelectItemModel.Entry> entries = commonData.getLayer1().stream().map(layer1Texture -> {
                String str = layer1Texture.getPath();
                String name = item.unwrapKey().orElseThrow().identifier().getPath() + "_" + str.substring(str.lastIndexOf('/') + 1);
                Identifier modelId = ppId("item/" + name);

                TextureMapping textureMapping = new TextureMapping()
                        .put(TextureSlot.LAYER0, new Material(commonData.getLayer0()))
                        .put(TextureSlot.LAYER1, new Material(layer1Texture));
                Identifier generatedItemModel = ModelTemplates.TWO_LAYERED_ITEM.create(modelId, textureMapping, blockModelGenerators.modelOutput);

                float threshold = commonData.getOverrideValue(layer1Texture);

                return new RangeSelectItemModel.Entry(
                        threshold,
                        ItemModelUtils.plainModel(generatedItemModel)
                );
            }).toList();

            RangeSelectItemModel.Unbaked rangeSelectItemModel = new RangeSelectItemModel.Unbaked(
                    Optional.empty(),
                    new EdibleChoiceProperty(),
                    1.0F,
                    entries,
                    Optional.of(ItemModelUtils.plainModel(fallbackItemModel))
            );

            blockModelGenerators.itemModelOutput.accept(item.value(), rangeSelectItemModel);
        }
    }

    public static class PotionEffectIconOverrideModelData extends ItemOverrideModelGenerator<Item> {
        public PotionEffectIconOverrideModelData(Supplier<Holder<Item>> itemSupplier, Identifier overridePropertyId) {
            super(itemSupplier);
        }

        @Override
        public void generate(BlockModelGenerators blockModelGenerators, ItemModelGenerators itemModelGenerators) {
            Holder<Item> item = getHolder();

            ItemModel.Unbaked fallbackItemModel = ItemModelUtils.plainModel(mc("item/stick"));

            List<RangeSelectItemModel.Entry> entries = PUtil.getAllMobEffects().stream().map(mobEffect -> {
                Identifier registryName = BuiltInRegistries.MOB_EFFECT.getKey(mobEffect);
                String name = "potion_effect_icon_" + registryName.getPath();
                Identifier modelId = ppId("item/" + name);

                TextureMapping textureMapping = new TextureMapping().put(TextureSlot.LAYER0, new Material(Identifier.fromNamespaceAndPath(registryName.getNamespace(), "mob_effect/" + registryName.getPath())));
                Identifier generatedItemModel = ModelTemplates.FLAT_ITEM.create(modelId, textureMapping, blockModelGenerators.modelOutput);

                float threshold = (grill24.potionsplus.core.potion.MobEffects.POTION_ICON_INDEX_MAP.get().get(registryName) - 1) / 64F;
                return ItemModelUtils.override(ItemModelUtils.plainModel(generatedItemModel), threshold);
            }).toList();

            RangeSelectItemModel.Unbaked rangeSelectItemModel = new RangeSelectItemModel.Unbaked(
                    Optional.empty(),
                    new Count(true),
                    1.0F,
                    entries,
                    Optional.of(fallbackItemModel)
            );

            blockModelGenerators.itemModelOutput.accept(item.value(), rangeSelectItemModel);
        }
    }

    public static class DynamicItemOverrideModelData extends ItemOverrideModelGenerator<Item> {
        private final Identifier[] textures;
        private final Map<Identifier, Integer> textureIndexMap;

        public DynamicItemOverrideModelData(Supplier<Holder<Item>> itemSupplier, Identifier overridePropertyId, Identifier[] textures, Map<Identifier, Integer> textureToItemStackCountMap) {
            super(itemSupplier);
            this.textureIndexMap = textureToItemStackCountMap;
            this.textures = textures;
        }

        public int getItemStackCountForTexture(Identifier textureLocation) {
            return textureIndexMap.getOrDefault(textureLocation, 1);
        }

        @Override
        public void generate(BlockModelGenerators blockModelGenerators, ItemModelGenerators itemModelGenerators) {
            Holder<Item> item = getHolder();

            TextureMapping fallbackItemTextureMapping = new TextureMapping().put(TextureSlot.LAYER0, new Material(DynamicIconItems.UNKNOWN_TEX_LOC));
            Identifier fallbackItemModelLocation = ModelTemplates.FLAT_ITEM.create(ppId("unknown_generic_icon_fallback"), fallbackItemTextureMapping, blockModelGenerators.modelOutput);
            ItemModel.Unbaked fallbackItemModel = ItemModelUtils.plainModel(fallbackItemModelLocation);

            List<RangeSelectItemModel.Entry> entries = Arrays.stream(textures).map(texture -> {
                int itemStackCount = getItemStackCountForTexture(texture);

                String str = texture.getPath();
                String name = "generic_icon_" + str.substring(str.lastIndexOf('/') + 1);
                Identifier modelId = ppId("item/" + name);

                TextureMapping textureMapping = new TextureMapping().put(TextureSlot.LAYER0, new Material(texture));
                Identifier generatedItemModel = ModelTemplates.FLAT_ITEM.create(modelId, textureMapping, blockModelGenerators.modelOutput);

                float threshold = itemStackCount / 64F;
                return ItemModelUtils.override(ItemModelUtils.plainModel(generatedItemModel), threshold);
            }).toList();

            RangeSelectItemModel.Unbaked rangeSelectItemModel = new RangeSelectItemModel.Unbaked(
                    Optional.empty(),
                    new Count(true),
                    1.0F,
                    entries,
                    Optional.of(fallbackItemModel)
            );

            blockModelGenerators.itemModelOutput.accept(item.value(), rangeSelectItemModel);
        }

    }
}
