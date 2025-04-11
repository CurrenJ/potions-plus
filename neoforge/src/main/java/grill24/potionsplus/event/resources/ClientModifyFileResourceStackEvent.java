package grill24.potionsplus.event.resources;

import grill24.potionsplus.core.PotionsPlus;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;

import java.util.List;
import java.util.Map;

public class ClientModifyFileResourceStackEvent extends Event implements IModBusEvent {
    private final Map<ResourceLocation, List<Resource>> resources;

    public ClientModifyFileResourceStackEvent(Map<ResourceLocation, List<Resource>> resources) {
        this.resources = resources;
    }

    public Map<ResourceLocation, List<Resource>> getResources() {
        return resources;
    }

    public void inject(IResourceModification modification) {
        List<Resource> generatedResources = modification.generateResourceStack(this, Minecraft.getInstance().getResourceManager());
        if (generatedResources != null && !generatedResources.isEmpty()) {
            PotionsPlus.LOGGER.info("Injecting resource modification: {}", modification);
            resources.put(modification.getNewResourceLocation(), generatedResources);
        }
    }
}
