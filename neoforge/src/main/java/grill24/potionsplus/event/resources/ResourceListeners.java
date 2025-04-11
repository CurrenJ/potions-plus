package grill24.potionsplus.event.resources;

import grill24.potionsplus.utility.ModInfo;
import grill24.potionsplus.utility.registration.RegistrationUtility;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = ModInfo.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ResourceListeners {
    @SubscribeEvent
    public static void onModelsLoaded(final ClientModifyFileResourcesEvent event) {
        RegistrationUtility.modifyRuntimeResources(event);
    }

    @SubscribeEvent
    public static void onModelStackLoaded(final ClientModifyFileResourceStackEvent event) {
        RegistrationUtility.modifyRuntimeResources(event);
    }
}
