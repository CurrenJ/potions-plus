package grill24.potionsplus.utility.registration;

import com.google.gson.JsonObject;
import grill24.potionsplus.core.PotionsPlus;
import grill24.potionsplus.debug.Debug;
import grill24.potionsplus.event.runtimeresource.GenerateRuntimeResourceInjectionsCacheEvent;
import grill24.potionsplus.event.runtimeresource.modification.IResourceModification;
import grill24.potionsplus.event.runtimeresource.modification.TextResourceModification;
import grill24.potionsplus.event.runtimeresource.modification.TextureResourceModification;
import grill24.potionsplus.item.RuntimeVariantItemDataComponent;
import grill24.potionsplus.utility.ResourceUtility;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.NotNull;
import oshi.util.tuples.Pair;

import java.util.*;
import java.util.function.Supplier;

import static grill24.potionsplus.utility.Utility.ppId;

public class RuntimeTextureVariantModelGenerator extends RuntimeBlockModelGenerator {
    private final BaseModel<?>[] baseModel;
    private final PropertyTexVariant[] propertyTexVariants;

    public record BaseModel<T extends Comparable<T>>(Optional<Property<T>> property,
                                                     Optional<T> propertyValue,
                                                     ResourceLocation baseModelShortId,
                                                     TextureResourceModification.OverlayImage... overlayTextureLongIds) {
        public String getBlockStateKey() {
            if (property.isEmpty() || propertyValue.isEmpty()) {
                return "";
            }
            return property.get().getName().toLowerCase() + "=" + propertyValue.get().toString().toLowerCase();
        }

        public String getRuntimeModelNameSuffix() {
            if (property.isEmpty() || propertyValue.isEmpty()) {
                return "";
            }
            return property.get().getName().toLowerCase() + "_" + propertyValue.get().toString().toLowerCase();
        }

        public BaseModel(ResourceLocation baseModelShortId) {
            this(Optional.empty(), Optional.empty(), baseModelShortId, new TextureResourceModification.OverlayImage[0]);
        }

        public BaseModel(ResourceLocation baseModelShortId, TextureResourceModification.OverlayImage... overlayTextureLongIds) {
            this(Optional.empty(), Optional.empty(), baseModelShortId, overlayTextureLongIds);
        }
    }

    private final Map<Property<Integer>, Map<Holder<Block>, Integer>> blockMappings = new HashMap<>();
    private final Map<Property<Integer>, Map<Integer, Holder<Block>>> blockMappingsByPropertyValue = new HashMap<>();

    private Map<Property<Integer>, Map<Holder<Item>, Integer>> itemMappings = new HashMap<>();

    public RuntimeTextureVariantModelGenerator(Supplier<Holder<Block>> blockHolder, ResourceLocation baseModelShortId, PropertyTexVariant... propertiesToGenerateTextureVariantsFor) {
        super(blockHolder);
        this.baseModel = new BaseModel[]{new BaseModel<>(baseModelShortId)};
        this.propertyTexVariants = propertiesToGenerateTextureVariantsFor;
    }

    public RuntimeTextureVariantModelGenerator(Supplier<Holder<Block>> blockHolder, BaseModel<?>[] baseModels, PropertyTexVariant... propertiesToGenerateTextureVariantsFor) {
        super(blockHolder);
        this.baseModel = baseModels;
        this.propertyTexVariants = propertiesToGenerateTextureVariantsFor;
    }

    public RuntimeTextureVariantModelGenerator(Supplier<Holder<Block>> blockHolder, BaseModel<?> baseModels, PropertyTexVariant... propertiesToGenerateTextureVariantsFor) {
        this(blockHolder, new BaseModel[]{baseModels}, propertiesToGenerateTextureVariantsFor);
    }

    @SafeVarargs
    public static @NotNull InteractionResult trySetTextureVariant(Block block, ItemStack stack, BlockState state, LevelAccessor level, BlockPos pos, Property<Integer>... textureVariantProperties) {
        for (AbstractRegistererBuilder<?, ?> gen : RegistrationUtility.BUILDERS) {
            if (gen.getHolder() != null && gen.getHolder().value() == block
                    && gen.getRuntimeModelGenerator() instanceof RuntimeTextureVariantModelGenerator textureGen) {
                for (Property<Integer> property : textureVariantProperties) {
                    int value = textureGen.getValueForProperty(property, stack);
                    if (value != -1) {
                        BlockState newState = state.setValue(property, value);
                        level.setBlock(pos, newState, 3);
                        return InteractionResult.SUCCESS;
                    }
                }

            }
        }

        return InteractionResult.PASS;
    }

