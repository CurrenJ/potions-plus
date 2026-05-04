package grill24.potionsplus.utility.registration.item;

import grill24.potionsplus.item.BrassicaOleraceaItem;
import grill24.potionsplus.item.GeneticCropItem;
import grill24.potionsplus.item.modelproperty.BrassicaOleraceaProperty;
import grill24.potionsplus.item.modelproperty.GeneticProperty;
import grill24.potionsplus.utility.registration.IModelGenerator;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.*;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.client.renderer.item.RangeSelectItemModel;
import net.minecraft.client.renderer.item.SelectItemModel;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import org.jspecify.annotations.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import static grill24.potionsplus.utility.Utility.mc;

public class ItemModelUtility {
    /**
     * Generates a simple item model with a single texture. If no texture is provided, texture will default to "item/X", where X is the item name.
     */
    public static class SimpleItemModelGenerator<I extends Item> implements IModelGenerator<I> {
        @Nullable
        protected final Identifier textureLocation;
        private final Supplier<Holder<I>> itemGetter;

        public SimpleItemModelGenerator(Supplier<Holder<I>> itemGetter, Identifier textureLocation) {
            super();
            this.textureLocation = textureLocation;
            this.itemGetter = itemGetter;
        }

        public SimpleItemModelGenerator(Supplier<Holder<I>> itemSupplier) {
            super();
            this.textureLocation = null;
            this.itemGetter = itemSupplier;
        }

        @Override
        public void generate(BlockModelGenerators blockModelGenerators, ItemModelGenerators itemModelGenerators) {
            Item item = getHolder().value();

            Identifier modelLocation = ModelLocationUtils.getModelLocation(item);
            Identifier texture = textureLocation == null ? modelLocation : textureLocation;

            ModelTemplates.FLAT_ITEM.create(modelLocation, new TextureMapping().put(TextureSlot.LAYER0, new Material(texture)), blockModelGenerators.modelOutput);
            blockModelGenerators.itemModelOutput.accept(item, ItemModelUtils.plainModel(modelLocation));
        }

        @Override
        public Holder<? extends I> getHolder() {
            return itemGetter.get();
        }
    }

    public static class SimpleBlockItemModelGenerator<I extends Item> implements IModelGenerator<I> {
        private final Supplier<Holder<I>> itemGetter;
        private final Supplier<Holder<Block>> block;
        @Nullable
        private final Identifier parentModel;

        public SimpleBlockItemModelGenerator(Supplier<Holder<I>> itemGetter, Supplier<Holder<Block>> block) {
            this(itemGetter, block, null);
        }

        public SimpleBlockItemModelGenerator(Supplier<Holder<I>> itemGetter, Supplier<Holder<Block>> block, Identifier parentModel) {
            super();
            this.itemGetter = itemGetter;
            this.block = block;
            this.parentModel = parentModel;
        }

        @Override
        public void generate(BlockModelGenerators blockModelGenerators, ItemModelGenerators itemModelGenerators) {
            Identifier blockModelLocation = parentModel != null ? parentModel : ModelLocationUtils.getModelLocation(block.get().value());

            ItemModel.Unbaked itemModel = ItemModelUtils.plainModel(blockModelLocation);
            blockModelGenerators.itemModelOutput.accept(getHolder().value(), itemModel);
        }

        @Override
        public Holder<? extends I> getHolder() {
            return itemGetter.get();
        }
    }

    public static class ItemFromModelFileGenerator<I extends Item> implements IModelGenerator<I> {
        private final Supplier<Holder<I>> itemGetter;
        private final Identifier modelFile;

        public ItemFromModelFileGenerator(Supplier<Holder<I>> itemGetter, Identifier modelFile) {
            super();
            this.itemGetter = itemGetter;
            this.modelFile = modelFile;
        }

        @Override
        public void generate(BlockModelGenerators blockModelGenerators, ItemModelGenerators itemModelGenerators) {
            Item item = getHolder().value();

            blockModelGenerators.itemModelOutput.accept(item, ItemModelUtils.plainModel(modelFile));
        }

        @Override
        public Holder<? extends I> getHolder() {
            return itemGetter.get();
        }
    }

    public static class TintedLayerItemModelGenerator<I extends Item> implements IModelGenerator<I> {
        private final Supplier<Holder<I>> itemGetter;

        private final ItemTintSource itemTintSource;
        private final Identifier tintedLayerTextureLocation;
        private final Identifier[] untintedLayerTextureLocations;


        protected static final TextureSlot[] LAYERS = new TextureSlot[]{
                TextureSlot.LAYER0,
                TextureSlot.LAYER1,
                TextureSlot.LAYER2,
        };

        protected static final ModelTemplate TWO_LAYERS = new ModelTemplate(
                Optional.of(mc("item/generated")),
                Optional.empty(),
                TextureSlot.LAYER0,
                TextureSlot.LAYER1
        );
        protected static final ModelTemplate THREE_LAYERS = new ModelTemplate(
                Optional.of(mc("item/generated")),
                Optional.empty(),
                TextureSlot.LAYER0,
                TextureSlot.LAYER1,
                TextureSlot.LAYER2
        );

