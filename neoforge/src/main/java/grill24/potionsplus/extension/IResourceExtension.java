package grill24.potionsplus.extension;

import net.minecraft.server.packs.resources.IoSupplier;

import java.io.InputStream;

public interface IResourceExtension {
    IoSupplier<InputStream> potions_plus$getStreamSuppler();
}
