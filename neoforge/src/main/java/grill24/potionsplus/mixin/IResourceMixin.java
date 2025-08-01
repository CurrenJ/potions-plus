package grill24.potionsplus.mixin;

import grill24.potionsplus.extension.IResourceExtension;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.server.packs.resources.Resource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.io.InputStream;

@Mixin(Resource.class)
public abstract class IResourceMixin implements IResourceExtension {
    @Shadow
    @Final
    private IoSupplier<InputStream> streamSupplier;

    @Unique
    @Override
    public IoSupplier<InputStream> potions_plus$getStreamSuppler() {
        return this.streamSupplier;
    }
}
