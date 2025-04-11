package grill24.potionsplus.utility.registration;

import com.google.gson.JsonObject;
import grill24.potionsplus.core.PotionsPlus;
import grill24.potionsplus.event.resources.ClientModifyFileResourceStackEvent;
import grill24.potionsplus.event.resources.ClientModifyFileResourcesEvent;
import grill24.potionsplus.event.resources.TextResourceModification;
import grill24.potionsplus.event.resources.IResourceModification;
import grill24.potionsplus.utility.ResourceUtility;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.Property;
import oshi.util.tuples.Pair;

import java.util.*;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import static grill24.potionsplus.utility.Utility.ppId;

public class RuntimeTextureVariantModelGenerator extends RuntimeBlockModelGenerator {
    private final ResourceLocation baseModel;
    private final PropertyTexVariant[] propertyTexVariants;

    public RuntimeTextureVariantModelGenerator(Supplier<Holder<Block>> blockHolder, ResourceLocation baseModel, PropertyTexVariant... propertiesToGenerateTextureVariantsFor) {
        super(blockHolder);
        this.baseModel = baseModel;
        this.propertyTexVariants = propertiesToGenerateTextureVariantsFor;
    }

    public PropertyTexVariant[] getPropertyTexVariants() {
        return propertyTexVariants;
    }

    @Override
    public void generate(final ClientModifyFileResourcesEvent event) {
        for (IResourceModification resource :
                get(getHolder(), baseModel, propertyTexVariants)) {
            event.inject(resource);
        }
    }

    @Override
    public void generate(final ClientModifyFileResourceStackEvent event) {
        for (IResourceModification resource :
                get(getHolder(), baseModel, propertyTexVariants)) {
            event.inject(resource);
        }
    }

    public record PropertyTexVariant(
            Property<Integer> property,
            Supplier<Collection<Holder<Block>>> blocks,
            String textureKeyInBaseModel
    ) {
        public static PropertyTexVariant fromKey(Property<Integer> property, TagKey<Block> tagKey, String textureKeyInBaseModel) {
            return new PropertyTexVariant(property, () -> BuiltInRegistries.BLOCK.getOrCreateTag(tagKey).stream().toList(), textureKeyInBaseModel);
        }
    }

