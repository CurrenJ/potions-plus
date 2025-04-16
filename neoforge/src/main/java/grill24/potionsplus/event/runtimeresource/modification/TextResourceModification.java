package grill24.potionsplus.event.runtimeresource.modification;

import com.google.gson.JsonObject;
import grill24.potionsplus.utility.FakeResource;
import grill24.potionsplus.utility.ResourceUtility;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
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
    public ResourceLocation getTargetResourceLocation() {
        return targetResourceLocation;
    }

    @Override
    public ResourceLocation getNewResourceLocation() {
        return newResource;
    }

    @Override
    public List<Resource> generateResourceStack() {
        return ResourceUtility.getResourceStack(targetResourceLocation)
                .stream()
                .map(getTransformer())
                .toList();
    }

    @Override
    public Optional<Resource> generateResource() {
        Optional<Resource> targetResource = ResourceUtility.getResource(targetResourceLocation);
        return targetResource.map(resource -> getTransformer().apply(resource));
    }

    @Override
    public String toString() {
        return "TextResourceModification{" +
                "targetResourceLocation=" + targetResourceLocation +
                ", newResource=" + newResource +
                ", transformer=" + transformer +
                '}';
    }
}
