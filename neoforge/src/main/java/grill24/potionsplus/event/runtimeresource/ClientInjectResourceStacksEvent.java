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
}
