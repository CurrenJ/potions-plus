package grill24.potionsplus.utility;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import org.joml.Vector3f;

public class ClientUtility {
    public static Player getLocalPlayer() {
        if (Minecraft.getInstance() == null || !(Minecraft.getInstance().player instanceof LocalPlayer)) {
            throw new IllegalStateException("Cannot get local player in a non-client environment.");
        }

        return Minecraft.getInstance().player;
    }

    public static Vector3f getLocalPlayerPosition() {
        return new Vector3f((float) getLocalPlayer().getX(), (float) getLocalPlayer().getY(), (float) getLocalPlayer().getZ());
    }
}
