package grill24.potionsplus.event.runtimeresource;

import grill24.potionsplus.core.PotionsPlus;
import grill24.potionsplus.event.runtimeresource.modification.IResourceModification;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;

import java.util.*;

public class GenerateRuntimeResourceInjectionsCacheEvent extends Event implements IModBusEvent {
    public record ResourceInjectionCacheEntry(ResourceLocation targetResourceLocation, ResourceLocation newResourceLocation, List<Resource> resource) { }

    public static final List<ResourceInjectionCacheEntry> RESOURCE_INJECTION_CACHE = new ArrayList<>();
    public static final List<ResourceInjectionCacheEntry> RESOURCE_STACKS_INJECTION_CACHE = new ArrayList<>();

    public GenerateRuntimeResourceInjectionsCacheEvent() {
        RESOURCE_INJECTION_CACHE.clear();
        RESOURCE_STACKS_INJECTION_CACHE.clear();
    }

    public void addModification(IResourceModification modification) {
        if (modification.getTargetResourceLocation() != null && modification.getNewResourceLocation() != null) {
            Optional<Resource> generatedResource = modification.generateResource();
            generatedResource.ifPresent(resource -> {
                PotionsPlus.LOGGER.info("Runtime resource modification: {}", modification);
                RESOURCE_INJECTION_CACHE.add(new ResourceInjectionCacheEntry(modification.getTargetResourceLocation(), modification.getNewResourceLocation(), List.of(resource)));
            });

            List<Resource> generatedResources = modification.generateResourceStack();
            RESOURCE_STACKS_INJECTION_CACHE.add(new ResourceInjectionCacheEntry(modification.getTargetResourceLocation(), modification.getNewResourceLocation(), generatedResources));
            if (generatedResources != null && !generatedResources.isEmpty()) {
                PotionsPlus.LOGGER.info("Runtime resource stacks modification: {}", modification);
            }
        } else {
            PotionsPlus.LOGGER.warn("Resource modification has null target or new resource location: {}", modification);
        }
    }

    public void addModifications(IResourceModification... modifications) {
        for (IResourceModification modification : modifications) {
            addModification(modification);
        }
    }
}
