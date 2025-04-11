package grill24.potionsplus.event.resources;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.List;
import java.util.Optional;

public interface IResourceModification {
    ResourceLocation getNewResourceLocation();
    List<Resource> generateResourceStack(ClientModifyFileResourceStackEvent event, final ResourceManager existingResources);
    Optional<Resource> generateResource(ClientModifyFileResourcesEvent event, final ResourceManager resources);
}
