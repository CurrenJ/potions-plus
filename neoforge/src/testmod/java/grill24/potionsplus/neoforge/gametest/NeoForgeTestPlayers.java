package grill24.potionsplus.neoforge.gametest;

import com.mojang.authlib.GameProfile;
import io.netty.channel.embedded.EmbeddedChannel;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.world.level.GameType;
import net.neoforged.neoforge.network.registration.NetworkRegistry;

import java.util.UUID;

/**
 * A real {@link ServerPlayer} placed in the game test level.
 *
 * <p>{@code GameTestHelper.makeMockPlayer} builds a player but never adds it to the level, so nothing
 * that looks players up by area - such as the brewing cauldron's experience check - can see it.
 *
 * <p>Vanilla's {@code makeMockServerPlayerInLevel} does place one, but its mock connection never goes
 * through the configuration-phase handshake that registers NeoForge's modded payload channels, so
 * {@code NetworkRegistry.checkPacket()} rejects any custom packet sent to it. This mod sends several on
 * join (known brewing recipes, paired abyssal trove), including to a synthetic player like this one.
 * {@code configureMockConnection} exists to pre-empt exactly that; it just has to run before
 * {@code placeNewPlayer}, which the vanilla helper gives no hook for.
 *
 * <p>Moved here from {@code common/src/testmod} (Phase 12) - {@code NetworkRegistry} is NeoForge-only,
 * so this class cannot live anywhere both loaders can compile it. Fabric's equivalent test wrapper uses
 * vanilla {@code helper::makeMockServerPlayerInLevel} directly (see {@code fabric.gametest.
 * PotionsPlusFabricGameTests}) since Fabric's networking registration does not have this same
 * connection-handshake gate - not independently re-verified against Fabric's actual networking
 * internals this session, carried over from the reference {@code dev/26.1.2} tree's identical choice.
 */
public final class NeoForgeTestPlayers {

    private NeoForgeTestPlayers() {}

    /**
     * A creative-mode player in the level. Creative matters for the brewing cauldron: it takes the
     * branch that credits the cauldron with experience without deducting any from the player, so a test
     * does not have to give the player levels first.
     */
    public static ServerPlayer makeMockCreativePlayerInLevel(GameTestHelper helper) {
        CommonListenerCookie cookie = CommonListenerCookie.createInitial(
                new GameProfile(UUID.randomUUID(), "test-mock-player"), false);

        ServerPlayer player = new ServerPlayer(
                helper.getLevel().getServer(), helper.getLevel(), cookie.gameProfile(), cookie.clientInformation());

        Connection connection = new Connection(PacketFlow.SERVERBOUND);
        new EmbeddedChannel(connection);
        NetworkRegistry.configureMockConnection(connection);
        helper.getLevel().getServer().getPlayerList().placeNewPlayer(connection, player, cookie);
        // 1.21.1's ServerPlayer has no overridable gameMode() hook like 26.1.2's - the game mode is a
        // ServerPlayerGameMode field set at construction time. Switch it after placement instead, once
        // player.connection exists (setGameMode sends a packet over it).
        player.setGameMode(GameType.CREATIVE);
        return player;
    }
}
