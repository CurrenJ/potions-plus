package grill24.potionsplus.event.resources;

import grill24.potionsplus.core.PotionsPlus;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;

import java.util.Map;
import java.util.Optional;

public class ClientModifyFileResourcesEvent extends Event implements IModBusEvent {
    private final Map<ResourceLocation, Resource> resources;

    public ClientModifyFileResourcesEvent(Map<ResourceLocation, Resource> resources) {
        this.resources = resources;
    }

    public Map<ResourceLocation, Resource> getResources() {
        return resources;
    }

    public void inject(IResourceModification modification) {
        Optional<Resource> generatedResource = modification.generateResource(this, Minecraft.getInstance().getResourceManager());
        generatedResource.ifPresent(resource -> {
            PotionsPlus.LOGGER.info("Injecting resource modification: {}", modification);
            resources.put(modification.getNewResourceLocation(), resource);
        });
    }
}
