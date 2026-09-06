package grill24.potionsplus.core.neoforge;

import grill24.potionsplus.network.*;
import grill24.potionsplus.network.neoforge.*;
import grill24.potionsplus.utility.ModInfo;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = ModInfo.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class Packets {
    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        // ----- Serverbound Packets -----

        // Construct Clothesline Packet
        final PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(
                ServerboundConstructClotheslinePacket.TYPE,
                ServerboundConstructClotheslinePacket.STREAM_CODEC,
                (pkt, ctx) -> ServerboundConstructClotheslinePacket.ServerPayloadHandler.handleDataOnMain(pkt, new NeoPacketContext(ctx))
        );

        // Spawn Double Jump Particles Packet
        registrar.playToServer(
                ServerboundSpawnDoubleJumpParticlesPacket.TYPE,
                ServerboundSpawnDoubleJumpParticlesPacket.STREAM_CODEC,
                (pkt, ctx) -> ServerboundSpawnDoubleJumpParticlesPacket.ServerPayloadHandler.handleDataOnMain(pkt, new NeoPacketContext(ctx))
        );

        // ----- Clientbound Packets -----

        // Block Entity Craft Recipe Packet
        registrar.playToClient(
                ClientboundBlockEntityCraftRecipePacket.TYPE,
                ClientboundBlockEntityCraftRecipePacket.STREAM_CODEC,
                (pkt, ctx) -> ClientboundBlockEntityCraftRecipePacket.ClientPayloadHandler.handleDataOnMain(pkt, new NeoPacketContext(ctx))
        );

        // Sanguine Altar Conversion State Packet
        registrar.playToClient(
                ClientboundSanguineAltarConversionStatePacket.TYPE,
                ClientboundSanguineAltarConversionStatePacket.STREAM_CODEC,
                (pkt, ctx) -> ClientboundSanguineAltarConversionStatePacket.ClientPayloadHandler.handleDataOnMain(pkt, new NeoPacketContext(ctx))
        );

        registrar.playToClient(
                ClientboundSanguineAltarConversionProgressPacket.TYPE,
                ClientboundSanguineAltarConversionProgressPacket.STREAM_CODEC,
                (pkt, ctx) -> ClientboundSanguineAltarConversionProgressPacket.ClientPayloadHandler.handleDataOnMain(pkt, new NeoPacketContext(ctx))
        );

        // Player Impulse Packet
        registrar.playToClient(
                ClientboundImpulsePlayerPacket.TYPE,
                ClientboundImpulsePlayerPacket.STREAM_CODEC,
                (pkt, ctx) -> ClientboundImpulsePlayerPacket.ClientPayloadHandler.handleDataOnMain(pkt, new NeoPacketContext(ctx))
        );

        // Alert w ItemStack
        registrar.playToClient(
                ClientboundDisplayAlertWithItemStackName.TYPE,
                ClientboundDisplayAlertWithItemStackName.STREAM_CODEC,
                (pkt, ctx) -> ClientboundDisplayAlertWithItemStackName.ClientPayloadHandler.handleDataOnMain(pkt, new NeoPacketContext(ctx))
                );

        // Alert
        registrar.playToClient(
                ClientboundDisplayAlertWithParameter.TYPE,
                ClientboundDisplayAlertWithParameter.STREAM_CODEC,
                (pkt, ctx) -> ClientboundDisplayAlertWithParameter.ClientPayloadHandler.handleDataOnMain(pkt, new NeoPacketContext(ctx))
        );

        // Acquired Brewing Recipe Knowledge Packet
        registrar.playToClient(
                ClientboundAcquiredBrewingRecipeKnowledgePacket.TYPE,
                ClientboundAcquiredBrewingRecipeKnowledgePacket.STREAM_CODEC,
                (pkt, ctx) -> ClientboundAcquiredBrewingRecipeKnowledgePacket.ClientPayloadHandler.handleDataOnMain(pkt, new NeoPacketContext(ctx))
        );

        // Sync Known Brewing Recipes Packet
        registrar.playToClient(
                ClientboundSyncKnownBrewingRecipesPacket.TYPE,
                ClientboundSyncKnownBrewingRecipesPacket.STREAM_CODEC,
                (pkt, ctx) -> ClientboundSyncKnownBrewingRecipesPacket.ClientPayloadHandler.handleDataOnMain(pkt, new NeoPacketContext(ctx))
                );

        // Sync Paired Abyssal Trove Packet
        registrar.playToClient(
                ClientboundSyncPairedAbyssalTrove.TYPE,
                ClientboundSyncPairedAbyssalTrove.STREAM_CODEC,
                (pkt, ctx) -> ClientboundSyncPairedAbyssalTrove.ClientPayloadHandler.handleDataOnMain(pkt, new NeoPacketContext(ctx))
        );

        // Display Alert Packet
        registrar.playToClient(
                ClientboundDisplayAlert.TYPE,
                ClientboundDisplayAlert.STREAM_CODEC,
                (pkt, ctx) -> ClientboundDisplayAlert.ClientPayloadHandler.handleDataOnMain(pkt, new NeoPacketContext(ctx))
        );

    }
}