    @SafeVarargs
    public static BlockState getTextureVariantBlockState(Block block, ItemStack stack, BlockState state, Property<Integer>... textureVariantProperties) {
        Optional<BlockState> optionalState = tryGetTextureVariantBlockState(block, stack, state, textureVariantProperties);
        return optionalState.orElse(state);
    }

    @SafeVarargs
    public static Optional<BlockState> tryGetTextureVariantBlockState(Block block, ItemStack stack, BlockState state, Property<Integer>... textureVariantProperties) {
        for (AbstractRegistererBuilder<?, ?> gen : RegistrationUtility.BUILDERS) {
            if (gen.getHolder() != null && gen.getHolder().value() == block
                    && gen.getRuntimeModelGenerator() instanceof RuntimeTextureVariantModelGenerator textureGen) {
                for (Property<Integer> property : textureVariantProperties) {
                    int value = textureGen.getValueForProperty(property, stack);
                    if (value != -1) {
                        return Optional.of(state.setValue(property, value));
                    }
                }
            }
        }

        return Optional.empty();
    }

    public static ItemStack getTextureVariantItemStack(BlockState variantBlockState, Property<Integer> textureVariantProperties)
    {
        return RuntimeVariantItemDataComponent.getStackFromBlockState(variantBlockState, textureVariantProperties);
    }

    public int getValueForProperty(Property<Integer> property, Holder<Item> item) {
        if (blockMappings.containsKey(property)) {
            Map<Holder<Item>, Integer> itemToPropertyValueMap = itemMappings.get(property);
            if (itemToPropertyValueMap.containsKey(item)) {
                return itemToPropertyValueMap.get(item);
            }
        }

        // Min of possible values
        return -1;
    }

    public int getValueForProperty(Property<Integer> property, ItemStack stack) {
        Holder<Item> itemHolder = stack.getItemHolder();
        return getValueForProperty(property, itemHolder);
    }

    @Override
    public void generateClient(final GenerateRuntimeResourceInjectionsCacheEvent event) {
        generateCommon();

        IResourceModification[] modifications = getModifications(getHolder(), baseModel, propertyTexVariants);
        event.addModifications(modifications);
    }

    @Override
    public void generateCommon() {
        this.blockMappings.clear();
        for (PropertyTexVariant propertyTexVariant : propertyTexVariants) {
            // Sort low to high
            List<Integer> possibleValues = propertyTexVariant.property().getPossibleValues().stream()
                    .sorted()
                    .toList();
            int index = 0;
            List<Holder<Block>> blocks = propertyTexVariant.blocks().get().stream().toList();
            for (Integer value : possibleValues) {
                // Get the block from the property value
                if (blocks.size() > index) {
                    Holder<Block> block = blocks.get(index);
                    blockMappings.computeIfAbsent(propertyTexVariant.property(), k -> new HashMap<>()).put(block, value);
                    blockMappingsByPropertyValue.computeIfAbsent(propertyTexVariant.property(), k -> new HashMap<>()).put(value, block);
                }
                index++;
            }
        }

        this.itemMappings = toItemMappings(blockMappings);
    }

