package grill24.potionsplus.core.fabric;

import grill24.potionsplus.network.*;
import grill24.potionsplus.network.PacketContext;
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
 */
public class Packets {
    public static void registerServer() {
        // ----- Serverbound Packets: codec (decode) + handler -----

        PayloadTypeRegistry.serverboundPlay().register(
                ServerboundConstructClotheslinePacket.TYPE,
                ServerboundConstructClotheslinePacket.STREAM_CODEC);
        ServerPlayNetworking.registerGlobalReceiver(
                ServerboundConstructClotheslinePacket.TYPE,
                (pkt, ctx) -> ServerboundConstructClotheslinePacket.ServerPayloadHandler.handleDataOnMain(pkt, FabricPacketContext.server(ctx)));

        // ----- Clientbound Packets: codec only (server encodes outgoing S2C; handlers live client-side) -----

        clientboundCodec(ClientboundBlockEntityCraftRecipePacket.TYPE, ClientboundBlockEntityCraftRecipePacket.STREAM_CODEC);
        clientboundCodec(ClientboundSanguineAltarConversionStatePacket.TYPE, ClientboundSanguineAltarConversionStatePacket.STREAM_CODEC);
        clientboundCodec(ClientboundSanguineAltarConversionProgressPacket.TYPE, ClientboundSanguineAltarConversionProgressPacket.STREAM_CODEC);
        clientboundCodec(ClientboundImpulsePlayerPacket.TYPE, ClientboundImpulsePlayerPacket.STREAM_CODEC);
        clientboundCodec(ClientboundDisplayAlertWithItemStackName.TYPE, ClientboundDisplayAlertWithItemStackName.STREAM_CODEC);
        clientboundCodec(ClientboundDisplayAlertWithParameter.TYPE, ClientboundDisplayAlertWithParameter.STREAM_CODEC);
        clientboundCodec(ClientboundAcquiredBrewingRecipeKnowledgePacket.TYPE, ClientboundAcquiredBrewingRecipeKnowledgePacket.STREAM_CODEC);
        clientboundCodec(ClientboundSyncKnownBrewingRecipesPacket.TYPE, ClientboundSyncKnownBrewingRecipesPacket.STREAM_CODEC);
        clientboundCodec(ClientboundSyncPairedAbyssalTrove.TYPE, ClientboundSyncPairedAbyssalTrove.STREAM_CODEC);
        clientboundCodec(ClientboundSyncRuntimeRecipesPacket.TYPE, ClientboundSyncRuntimeRecipesPacket.STREAM_CODEC);
        clientboundCodec(ClientboundDisplayAlert.TYPE, ClientboundDisplayAlert.STREAM_CODEC);
    }

    public static void registerClient() {
        // ----- Serverbound Packets: codec only (client encodes outgoing C2S). Tolerate the
        // integrated-server double-registration where registerServer() already ran. -----

        try {
            PayloadTypeRegistry.serverboundPlay().register(
                    ServerboundConstructClotheslinePacket.TYPE,
                    ServerboundConstructClotheslinePacket.STREAM_CODEC);
        } catch (IllegalArgumentException ignored) {
            // Already registered by the server-side entrypoint in an integrated (singleplayer) environment.
        }

        // ----- Clientbound Packets: codec (decode) + handler -----

        clientbound(ClientboundBlockEntityCraftRecipePacket.TYPE, ClientboundBlockEntityCraftRecipePacket.STREAM_CODEC,
                ClientboundBlockEntityCraftRecipePacket.ClientPayloadHandler::handleDataOnMain);
        clientbound(ClientboundSanguineAltarConversionStatePacket.TYPE, ClientboundSanguineAltarConversionStatePacket.STREAM_CODEC,
                ClientboundSanguineAltarConversionStatePacket.ClientPayloadHandler::handleDataOnMain);
        clientbound(ClientboundSanguineAltarConversionProgressPacket.TYPE, ClientboundSanguineAltarConversionProgressPacket.STREAM_CODEC,
                ClientboundSanguineAltarConversionProgressPacket.ClientPayloadHandler::handleDataOnMain);
        clientbound(ClientboundImpulsePlayerPacket.TYPE, ClientboundImpulsePlayerPacket.STREAM_CODEC,
                ClientboundImpulsePlayerPacket.ClientPayloadHandler::handleDataOnMain);
        clientbound(ClientboundDisplayAlertWithItemStackName.TYPE, ClientboundDisplayAlertWithItemStackName.STREAM_CODEC,
                ClientboundDisplayAlertWithItemStackName.ClientPayloadHandler::handleDataOnMain);
        clientbound(ClientboundDisplayAlertWithParameter.TYPE, ClientboundDisplayAlertWithParameter.STREAM_CODEC,
                ClientboundDisplayAlertWithParameter.ClientPayloadHandler::handleDataOnMain);
        clientbound(ClientboundAcquiredBrewingRecipeKnowledgePacket.TYPE, ClientboundAcquiredBrewingRecipeKnowledgePacket.STREAM_CODEC,
                ClientboundAcquiredBrewingRecipeKnowledgePacket.ClientPayloadHandler::handleDataOnMain);
        clientbound(ClientboundSyncKnownBrewingRecipesPacket.TYPE, ClientboundSyncKnownBrewingRecipesPacket.STREAM_CODEC,
                ClientboundSyncKnownBrewingRecipesPacket.ClientPayloadHandler::handleDataOnMain);
        clientbound(ClientboundSyncPairedAbyssalTrove.TYPE, ClientboundSyncPairedAbyssalTrove.STREAM_CODEC,
                ClientboundSyncPairedAbyssalTrove.ClientPayloadHandler::handleDataOnMain);
        clientbound(ClientboundSyncRuntimeRecipesPacket.TYPE, ClientboundSyncRuntimeRecipesPacket.STREAM_CODEC,
                ClientboundSyncRuntimeRecipesPacket.ClientPayloadHandler::handleDataOnMain);
        clientbound(ClientboundDisplayAlert.TYPE, ClientboundDisplayAlert.STREAM_CODEC,
                ClientboundDisplayAlert.ClientPayloadHandler::handleDataOnMain);
    }

    private static <T extends CustomPacketPayload> void clientboundCodec(
            CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> codec) {
        PayloadTypeRegistry.clientboundPlay().register(type, codec);
    }

    private static <T extends CustomPacketPayload> void clientbound(
            CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> codec,
            BiConsumer<T, PacketContext> handler) {
        try {
            PayloadTypeRegistry.clientboundPlay().register(type, codec);
        } catch (IllegalArgumentException ignored) {
            // Already registered by the server-side entrypoint in an integrated (singleplayer) environment.
        }
        ClientPlayNetworking.registerGlobalReceiver(type, (pkt, ctx) -> handler.accept(pkt, FabricPacketContext.client(ctx)));
    }
}
