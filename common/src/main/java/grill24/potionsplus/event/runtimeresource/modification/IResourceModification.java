package grill24.potionsplus.event.runtimeresource.modification;

import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;

import java.util.List;
import java.util.Optional;

public interface IResourceModification {
    Identifier getTargetResourceLocation();

    Identifier getNewResourceLocation();

    List<Resource> generateResourceStack();

    Optional<Resource> generateResource();
}