    private static Map<Property<Integer>, Map<Holder<Item>, Integer>> toItemMappings(Map<Property<Integer>, Map<Holder<Block>, Integer>> blockMappings) {
        Map<Property<Integer>, Map<Holder<Item>, Integer>> itemMappings = new HashMap<>();
        for (Map.Entry<Property<Integer>, Map<Holder<Block>, Integer>> entry : blockMappings.entrySet()) {
            Property<Integer> property = entry.getKey();
            Map<Holder<Block>, Integer> blockToValueMap = entry.getValue();

            Map<Holder<Item>, Integer> itemToValueMap = new HashMap<>();
            for (Map.Entry<Holder<Block>, Integer> blockEntry : blockToValueMap.entrySet()) {
                Holder<Block> block = blockEntry.getKey();
                int value = blockEntry.getValue();

                // Get the item from the block
                Optional<ResourceKey<Item>> itemKey = BuiltInRegistries.ITEM.getResourceKey(block.value().asItem());
                if (itemKey.isPresent()) {
                    Optional<Holder.Reference<Item>> item = BuiltInRegistries.ITEM.get(itemKey.get());
                    if (item.isPresent()) {
                        itemToValueMap.put(item.get(), value);
                    } else {
                        PotionsPlus.LOGGER.warn("Item is null or not bound during runtime model mapping population. {}", itemKey);
                    }
                }

            }
            itemMappings.put(property, itemToValueMap);
        }
        return itemMappings;
    }

    /**
     * A record to hold the property, the blocks to use for the texture variants, and the texture key in the base model.
     *
     * @param property              The block property to use for the texture variants. Will be permuted with the properties from other PropertyTexVariants.
     * @param blocks                The blocks to use for the texture variants.
     * @param textureKeyInBaseModel The texture key in the base model to replace with the block texture.
     */
    public record PropertyTexVariant(
            Property<Integer> property,
            Supplier<Collection<Holder<Block>>> blocks,
            String textureKeyInBaseModel
    ) {
        private static Supplier<Collection<Holder<Block>>> tagToBlocks(TagKey<Block> tagKey) {
            return () -> {
                Optional<HolderSet.Named<Block>> blockHolder = BuiltInRegistries.BLOCK.get(tagKey);
                if (blockHolder.isPresent()) {
                    return blockHolder.get().stream().toList();
                } else {
                    PotionsPlus.LOGGER.warn("Tag is null or not bound during runtime model mapping population. {}", tagKey);
                    return Collections.emptyList();
                }
            };
        }

        public static PropertyTexVariant fromTag(Property<Integer> property, TagKey<Block> tagKey, String textureKeyInBaseModel) {
            return new PropertyTexVariant(property, tagToBlocks(tagKey), textureKeyInBaseModel);
        }

        public static PropertyTexVariant fromTagWithOverlay(Property<Integer> property, TagKey<Block> tagKey, String textureKeyInBaseModel) {
            return new PropertyTexVariant(property, tagToBlocks(tagKey), textureKeyInBaseModel);
        }

        @Override
        public String toString() {
            return "PropertyTexVariant{" +
                    "property=" + property.getName() +
                    ", blocks=" + blocks.get() +
                    ", textureKeyInBaseModel='" + textureKeyInBaseModel + '\'' +
                    '}';
        }
    }

    public record AdditionalModel(Holder<Block> blockHolder, ResourceLocation texLongId, ResourceLocation texShortId,
                                  String baseTextureKey) {
    }

