//package grill24.potionsplus.network;
//
//import grill24.potionsplus.utility.ModInfo;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.server.MinecraftServer;
//import net.minecraft.server.level.ServerPlayer;
//import net.neoforged.fml.common.EventBusSubscriber;
//import org.apache.logging.log4j.util.TriConsumer;
//import net.neoforged.neoforge.network.simple.SimpleChannel;
//
//
//import java.util.function.Consumer;
//
//
//@EventBusSubscriber(modid = ModInfo.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
//public class PotionsPlusPacketHandler {
//    private static final String PROTOCOL_VERSION = "1";
//    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
//            new ResourceLocation(ModInfo.MOD_ID, "main"),
//            () -> PROTOCOL_VERSION,
//            PROTOCOL_VERSION::equals,
//            PROTOCOL_VERSION::equals
//    );
//
//    @SubscribeEvent
//    public static void init(final FMLCommonSetupEvent event) {
//        int id = 0;
//
//        // Clientbound packets
//
//        CHANNEL.registerMessage(id++, OldClientboundImpulsePlayerPacket.class, OldClientboundImpulsePlayerPacket::encode, OldClientboundImpulsePlayerPacket::decode,
//                makeClientBoundHandler(OldClientboundImpulsePlayerPacket.Handler::handle));
//
//        CHANNEL.registerMessage(id++, OldSanguineAltarConversionStatePacket.class, OldSanguineAltarConversionStatePacket::encode, OldSanguineAltarConversionStatePacket::decode,
//                makeClientBoundHandler(OldSanguineAltarConversionStatePacket.Handler::handle));
//
//        CHANNEL.registerMessage(id++, OldSanguineAltarConversionProgressPacket.class, OldSanguineAltarConversionProgressPacket::encode, OldSanguineAltarConversionProgressPacket::decode,
//                makeClientBoundHandler(OldSanguineAltarConversionProgressPacket.Handler::handle));
//
//        CHANNEL.registerMessage(id++, OldClientboundBlockEntityCraftRecipePacket.class, OldClientboundBlockEntityCraftRecipePacket::encode, OldClientboundBlockEntityCraftRecipePacket::decode,
//                makeClientBoundHandler(OldClientboundBlockEntityCraftRecipePacket.Handler::handle));
//
//        // Serverbound packets
//
//        CHANNEL.registerMessage(id++, OldServerboundConstructClotheslinePacket.class, OldServerboundConstructClotheslinePacket::encode, OldServerboundConstructClotheslinePacket::decode,
//                makeServerBoundHandler(OldServerboundConstructClotheslinePacket.Handler::handle));
//    }
//
//    private static <T> BiConsumer<T, Supplier<Context>> makeServerBoundHandler(TriConsumer<T, MinecraftServer, ServerPlayer> handler) {
//        return (m, ctx) -> {
//            handler.accept(m, ctx.get().getSender().getServer(), ctx.get().getSender());
//            ctx.get().setPacketHandled(true);
//        };
//    }
//
//    private static <T> BiConsumer<T, Supplier<Context>> makeClientBoundHandler(Consumer<T> consumer) {
//        return (m, ctx) -> {
//            consumer.accept(m);
//            ctx.get().setPacketHandled(true);
//        };
//    }
//}
