package grill24.potionsplus.network;

import grill24.potionsplus.client.integration.jei.JeiPotionsPlusPlugin;
import grill24.potionsplus.core.PotionsPlus;
import grill24.potionsplus.persistence.SavedData;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

import static grill24.potionsplus.utility.Utility.ppId;

public record ClientboundSyncKnownBrewingRecipesPacket(String recipeId) implements CustomPacketPayload {
    public static final Type<ClientboundSyncKnownBrewingRecipesPacket> TYPE = new Type<>(ppId("sync_known_brewing_recipes"));

    public static final StreamCodec<ByteBuf, ClientboundSyncKnownBrewingRecipesPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            ClientboundSyncKnownBrewingRecipesPacket::recipeId,
            ClientboundSyncKnownBrewingRecipesPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static class ClientPayloadHandler {
        public static void handleDataOnMain (final ClientboundSyncKnownBrewingRecipesPacket packet, final IPayloadContext context){
            context.enqueueWork(
                    () -> {
                        Minecraft mc = Minecraft.getInstance();
                        if (mc.level == null) {
                            return;
                        }

                        Player clientPlayer = context.player();
                        // Split string into array of recipe ids by comma
                        String[] recipeIds = packet.recipeId().split(",");
                        for (String recipeId : recipeIds) {
                            SavedData.instance.getData(clientPlayer).addKnownRecipe(recipeId);
                        }
                        // Update JEI
                        JeiPotionsPlusPlugin.scheduleUpdateJeiHiddenBrewingCauldronRecipes();

                        PotionsPlus.LOGGER.info("Received {} brewing recipes knowledge from server " + (recipeIds.length > 0 ? ":D" : ":("), recipeIds.length);
                    }
            );
        }
    }

    public static ClientboundSyncKnownBrewingRecipesPacket of(List<String> recipeId) {
        return new ClientboundSyncKnownBrewingRecipesPacket(recipeId.stream().reduce((s1, s2) -> s1 + "," + s2).orElse(""));
    }
}