    private static TextResourceModification[] get(Holder<? extends Block> blockHolder, ResourceLocation baseModelShortId, PropertyTexVariant... propertiesToGenerateTextureVariantsFor) {
        if (blockHolder == null) {
            PotionsPlus.LOGGER.warn("Holder is null or not bound during runtime model injection. | {}", Arrays.toString(propertiesToGenerateTextureVariantsFor));
            return new TextResourceModification[0];
        }

        List<TextResourceModification> resources = new ArrayList<>();
        List<PropertyTexVariant> properties = new ArrayList<>(Arrays.asList(propertiesToGenerateTextureVariantsFor));

        ResourceLocation baseModelLongId = ResourceLocation.fromNamespaceAndPath(
                baseModelShortId.getNamespace(),
                "models/" + baseModelShortId.getPath() + ".json"
        );

        List<List<Integer>> permutations = generatePermutations(properties.stream().map(p -> new Pair<>(p.property(), p.property().getPossibleValues())).toList());
        // Variant key, model name
        List<Pair<String, String>> blockStateVariantModels = new ArrayList<>();
        for (List<Integer> permutation : permutations) {
            // (Variant texture, texture key to replace from the base model)[]
            List<Pair<Holder<Block>, String>> subblocks = new ArrayList<>();
            // Convert the blockstate permutation into the blocks we will use as textures for that permutation's model
            StringBuilder blockStatePermutationKey = new StringBuilder();
            for (int i = 0; i < permutation.size(); i++) {
                PropertyTexVariant variant = properties.get(i);

                final int value = permutation.get(i);
                Optional<Holder<Block>> block = variant.blocks().get().stream().skip(value).findFirst();
                block.ifPresent(b -> subblocks.add(new Pair<>(b, variant.textureKeyInBaseModel())));

                // Create the blockstate permutation key
                if (i != 0) {
                    blockStatePermutationKey.append(",");
                }
                blockStatePermutationKey.append(variant.property().getName()).append("=").append(value);
            }

            Optional<String> subModelSuffix = subblocks.stream().map(h -> h.getA().getKey().location().getPath()).reduce(String::concat);
            if (subModelSuffix.isPresent()) {
                // Split base model on '.' and replace the last part with the submodel suffix - basically add our suffix to model name
                ResourceLocation subModelLongId = ResourceLocation.fromNamespaceAndPath(
                        baseModelLongId.getNamespace(),
                        baseModelLongId.getPath().substring(0, baseModelLongId.getPath().lastIndexOf('.')) + "_" + subModelSuffix.get() + ".json"
                );
                ResourceLocation subModelShortId = ResourceLocation.fromNamespaceAndPath(
                        baseModelShortId.getNamespace(),
                        baseModelShortId.getPath() + "_" + subModelSuffix.get()
                );

                // Store blockstate variant info for blockstate generation later
                blockStateVariantModels.add(new Pair<>(blockStatePermutationKey.toString(), subModelShortId.toString()));

                resources.add(new TextResourceModification(
                        baseModelLongId, subModelLongId,
                        TextResourceModification.jsonTransform(json -> {
                            json = new JsonObject();
                            json.addProperty("parent", baseModelShortId.toString());

                            JsonObject textures = new JsonObject();
                            for (Pair<Holder<Block>, String> block : subblocks) {
                                ResourceLocation blockTexture = ResourceUtility.getDefaultTexture(block.getA()).orElse(ppId("textures/item/unknown.png"));
                                textures.addProperty(block.getB(), blockTexture.toString());
                            }

                            json.add("textures", textures);
                            return json;
                        })));
            } else {
                blockStateVariantModels.add(new Pair<>(blockStatePermutationKey.toString(), baseModelShortId.toString()));
            }
        }

        generateBlockstate(blockHolder, blockStateVariantModels, resources);

        return resources.toArray(new TextResourceModification[0]);
    }

    private static void generateBlockstate(Holder<? extends Block> blockHolder, List<Pair<String, String>> blockStateVariantModels, List<TextResourceModification> resources) {
        // Blockstates
        UnaryOperator<String> generateBlockstatesJson = s -> {
            JsonObject blockstates = new JsonObject();
            JsonObject variants = new JsonObject();

            // For permutations
            for (Pair<String, String> pair : blockStateVariantModels) {
                JsonObject variant = new JsonObject();
                variant.addProperty("model", pair.getB());
                variants.add(pair.getA(), variant);
            }
            blockstates.add("variants", variants);

            return blockstates.toString();
        };

        ResourceLocation blockStateLongId = ResourceLocation.fromNamespaceAndPath(
                blockHolder.getKey().location().getNamespace(),
                "blockstates/" + blockHolder.getKey().location().getPath() + ".json"
        );
        resources.add(new TextResourceModification(
                blockStateLongId, blockStateLongId,
                generateBlockstatesJson
        ));
    }

    private static List<List<Integer>> generatePermutations(List<Pair<Property<Integer>, Collection<Integer>>> properties) {
        List<List<Integer>> permutations = new ArrayList<>();
        generatePermutationsRecursive(properties, 0, new ArrayList<>(), permutations);
        return permutations;
    }

    /**
     * Recursive function to generate all permutations of the given properties.
     *
     * @param properties
     * @param index
     * @param current
     * @param result
     */
    private static void generatePermutationsRecursive(
            List<Pair<Property<Integer>, Collection<Integer>>> properties,
            int index,
            List<Integer> current,
            List<List<Integer>> result) {
        if (index == properties.size()) {
            result.add(new ArrayList<>(current));
            return;
        }

        Pair<Property<Integer>, Collection<Integer>> property = properties.get(index);
        for (Integer value : property.getB()) {
            current.add(value);
            generatePermutationsRecursive(properties, index + 1, current, result);
            current.remove(current.size() - 1);
        }
    }
}
