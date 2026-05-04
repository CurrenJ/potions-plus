package grill24.potionsplus.utility;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import grill24.potionsplus.core.PotionsPlus;
import grill24.potionsplus.platform.Platform;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ResourceUtility {
    /**
     * Gets the first texture found within a given Block's blockstate.
     *
     * @param blockHolder
     * @return
     */
    public static Optional<Identifier> getDefaultTexture(Holder<? extends Block> blockHolder) {
        Optional<Identifier> defaultBlockModel = getDefaultModel(blockHolder);
        if (defaultBlockModel.isPresent()) {
            return getDefaultTexture(defaultBlockModel.get());
        } else {
            return Optional.empty();
        }
    }

    /**
     * Gets the first model found within a given Block's blockstate.
     *
     * @param blockHolder
     * @return Model location in the format of "namespace:models/path/to/model.json", as parsable by the ResourceManager
     */
    private static Optional<Identifier> getDefaultModel(Holder<? extends Block> blockHolder) {
        ResourceManager rm = Minecraft.getInstance().getResourceManager();

        Identifier blockStateLocation = blockHolder.unwrapKey().orElseThrow().identifier().withPrefix("blockstates/").withSuffix(".json");
        List<Resource> resources = rm.getResourceStack(blockStateLocation);
        for (Resource resource : resources) {
            String json = getRawResourceJson(resource);
            if (json.isEmpty()) {
                PotionsPlus.LOGGER.error("Couldn't find blockstate JSON for block: {}", blockHolder.unwrapKey().orElseThrow().identifier());
                continue;
            }

            int modelIndex = json.indexOf("\"model\"");
            if (modelIndex != -1) {
                int startIndex = json.indexOf("\"", modelIndex + 7) + 1;
                int endIndex = json.indexOf("\"", startIndex);
                String modelPath = json.substring(startIndex, endIndex);
                Identifier modelLocation = Identifier.parse(modelPath);
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
    private static Optional<Identifier> getDefaultTexture(Identifier modelLocation) {
        ResourceManager rm = Minecraft.getInstance().getResourceManager();

        Optional<Resource> resource = rm.getResource(modelLocation);
        if (resource.isPresent()) {
            String json = getRawResourceJson(resource.get());
            if (json.isEmpty()) {
                PotionsPlus.LOGGER.error("Couldn't find model JSON: {}", modelLocation);
                Optional.empty();
            }

            // Parse json to find the texture
            JsonObject jsonObject = GsonHelper.parse(json);
            JsonObject textureObj = jsonObject.get("textures").getAsJsonObject();
            List<JsonElement> textureEntries = textureObj.asMap().values().stream().toList();
            if (textureEntries.isEmpty()) {
                PotionsPlus.LOGGER.error("No textures found in JSON: {}", json);
                return Optional.empty();
            }
            String texturePath = textureEntries.getFirst().getAsString();

            Identifier textureResourceLocation = Identifier.parse(texturePath);
            return Optional.of(textureResourceLocation);
        } else {
            PotionsPlus.LOGGER.error("No model found for: {}", modelLocation);
        }

        return Optional.empty();
    }

    private static String getRawResourceJson(Resource resource) {
        try {
            return resource.openAsReader().lines().reduce(String::concat).orElse("");
        } catch (IOException e) {
            PotionsPlus.LOGGER.error("Error reading resource: {}", e.getMessage());
            return "";
        }
    }

    public static Optional<Resource> getResource(Identifier longId) {
        ResourceManager rm = Minecraft.getInstance().getResourceManager();
        return rm.getResource(longId);
    }

    public static List<Resource> getResourceStack(Identifier longId) {
        ResourceManager rm = Minecraft.getInstance().getResourceManager();
        return rm.getResourceStack(longId);
    }

    public static void postResourceEvent(CallbackInfoReturnable<Map<Identifier, Resource>> cir) {
        Map<Identifier, Resource> resources = cir.getReturnValue();
        if (resources != null) {
            Platform.postClientInjectResourcesEvent(resources);
        } else {
            PotionsPlus.LOGGER.warn("Resources null - not injecting additional runtime resources.");
        }
    }

    public static void postResourceStackEvent(CallbackInfoReturnable<Map<Identifier, List<Resource>>> cir) {
        Map<Identifier, List<Resource>> resources = cir.getReturnValue();
        if (resources != null) {
            Platform.postClientInjectResourceStacksEvent(resources);
        } else {
            PotionsPlus.LOGGER.warn("Resources null - not injecting additional runtime resource stacks.");
        }
    }
}
