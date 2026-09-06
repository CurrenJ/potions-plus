package grill24.potionsplus.core.forge;

import grill24.potionsplus.network.*;
import grill24.potionsplus.network.forge.ForgePacketContext;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.network.Channel;
import net.minecraftforge.network.ChannelBuilder;

import java.util.function.BiConsumer;

/**
 * Forge packet registration hub. Forge has its own {@code net.minecraftforge.network} API (no
 * {@code RegisterPayloadHandlersEvent}/{@code PayloadRegistrar} — those are NeoForge-only); it uses a
 * {@link ChannelBuilder} + {@link Channel} with the {@code payloadChannel()} variant (confirmed present
 * on Forge 52.1.2 via javap).
 *
 * 11 payloads (of 12) are wired here as of Phase 7 (2026-09-04 re-audit) — see
 * {@code core/fabric/Packets.java} for why the last one ({@code ServerboundConstructClotheslinePacket})
 * stays NeoForge-only for now.
 */
public class Packets {
    public static Channel<CustomPacketPayload> CHANNEL;

    public static void register() {
        CHANNEL = ChannelBuilder.named(ResourceLocation.fromNamespaceAndPath(ModInfo.MOD_ID, "main"))
                .networkProtocolVersion(1)
                .optional()
                .payloadChannel()
                .play()

                // ----- Serverbound Packets -----

                .serverbound()
                .add(
                        ServerboundSpawnDoubleJumpParticlesPacket.TYPE,
                        playCodec(ServerboundSpawnDoubleJumpParticlesPacket.STREAM_CODEC),
                        handled((pkt, ctx) -> ServerboundSpawnDoubleJumpParticlesPacket.ServerPayloadHandler.handleDataOnMain(pkt, new ForgePacketContext(ctx)))
                )

                // ----- Clientbound Packets -----

                .clientbound()
                .add(
                        ClientboundBlockEntityCraftRecipePacket.TYPE,
                        playCodec(ClientboundBlockEntityCraftRecipePacket.STREAM_CODEC),
                        handled((pkt, ctx) -> ClientboundBlockEntityCraftRecipePacket.ClientPayloadHandler.handleDataOnMain(pkt, new ForgePacketContext(ctx)))
                )
                .add(
                        ClientboundImpulsePlayerPacket.TYPE,
                        playCodec(ClientboundImpulsePlayerPacket.STREAM_CODEC),
                        handled((pkt, ctx) -> ClientboundImpulsePlayerPacket.ClientPayloadHandler.handleDataOnMain(pkt, new ForgePacketContext(ctx)))
                )
                .add(
                        ClientboundDisplayAlertWithItemStackName.TYPE,
                        playCodec(ClientboundDisplayAlertWithItemStackName.STREAM_CODEC),
                        handled((pkt, ctx) -> ClientboundDisplayAlertWithItemStackName.ClientPayloadHandler.handleDataOnMain(pkt, new ForgePacketContext(ctx)))
                )
                .add(
                        ClientboundDisplayAlertWithParameter.TYPE,
                        playCodec(ClientboundDisplayAlertWithParameter.STREAM_CODEC),
                        handled((pkt, ctx) -> ClientboundDisplayAlertWithParameter.ClientPayloadHandler.handleDataOnMain(pkt, new ForgePacketContext(ctx)))
                )
                .add(
                        ClientboundDisplayAlert.TYPE,
                        playCodec(ClientboundDisplayAlert.STREAM_CODEC),
                        handled((pkt, ctx) -> ClientboundDisplayAlert.ClientPayloadHandler.handleDataOnMain(pkt, new ForgePacketContext(ctx)))
                )
                .add(
                        ClientboundAcquiredBrewingRecipeKnowledgePacket.TYPE,
                        playCodec(ClientboundAcquiredBrewingRecipeKnowledgePacket.STREAM_CODEC),
                        handled((pkt, ctx) -> ClientboundAcquiredBrewingRecipeKnowledgePacket.ClientPayloadHandler.handleDataOnMain(pkt, new ForgePacketContext(ctx)))
                )
                .add(
                        ClientboundSanguineAltarConversionStatePacket.TYPE,
                        playCodec(ClientboundSanguineAltarConversionStatePacket.STREAM_CODEC),
                        handled((pkt, ctx) -> ClientboundSanguineAltarConversionStatePacket.ClientPayloadHandler.handleDataOnMain(pkt, new ForgePacketContext(ctx)))
                )
                .add(
                        ClientboundSanguineAltarConversionProgressPacket.TYPE,
                        playCodec(ClientboundSanguineAltarConversionProgressPacket.STREAM_CODEC),
                        handled((pkt, ctx) -> ClientboundSanguineAltarConversionProgressPacket.ClientPayloadHandler.handleDataOnMain(pkt, new ForgePacketContext(ctx)))
                )
                .add(
                        ClientboundSyncKnownBrewingRecipesPacket.TYPE,
                        playCodec(ClientboundSyncKnownBrewingRecipesPacket.STREAM_CODEC),
                        handled((pkt, ctx) -> ClientboundSyncKnownBrewingRecipesPacket.ClientPayloadHandler.handleDataOnMain(pkt, new ForgePacketContext(ctx)))
                )
                .add(
                        ClientboundSyncPairedAbyssalTrove.TYPE,
                        playCodec(ClientboundSyncPairedAbyssalTrove.STREAM_CODEC),
                        handled((pkt, ctx) -> ClientboundSyncPairedAbyssalTrove.ClientPayloadHandler.handleDataOnMain(pkt, new ForgePacketContext(ctx)))
                )

                .build();
    }

    /**
     * Forge only treats a play-phase custom payload as "handled" if the consumer calls
     * {@code ctx.setPacketHandled(true)} - our handlers just call {@code enqueueWork(...)}, which
     * does NOT set that flag. Left unset, {@code ForgeHooks.onCustomPayload} falls through past
     * {@code NetworkInstance.dispatch} (which already ran our handler successfully) and additionally
     * fires {@code CustomPayloadEvent.BUS} for the same event/buffer - re-touching an already-fully
     * -read buffer, which throws {@code IndexOutOfBoundsException} and disconnects the client. This
     * wrapper marks every packet handled right after dispatch so that fallback path is never reached.
     */
    private static <MSG extends CustomPacketPayload> BiConsumer<MSG, CustomPayloadEvent.Context> handled(BiConsumer<MSG, CustomPayloadEvent.Context> consumer) {
        return (msg, ctx) -> {
            consumer.accept(msg, ctx);
            ctx.setPacketHandled(true);
        };
    }

    /**
     * Forge's {@code play()} flow is typed over {@code RegistryFriendlyByteBuf}, so {@code add(...)}
     * expects a {@code StreamCodec<RegistryFriendlyByteBuf, MSG>}. Several common packets declare their
     * codec over the wider {@code ByteBuf} buffer type; that codec still encodes/decodes a
     * {@code RegistryFriendlyByteBuf} at runtime (it is a {@code ByteBuf}), so the narrowing cast is safe.
     */
    @SuppressWarnings("unchecked")
    private static <T extends CustomPacketPayload> StreamCodec<RegistryFriendlyByteBuf, T> playCodec(StreamCodec<? super RegistryFriendlyByteBuf, T> codec) {
        return (StreamCodec<RegistryFriendlyByteBuf, T>) codec;
    }
}
