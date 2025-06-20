package grill24.potionsplus.network;

import grill24.potionsplus.client.integration.jei.JeiPotionsPlusPlugin;
import grill24.potionsplus.core.PotionsPlus;
import grill24.potionsplus.persistence.SavedData;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.Recipe;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

import static grill24.potionsplus.utility.Utility.ppId;

public record ClientboundSyncKnownBrewingRecipesPacket(List<ResourceKey<Recipe<?>>> recipeKeys) implements CustomPacketPayload {
    public static final Type<ClientboundSyncKnownBrewingRecipesPacket> TYPE = new Type<>(ppId("sync_known_brewing_recipes"));

    public static final StreamCodec<ByteBuf, ClientboundSyncKnownBrewingRecipesPacket> STREAM_CODEC = StreamCodec.composite(
            ResourceKey.streamCodec(Registries.RECIPE).apply(ByteBufCodecs.list()),
            ClientboundSyncKnownBrewingRecipesPacket::recipeKeys,
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

                        if(packet.recipeKeys().isEmpty()) {
                            PotionsPlus.LOGGER.warn("Received empty brewing recipes knowledge from server :(");
                            return;
                        }

                        Player clientPlayer = context.player();
                        SavedData.instance.getData(clientPlayer).clearKnownRecipes();

                        // Split string into array of recipe ids by comma
                        for (ResourceKey<Recipe<?>> recipeResourceKey : packet.recipeKeys()) {
                            SavedData.instance.getData(clientPlayer).addKnownRecipe(recipeResourceKey);
                        }

                        // Update JEI
                        try {
                            JeiPotionsPlusPlugin.scheduleUpdateJeiHiddenBrewingCauldronRecipes();
                        } catch (NoClassDefFoundError error) {
                            PotionsPlus.LOGGER.warn("JEI is not loaded, cannot update brewing recipes knowledge in JEI.");
                        }

                        PotionsPlus.LOGGER.info("Received {} brewing recipes knowledge from server " + (!packet.recipeKeys().isEmpty() ? ":D" : ":("), packet.recipeKeys().size());
                    }
            );
        }
    }
}
