package grill24.potionsplus.network;

import grill24.potionsplus.core.DataAttachments;
import grill24.potionsplus.gui.skill.SkillsScreen;
import grill24.potionsplus.skill.SkillsData;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import static grill24.potionsplus.utility.Utility.ppId;

public class ClientboundSyncPlayerSkillData implements CustomPacketPayload {
    public static final Type<ClientboundSyncPlayerSkillData> TYPE = new Type<>(ppId("sync_skill_data"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundSyncPlayerSkillData> STREAM_CODEC = StreamCodec.composite(
            SkillsData.STREAM_CODEC,
            packet -> packet.skillsData,
            ClientboundSyncPlayerSkillData::new
    );

    public final SkillsData skillsData;

    public ClientboundSyncPlayerSkillData(SkillsData skillData) {
        this.skillsData = new SkillsData(skillData);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static class ClientPayloadHandler {
        public static void handleDataOnMain(final ClientboundSyncPlayerSkillData packet, final PacketContext context) {
            context.enqueueWork(
                    () -> {
                        Minecraft mc = Minecraft.getInstance();
                        if (mc.level == null) {
                            return;
                        }

                        if (packet.skillsData != null) {
                            DataAttachments.setSkillsData(context.player(), packet.skillsData);

                            if (Minecraft.getInstance().screen instanceof SkillsScreen skillsScreen) {
                                skillsScreen.onSkillsSync();
                            }
                        }
                    }
            );
        }
    }
}
