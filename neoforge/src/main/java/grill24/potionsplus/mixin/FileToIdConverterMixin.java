package grill24.potionsplus.mixin;

import grill24.potionsplus.core.PotionsPlus;
import grill24.potionsplus.event.resources.ClientModifyFileResourceStackEvent;
import grill24.potionsplus.event.resources.ClientModifyFileResourcesEvent;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.neoforged.fml.ModLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;

@Mixin(FileToIdConverter.class)
public abstract class FileToIdConverterMixin {
    @Inject(method = "listMatchingResources", at = @At(value = "RETURN"))
    private void potions_plus$listMatchingResources(ResourceManager resourceManager, CallbackInfoReturnable<Map<ResourceLocation, Resource>> cir) {
        postResourceEvent(cir);
    }

    @Inject(method = "listMatchingResourceStacks", at = @At(value = "RETURN"))
    private void potions_plus$listMatchingResourceStacks(ResourceManager resourceManager, CallbackInfoReturnable<Map<ResourceLocation, List<Resource>>> cir) {
        postResourceStackEvent(cir);
    }

    private static void postResourceEvent(CallbackInfoReturnable<Map<ResourceLocation, Resource>> cir) {
        Map<ResourceLocation, Resource> resources = cir.getReturnValue();
        if (resources != null) {
            ModLoader.postEvent(new ClientModifyFileResourcesEvent(resources));
        } else {
            PotionsPlus.LOGGER.warn("Resources null - not injecting additional runtime resources.");
        }
    }

    private static void postResourceStackEvent(CallbackInfoReturnable<Map<ResourceLocation, List<Resource>>> cir) {
        Map<ResourceLocation, List<Resource>> resources = cir.getReturnValue();
        if (resources != null) {
            ModLoader.postEvent(new ClientModifyFileResourceStackEvent(resources));
        } else {
            PotionsPlus.LOGGER.warn("Resources null - not injecting additional runtime resource stacks.");
        }
    }
}