    private IResourceModification[] getModifications(Holder<? extends Block> blockHolder, BaseModel<?>[] baseModels, PropertyTexVariant... propertiesToGenerateTextureVariantsFor) {
        if (blockHolder == null) {
            PotionsPlus.LOGGER.warn("Holder is null or not bound during runtime model injection. | {}", Arrays.toString(propertiesToGenerateTextureVariantsFor));
            return new TextResourceModification[0];
        }

        List<IResourceModification> modifications = new ArrayList<>();
        List<PropertyTexVariant> properties = new ArrayList<>(Arrays.asList(propertiesToGenerateTextureVariantsFor));

        // Generate all permutations of properties that were provided
        List<List<Integer>> permutations = ClientRuntimeTextureVariantUtility.generatePermutations(properties.stream().map(p -> new Pair<>(p.property(), p.property().getPossibleValues())).toList());
        // Variant blockstate key (e.g. "lit=true,some_int_property=5"), model short id (e.g "minecraft:block/cube_all")
        List<Pair<String, String>> blockStateVariantModels = new ArrayList<>();
        long sysTime = System.currentTimeMillis();
        for (List<Integer> permutation : permutations) {
            ModelsAndTextureGenerationData modelsAndTextureGenerationData = prepareGenerationData(propertiesToGenerateTextureVariantsFor, permutation, properties);

            for (BaseModel<?> baseModel : baseModels) {
                StringBuilder blockStatePermutationKey = new StringBuilder(modelsAndTextureGenerationData.blockStatePermutationKey());
                if (baseModel.property.isPresent() && baseModel.propertyValue.isPresent()) {
                    if (!blockStatePermutationKey.isEmpty()) {
                        blockStatePermutationKey.append(",");
                    }
                    blockStatePermutationKey.append(baseModel.getBlockStateKey());
                }

                List<IResourceModification> generatedModelsAndTextures = generateModelsAndTextures(
                        baseModel,
                        modelsAndTextureGenerationData.additionalModels(),
                        blockStateVariantModels,
                        blockStatePermutationKey.toString());
                modifications.addAll(generatedModelsAndTextures);
            }
        }
        if (Debug.DEBUG_RUNTIME_RESOURCE_TIME) {
            PotionsPlus.LOGGER.info("Generated {} modifications in {}ms", permutations.size(), System.currentTimeMillis() - sysTime);
        }

        // Generate the blockstate that maps the blockstate permutations to the models
        IResourceModification generatedBlockState = ClientRuntimeTextureVariantUtility.generateBlockstate(blockHolder, blockStateVariantModels);
        modifications.add(generatedBlockState);

        // Generate the client item definition that maps the blockstate permutations to the item model
        IResourceModification generatedClientItemDefinition = ClientRuntimeTextureVariantUtility.generateClientItemDefinition(blockHolder, blockStateVariantModels);
        modifications.add(generatedClientItemDefinition);

        return modifications.toArray(new IResourceModification[0]);
    }

    private @NotNull RuntimeTextureVariantModelGenerator.ModelsAndTextureGenerationData prepareGenerationData(PropertyTexVariant[] propertiesToGenerateTextureVariantsFor, List<Integer> permutation, List<PropertyTexVariant> properties) {
        // (Variant texture, texture key to replace from the base model)[]
        List<AdditionalModel> additionalModels = new ArrayList<>();
        // Convert the blockstate permutation into the blocks we will use as textures for that permutation's model
        StringBuilder blockStatePermutationKey = new StringBuilder();
        for (int i = 0; i < permutation.size(); i++) {
            PropertyTexVariant variant = properties.get(i);
            final int propertyValue = permutation.get(i);

            // Create the blockstate key representing all the property values of this permutation.
            // Used in the generated blockstate to map the blockstate to the model.
            if (!blockStatePermutationKey.isEmpty()) {
                blockStatePermutationKey.append(",");
            }
            blockStatePermutationKey.append(variant.property().getName()).append("=").append(propertyValue);


            Collection<Holder<Block>> variantBlocks = variant.blocks().get();
            Optional<Holder<Block>> variantBlock = Optional.ofNullable(blockMappingsByPropertyValue.getOrDefault(variant.property(), new HashMap<>()).getOrDefault(propertyValue, null));
            // Be safe and check if the block is null or not bound
            if (variantBlock.isEmpty()) {
                if (propertyValue < variantBlocks.size()) {
                    PotionsPlus.LOGGER.warn("Block is null or not bound during runtime model injection. | {}", Arrays.toString(propertiesToGenerateTextureVariantsFor));
                }
                continue;
            }

            // Get the texture to use as an override in the base model.
            // The utility method here searches for the first texture it can find in the provided block's model.
            ResourceLocation textureShortId = ResourceUtility.getDefaultTexture(variantBlock.get()).orElse(ppId("textures/item/unknown.png"));
            ResourceLocation textureLongId = ResourceLocation.fromNamespaceAndPath(
                    textureShortId.getNamespace(),
                    "textures/" + textureShortId.getPath() + ".png"
            );

            // Populate additional models, for each property value in the permutation, so we can generate the model later.
            additionalModels.add(new AdditionalModel(variantBlock.get(), textureLongId, textureShortId, variant.textureKeyInBaseModel()));
        }
        return new ModelsAndTextureGenerationData(additionalModels, blockStatePermutationKey.toString());
    }

    private record ModelsAndTextureGenerationData(List<AdditionalModel> additionalModels,
                                                  String blockStatePermutationKey) {
    }

