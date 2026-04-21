package grill24.potionsplus.mixin;

import grill24.potionsplus.utility.ResourceUtility;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.FallbackResourceManager;
import net.minecraft.server.packs.resources.MultiPackResourceManager;
import net.minecraft.server.packs.resources.Resource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * We apply the same mixin for {@link FallbackResourceManager} as we do for
 * {@link net.minecraft.server.packs.resources.ReloadableResourceManager} and {@link MultiPackResourceManager}.
 * <p>
 * Ideally, we would only apply this mixin to the interface, but it does not provide a default implementation of the
 * methods we need to inject into. So we have to apply it to the concrete classes.
 */
@Mixin(FallbackResourceManager.class)
public abstract class FallbackResourceManagerMixin {
    @Inject(method = "listResources", at = @At(value = "RETURN"))
    private void potions_plus$listResources(String par1, Predicate<ResourceLocation> par2, CallbackInfoReturnable<Map<ResourceLocation, Resource>> cir) {
        ResourceUtility.postResourceEvent(cir);
    }

    @Inject(method = "listResourceStacks", at = @At(value = "RETURN"))
    private void potions_plus$listResourceStacks(String par1, Predicate<ResourceLocation> par2, CallbackInfoReturnable<Map<ResourceLocation, List<Resource>>> cir) {
        ResourceUtility.postResourceStackEvent(cir);
    }
}
