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
                helper.getLevel().getServer(), helper.getLevel(), cookie.gameProfile(), cookie.clientInformation()) {
            @Override
            public GameType gameMode() {
                return GameType.CREATIVE;
            }
        };

        Connection connection = new Connection(PacketFlow.SERVERBOUND);
        new EmbeddedChannel(connection);
        NetworkRegistry.configureMockConnection(connection);
        helper.getLevel().getServer().getPlayerList().placeNewPlayer(connection, player, cookie);
        return player;
    }
}
