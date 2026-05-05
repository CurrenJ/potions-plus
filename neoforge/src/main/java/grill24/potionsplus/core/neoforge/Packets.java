package grill24.potionsplus.core.neoforge;

import grill24.potionsplus.network.*;
import grill24.potionsplus.utility.ModInfo;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = ModInfo.MOD_ID)
public class Packets {
    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");

        // ----- Serverbound Packets -----

        registrar.playToServer(
                ServerboundConstructClotheslinePacket.TYPE,
                ServerboundConstructClotheslinePacket.STREAM_CODEC,
                (pkt, ctx) -> ServerboundConstructClotheslinePacket.ServerPayloadHandler.handleDataOnMain(pkt, new NeoPacketContext(ctx))
        );

        registrar.playToServer(
                ServerboundUpdateAbilityStrengthPacket.TYPE,
                ServerboundUpdateAbilityStrengthPacket.STREAM_CODEC,
                (pkt, ctx) -> ServerboundUpdateAbilityStrengthPacket.ServerPayloadHandler.handleDataOnMain(pkt, new NeoPacketContext(ctx))
        );

        registrar.playToServer(
                ServerboundToggleAbilityPacket.TYPE,
                ServerboundToggleAbilityPacket.STREAM_CODEC,
                (pkt, ctx) -> ServerboundToggleAbilityPacket.ServerPayloadHandler.handleDataOnMain(pkt, new NeoPacketContext(ctx))
        );

        registrar.playToServer(
                ServerboundSpawnDoubleJumpParticlesPacket.TYPE,
                ServerboundSpawnDoubleJumpParticlesPacket.STREAM_CODEC,
                (pkt, ctx) -> ServerboundSpawnDoubleJumpParticlesPacket.ServerPayloadHandler.handleDataOnMain(pkt, new NeoPacketContext(ctx))
        );

        registrar.playToServer(
                ServerboundSetupFilterHopperFromContainerPacket.TYPE,
                ServerboundSetupFilterHopperFromContainerPacket.STREAM_CODEC,
                (pkt, ctx) -> ServerboundSetupFilterHopperFromContainerPacket.ServerPayloadHandler.handleDataOnMain(pkt, new NeoPacketContext(ctx))
        );

        registrar.playToServer(
                ServerboundTryClaimSkillReward.TYPE,
                ServerboundTryClaimSkillReward.STREAM_CODEC,
                (pkt, ctx) -> ServerboundTryClaimSkillReward.ServerPayloadHandler.handleDataOnMain(pkt, new NeoPacketContext(ctx))
        );

        // ----- Clientbound Packets -----

        registrar.playToClient(
                ClientboundBlockEntityCraftRecipePacket.TYPE,
                ClientboundBlockEntityCraftRecipePacket.STREAM_CODEC,
                (pkt, ctx) -> ClientboundBlockEntityCraftRecipePacket.ClientPayloadHandler.handleDataOnMain(pkt, new NeoPacketContext(ctx))
        );

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

        registrar.playToClient(
                ClientboundImpulsePlayerPacket.TYPE,
                ClientboundImpulsePlayerPacket.STREAM_CODEC,
                (pkt, ctx) -> ClientboundImpulsePlayerPacket.ClientPayloadHandler.handleDataOnMain(pkt, new NeoPacketContext(ctx))
        );

        registrar.playToClient(
                ClientboundDisplayAlertWithItemStackName.TYPE,
                ClientboundDisplayAlertWithItemStackName.STREAM_CODEC,
                (pkt, ctx) -> ClientboundDisplayAlertWithItemStackName.ClientPayloadHandler.handleDataOnMain(pkt, new NeoPacketContext(ctx))
        );

        registrar.playToClient(
                ClientboundDisplayAlertWithParameter.TYPE,
                ClientboundDisplayAlertWithParameter.STREAM_CODEC,
                (pkt, ctx) -> ClientboundDisplayAlertWithParameter.ClientPayloadHandler.handleDataOnMain(pkt, new NeoPacketContext(ctx))
        );

