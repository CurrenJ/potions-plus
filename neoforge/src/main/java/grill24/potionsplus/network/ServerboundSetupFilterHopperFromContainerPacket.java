package grill24.potionsplus.network;

import grill24.potionsplus.blockentity.filterhopper.FilterHopperBlockEntity;
import grill24.potionsplus.blockentity.filterhopper.FilterHopperMenu;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.ServerPayloadContext;

import java.util.Optional;

import static grill24.potionsplus.utility.Utility.ppId;

public record ServerboundSetupFilterHopperFromContainerPacket() implements CustomPacketPayload {
    public static final Type<ServerboundSetupFilterHopperFromContainerPacket> TYPE = new Type<>(ppId("filter_hopper_setup_from_container"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundSetupFilterHopperFromContainerPacket> STREAM_CODEC = StreamCodec.of(
            (instance, buf) -> new ServerboundSetupFilterHopperFromContainerPacket(),
            (buf) -> new ServerboundSetupFilterHopperFromContainerPacket()
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static class ServerPayloadHandler {
        public static void handleDataOnMain(ServerboundSetupFilterHopperFromContainerPacket packet, final IPayloadContext context) {
            context.enqueueWork(() -> {
                ServerPayloadContext serverContext = (ServerPayloadContext) context;

                if(serverContext.player() instanceof ServerPlayer serverPlayer && serverPlayer.containerMenu instanceof FilterHopperMenu filterHopperMenu) {
                    Optional<FilterHopperBlockEntity> filterHopper = filterHopperMenu.getFilterHopperBlockEntity();
                    filterHopper.ifPresent(FilterHopperBlockEntity::addConnectedContainerContentsToFilter);
                }
            });
        }
    }
}
