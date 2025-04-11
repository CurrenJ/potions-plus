package grill24.potionsplus.event.resources;

import grill24.potionsplus.utility.FakeResource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public record ResourceModification(ResourceLocation targetResourceLocation, ResourceLocation newResourceLocation, Function<Resource, FakeResource> resource) implements IResourceModification {
    @Override
    public ResourceLocation getNewResourceLocation() {
        return newResourceLocation;
    }

    @Override
    public List<Resource> generateResourceStack(ClientModifyFileResourceStackEvent event, ResourceManager resourceManager) {
        return event.getResources().getOrDefault(targetResourceLocation, List.of())
                .stream()
                .map(resource)
                .map(r -> (Resource) r)
                .toList();
    }

    @Override
    public Optional<Resource> generateResource(ClientModifyFileResourcesEvent event, ResourceManager resourceManager) {
        Optional<Resource> targetResource = Optional.ofNullable(event.getResources().get(targetResourceLocation));
        return targetResource.map(resource);
    }
}
