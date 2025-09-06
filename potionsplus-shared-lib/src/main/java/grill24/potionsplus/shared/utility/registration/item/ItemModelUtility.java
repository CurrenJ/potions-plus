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
import net.minecraft.client.renderer.item.BlockModelWrapper;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.RangeSelectItemModel;
import net.minecraft.client.renderer.item.SelectItemModel;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nullable;
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
        protected final ResourceLocation textureLocation;
        private final Supplier<Holder<I>> itemGetter;

        public SimpleItemModelGenerator(Supplier<Holder<I>> itemGetter, ResourceLocation textureLocation) {
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

            ResourceLocation modelLocation = ModelLocationUtils.getModelLocation(item);
            ResourceLocation texture = textureLocation == null ? modelLocation : textureLocation;

            ModelTemplates.FLAT_ITEM.create(modelLocation, new TextureMapping().put(TextureSlot.LAYER0, texture), itemModelGenerators.modelOutput);
            itemModelGenerators.itemModelOutput.accept(item, ItemModelUtils.plainModel(modelLocation));
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
        private final ResourceLocation parentModel;

        public SimpleBlockItemModelGenerator(Supplier<Holder<I>> itemGetter, Supplier<Holder<Block>> block) {
            this(itemGetter, block, null);
        }

        public SimpleBlockItemModelGenerator(Supplier<Holder<I>> itemGetter, Supplier<Holder<Block>> block, ResourceLocation parentModel) {
            super();
            this.itemGetter = itemGetter;
            this.block = block;
            this.parentModel = parentModel;
        }

        @Override
        public void generate(BlockModelGenerators blockModelGenerators, ItemModelGenerators itemModelGenerators) {
            ResourceLocation blockModelLocation = parentModel != null ? parentModel : ModelLocationUtils.getModelLocation(block.get().value());

            ItemModel.Unbaked itemModel = ItemModelUtils.plainModel(blockModelLocation);
            itemModelGenerators.itemModelOutput.accept(getHolder().value(), itemModel);
        }

        @Override
        public Holder<? extends I> getHolder() {
            return itemGetter.get();
        }
    }

    public static class ItemFromModelFileGenerator<I extends Item> implements IModelGenerator<I> {
        private final Supplier<Holder<I>> itemGetter;
        private final ResourceLocation modelFile;

        public ItemFromModelFileGenerator(Supplier<Holder<I>> itemGetter, ResourceLocation modelFile) {
            super();
            this.itemGetter = itemGetter;
            this.modelFile = modelFile;
        }

        @Override
        public void generate(BlockModelGenerators blockModelGenerators, ItemModelGenerators itemModelGenerators) {
            Item item = getHolder().value();

            itemModelGenerators.itemModelOutput.accept(item, ItemModelUtils.plainModel(modelFile));
        }

        @Override
        public Holder<? extends I> getHolder() {
            return itemGetter.get();
        }
    }

    public static class TintedLayerItemModelGenerator<I extends Item> implements IModelGenerator<I> {
        private final Supplier<Holder<I>> itemGetter;

        private final ItemTintSource itemTintSource;
        private final ResourceLocation tintedLayerTextureLocation;
        private final ResourceLocation[] untintedLayerTextureLocations;


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

        public TintedLayerItemModelGenerator(Supplier<Holder<I>> itemGetter, Supplier<ItemTintSource> itemTintSourceSupplier, ResourceLocation tintedLayerTextureLocation, ResourceLocation... untintedLayerTextureLocation) {
            super();
            this.itemGetter = itemGetter;
            this.itemTintSource = itemTintSourceSupplier.get();
            this.tintedLayerTextureLocation = tintedLayerTextureLocation;
            this.untintedLayerTextureLocations = untintedLayerTextureLocation;
        }

        @Override
        public void generate(BlockModelGenerators blockModelGenerators, ItemModelGenerators itemModelGenerators) {
            Item item = getHolder().value();

            ResourceLocation modelLocation = ModelLocationUtils.getModelLocation(item);
            TextureMapping textureMapping = new TextureMapping();

            for (int i = 0; i < untintedLayerTextureLocations.length + 1 && i < LAYERS.length; i++) {
                if (i == 0) {
                    textureMapping.put(LAYERS[i], tintedLayerTextureLocation);
                } else {
                    textureMapping.put(LAYERS[i], untintedLayerTextureLocations[i - 1]);
                }
            }

            LAYER_COUNT_TO_TEMPLATE.getOrDefault(untintedLayerTextureLocations.length, ModelTemplates.FLAT_ITEM)
                    .create(modelLocation, textureMapping, itemModelGenerators.modelOutput);

            BlockModelWrapper.Unbaked model = new BlockModelWrapper.Unbaked(
                    modelLocation,
                    List.of(itemTintSource)
            );

            itemModelGenerators.itemModelOutput.accept(item, model);
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

        private final ResourceLocation fallbackTexture;

        @Override
        public Holder<? extends I> getHolder() {
            return itemGetter.get();
        }

        public record ModelData(float weightThreshold,
                                boolean tintFirstLayer,
                                ResourceLocation... untintedLayerTextureLocations) {
            BlockModelWrapper.Unbaked createModel(ItemModelGenerators itemModelGenerators, Supplier<ItemTintSource> itemTintSource) {
                if (untintedLayerTextureLocations.length == 0) {
                    throw new IllegalArgumentException("At least one untinted layer texture location must be provided.");
                }

                ResourceLocation modelLocation = untintedLayerTextureLocations[0];
                TextureMapping textureMapping = new TextureMapping();

                for (int i = 0; i < untintedLayerTextureLocations.length; i++) {
                    textureMapping.put(TintedLayerItemModelGenerator.LAYERS[i], untintedLayerTextureLocations[i]);
                }

                List<ItemTintSource> itemTintSources = tintFirstLayer ?
                        List.of(itemTintSource.get()) :
                        List.of();

                TintedLayerItemModelGenerator.LAYER_COUNT_TO_TEMPLATE.getOrDefault(untintedLayerTextureLocations.length, ModelTemplates.FLAT_ITEM)
                        .create(modelLocation, textureMapping, itemModelGenerators.modelOutput);

                return new BlockModelWrapper.Unbaked(
                        modelLocation,
                        itemTintSources
                );
            }
        }

        public GeneticCropWeightOverrideModelGenerator(Supplier<Holder<I>> itemGetter, ResourceLocation fallbackTexture, Supplier<ItemTintSource> itemTintSourceSupplier, ModelData... data) {
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
                            data.createModel(itemModelGenerators, itemTintSourceSupplier)
                    ))
                    .toList();

            RangeSelectItemModel.Unbaked rangeSelectItemModel = new RangeSelectItemModel.Unbaked(
                    new GeneticProperty(GeneticCropItem.WEIGHT_CHROMOSOME_INDEX),
                    1,
                    entries,
                    Optional.of(new BlockModelWrapper.Unbaked(
                            ModelLocationUtils.getModelLocation(getHolder().value()),
                            List.of(itemTintSourceSupplier.get())))
            );

            itemModelGenerators.itemModelOutput.accept(getHolder().value(), rangeSelectItemModel);
        }

        private BlockModelWrapper.Unbaked createFallbackModel(ItemModelGenerators itemModelGenerators) {
            ResourceLocation itemModelLocation = ModelLocationUtils.getModelLocation(getHolder().value());
            ResourceLocation fallbackModelLocation = ResourceLocation.fromNamespaceAndPath(itemModelLocation.getNamespace(), itemModelLocation.getPath() + "_fallback");

            TextureMapping textureMapping = new TextureMapping();
            textureMapping.put(TextureSlot.LAYER0, fallbackModelLocation);

            ModelTemplates.FLAT_ITEM.create(itemModelLocation, textureMapping, itemModelGenerators.modelOutput);

            return new BlockModelWrapper.Unbaked(
                    fallbackModelLocation,
                    List.of()
            );
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

        public record ModelData(ResourceLocation brassicaOleraceaTextureLocation,
                                ResourceLocation cabbageTextureLocation,
                                ResourceLocation kaleTextureLocation,
                                ResourceLocation broccoliTextureLocation,
                                ResourceLocation cauliflowerTextureLocation,
                                ResourceLocation brusselsSproutsTextureLocation,
                                ResourceLocation kohlrabiTextureLocation) {
            Map<BrassicaOleraceaItem.Variation, BlockModelWrapper.Unbaked> createModels(ItemModelGenerators itemModelGenerators) {
                return Map.of(
                        BrassicaOleraceaItem.Variation.BRASSICA_OLERACEA, createModel(itemModelGenerators, brassicaOleraceaTextureLocation),
                        BrassicaOleraceaItem.Variation.CABBAGE, createModel(itemModelGenerators, cabbageTextureLocation),
                        BrassicaOleraceaItem.Variation.KALE, createModel(itemModelGenerators, kaleTextureLocation),
                        BrassicaOleraceaItem.Variation.BROCCOLI, createModel(itemModelGenerators, broccoliTextureLocation),
                        BrassicaOleraceaItem.Variation.CAULIFLOWER, createModel(itemModelGenerators, cauliflowerTextureLocation),
                        BrassicaOleraceaItem.Variation.BRUSSELS_SPROUTS, createModel(itemModelGenerators, brusselsSproutsTextureLocation),
                        BrassicaOleraceaItem.Variation.KOHLRABI, createModel(itemModelGenerators, kohlrabiTextureLocation)
                );
            }

            private BlockModelWrapper.Unbaked createModel(ItemModelGenerators itemModelGenerators, ResourceLocation textureLocation) {
                ResourceLocation modelLocation = textureLocation;
                TextureMapping textureMapping = new TextureMapping();
                textureMapping.put(TextureSlot.LAYER0, textureLocation);

                ModelTemplates.FLAT_ITEM.create(modelLocation, textureMapping, itemModelGenerators.modelOutput);
                return new BlockModelWrapper.Unbaked(modelLocation, List.of());
            }
        }

        @Override
        public void generate(BlockModelGenerators blockModelGenerators, ItemModelGenerators itemModelGenerators) {
            Map<BrassicaOleraceaItem.Variation, BlockModelWrapper.Unbaked> entries = modelData.createModels(itemModelGenerators);
            List<SelectItemModel.SwitchCase<BrassicaOleraceaItem.Variation>> cases = entries.entrySet().stream()
                    .map(entry -> new SelectItemModel.SwitchCase<>(
                            List.of(entry.getKey()),
                            entry.getValue()
                    ))
                    .toList();
            SelectItemModel.UnbakedSwitch<BrassicaOleraceaProperty, BrassicaOleraceaItem.Variation> selectItemModel = new SelectItemModel.UnbakedSwitch<>(
                    new BrassicaOleraceaProperty(),
                    cases
            );

            SelectItemModel.Unbaked rangeSelectItemModel = new SelectItemModel.Unbaked(
                    selectItemModel,
                    Optional.of(entries.get(BrassicaOleraceaItem.Variation.BRASSICA_OLERACEA))
            );

            itemModelGenerators.itemModelOutput.accept(getHolder().value(), rangeSelectItemModel);
        }

        @Override
        public Holder<? extends I> getHolder() {
            return itemGetter.get();
        }
    }
}
