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
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ModInfo.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ClientTickHandler {

    private ClientTickHandler() {
    }

    public static int ticksInGame = 0;
    public static float partialTicks = 0;

    public static float total() {
        return ticksInGame + partialTicks;
    }

    @SubscribeEvent
    public static void renderTick(final TickEvent.RenderTickEvent event) {
        partialTicks = event.renderTickTime;
    }

    @SubscribeEvent
    public static void clientTickEnd(final TickEvent.ClientTickEvent event) {
        if (!Minecraft.getInstance().isPaused()) {
            ticksInGame++;
            partialTicks = 0;
        }
    }
}