        protected static final Map<Integer, ModelTemplate> LAYER_COUNT_TO_TEMPLATE = Map.of(
                1, ModelTemplates.FLAT_ITEM,
                2, TWO_LAYERS,
                3, THREE_LAYERS
        );

        public TintedLayerItemModelGenerator(Supplier<Holder<I>> itemGetter, Supplier<ItemTintSource> itemTintSourceSupplier, Identifier tintedLayerTextureLocation, Identifier... untintedLayerTextureLocation) {
            super();
            this.itemGetter = itemGetter;
            this.itemTintSource = itemTintSourceSupplier.get();
            this.tintedLayerTextureLocation = tintedLayerTextureLocation;
            this.untintedLayerTextureLocations = untintedLayerTextureLocation;
        }

        @Override
        public void generate(BlockModelGenerators blockModelGenerators, ItemModelGenerators itemModelGenerators) {
            Item item = getHolder().value();

            Identifier modelLocation = ModelLocationUtils.getModelLocation(item);
            TextureMapping textureMapping = new TextureMapping();

            for (int i = 0; i < untintedLayerTextureLocations.length + 1 && i < LAYERS.length; i++) {
                if (i == 0) {
                    textureMapping.put(LAYERS[i], new Material(tintedLayerTextureLocation));
                } else {
                    textureMapping.put(LAYERS[i], new Material(untintedLayerTextureLocations[i - 1]));
                }
            }

            LAYER_COUNT_TO_TEMPLATE.getOrDefault(untintedLayerTextureLocations.length, ModelTemplates.FLAT_ITEM)
                    .create(modelLocation, textureMapping, blockModelGenerators.modelOutput);

            ItemModel.Unbaked model = ItemModelUtils.tintedModel(modelLocation, itemTintSource);

            blockModelGenerators.itemModelOutput.accept(item, model);
        }

        @Override
        public Holder<? extends I> getHolder() {
            return itemGetter.get();
        }
    }

    public static class GeneticCropWeightOverrideModelGenerator<I extends Item> implements IModelGenerator<I> {
        private final Supplier<Holder<I>> itemGetter;
        private final Supplier<ItemTintSource> itemTintSourceSupplier;
        private final ModelData[] modelData;

        private final Identifier fallbackTexture;

        @Override
        public Holder<? extends I> getHolder() {
            return itemGetter.get();
        }

        public record ModelData(float weightThreshold,
                                boolean tintFirstLayer,
                                Identifier... untintedLayerTextureLocations) {
            ItemModel.Unbaked createModel(BlockModelGenerators blockModelGenerators, ItemModelGenerators itemModelGenerators, Supplier<ItemTintSource> itemTintSource) {
                if (untintedLayerTextureLocations.length == 0) {
                    throw new IllegalArgumentException("At least one untinted layer texture location must be provided.");
                }

                Identifier modelLocation = untintedLayerTextureLocations[0];
                TextureMapping textureMapping = new TextureMapping();

                for (int i = 0; i < untintedLayerTextureLocations.length; i++) {
                    textureMapping.put(TintedLayerItemModelGenerator.LAYERS[i], new Material(untintedLayerTextureLocations[i]));
                }

                List<ItemTintSource> itemTintSources = tintFirstLayer ?
                        List.of(itemTintSource.get()) :
                        List.of();

                TintedLayerItemModelGenerator.LAYER_COUNT_TO_TEMPLATE.getOrDefault(untintedLayerTextureLocations.length, ModelTemplates.FLAT_ITEM)
                        .create(modelLocation, textureMapping, blockModelGenerators.modelOutput);

                return ItemModelUtils.plainModel(modelLocation);
            }
        }

        public GeneticCropWeightOverrideModelGenerator(Supplier<Holder<I>> itemGetter, Identifier fallbackTexture, Supplier<ItemTintSource> itemTintSourceSupplier, ModelData... data) {
            super();
            this.itemGetter = itemGetter;
            this.itemTintSourceSupplier = itemTintSourceSupplier;
            this.modelData = data;
            this.fallbackTexture = fallbackTexture;
        }

        @Override
        public void generate(BlockModelGenerators blockModelGenerators, ItemModelGenerators itemModelGenerators) {
            List<RangeSelectItemModel.Entry> entries = Arrays.stream(modelData)
                    .map(data -> new RangeSelectItemModel.Entry(
                            data.weightThreshold,
                            data.createModel(blockModelGenerators, itemModelGenerators, itemTintSourceSupplier)
                    ))
                    .toList();

            RangeSelectItemModel.Unbaked rangeSelectItemModel = new RangeSelectItemModel.Unbaked(
                    Optional.empty(),
                    new GeneticProperty(GeneticCropItem.WEIGHT_CHROMOSOME_INDEX),
                    1.0F,
                    entries,
                    Optional.of(ItemModelUtils.tintedModel(
                            ModelLocationUtils.getModelLocation(getHolder().value()),
                            itemTintSourceSupplier.get()))
            );

            blockModelGenerators.itemModelOutput.accept(getHolder().value(), rangeSelectItemModel);
        }

