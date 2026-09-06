package grill24.potionsplus.event.neoforge;

import grill24.potionsplus.utility.ModInfo;
import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;

/**
 * NeoForge MOD-bus wiring that adds every attribute registered through the common
 * {@code core.Attributes} hub to the player. Split out of the old neoforge-only
 * {@code core.neoforge.Attributes} class (deleted in Phase 4) so the common hub holds no
 * platform-specific logic. See docs/multi-loader-expansion.md Phase 4.
 */
@EventBusSubscriber(modid = ModInfo.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class NeoAttributeEvents {
    @SubscribeEvent
    public static void onModifyEntityAttributesEvent(final EntityAttributeModificationEvent event) {
        grill24.potionsplus.core.Attributes.getAllAttributes().forEach(attributeHolder -> event.add(EntityType.PLAYER, attributeHolder));
    }
}
