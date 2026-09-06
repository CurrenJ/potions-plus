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

public final class ClientTickHandler {

    private ClientTickHandler() {
    }

    public static int ticksInGame = 0;
    public static float partialTicks = 0;

    public static float total() {
        return ticksInGame + partialTicks;
    }

    public static void renderTick(float partialTick) {
        partialTicks = partialTick;
    }

    public static void clientTickEnd() {
        if (!Minecraft.getInstance().isPaused()) {
            ticksInGame++;
            partialTicks = 0;
        }
    }
}
