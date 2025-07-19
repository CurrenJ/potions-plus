package grill24.potionsplus.event.runtimeresource.modification;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;

import java.util.List;
import java.util.Optional;

public interface IResourceModification {
    /**
     * Every modification must have a target resource location and a new resource location.
     * To replace a resource, the target resource location must be the same as the new resource location.
     * Otherwise, the resource is used as the base for a new {@link grill24.potionsplus.utility.FakeResource} that is injected.
     * <p>
     * We need a target resource because we copy information about the resource pack and other metadata from the original resource.
     * Could probably mock these values ourselves, but this is easier and cleaner.
     *
     * @return the target resource location (long id, i.e. "modid:textures/item/resource.png")
     */
    ResourceLocation getTargetResourceLocation();

    ResourceLocation getNewResourceLocation();

    List<Resource> generateResourceStack();

    Optional<Resource> generateResource();
}
