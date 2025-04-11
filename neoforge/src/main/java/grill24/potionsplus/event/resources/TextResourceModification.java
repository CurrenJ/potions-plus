package grill24.potionsplus.event.resources;

import com.google.gson.JsonObject;
import grill24.potionsplus.utility.FakeResource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;

import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;

public record TextResourceModification(ResourceLocation targetResourceLocation, ResourceLocation newResource,
                                       UnaryOperator<String> transformer) implements IResourceModification {
    public UnaryOperator<Resource> getTransformer() {
        return resource -> new FakeResource(resource, transformer);
    }

    public static UnaryOperator<String> jsonTransform(UnaryOperator<JsonObject> jsonObject) {
        return s -> {
            JsonObject json = GsonHelper.parse(s);
            json = jsonObject.apply(json);
            return json.toString();
        };
    }

    @Override
    public ResourceLocation getNewResourceLocation() {
        return newResource;
    }

    @Override
    public List<Resource> generateResourceStack(ClientModifyFileResourceStackEvent event, ResourceManager resourceManager) {
        return event.getResources().getOrDefault(targetResourceLocation, List.of())
                .stream()
                .map(getTransformer())
                .toList();
    }

    @Override
    public Optional<Resource> generateResource(ClientModifyFileResourcesEvent event, ResourceManager resources) {
        Optional<Resource> targetResource = Optional.ofNullable(event.getResources().get(targetResourceLocation));
        return targetResource.map(resource -> getTransformer().apply(resource));
    }
}
