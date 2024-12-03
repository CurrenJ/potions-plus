package grill24.potionsplus.network;

import grill24.potionsplus.core.DataAttachments;
import grill24.potionsplus.skill.SkillsData;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static grill24.potionsplus.utility.Utility.ppId;

public record ClientboundSyncPlayerSkillData(SkillsData skillData) implements CustomPacketPayload {
    public static final Type<ClientboundSyncPlayerSkillData> TYPE = new Type<>(ppId("sync_skill_data"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundSyncPlayerSkillData> STREAM_CODEC = StreamCodec.composite(
            SkillsData.STREAM_CODEC,
            ClientboundSyncPlayerSkillData::skillData,
            ClientboundSyncPlayerSkillData::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static class ClientPayloadHandler {
        public static void handleDataOnMain (final ClientboundSyncPlayerSkillData packet, final IPayloadContext context){
            context.enqueueWork(
                    () -> {
                        Minecraft mc = Minecraft.getInstance();
                        if (mc.level == null) {
                            return;
                        }

                        if(packet.skillData != null) {
                            context.player().setData(DataAttachments.SKILL_PLAYER_DATA, packet.skillData);
                        }
                    }
            );
        }
    }
}
