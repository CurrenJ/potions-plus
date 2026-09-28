package grill24.potionsplus.forge.gametest;

import com.mojang.authlib.GameProfile;
import io.netty.channel.embedded.EmbeddedChannel;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.world.level.GameType;

import java.util.UUID;

/**
 * A real {@link ServerPlayer} placed in the game test level - the Forge counterpart of
 * {@code NeoForgeTestPlayers}. Unlike NeoForge's {@code NetworkRegistry.checkPacket()}, Forge's
 * {@code Channel.send}/{@code PacketDistributor} builds and sends the packet straight to
 * {@code Connection.send} with no registered-channel gate (verified by decompiling
 * {@code net.minecraftforge.network.Channel}), so the plain mock-connection dance vanilla's own
 * (deprecated) {@code GameTestHelper.makeMockServerPlayerInLevel} does is sufficient here - no
 * Forge-specific handshake pre-empting is needed the way NeoForge's is.
 */
public final class ForgeTestPlayers {

    private ForgeTestPlayers() {}

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
        helper.getLevel().getServer().getPlayerList().placeNewPlayer(connection, player, cookie);
        return player;
    }
}
