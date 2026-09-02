package grill24.potionsplus.core.fabric;

/**
 * PHASE 5 (networking): Fabric packet registration hub. Fabric splits registration between the
 * server entrypoint ({@link #registerServer()}) and the client entrypoint ({@link #registerClient()}).
 * No payloads are registered until the common packet classes are ported (they currently live in
 * {@code .network.neoforge}).
 */
public class Packets {
    public static void registerServer() {
        // PHASE 5: PayloadTypeRegistry.serverboundPlay().register(...) + ServerPlayNetworking
        // global receivers for the serverbound packets.
    }

    public static void registerClient() {
        // PHASE 5: clientbound codecs + ClientPlayNetworking global receivers.
    }
}
