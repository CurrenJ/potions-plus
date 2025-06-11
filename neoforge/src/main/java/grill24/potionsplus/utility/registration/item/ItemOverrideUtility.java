package grill24.potionsplus.utility.registration.item;

import grill24.potionsplus.core.items.DynamicIconItems;
import grill24.potionsplus.item.EdibleChoiceItem;
import grill24.potionsplus.item.modelproperty.EdibleChoiceProperty;
import grill24.potionsplus.utility.PUtil;
import grill24.potionsplus.utility.registration.IModelGenerator;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.renderer.item.BlockModelWrapper;
import net.minecraft.client.renderer.item.RangeSelectItemModel;
import net.minecraft.client.renderer.item.properties.numeric.Count;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.*;
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

    public static class EdibleChoiceItemOverrideModelGenerator extends ItemOverrideModelGenerator<EdibleChoiceItem> {
        private final ItemOverrideCommonUtility.EdibleChoiceItemOverrideData commonData;

        public EdibleChoiceItemOverrideModelGenerator(Supplier<Holder<Item>> itemGetter, ItemOverrideCommonUtility.EdibleChoiceItemOverrideData commonData) {
            super(itemGetter, commonData.getOverridePropertyId());
            this.commonData = commonData;
        }


        @Override
        public void generate(BlockModelGenerators blockModelGenerators, ItemModelGenerators itemModelGenerators) {
            Holder<? extends Item> item = getHolder();

            TextureMapping fallbackItemTextureMapping = new TextureMapping().put(TextureSlot.LAYER0, commonData.getLayer0());
            ResourceLocation fallbackItemModelId = ppId(item.getKey().location().getPath() + "_fallback");
            ResourceLocation fallbackItemModel = ModelTemplates.FLAT_ITEM.create(fallbackItemModelId, fallbackItemTextureMapping, itemModelGenerators.modelOutput);

            List<RangeSelectItemModel.Entry> entries = commonData.getLayer1().stream().map(layer1Texture -> {
                String str = layer1Texture.getPath();
                String name = item.getKey().location().getPath() + "_" + str.substring(str.lastIndexOf('/') + 1);
                ResourceLocation modelId = ppId("item/" + name);

                TextureMapping textureMapping = new TextureMapping()
                        .put(TextureSlot.LAYER0, commonData.getLayer0())
                        .put(TextureSlot.LAYER1, layer1Texture);
                ResourceLocation generatedItemModel = ModelTemplates.TWO_LAYERED_ITEM.create(modelId, textureMapping, itemModelGenerators.modelOutput);

                float threshold = commonData.getOverrideValue(layer1Texture);

                return new RangeSelectItemModel.Entry(
                        threshold,
                        new BlockModelWrapper.Unbaked(
                                generatedItemModel,
                                Collections.emptyList()
                        )
                );
            }).toList();

            RangeSelectItemModel.Unbaked rangeSelectItemModel = new RangeSelectItemModel.Unbaked(
                    new EdibleChoiceProperty(),
                    1,
                    entries,
                    Optional.of(new BlockModelWrapper.Unbaked(fallbackItemModel, Collections.emptyList()))
            );

            itemModelGenerators.itemModelOutput.accept(item.value(), rangeSelectItemModel);
        }
    }

    public static class PotionEffectIconOverrideModelData extends ItemOverrideModelGenerator<Item> {
        public PotionEffectIconOverrideModelData(Supplier<Holder<Item>> itemSupplier, ResourceLocation overridePropertyId) {
            super(itemSupplier, overridePropertyId);
        }

        @Override
        public void generate(BlockModelGenerators blockModelGenerators, ItemModelGenerators itemModelGenerators) {
            Holder<Item> item = getHolder();

            BlockModelWrapper.Unbaked fallbackItemModel = new BlockModelWrapper.Unbaked(mc("item/stick"), Collections.emptyList());

            List<RangeSelectItemModel.Entry> entries = PUtil.getAllMobEffects().stream().map(mobEffect -> {
                ResourceLocation registryName = BuiltInRegistries.MOB_EFFECT.getKey(mobEffect);
                String name = "potion_effect_icon_" + registryName.getPath();
                ResourceLocation modelId = ppId("item/" + name);

                TextureMapping textureMapping = new TextureMapping().put(TextureSlot.LAYER0, ResourceLocation.fromNamespaceAndPath(registryName.getNamespace(), "mob_effect/" + registryName.getPath()));
                ResourceLocation generatedItemModel = ModelTemplates.FLAT_ITEM.create(modelId, textureMapping, itemModelGenerators.modelOutput);

                float threshold = (grill24.potionsplus.core.potion.MobEffects.POTION_ICON_INDEX_MAP.get().get(registryName) - 1) / 64F;
                return new RangeSelectItemModel.Entry(
                        threshold,
                        new BlockModelWrapper.Unbaked(
                                generatedItemModel,
                                Collections.emptyList()
                        )
                );
            }).toList();

            RangeSelectItemModel.Unbaked rangeSelectItemModel = new RangeSelectItemModel.Unbaked(
                    new Count(true),
                    1,
                    entries,
                    Optional.of(fallbackItemModel)
            );

            itemModelGenerators.itemModelOutput.accept(item.value(), rangeSelectItemModel);
        }
    }

    public static class DynamicItemOverrideModelData extends ItemOverrideModelGenerator<Item> {
        private final ResourceLocation[] textures;
        private final Map<ResourceLocation, Integer> textureIndexMap;

        public DynamicItemOverrideModelData(Supplier<Holder<Item>> itemSupplier, ResourceLocation overridePropertyId, ResourceLocation[] textures, Map<ResourceLocation, Integer> textureToItemStackCountMap) {
            super(itemSupplier, overridePropertyId);
            this.textureIndexMap = textureToItemStackCountMap;
            this.textures = textures;
        }

        public int getItemStackCountForTexture(ResourceLocation textureLocation) {
            return textureIndexMap.getOrDefault(textureLocation, 1);
        }

        @Override
        public void generate(BlockModelGenerators blockModelGenerators, ItemModelGenerators itemModelGenerators) {
            Holder<Item> item = getHolder();

            TextureMapping fallbackItemTextureMapping = new TextureMapping().put(TextureSlot.LAYER0, DynamicIconItems.UNKNOWN_TEX_LOC);
            ResourceLocation fallbackItemModelLocation = ModelTemplates.FLAT_ITEM.create(ppId("unknown_generic_icon_fallback"), fallbackItemTextureMapping, itemModelGenerators.modelOutput);
            BlockModelWrapper.Unbaked fallbackItemModel = new BlockModelWrapper.Unbaked(fallbackItemModelLocation, Collections.emptyList());

            List<RangeSelectItemModel.Entry> entries = Arrays.stream(textures).map(texture -> {
                int itemStackCount = getItemStackCountForTexture(texture);

                String str = texture.getPath();
                String name = "generic_icon_" + str.substring(str.lastIndexOf('/') + 1);
                ResourceLocation modelId = ppId("item/" + name);

                TextureMapping textureMapping = new TextureMapping().put(TextureSlot.LAYER0, texture);
                ResourceLocation generatedItemModel = ModelTemplates.FLAT_ITEM.create(modelId, textureMapping, itemModelGenerators.modelOutput);

                float threshold = itemStackCount / 64F;
                return new RangeSelectItemModel.Entry(
                        threshold,
                        new BlockModelWrapper.Unbaked(
                                generatedItemModel,
                                Collections.emptyList()
                        )
                );
            }).toList();

            RangeSelectItemModel.Unbaked rangeSelectItemModel = new RangeSelectItemModel.Unbaked(
                    new Count(true),
                    1,
                    entries,
                    Optional.of(fallbackItemModel)
            );

            itemModelGenerators.itemModelOutput.accept(item.value(), rangeSelectItemModel);
        }

        @Override
        public ResourceLocation getOverridePropertyId() {
            return overridePropertyId;
        }
    }
}
