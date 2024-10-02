package grill24.potionsplus.core;

import grill24.potionsplus.network.*;
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
                ServerboundConstructClotheslinePacket.ServerPayloadHandler::handleDataOnMain
        );


        // ----- Clientbound Packets -----

        // Block Entity Craft Recipe Packet
        registrar.playToClient(
                ClientboundBlockEntityCraftRecipePacket.TYPE,
                ClientboundBlockEntityCraftRecipePacket.STREAM_CODEC,
                ClientboundBlockEntityCraftRecipePacket.ClientPayloadHandler::handleDataOnMain
        );

        // Sanguine Altar Conversion State Packet
        registrar.playToClient(
                ClientboundSanguineAltarConversionStatePacket.TYPE,
                ClientboundSanguineAltarConversionStatePacket.STREAM_CODEC,
                ClientboundSanguineAltarConversionStatePacket.ClientPayloadHandler::handleDataOnMain
        );

        registrar.playToClient(
                ClientboundSanguineAltarConversionProgressPacket.TYPE,
                ClientboundSanguineAltarConversionProgressPacket.STREAM_CODEC,
                ClientboundSanguineAltarConversionProgressPacket.ClientPayloadHandler::handleDataOnMain
        );

        // Player Impulse Packet
        registrar.playToClient(
                ClientboundImpulsePlayerPacket.TYPE,
                ClientboundImpulsePlayerPacket.STREAM_CODEC,
                ClientboundImpulsePlayerPacket.ClientPayloadHandler::handleDataOnMain
        );

        // Brewing Ingredient Knowledge Packet
        registrar.playToClient(
                ClientboundBrewingIngredientKnowledgePacket.TYPE,
                ClientboundBrewingIngredientKnowledgePacket.STREAM_CODEC,
                ClientboundBrewingIngredientKnowledgePacket.ClientPayloadHandler::handleDataOnMain
        );

        // Acquired Brewing Recipe Knowledge Packet
        registrar.playToClient(
                ClientboundAcquiredBrewingRecipeKnowledgePacket.TYPE,
                ClientboundAcquiredBrewingRecipeKnowledgePacket.STREAM_CODEC,
                ClientboundAcquiredBrewingRecipeKnowledgePacket.ClientPayloadHandler::handleDataOnMain
        );

        // Sync Known Brewing Recipes Packet
        registrar.playToClient(
                ClientboundSyncKnownBrewingRecipesPacket.TYPE,
                ClientboundSyncKnownBrewingRecipesPacket.STREAM_CODEC,
                ClientboundSyncKnownBrewingRecipesPacket.ClientPayloadHandler::handleDataOnMain
        );
    }
}
