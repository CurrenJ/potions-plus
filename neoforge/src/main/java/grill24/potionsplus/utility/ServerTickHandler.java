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
import net.neoforged.neoforge.event.tick.ServerTickEvent;

@EventBusSubscriber(modid = ModInfo.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public final class ServerTickHandler {

    private ServerTickHandler() {
    }

    public static int ticksInGame = 0;

    public static float getTicks() {
        return ticksInGame;
    }

    @SubscribeEvent
    public static void onServerTickEnd(final ServerTickEvent.Post event) {
        ticksInGame++;
    }
}
