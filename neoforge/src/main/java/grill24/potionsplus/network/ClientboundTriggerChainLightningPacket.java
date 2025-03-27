package grill24.potionsplus.network;

import grill24.potionsplus.core.Particles;
import grill24.potionsplus.core.Sounds;
import grill24.potionsplus.skill.ability.ChainLightningAbility;
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

public record ClientboundTriggerChainLightningPacket(BlockPos soundOrigin, List<Integer> entities) implements CustomPacketPayload {
    public static final Type<ClientboundTriggerChainLightningPacket> TYPE = new Type<>(ppId("trigger_chain_lightning"));

    public static final StreamCodec<ByteBuf, ClientboundTriggerChainLightningPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            ClientboundTriggerChainLightningPacket::soundOrigin,
            ByteBufCodecs.INT.apply(ByteBufCodecs.list()),
            ClientboundTriggerChainLightningPacket::entities,
            ClientboundTriggerChainLightningPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static class ClientPayloadHandler {
        public static void handleDataOnMain (final ClientboundTriggerChainLightningPacket packet, final IPayloadContext context){
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
                        for (int i = 0; i < entities.size() - 1; i++) {
                            Entity entity = entities.get(i);

                            float progress = (float) i / (entities.size() - 1); // 0 = first entity, 1 = last entity
                            ChainLightningAbility.spawnLineOfParticlesBetweenEntities(entities.get(i), entities.get(i + 1), 0.4 - 0.05 * i, (1 - 0.1 * progress) * 3);

                            mc.particleEngine.createTrackingEmitter(entity, Particles.ELECTRICAL_SPARK.value());
                        }

                        if (entities.size() == 1) {
                            mc.particleEngine.createTrackingEmitter(entities.getFirst(), Particles.ELECTRICAL_SPARK.value());
                        }

                        // Play sound at sound origin
                        mc.level.playSound(context.player(), packet.soundOrigin, Sounds.LIGHTNING_BOLT_ABILITY.value(), SoundSource.PLAYERS, 0.5F, 1.0F);
                    }
            );
        }
    }
}
