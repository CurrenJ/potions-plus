package grill24.potionsplus.event;

import grill24.potionsplus.core.Attributes;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;

/**
 * Platform-specific event handlers for NeoForge attribute events.
 * Handles adding mod attributes to entity types via EntityAttributeModificationEvent.
 */
@EventBusSubscriber(modid = ModInfo.MOD_ID)
public class NeoAttributeEvents {

    @SubscribeEvent
    public static void onModifyEntityAttributesEvent(final EntityAttributeModificationEvent event) {
        Attributes.getAllAttributes().forEach(attributeHolder -> event.add(EntityType.PLAYER, attributeHolder));
    }
}
