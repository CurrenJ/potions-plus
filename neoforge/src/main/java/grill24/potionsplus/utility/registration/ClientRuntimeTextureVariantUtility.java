package grill24.potionsplus.utility.registration;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import grill24.potionsplus.core.PotionsPlus;
import grill24.potionsplus.event.runtimeresource.modification.IResourceModification;
import grill24.potionsplus.event.runtimeresource.modification.TextResourceModification;
import grill24.potionsplus.item.modelproperty.RuntimeTextureVariantProperty;
import net.minecraft.client.renderer.item.BlockModelWrapper;
import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.client.renderer.item.RangeSelectItemModel;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.Property;
import oshi.util.tuples.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;

public class ClientRuntimeTextureVariantUtility {
    static IResourceModification generateClientItemDefinition(Holder<? extends Block> blockHolder, List<Pair<String, String>> blockStateVariantModels) {
        UnaryOperator<String> generateItemDefinitionJson = TextResourceModification.jsonTransform(json -> {
            List<RangeSelectItemModel.Entry> entries = new ArrayList<>();
            for (Pair<String, String> pair : blockStateVariantModels) {
                float threshold = (float) entries.size() / (blockStateVariantModels.size() - 1); // Normalize threshold to [0, 1], as expected in RangeSelectItemModel.Entry
                RangeSelectItemModel.Entry entry = new RangeSelectItemModel.Entry(
                        threshold,
                        new BlockModelWrapper.Unbaked(
                                ResourceLocation.parse(pair.getB()),
                                List.of()
                        )
                );
                entries.add(entry);
            }

            RangeSelectItemModel.Unbaked rangeSelectItemModel = new RangeSelectItemModel.Unbaked(
                    new RuntimeTextureVariantProperty(),
                    1,
                    entries,
                    Optional.empty()
            );

            ClientItem clientItem = new ClientItem(rangeSelectItemModel, ClientItem.Properties.DEFAULT);
            DataResult<JsonElement> result = ClientItem.CODEC.encodeStart(JsonOps.INSTANCE, clientItem);

            if (result.isSuccess()) {
                return result.getOrThrow().getAsJsonObject();
            }

            if (result.error().isPresent()) {
                PotionsPlus.LOGGER.error("Failed to generate item definition JSON for {}: {}", blockHolder.getKey(), result.error());
            }

            return json;
        });

        ResourceLocation itemDefinitionLongId = ResourceLocation.fromNamespaceAndPath(
                blockHolder.getKey().location().getNamespace(),
                "items/" + blockHolder.getKey().location().getPath() + ".json"
        );

        return new TextResourceModification(
                itemDefinitionLongId, itemDefinitionLongId,
                generateItemDefinitionJson
        );
    }

    static IResourceModification generateBlockstate(Holder<? extends Block> blockHolder, List<Pair<String, String>> blockStateVariantModels) {
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
        return new TextResourceModification(
                blockStateLongId, blockStateLongId,
                generateBlockstatesJson
        );
    }

    static List<List<Integer>> generatePermutations(List<Pair<Property<Integer>, List<Integer>>> properties) {
        if (properties.isEmpty()) {
            return List.of();
        }
        
        // Calculate total number of permutations to pre-size array
        int totalPermutations = 1;
        for (Pair<Property<Integer>, List<Integer>> property : properties) {
            totalPermutations *= property.getB().size();
        }
        
        List<List<Integer>> permutations = new ArrayList<>(totalPermutations);
        
        // Use iterative approach instead of recursive to reduce memory allocation
        generatePermutationsIterative(properties, permutations);
        return permutations;
    }

    /**
     * Iterative function to generate all permutations of the given properties.
     * This is more memory efficient than the recursive approach as it doesn't 
     * create deep call stacks and temporary array copies.
     */
    private static void generatePermutationsIterative(
            List<Pair<Property<Integer>, List<Integer>>> properties,
            List<List<Integer>> result) {
        
        // Convert to arrays for faster access
        List<Integer>[] propertyValues = new List[properties.size()];
        int[] indices = new int[properties.size()];
        
        for (int i = 0; i < properties.size(); i++) {
            propertyValues[i] = properties.get(i).getB();
        }
        
        // Generate all combinations using counter-like approach
        do {
            // Create current permutation
            List<Integer> permutation = new ArrayList<>(properties.size());
            for (int i = 0; i < properties.size(); i++) {
                permutation.add(propertyValues[i].get(indices[i]));
            }
            result.add(permutation);
            
            // Increment indices like an odometer
            int carry = 1;
            for (int i = properties.size() - 1; i >= 0 && carry > 0; i--) {
                indices[i] += carry;
                if (indices[i] >= propertyValues[i].size()) {
                    indices[i] = 0;
                    carry = 1;
                } else {
                    carry = 0;
                }
            }
            
            // Stop when we've rolled over all counters
            if (carry > 0) {
                break;
            }
        } while (true);
    }
}