    private static List<IResourceModification> generateModelsAndTextures(BaseModel<?> baseModel, List<AdditionalModel> subblocks, List<Pair<String, String>> blockStateVariantModels, String blockStatePermutationKey) {
        List<IResourceModification> generatedModelsAndTextures = new ArrayList<>();

        ResourceLocation baseModelLongId = ResourceLocation.fromNamespaceAndPath(
                baseModel.baseModelShortId().getNamespace(),
                "models/" + baseModel.baseModelShortId().getPath() + ".json"
        );

        Optional<String> blockTextureSuffix = subblocks.stream().map(modelData -> modelData.blockHolder().getKey().location().getPath()).reduce((a, b) -> a + "_" + b);
        if (blockTextureSuffix.isPresent()) {
            // Split base model on '.' and replace the last part with the submodel suffix - basically add our suffix to model name
            String suffix = baseModel.getRuntimeModelNameSuffix();
            ResourceLocation subModelLongId = ResourceLocation.fromNamespaceAndPath(
                    baseModelLongId.getNamespace(),
                    baseModelLongId.getPath().substring(0, baseModelLongId.getPath().lastIndexOf('.')) + "_" + blockTextureSuffix.get() + "_" + suffix + ".json"
            );
            ResourceLocation subModelShortId = ResourceLocation.fromNamespaceAndPath(
                    baseModel.baseModelShortId().getNamespace(),
                    baseModel.baseModelShortId().getPath() + "_" + blockTextureSuffix.get() + "_" + suffix
            );

            // Store blockstate variant info for blockstate generation later
            blockStateVariantModels.add(new Pair<>(blockStatePermutationKey.toString(), subModelShortId.toString()));


            // Some textures are generated at runtime, so we need to modify the base model to use the generated texture.
            // Generate the runtime texture for the block if a generator is provided.
            List<AdditionalModel> modelsWithReplacementTextures = new ArrayList<>();
            for (AdditionalModel modelData : subblocks) {
                if (baseModel.overlayTextureLongIds().length > 0) {
                    ResourceLocation generatedTextureLongId = ResourceLocation.fromNamespaceAndPath(
                            baseModelLongId.getNamespace(),
                            "textures/" + subModelShortId.getPath() + "_" + modelData.baseTextureKey + ".png"
                    );
                    ResourceLocation generatedTextureShortId = ResourceLocation.fromNamespaceAndPath(
                            baseModel.baseModelShortId().getNamespace(),
                            subModelShortId.getPath() + "_" + modelData.baseTextureKey
                    );

                    // Add the texture resource modification to the list
                    generatedModelsAndTextures.add(new TextureResourceModification(
                            modelData.texLongId(), generatedTextureLongId,
                            TextureResourceModification.overlay(modelData.texLongId(), baseModel.overlayTextureLongIds())
                    ));

                    // Add the model data with the generated texture to the list
                    modelsWithReplacementTextures.add(new AdditionalModel(modelData.blockHolder(), generatedTextureLongId, generatedTextureShortId, modelData.baseTextureKey()));
                } else {
                    // If no overlay texture is provided, just use the original texture
                    modelsWithReplacementTextures.add(modelData);
                }
            }

            // Create a new model for the blockstate permutation
            generatedModelsAndTextures.add(new TextResourceModification(
                    baseModelLongId, subModelLongId,
                    TextResourceModification.jsonTransform(json -> {
                        json = new JsonObject();
                        json.addProperty("parent", baseModel.baseModelShortId().toString());

                        JsonObject textures = new JsonObject();
                        for (AdditionalModel modelData : modelsWithReplacementTextures) {
                            textures.addProperty(modelData.baseTextureKey(), modelData.texShortId().toString());
                        }

                        if (Debug.DEBUG_RUNTIME_RESOURCE_INJECTION) {
                            PotionsPlus.LOGGER.info("NEW MODEL: {} | {}", subModelLongId, textures);
                        }

                        json.add("textures", textures);
                        return json;
                    })));

        } else {
            // Else, default model for the blockstate permutation bc every valid blockstate permutation must have a model
            blockStateVariantModels.add(new Pair<>(blockStatePermutationKey, baseModel.baseModelShortId().toString()));
        }

        return generatedModelsAndTextures;
    }

}
