package grill24.potionsplus.core;

import com.mojang.blaze3d.platform.InputConstants;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.common.util.Lazy;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(value = Dist.CLIENT, modid = ModInfo.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class KeyMappings {
    public static final Lazy<KeyMapping> ACTIVATE_ABILITY = Lazy.of(() ->
            new KeyMapping(
                    "key.potionsplus.activate_ability",
                    KeyConflictContext.IN_GAME,
                    InputConstants.Type.MOUSE,
                    GLFW.GLFW_MOUSE_BUTTON_2,
                    "key.categories.potionsplus"));

    @SubscribeEvent
    public static void registerBindings(final RegisterKeyMappingsEvent event) {
        event.register(ACTIVATE_ABILITY.get());
    }
}
