package grill24.potionsplus.network;

import grill24.potionsplus.utility.ModInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.apache.logging.log4j.util.TriConsumer;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static net.minecraftforge.network.NetworkEvent.Context;

@Mod.EventBusSubscriber(modid = ModInfo.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class PotionsPlusPacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(ModInfo.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    @SubscribeEvent
    public static void init(final FMLCommonSetupEvent event) {
        int id = 0;

        // Clientbound packets

        CHANNEL.registerMessage(id++, ClientboundImpulsePlayerPacket.class, ClientboundImpulsePlayerPacket::encode, ClientboundImpulsePlayerPacket::decode,
                makeClientBoundHandler(ClientboundImpulsePlayerPacket.Handler::handle));

        CHANNEL.registerMessage(id++, SanguineAltarConversionStatePacket.class, SanguineAltarConversionStatePacket::encode, SanguineAltarConversionStatePacket::decode,
                makeClientBoundHandler(SanguineAltarConversionStatePacket.Handler::handle));

        CHANNEL.registerMessage(id++, SanguineAltarConversionProgressPacket.class, SanguineAltarConversionProgressPacket::encode, SanguineAltarConversionProgressPacket::decode,
                makeClientBoundHandler(SanguineAltarConversionProgressPacket.Handler::handle));

        CHANNEL.registerMessage(id++, ClientboundClotheslineCraftPacket.class, ClientboundClotheslineCraftPacket::encode, ClientboundClotheslineCraftPacket::decode,
                makeClientBoundHandler(ClientboundClotheslineCraftPacket.Handler::handle));
    }

    private static <T> BiConsumer<T, Supplier<Context>> makeServerBoundHandler(TriConsumer<T, MinecraftServer, ServerPlayer> handler) {
        return (m, ctx) -> {
            handler.accept(m, ctx.get().getSender().getServer(), ctx.get().getSender());
            ctx.get().setPacketHandled(true);
        };
    }

    private static <T> BiConsumer<T, Supplier<Context>> makeClientBoundHandler(Consumer<T> consumer) {
        return (m, ctx) -> {
            consumer.accept(m);
            ctx.get().setPacketHandled(true);
        };
    }
}
