package grill24.potionsplus.core.fabric;

import grill24.potionsplus.network.*;
import grill24.potionsplus.network.fabric.FabricPacketContext;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.function.BiConsumer;

/**
 * Fabric packet registration hub. Fabric splits registration between the server entrypoint
 * ({@link #registerServer()}) and the client entrypoint ({@link #registerClient()}).
 *
 * Only 7 payloads (of 12) are wired here as of Phase 11a - {@code
 * ClientboundAcquiredBrewingRecipeKnowledgePacket} moved to {@code common/network/} in Phase 11a
 * (its only remaining dependency, {@code JeiPotionsPlusPlugin}, is already common). The other 5
 * ({@code ServerboundConstructClotheslinePacket} depends on {@code ClotheslineBehaviour};
 * {@code ClientboundSanguineAltarConversion{State,Progress}Packet} depend on the concrete,
 * still-neoforge-only {@code SanguineAltarBlockEntity} class - not just its {@code BlockEntityType}
 * holder, which moving the BE-type hub to {@code core.Blocks} in Phase 11a did NOT unblock - see the
 * Phase 11a progress-log entry; {@code ClientboundSyncKnownBrewingRecipesPacket} and
 * {@code ClientboundSyncPairedAbyssalTrove} depend on {@code JeiPotionsPlusPlugin} plus other
 * still-neoforge-only BE classes) stay registered only in {@code core/neoforge/Packets.java} until
 * those dependencies move to {@code common/}.
 */
public class Packets {
    public static void registerServer() {
        // ----- Serverbound Packets: codec (decode) + handler -----

        PayloadTypeRegistry.playC2S().register(
                ServerboundSpawnDoubleJumpParticlesPacket.TYPE,
                ServerboundSpawnDoubleJumpParticlesPacket.STREAM_CODEC);
        ServerPlayNetworking.registerGlobalReceiver(
                ServerboundSpawnDoubleJumpParticlesPacket.TYPE,
                (pkt, ctx) -> ServerboundSpawnDoubleJumpParticlesPacket.ServerPayloadHandler.handleDataOnMain(pkt, FabricPacketContext.server(ctx)));

        // ----- Clientbound Packets: codec only (server encodes outgoing S2C; handlers live client-side) -----

        clientboundCodec(ClientboundBlockEntityCraftRecipePacket.TYPE, ClientboundBlockEntityCraftRecipePacket.STREAM_CODEC);
        clientboundCodec(ClientboundImpulsePlayerPacket.TYPE, ClientboundImpulsePlayerPacket.STREAM_CODEC);
        clientboundCodec(ClientboundDisplayAlertWithItemStackName.TYPE, ClientboundDisplayAlertWithItemStackName.STREAM_CODEC);
        clientboundCodec(ClientboundDisplayAlertWithParameter.TYPE, ClientboundDisplayAlertWithParameter.STREAM_CODEC);
        clientboundCodec(ClientboundDisplayAlert.TYPE, ClientboundDisplayAlert.STREAM_CODEC);
        clientboundCodec(ClientboundAcquiredBrewingRecipeKnowledgePacket.TYPE, ClientboundAcquiredBrewingRecipeKnowledgePacket.STREAM_CODEC);
    }

    public static void registerClient() {
        // ----- Serverbound Packets: codec only (client encodes outgoing C2S). Tolerate the
        // integrated-server double-registration where registerServer() already ran. -----

        try {
            PayloadTypeRegistry.playC2S().register(
                    ServerboundSpawnDoubleJumpParticlesPacket.TYPE,
                    ServerboundSpawnDoubleJumpParticlesPacket.STREAM_CODEC);
        } catch (IllegalArgumentException ignored) {
            // Already registered by the server-side entrypoint in an integrated (singleplayer) environment.
        }

        // ----- Clientbound Packets: codec (decode) + handler -----

        clientbound(ClientboundBlockEntityCraftRecipePacket.TYPE, ClientboundBlockEntityCraftRecipePacket.STREAM_CODEC,
                ClientboundBlockEntityCraftRecipePacket.ClientPayloadHandler::handleDataOnMain);
        clientbound(ClientboundImpulsePlayerPacket.TYPE, ClientboundImpulsePlayerPacket.STREAM_CODEC,
                ClientboundImpulsePlayerPacket.ClientPayloadHandler::handleDataOnMain);
        clientbound(ClientboundDisplayAlertWithItemStackName.TYPE, ClientboundDisplayAlertWithItemStackName.STREAM_CODEC,
                ClientboundDisplayAlertWithItemStackName.ClientPayloadHandler::handleDataOnMain);
        clientbound(ClientboundDisplayAlertWithParameter.TYPE, ClientboundDisplayAlertWithParameter.STREAM_CODEC,
                ClientboundDisplayAlertWithParameter.ClientPayloadHandler::handleDataOnMain);
        clientbound(ClientboundDisplayAlert.TYPE, ClientboundDisplayAlert.STREAM_CODEC,
                ClientboundDisplayAlert.ClientPayloadHandler::handleDataOnMain);
        clientbound(ClientboundAcquiredBrewingRecipeKnowledgePacket.TYPE, ClientboundAcquiredBrewingRecipeKnowledgePacket.STREAM_CODEC,
                ClientboundAcquiredBrewingRecipeKnowledgePacket.ClientPayloadHandler::handleDataOnMain);
    }

    private static <T extends CustomPacketPayload> void clientboundCodec(
            CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> codec) {
        PayloadTypeRegistry.playS2C().register(type, codec);
    }

    private static <T extends CustomPacketPayload> void clientbound(
            CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> codec,
            BiConsumer<T, PacketContext> handler) {
        try {
            PayloadTypeRegistry.playS2C().register(type, codec);
        } catch (IllegalArgumentException ignored) {
            // Already registered by the server-side entrypoint in an integrated (singleplayer) environment.
        }
        ClientPlayNetworking.registerGlobalReceiver(type, (pkt, ctx) -> handler.accept(pkt, FabricPacketContext.client(ctx)));
    }
}
