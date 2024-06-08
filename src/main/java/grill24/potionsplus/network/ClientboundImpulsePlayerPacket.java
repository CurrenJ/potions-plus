package grill24.potionsplus.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record ClientboundImpulsePlayerPacket(double dx, double dy, double dz) implements PotionsPlusPacket {
    public static final ResourceLocation ID = new ResourceLocation("potionsplus:impulse_player");

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeDouble(dx);
        buf.writeDouble(dy);
        buf.writeDouble(dz);
    }

    @Override
    public ResourceLocation getFabricId() {
        return ID;
    }

    public static ClientboundImpulsePlayerPacket decode(FriendlyByteBuf buf) {
        return new ClientboundImpulsePlayerPacket(buf.readDouble(), buf.readDouble(), buf.readDouble());
    }

    public static class Handler {
        public static void handle(ClientboundImpulsePlayerPacket packet) {
            // Lambda trips verifier on forge
            Minecraft.getInstance().execute(
                    new Runnable() {
                        @Override
                        public void run() {
                            if (Minecraft.getInstance().player != null) {
                                Minecraft.getInstance().player.push(packet.dx, packet.dy, packet.dz);
                            }
                        }
                    }

            );
        }
    }
}
