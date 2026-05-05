package grill24.potionsplus.event.runtimeresource.modification.neoforge;

import grill24.potionsplus.utility.FakeResource;
import grill24.potionsplus.utility.ResourceUtility;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import grill24.potionsplus.event.runtimeresource.modification.IResourceModification;

public record ResourceModification(Identifier targetResourceLocation, Identifier newResourceLocation,
                                   Function<Resource, FakeResource> resource) implements IResourceModification {
    @Override
    public Identifier getTargetResourceLocation() {
        return targetResourceLocation;
    }

    @Override
    public Identifier getNewResourceLocation() {
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
