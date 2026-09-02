package grill24.potionsplus.core.forge;

/**
 * PHASE 5 (networking): Forge packet registration hub. Unlike Fabric (which splits registration
 * between the server and client entrypoints), Forge registers all payloads in one place. No payloads
 * are registered until the common packet classes are ported (they currently live in
 * {@code .network.neoforge}).
 */
public class Packets {
    public static void register() {
        // PHASE 5: payload type registration + play channel handlers for the ported packets.
    }
}
