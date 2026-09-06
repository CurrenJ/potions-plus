package grill24.potionsplus.core.neoforge;

import com.mojang.blaze3d.platform.InputConstants;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(value = Dist.CLIENT, modid = ModInfo.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class KeyMappings {
    @SubscribeEvent
    public static void registerBindings(final RegisterKeyMappingsEvent event) {
        grill24.potionsplus.core.KeyMappings.ACTIVATE_ABILITY = new KeyMapping(
                grill24.potionsplus.core.KeyMappings.ACTIVATE_ABILITY_TRANSLATION_KEY,
                KeyConflictContext.IN_GAME,
                InputConstants.Type.MOUSE,
                GLFW.GLFW_MOUSE_BUTTON_2,
                grill24.potionsplus.core.KeyMappings.CATEGORY_TRANSLATION_KEY);
        event.register(grill24.potionsplus.core.KeyMappings.ACTIVATE_ABILITY);
    }
}
