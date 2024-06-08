package grill24.potionsplus.network;

import grill24.potionsplus.blockentity.SanguineAltarBlockEntity;
import grill24.potionsplus.core.Blocks;
import grill24.potionsplus.core.Sounds;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

public record SanguineAltarConversionStatePacket(BlockPos pos, SanguineAltarBlockEntity.State state) implements PotionsPlusPacket {
    public static final ResourceLocation ID = new ResourceLocation("potionsplus:sanguine_altar_conversion_state");

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeEnum(state);
    }

    @Override
    public ResourceLocation getFabricId() {
        return ID;
    }

    public static SanguineAltarConversionStatePacket decode(FriendlyByteBuf buf) {
        return new SanguineAltarConversionStatePacket(buf.readBlockPos(), buf.readEnum(SanguineAltarBlockEntity.State.class));
    }

    public static class Handler {
        public static void handle(SanguineAltarConversionStatePacket packet) {
            // Lambda trips verifier on forge
            Minecraft.getInstance().execute(
                    new Runnable() {
                        @Override
                        public void run() {
                            Minecraft mc = Minecraft.getInstance();
                            if (mc.level == null) {
                                return;
                            }

                            mc.level.getBlockEntity(packet.pos, Blocks.SANGUINE_ALTAR_BLOCK_ENTITY.get()).ifPresent(
                                blockEntity -> {
                                    blockEntity.state = packet.state;

                                    switch (packet.state) {
                                        case CONVERTED -> {
                                            mc.level.addParticle(ParticleTypes.EXPLOSION_EMITTER, packet.pos.getX() + 0.5, packet.pos.getY() + 1, packet.pos.getZ() + 0.5, 0, 0, 0);
                                            mc.level.playLocalSound(packet.pos, SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 1, 1, false);
                                            mc.level.playLocalSound(packet.pos, Sounds.MUTED_PLUCKS_0.get(), SoundSource.BLOCKS, 1, 1, false);
                                        }
                                        case FAILED -> {
                                            mc.level.addParticle(ParticleTypes.EXPLOSION_EMITTER, packet.pos.getX() + 0.5, packet.pos.getY() + 1, packet.pos.getZ() + 0.5, 0, 0, 0);
                                            mc.level.playLocalSound(packet.pos, SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 1, 1, false);
                                            mc.level.playLocalSound(packet.pos, Sounds.MUTED_PLUCKS_1.get(), SoundSource.BLOCKS, 1, 1, false);
                                        }
                                    }
                                }
                            );
                        }
                    }

            );
        }
    }
}
