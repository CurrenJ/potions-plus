package grill24.potionsplus.network;

import grill24.potionsplus.render.animation.keyframe.SpatialAnimationData;
import grill24.potionsplus.render.animation.keyframe.SpatialAnimations;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static grill24.potionsplus.utility.Utility.ppId;

public record ClientboundSyncSpatialAnimationDataPacket(ResourceLocation id,
                                                        SpatialAnimationData spatialAnimationData) implements CustomPacketPayload {
    public static final Type<ClientboundSyncSpatialAnimationDataPacket> TYPE = new Type<>(ppId("sync_spatial_animation_data"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundSyncSpatialAnimationDataPacket> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC,
            ClientboundSyncSpatialAnimationDataPacket::id,
            SpatialAnimationData.STREAM_CODEC,
            ClientboundSyncSpatialAnimationDataPacket::spatialAnimationData,
            ClientboundSyncSpatialAnimationDataPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static class ClientPayloadHandler {
        public static void handleDataOnMain(final ClientboundSyncSpatialAnimationDataPacket packet, final IPayloadContext context) {
            context.enqueueWork(
                    () -> {
                        Minecraft mc = Minecraft.getInstance();
                        if (mc.level == null) {
                            return;
                        }

                        if (packet.spatialAnimationData != null) {
                            SpatialAnimations.get(packet.id).set(packet.spatialAnimationData);
                        }
                    }
            );
        }
    }
}
