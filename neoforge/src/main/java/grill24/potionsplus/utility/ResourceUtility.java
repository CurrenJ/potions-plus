package grill24.potionsplus.utility;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import grill24.potionsplus.core.PotionsPlus;
import grill24.potionsplus.event.runtimeresource.ClientInjectResourceStacksEvent;
import grill24.potionsplus.event.runtimeresource.ClientInjectResourcesEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.Block;
import net.neoforged.fml.ModLoader;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class ResourceUtility {
    // Cache for texture lookups to avoid expensive JSON parsing and resource loading
    private static final ConcurrentHashMap<ResourceLocation, Optional<ResourceLocation>> BLOCK_TEXTURE_CACHE = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<ResourceLocation, Optional<ResourceLocation>> MODEL_TEXTURE_CACHE = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<ResourceLocation, String> RAW_JSON_CACHE = new ConcurrentHashMap<>();
    
    // Statistics for monitoring cache performance
    private static long textureHits = 0;
    private static long textureMisses = 0;
    
    /**
     * Clears all caches. Called when resource packs are reloaded.
     */
    public static void clearCaches() {
        long totalEntries = BLOCK_TEXTURE_CACHE.size() + MODEL_TEXTURE_CACHE.size() + RAW_JSON_CACHE.size();
        BLOCK_TEXTURE_CACHE.clear();
        MODEL_TEXTURE_CACHE.clear();
        RAW_JSON_CACHE.clear();
        
        if (totalEntries > 0) {
            PotionsPlus.LOGGER.info("ResourceUtility cleared {} cached entries", totalEntries);
            logStatistics();
            textureHits = textureMisses = 0;
        }
    }
    
    /**
     * Logs cache performance statistics.
     */
    public static void logStatistics() {
        long totalRequests = textureHits + textureMisses;
        if (totalRequests > 0) {
            double hitRate = (double) textureHits / totalRequests * 100;
            PotionsPlus.LOGGER.info("ResourceUtility Cache: {} hits, {} misses, {:.1f}% hit rate", 
                textureHits, textureMisses, hitRate);
        }
    }
    /**
     * Gets the first texture found within a given Block's blockstate.
     *
     * @param blockHolder
     * @return
     */
    public static Optional<ResourceLocation> getDefaultTexture(Holder<? extends Block> blockHolder) {
        ResourceLocation blockKey = blockHolder.getKey().location();
        
        // Check cache first
        Optional<ResourceLocation> cached = BLOCK_TEXTURE_CACHE.get(blockKey);
        if (cached != null) {
            textureHits++;
            return cached;
        }
        
        textureMisses++;
        Optional<ResourceLocation> defaultBlockModel = getDefaultModel(blockHolder);
        Optional<ResourceLocation> result;
        if (defaultBlockModel.isPresent()) {
            result = getDefaultTexture(defaultBlockModel.get());
        } else {
            result = Optional.empty();
        }
        
        // Cache the result
        BLOCK_TEXTURE_CACHE.put(blockKey, result);
        return result;
    }

    /**
     * Gets the first model found within a given Block's blockstate.
     *
     * @param blockHolder
     * @return Model location in the format of "namespace:models/path/to/model.json", as parsable by the ResourceManager
     */
    private static Optional<ResourceLocation> getDefaultModel(Holder<? extends Block> blockHolder) {
        ResourceManager rm = Minecraft.getInstance().getResourceManager();

        ResourceLocation blockStateLocation = blockHolder.getKey().location().withPrefix("blockstates/").withSuffix(".json");
        List<Resource> resources = rm.getResourceStack(blockStateLocation);
        for (Resource resource : resources) {
            String json = getRawResourceJson(resource);
            if (json.isEmpty()) {
                PotionsPlus.LOGGER.error("Couldn't find blockstate JSON for block: {}", blockHolder.getKey().location());
                continue;
            }

            int modelIndex = json.indexOf("\"model\"");
            if (modelIndex != -1) {
                int startIndex = json.indexOf("\"", modelIndex + 7) + 1;
                int endIndex = json.indexOf("\"", startIndex);
                String modelPath = json.substring(startIndex, endIndex);
                ResourceLocation modelLocation = ResourceLocation.parse(modelPath);
                return Optional.of(modelLocation.withPrefix("models/").withSuffix(".json"));
            } else {
                PotionsPlus.LOGGER.error("No model key found in JSON: {}", json);
            }
        }

        return Optional.empty();
    }

    /**
     * Gets the first texture found within a given model.
     *
     * @param modelLocation
     * @return Texture location in the format of "namespace:path/to/texture.png", as parsable by a block model definition .json.
     */
    private static Optional<ResourceLocation> getDefaultTexture(ResourceLocation modelLocation) {
        // Check cache first
        Optional<ResourceLocation> cached = MODEL_TEXTURE_CACHE.get(modelLocation);
        if (cached != null) {
            return cached;
        }
        
        ResourceManager rm = Minecraft.getInstance().getResourceManager();

        Optional<Resource> resource = rm.getResource(modelLocation);
        Optional<ResourceLocation> result;
        if (resource.isPresent()) {
            String json = getRawResourceJsonCached(resource.get(), modelLocation);
            if (json.isEmpty()) {
                PotionsPlus.LOGGER.error("Couldn't find model JSON: {}", modelLocation);
                result = Optional.empty();
            } else {
                result = parseTextureFromModelJson(json, modelLocation);
            }
        } else {
            PotionsPlus.LOGGER.error("No model found for: {}", modelLocation);
            result = Optional.empty();
        }

        // Cache the result
        MODEL_TEXTURE_CACHE.put(modelLocation, result);
        return result;
    }
    
    /**
     * Optimized texture parsing from model JSON.
     */
    private static Optional<ResourceLocation> parseTextureFromModelJson(String json, ResourceLocation modelLocation) {
        try {
            // Parse json to find the texture
            JsonObject jsonObject = GsonHelper.parse(json);
            if (!jsonObject.has("textures")) {
                PotionsPlus.LOGGER.error("No textures object found in model JSON: {}", modelLocation);
                return Optional.empty();
            }
            
            JsonObject textureObj = jsonObject.get("textures").getAsJsonObject();
            if (textureObj.size() == 0) {
                PotionsPlus.LOGGER.error("No textures found in model JSON: {}", modelLocation);
                return Optional.empty();
            }
            
            // Get the first texture entry more efficiently
            Map.Entry<String, JsonElement> firstEntry = textureObj.entrySet().iterator().next();
            String texturePath = firstEntry.getValue().getAsString();

            ResourceLocation textureResourceLocation = ResourceLocation.parse(texturePath);
            return Optional.of(textureResourceLocation);
        } catch (Exception e) {
            PotionsPlus.LOGGER.error("Error parsing model JSON for {}: {}", modelLocation, e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Cached version of getRawResourceJson to avoid repeated file I/O.
     */
    private static String getRawResourceJsonCached(Resource resource, ResourceLocation location) {
        String cached = RAW_JSON_CACHE.get(location);
        if (cached != null) {
            return cached;
        }
        
        String json = getRawResourceJson(resource);
        RAW_JSON_CACHE.put(location, json);
        return json;
    }

    private static String getRawResourceJson(Resource resource) {
        try {
            return resource.openAsReader().lines().reduce(String::concat).orElse("");
        } catch (IOException e) {
            PotionsPlus.LOGGER.error("Error reading resource: {}", e.getMessage());
            return "";
        }
    }

    public static Optional<Resource> getResource(ResourceLocation longId) {
        ResourceManager rm = Minecraft.getInstance().getResourceManager();
        return rm.getResource(longId);
    }

    public static List<Resource> getResourceStack(ResourceLocation longId) {
        ResourceManager rm = Minecraft.getInstance().getResourceManager();
        return rm.getResourceStack(longId);
    }

    public static void postResourceEvent(CallbackInfoReturnable<Map<ResourceLocation, Resource>> cir) {
        Map<ResourceLocation, Resource> resources = cir.getReturnValue();
        if (resources != null) {
            ModLoader.postEvent(new ClientInjectResourcesEvent(resources));
        } else {
            PotionsPlus.LOGGER.warn("Resources null - not injecting additional runtime resources.");
        }
    }

    public static void postResourceStackEvent(CallbackInfoReturnable<Map<ResourceLocation, List<Resource>>> cir) {
        Map<ResourceLocation, List<Resource>> resources = cir.getReturnValue();
        if (resources != null) {
            ModLoader.postEvent(new ClientInjectResourceStacksEvent(resources));
        } else {
            PotionsPlus.LOGGER.warn("Resources null - not injecting additional runtime resource stacks.");
        }
    }
}