        private ItemModel.Unbaked createFallbackModel(BlockModelGenerators blockModelGenerators, ItemModelGenerators itemModelGenerators) {
            Identifier itemModelLocation = ModelLocationUtils.getModelLocation(getHolder().value());
            Identifier fallbackModelLocation = Identifier.fromNamespaceAndPath(itemModelLocation.getNamespace(), itemModelLocation.getPath() + "_fallback");

            TextureMapping textureMapping = new TextureMapping();
            textureMapping.put(TextureSlot.LAYER0, new Material(fallbackModelLocation));

            ModelTemplates.FLAT_ITEM.create(itemModelLocation, textureMapping, blockModelGenerators.modelOutput);

            return ItemModelUtils.plainModel(fallbackModelLocation);
        }
    }

    public static class BrassicaOleraceaModelGenerator<I extends Item> implements IModelGenerator<I> {
        private final Supplier<Holder<I>> itemGetter;
        private final ModelData modelData;

        public BrassicaOleraceaModelGenerator(Supplier<Holder<I>> itemGetter, ModelData modelData) {
            super();
            this.itemGetter = itemGetter;
            this.modelData = modelData;
        }

        public record ModelData(Identifier brassicaOleraceaTextureLocation,
                                Identifier cabbageTextureLocation,
                                Identifier kaleTextureLocation,
                                Identifier broccoliTextureLocation,
                                Identifier cauliflowerTextureLocation,
                                Identifier brusselsSproutsTextureLocation,
                                Identifier kohlrabiTextureLocation) {
            Map<BrassicaOleraceaItem.Variation, ItemModel.Unbaked> createModels(BlockModelGenerators blockModelGenerators, ItemModelGenerators itemModelGenerators) {
                return Map.of(
                        BrassicaOleraceaItem.Variation.BRASSICA_OLERACEA, createModel(blockModelGenerators, itemModelGenerators, brassicaOleraceaTextureLocation),
                        BrassicaOleraceaItem.Variation.CABBAGE, createModel(blockModelGenerators, itemModelGenerators, cabbageTextureLocation),
                        BrassicaOleraceaItem.Variation.KALE, createModel(blockModelGenerators, itemModelGenerators, kaleTextureLocation),
                        BrassicaOleraceaItem.Variation.BROCCOLI, createModel(blockModelGenerators, itemModelGenerators, broccoliTextureLocation),
                        BrassicaOleraceaItem.Variation.CAULIFLOWER, createModel(blockModelGenerators, itemModelGenerators, cauliflowerTextureLocation),
                        BrassicaOleraceaItem.Variation.BRUSSELS_SPROUTS, createModel(blockModelGenerators, itemModelGenerators, brusselsSproutsTextureLocation),
                        BrassicaOleraceaItem.Variation.KOHLRABI, createModel(blockModelGenerators, itemModelGenerators, kohlrabiTextureLocation)
                );
            }

            private ItemModel.Unbaked createModel(BlockModelGenerators blockModelGenerators, ItemModelGenerators itemModelGenerators, Identifier textureLocation) {
                Identifier modelLocation = textureLocation;
                TextureMapping textureMapping = new TextureMapping();
                textureMapping.put(TextureSlot.LAYER0, new Material(textureLocation));

                ModelTemplates.FLAT_ITEM.create(modelLocation, textureMapping, blockModelGenerators.modelOutput);
                return ItemModelUtils.plainModel(modelLocation);
            }
        }

        @Override
        public void generate(BlockModelGenerators blockModelGenerators, ItemModelGenerators itemModelGenerators) {
            Map<BrassicaOleraceaItem.Variation, ItemModel.Unbaked> entries = modelData.createModels(blockModelGenerators, itemModelGenerators);
            List<SelectItemModel.SwitchCase<BrassicaOleraceaItem.Variation>> cases = entries.entrySet().stream()
                    .map(entry -> new SelectItemModel.SwitchCase<>(
                            List.of(entry.key()),
                            entry.getValue()
                    ))
                    .toList();
            SelectItemModel.UnbakedSwitch<BrassicaOleraceaProperty, BrassicaOleraceaItem.Variation> selectItemModel = new SelectItemModel.UnbakedSwitch<>(
                    new BrassicaOleraceaProperty(),
                    cases
            );

            SelectItemModel.Unbaked rangeSelectItemModel = new SelectItemModel.Unbaked(
                    Optional.empty(),
                    selectItemModel,
                    Optional.of(entries.get(BrassicaOleraceaItem.Variation.BRASSICA_OLERACEA))
            );

            blockModelGenerators.itemModelOutput.accept(getHolder().value(), rangeSelectItemModel);
        }

        @Override
        public Holder<? extends I> getHolder() {
            return itemGetter.get();
        }
    }
}
