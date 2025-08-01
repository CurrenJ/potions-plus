package grill24.potionsplus.network;

import grill24.potionsplus.blockentity.SanguineAltarBlockEntity;
import grill24.potionsplus.core.Blocks;
import grill24.potionsplus.core.Sounds;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static grill24.potionsplus.utility.Utility.ppId;

public record ClientboundSanguineAltarConversionStatePacket(BlockPos pos,
                                                            int state) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ClientboundSanguineAltarConversionStatePacket> TYPE = new CustomPacketPayload.Type<>(ppId("sanguine_altar_conversion_state"));

    public static final StreamCodec<ByteBuf, ClientboundSanguineAltarConversionStatePacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            ClientboundSanguineAltarConversionStatePacket::pos,
            ByteBufCodecs.INT,
            ClientboundSanguineAltarConversionStatePacket::state,
            ClientboundSanguineAltarConversionStatePacket::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static class ClientPayloadHandler {
        public static void handleDataOnMain(final ClientboundSanguineAltarConversionStatePacket packet, final IPayloadContext context) {
            context.enqueueWork(
                    () -> {
                        Minecraft mc = Minecraft.getInstance();
                        if (mc.level == null) {
                            return;
                        }

                        mc.level.getBlockEntity(packet.pos, Blocks.SANGUINE_ALTAR_BLOCK_ENTITY.get()).ifPresent(
                                blockEntity -> {
                                    // parse int to enum
                                    blockEntity.state = SanguineAltarBlockEntity.State.values()[packet.state];

                                    // Display the appropriate particle effects, text alerts, and sounds to players within 16 blocks of the altar
                                    if (context.player().position().distanceTo(new Vec3(packet.pos.getX(), packet.pos.getY(), packet.pos.getZ())) < 16) {
                                        switch (blockEntity.state) {
                                            case CONVERTED -> {
                                                mc.level.addParticle(ParticleTypes.EXPLOSION_EMITTER, packet.pos.getX() + 0.5, packet.pos.getY() + 1, packet.pos.getZ() + 0.5, 0, 0, 0);
                                                mc.level.playLocalSound(packet.pos, SoundEvents.GENERIC_EXPLODE.value(), SoundSource.BLOCKS, 1, 1, false);
                                                mc.level.playLocalSound(packet.pos, Sounds.MUTED_PLUCKS_0.value(), SoundSource.BLOCKS, 1, 1, false);

                                                // Display the text alert with the item name
                                                if (mc.level.getBlockEntity(packet.pos) instanceof SanguineAltarBlockEntity sanguineAltarBlockEntity) {
                                                    ItemStack displayItem = sanguineAltarBlockEntity.chainedIngredientToDisplay;
                                                    if (!displayItem.isEmpty()) {
                                                        MutableComponent text = Component.translatable("sanguine_altar.converted", displayItem.getHoverName());
                                                        context.player().displayClientMessage(text, true);
                                                    }
                                                }
                                            }
                                            case FAILED -> {
                                                mc.level.addParticle(ParticleTypes.EXPLOSION_EMITTER, packet.pos.getX() + 0.5, packet.pos.getY() + 1, packet.pos.getZ() + 0.5, 0, 0, 0);
                                                mc.level.playLocalSound(packet.pos, SoundEvents.GENERIC_EXPLODE.value(), SoundSource.BLOCKS, 1, 1, false);
                                                mc.level.playLocalSound(packet.pos, Sounds.MUTED_PLUCKS_1.value(), SoundSource.BLOCKS, 1, 1, false);
                                            }
                                        }
                                    }
                                }
                        );
                    }
            );
        }
    }
}
