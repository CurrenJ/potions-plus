package grill24.potionsplus.event.runtimeresource.modification;

import grill24.potionsplus.utility.FakeResource;
import grill24.potionsplus.utility.ResourceUtility;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public record ResourceModification(ResourceLocation targetResourceLocation, ResourceLocation newResourceLocation, Function<Resource, FakeResource> resource) implements IResourceModification {
    @Override
    public ResourceLocation getTargetResourceLocation() {
        return targetResourceLocation;
    }

    @Override
    public ResourceLocation getNewResourceLocation() {
        return newResourceLocation;
    }

    @Override
    public List<Resource> generateResourceStack() {
        return ResourceUtility.getResourceStack(targetResourceLocation)
                .stream()
                .map(resource)
                .map(r -> (Resource) r)
                .toList();
    }

    @Override
    public Optional<Resource> generateResource() {
        Optional<Resource> targetResource = ResourceUtility.getResource(targetResourceLocation);
        return targetResource.map(resource);
    }
}
