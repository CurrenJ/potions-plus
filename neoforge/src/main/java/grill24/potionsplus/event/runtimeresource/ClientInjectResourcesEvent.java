package grill24.potionsplus.event.runtimeresource;

import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;

import java.util.Map;

public class ClientInjectResourcesEvent extends Event implements IModBusEvent {
    private final Map<Identifier, Resource> resources;

    public ClientInjectResourcesEvent(Map<Identifier, Resource> resources) {
        this.resources = resources;
    }

    public Map<Identifier, Resource> getResources() {
        return resources;
    }
}
