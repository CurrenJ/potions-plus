package grill24.potionsplus.network;

import grill24.potionsplus.core.Particles;
import grill24.potionsplus.core.Sounds;
import grill24.potionsplus.extension.IParticleEngineExtension;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;
import java.util.Objects;

import static grill24.potionsplus.utility.Utility.ppId;

public record ClientboundTriggerStunShotPacket(int duration, BlockPos soundOrigin,
                                               List<Integer> entities) implements CustomPacketPayload {
    public static final Type<ClientboundTriggerStunShotPacket> TYPE = new Type<>(ppId("trigger_stun_shot"));

    public static final StreamCodec<ByteBuf, ClientboundTriggerStunShotPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            ClientboundTriggerStunShotPacket::duration,
            BlockPos.STREAM_CODEC,
            ClientboundTriggerStunShotPacket::soundOrigin,
            ByteBufCodecs.INT.apply(ByteBufCodecs.list()),
            ClientboundTriggerStunShotPacket::entities,
            ClientboundTriggerStunShotPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static class ClientPayloadHandler {
        public static void handleDataOnMain(final ClientboundTriggerStunShotPacket packet, final IPayloadContext context) {
            context.enqueueWork(
                    () -> {
                        Minecraft mc = Minecraft.getInstance();
                        if (mc.level == null) {
                            return;
                        }

                        // Convert entity IDs to entities
                        List<Entity> entities = packet.entities.stream()
                                .map(id -> mc.level.getEntity(id))
                                .filter(Objects::nonNull)
                                .toList();
                        // Spawn particles between entities
                        for (Entity entity : entities) {
                            ((IParticleEngineExtension) mc.particleEngine).potions_plus$createTrackingEmitter(entity, Particles.STUN_STARS.value(), packet.duration, 0.25F);
                        }
                        // Play sound at sound origin
                        mc.level.playSound(context.player(), packet.soundOrigin, Sounds.HEAVY_IMPACT.value(), SoundSource.PLAYERS, 0.5F, 1.0F);
                    }
            );
        }
    }
}
