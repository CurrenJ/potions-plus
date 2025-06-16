package grill24.potionsplus.network;

import grill24.potionsplus.client.integration.jei.JeiPotionsPlusPlugin;
import grill24.potionsplus.core.PotionsPlus;
import grill24.potionsplus.core.Sounds;
import grill24.potionsplus.debug.Debug;
import grill24.potionsplus.persistence.SavedData;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static grill24.potionsplus.utility.Utility.ppId;

public record ClientboundAcquiredBrewingRecipeKnowledgePacket(ResourceKey<Recipe<?>> recipeKey, ItemStack result) implements CustomPacketPayload {
    public static final Type<ClientboundAcquiredBrewingRecipeKnowledgePacket> TYPE = new Type<>(ppId("brewing_recipe_knowledge"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundAcquiredBrewingRecipeKnowledgePacket> STREAM_CODEC = StreamCodec.composite(
            ResourceKey.streamCodec(Registries.RECIPE),
            ClientboundAcquiredBrewingRecipeKnowledgePacket::recipeKey,
            ItemStack.STREAM_CODEC,
            ClientboundAcquiredBrewingRecipeKnowledgePacket::result,
            ClientboundAcquiredBrewingRecipeKnowledgePacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static class ClientPayloadHandler {
        public static void handleDataOnMain (final ClientboundAcquiredBrewingRecipeKnowledgePacket packet, final IPayloadContext context){
            context.enqueueWork(
                    () -> {
                        Minecraft mc = Minecraft.getInstance();
                        if (mc.level == null) {
                            return;
                        }

                        Player clientPlayer = context.player();
                        JeiPotionsPlusPlugin.scheduleUpdateJeiHiddenBrewingCauldronRecipes();
                        SavedData.instance.getData(clientPlayer).onNewRecipeKnowledgeAcquiredClient(packet.recipeKey());
                        MutableComponent text = Component.translatable("chat.potionsplus.brewing_cauldron_recipe_unlocked", packet.result.getHoverName());
                        clientPlayer.displayClientMessage(text, true);
                        clientPlayer.playSound(Sounds.RECIPE_UNLOCKED.value(), 1.0F, 1.0F);

                        if (Debug.DEBUG) {
                            PotionsPlus.LOGGER.info("Acquired new brewing recipe knowledge: {}", packet.recipeKey());
                        }
                    }
            );
        }
    }
}
