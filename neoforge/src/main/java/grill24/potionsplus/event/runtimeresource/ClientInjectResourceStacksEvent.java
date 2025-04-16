package grill24.potionsplus.event.runtimeresource;

import grill24.potionsplus.core.PotionsPlus;
import grill24.potionsplus.event.runtimeresource.modification.IResourceModification;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;

import java.util.List;
import java.util.Map;

public class ClientInjectResourceStacksEvent extends Event implements IModBusEvent {
    private final Map<ResourceLocation, List<Resource>> resources;

    public ClientInjectResourceStacksEvent(Map<ResourceLocation, List<Resource>> resources) {
        this.resources = resources;
    }

    public Map<ResourceLocation, List<Resource>> getResources() {
        return resources;
    }

    /**
     * Check if the generated resources are not null or empty AND
     * Check if the target resource location exists in the resources map - this is necessary because we are injecting resources not into the resource manager,
     * but into the result of {@link net.minecraft.resources.FileToIdConverter#listMatchingResourceStacks(ResourceManager)}, which is only a subset of resources from the resource manager.
     * It could be a subset of model .jsons or texture .pngs, for example. SO, make sure to check if the target resource location exists in the resources map, so we don't
     * inject an unexpected resource type where it doesn't belong.
     */
    public void inject(IResourceModification modification) {
        List<Resource> generatedResources = modification.generateResourceStack();
        if (generatedResources != null && !generatedResources.isEmpty() && this.resources.containsKey(modification.getTargetResourceLocation())) {
            PotionsPlus.LOGGER.info("Injecting resource modification: {}", modification);
            resources.put(modification.getNewResourceLocation(), generatedResources);
        }
    }
}