        registrar.playToClient(
                ClientboundAcquiredBrewingRecipeKnowledgePacket.TYPE,
                ClientboundAcquiredBrewingRecipeKnowledgePacket.STREAM_CODEC,
                (pkt, ctx) -> ClientboundAcquiredBrewingRecipeKnowledgePacket.ClientPayloadHandler.handleDataOnMain(pkt, new NeoPacketContext(ctx))
        );

        registrar.playToClient(
                ClientboundSyncKnownBrewingRecipesPacket.TYPE,
                ClientboundSyncKnownBrewingRecipesPacket.STREAM_CODEC,
                (pkt, ctx) -> ClientboundSyncKnownBrewingRecipesPacket.ClientPayloadHandler.handleDataOnMain(pkt, new NeoPacketContext(ctx))
        );

        registrar.playToClient(
                ClientboundSyncPairedAbyssalTrove.TYPE,
                ClientboundSyncPairedAbyssalTrove.STREAM_CODEC,
                (pkt, ctx) -> ClientboundSyncPairedAbyssalTrove.ClientPayloadHandler.handleDataOnMain(pkt, new NeoPacketContext(ctx))
        );

        registrar.playToClient(
                ClientboundSyncPlayerSkillData.TYPE,
                ClientboundSyncPlayerSkillData.STREAM_CODEC,
                (pkt, ctx) -> ClientboundSyncPlayerSkillData.ClientPayloadHandler.handleDataOnMain(pkt, new NeoPacketContext(ctx))
        );

        registrar.playToClient(
                ClientboundDisplayItemActivationPacket.TYPE,
                ClientboundDisplayItemActivationPacket.STREAM_CODEC,
                (pkt, ctx) -> ClientboundDisplayItemActivationPacket.ClientPayloadHandler.handleDataOnMain(pkt, new NeoPacketContext(ctx))
        );

        registrar.playToClient(
                ClientboundDisplayWheelAnimationPacket.TYPE,
                ClientboundDisplayWheelAnimationPacket.STREAM_CODEC,
                (pkt, ctx) -> ClientboundDisplayWheelAnimationPacket.ClientPayloadHandler.handleDataOnMain(pkt, new NeoPacketContext(ctx))
        );

        registrar.playToClient(
                ClientboundDisplayTossupAnimationPacket.TYPE,
                ClientboundDisplayTossupAnimationPacket.STREAM_CODEC,
                (pkt, ctx) -> ClientboundDisplayTossupAnimationPacket.ClientPayloadHandler.handleDataOnMain(pkt, new NeoPacketContext(ctx))
        );

        registrar.playToClient(
                ClientboundSyncSpatialAnimationDataPacket.TYPE,
                ClientboundSyncSpatialAnimationDataPacket.STREAM_CODEC,
                (pkt, ctx) -> ClientboundSyncSpatialAnimationDataPacket.ClientPayloadHandler.handleDataOnMain(pkt, new NeoPacketContext(ctx))
        );

        registrar.playToClient(
                ClientboundDisplayAlert.TYPE,
                ClientboundDisplayAlert.STREAM_CODEC,
                (pkt, ctx) -> ClientboundDisplayAlert.ClientPayloadHandler.handleDataOnMain(pkt, new NeoPacketContext(ctx))
        );

        registrar.playToClient(
                ClientboundTriggerChainLightningPacket.TYPE,
                ClientboundTriggerChainLightningPacket.STREAM_CODEC,
                (pkt, ctx) -> ClientboundTriggerChainLightningPacket.ClientPayloadHandler.handleDataOnMain(pkt, new NeoPacketContext(ctx))
        );

        registrar.playToClient(
                ClientboundTriggerStunShotPacket.TYPE,
                ClientboundTriggerStunShotPacket.STREAM_CODEC,
                (pkt, ctx) -> ClientboundTriggerStunShotPacket.ClientPayloadHandler.handleDataOnMain(pkt, new NeoPacketContext(ctx))
        );
    }
}
