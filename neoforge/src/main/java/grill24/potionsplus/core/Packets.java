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

        // Update Ability Strength Packet
        registrar.playToServer(
                ServerboundUpdateAbilityStrengthPacket.TYPE,
                ServerboundUpdateAbilityStrengthPacket.STREAM_CODEC,
                ServerboundUpdateAbilityStrengthPacket.ServerPayloadHandler::handleDataOnMain
        );

        // End Fishing Minigame on Server
        registrar.playToServer(
                ServerboundEndFishingMinigame.TYPE,
                ServerboundEndFishingMinigame.STREAM_CODEC,
                ServerboundEndFishingMinigame.ServerPayloadHandler::handleDataOnMain
        );

        // Toggle Ability
        registrar.playToServer(
                ServerboundToggleAbilityPacket.TYPE,
                ServerboundToggleAbilityPacket.STREAM_CODEC,
                ServerboundToggleAbilityPacket.ServerPayloadHandler::handleDataOnMain
        );

        // Spawn Double Jump Particles Packet
        registrar.playToServer(
                ServerboundSpawnDoubleJumpParticlesPacket.TYPE,
                ServerboundSpawnDoubleJumpParticlesPacket.STREAM_CODEC,
                ServerboundSpawnDoubleJumpParticlesPacket.ServerPayloadHandler::handleDataOnMain
        );

        // Setup Filter Hopper From Container Packet
        registrar.playToServer(
                ServerboundSetupFilterHopperFromContainerPacket.TYPE,
                ServerboundSetupFilterHopperFromContainerPacket.STREAM_CODEC,
                ServerboundSetupFilterHopperFromContainerPacket.ServerPayloadHandler::handleDataOnMain
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

        // Alert w ItemStack
        registrar.playToClient(
                ClientboundDisplayAlertWithItemStackName.TYPE,
                ClientboundDisplayAlertWithItemStackName.STREAM_CODEC,
                ClientboundDisplayAlertWithItemStackName.ClientPayloadHandler::handleDataOnMain
                );

        // Alert
        registrar.playToClient(
                ClientboundDisplayAlertWithParameter.TYPE,
                ClientboundDisplayAlertWithParameter.STREAM_CODEC,
                ClientboundDisplayAlertWithParameter.ClientPayloadHandler::handleDataOnMain
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

        // Sync Paired Abyssal Trove Packet
        registrar.playToClient(
                ClientboundSyncPairedAbyssalTrove.TYPE,
                ClientboundSyncPairedAbyssalTrove.STREAM_CODEC,
                ClientboundSyncPairedAbyssalTrove.ClientPayloadHandler::handleDataOnMain
        );

        // Sync Player Skill Data Packet
        registrar.playToClient(
                ClientboundSyncPlayerSkillData.TYPE,
                ClientboundSyncPlayerSkillData.STREAM_CODEC,
                ClientboundSyncPlayerSkillData.ClientPayloadHandler::handleDataOnMain
        );

        // Display Item Activation Packet
        registrar.playToClient(
                ClientboundDisplayItemActivationPacket.TYPE,
                ClientboundDisplayItemActivationPacket.STREAM_CODEC,
                ClientboundDisplayItemActivationPacket.ClientPayloadHandler::handleDataOnMain
        );

        // Display Wheel Animation Packet
        registrar.playToClient(
                ClientboundDisplayWheelAnimationPacket.TYPE,
                ClientboundDisplayWheelAnimationPacket.STREAM_CODEC,
                ClientboundDisplayWheelAnimationPacket.ClientPayloadHandler::handleDataOnMain
        );

        // Start Fishing Minigame on Client
        registrar.playToClient(
                ClientboundStartFishingMinigamePacket.TYPE,
                ClientboundStartFishingMinigamePacket.STREAM_CODEC,
                ClientboundStartFishingMinigamePacket.ClientPayloadHandler::handleDataOnMain
        );

        // Reset Fishing Minigame Data on Client
        registrar.playToClient(
                ClientboundResetFishingMinigame.TYPE,
                ClientboundResetFishingMinigame.STREAM_CODEC,
                ClientboundResetFishingMinigame.ClientPayloadHandler::handleDataOnMain
        );

        // Display Tossup Animation Packet
        registrar.playToClient(
                ClientboundDisplayTossupAnimationPacket.TYPE,
                ClientboundDisplayTossupAnimationPacket.STREAM_CODEC,
                ClientboundDisplayTossupAnimationPacket.ClientPayloadHandler::handleDataOnMain
        );

        // Sync Spatial Animation Data Packet
        registrar.playToClient(
                ClientboundSyncSpatialAnimationDataPacket.TYPE,
                ClientboundSyncSpatialAnimationDataPacket.STREAM_CODEC,
                ClientboundSyncSpatialAnimationDataPacket.ClientPayloadHandler::handleDataOnMain
        );

        // Display Alert Packet
        registrar.playToClient(
                ClientboundDisplayAlert.TYPE,
                ClientboundDisplayAlert.STREAM_CODEC,
                ClientboundDisplayAlert.ClientPayloadHandler::handleDataOnMain
        );
    }
}
