/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */

// Taken from Botania (and tweaked for my needs)! Thanks Vazkii!
package grill24.potionsplus.utility;

import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderFrameEvent;

@EventBusSubscriber(modid = ModInfo.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public final class ClientTickHandler {

    private ClientTickHandler() {
    }

    public static int ticksInGame = 0;
    public static float partialTicks = 0;

    public static float total() {
        return ticksInGame + partialTicks;
    }

    @SubscribeEvent
    public static void renderTick(final RenderFrameEvent.Post event) {
        partialTicks = event.getPartialTick().getRealtimeDeltaTicks();
    }

    @SubscribeEvent
    public static void clientTickEnd(final ClientTickEvent.Post event) {
        if (!Minecraft.getInstance().isPaused()) {
            ticksInGame++;
            partialTicks = 0;
        }
    }
}
