package grill24.potionsplus.event.runtimeresource;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;

import java.util.Map;

public class ClientInjectResourcesEvent extends Event implements IModBusEvent {
    private final Map<ResourceLocation, Resource> resources;

    public ClientInjectResourcesEvent(Map<ResourceLocation, Resource> resources) {
        this.resources = resources;
    }

    public Map<ResourceLocation, Resource> getResources() {
        return resources;
    }
}
