package grill24.potionsplus.network;

import grill24.potionsplus.core.PotionsPlus;
import grill24.potionsplus.core.Recipes;
import grill24.potionsplus.platform.PacketNetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeMap;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static grill24.potionsplus.utility.Utility.ppId;

/**
 * Ships this mod's custom-{@code RecipeType} recipes (brewing cauldron, clothesline, sanguine altar)
 * to a remote client, so client-only consumers - ingredient potion tooltips, the abyssal trove
 * display, JEI - can read them out of {@link Recipes#recipes}.
 *
 * <p>Needed because most of these recipes are generated at runtime from the world seed (see
 * {@code RecipeManagerMixin}), and because since MC 1.21.2 the server no longer network-syncs whole
 * recipes: the vanilla client receives only recipe <em>displays</em> for the recipe book. NeoForge
 * papers over that with {@code OnDatapackSyncEvent#sendRecipes} + {@code RecipesReceivedEvent}, which
 * is why the NeoForge module has no packet of its own; Forge and Fabric have no equivalent hook, so
 * this packet is their replacement. It is modelled on NeoForge's internal {@code RecipeContentPayload}
 * (a plain list of {@link RecipeHolder}s, rebuilt into a {@link RecipeMap} client-side).
 *
 * <p>Sent in batches of {@link #BATCH_SIZE}: a world can easily carry 500+ generated recipes, and a
 * single custom payload is capped at 1 MiB by {@code ClientboundCustomPayloadPacket}. The client
 * accumulates batches and only post-processes once the last one lands, so the analyses are never
 * computed against a half-received set.
 */
public record ClientboundSyncRuntimeRecipesPacket(int batchIndex, int batchCount,
                                                  List<RecipeHolder<?>> recipes) implements CustomPacketPayload {
    public static final Type<ClientboundSyncRuntimeRecipesPacket> TYPE = new Type<>(ppId("sync_runtime_recipes"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundSyncRuntimeRecipesPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ClientboundSyncRuntimeRecipesPacket::batchIndex,
            ByteBufCodecs.VAR_INT, ClientboundSyncRuntimeRecipesPacket::batchCount,
            RecipeHolder.STREAM_CODEC.apply(ByteBufCodecs.list()), ClientboundSyncRuntimeRecipesPacket::recipes,
            ClientboundSyncRuntimeRecipesPacket::new
    );

    /** Conservative: even a potion-heavy brewing cauldron recipe stays far inside the 1 MiB payload cap at this count. */
    public static final int BATCH_SIZE = 64;

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    /**
     * Sends every synced recipe to one player, as one or more batches. Always sends at least one
     * packet, so a client that previously had recipes (e.g. across a {@code /reload} that removed
     * them) clears its copy.
     */
    public static void sendTo(ServerPlayer player) {
        List<ClientboundSyncRuntimeRecipesPacket> batches = createBatches(Recipes.recipes);
        for (ClientboundSyncRuntimeRecipesPacket batch : batches) {
            PacketNetwork.sendToPlayer(player, batch);
        }
    }

    public static List<ClientboundSyncRuntimeRecipesPacket> createBatches(RecipeMap recipeMap) {
        List<RecipeHolder<?>> toSend = collectSyncedRecipes(recipeMap);

        int batchCount = Math.max(1, (toSend.size() + BATCH_SIZE - 1) / BATCH_SIZE);
        List<ClientboundSyncRuntimeRecipesPacket> batches = new ArrayList<>(batchCount);
        for (int i = 0; i < batchCount; i++) {
            int from = i * BATCH_SIZE;
            int to = Math.min(from + BATCH_SIZE, toSend.size());
            batches.add(new ClientboundSyncRuntimeRecipesPacket(i, batchCount, List.copyOf(toSend.subList(from, to))));
        }
        return batches;
    }

    private static List<RecipeHolder<?>> collectSyncedRecipes(RecipeMap recipeMap) {
        if (recipeMap == null) {
            // Recipes.recipes is only populated once RecipeManagerMixin has post-processed a reload.
            PotionsPlus.LOGGER.warn("No recipe map available to sync to client - runtime recipes have not been injected yet.");
            return List.of();
        }

        // Built per call, not cached: the RecipeType suppliers are registry holders, and this runs
        // well after registration on every loader.
        Set<RecipeType<?>> syncedTypes = new HashSet<>(List.of(
                Recipes.BREWING_CAULDRON_RECIPE.get(),
                Recipes.CLOTHESLINE_RECIPE.get(),
                Recipes.SANGUINE_ALTAR_RECIPE.get()
        ));

        return recipeMap.values().stream()
                .filter(holder -> syncedTypes.contains(holder.value().getType()))
                .toList();
    }

    public static class ClientPayloadHandler {
        /**
         * Batches accumulate here until the last one arrives. Only touched on the client thread (the
         * handler body runs inside {@code enqueueWork}).
         */
        private static final List<RecipeHolder<?>> PENDING = new ArrayList<>();

        public static void handleDataOnMain(final ClientboundSyncRuntimeRecipesPacket packet, final PacketContext context) {
            context.enqueueWork(() -> {
                if (packet.batchIndex() == 0) {
                    PENDING.clear();
                }
                PENDING.addAll(packet.recipes());

                if (packet.batchIndex() < packet.batchCount() - 1) {
                    return;
                }

                if (Minecraft.getInstance().hasSingleplayerServer()) {
                    // Singleplayer / LAN host: client and integrated server share this JVM's statics,
                    // so Recipes.recipes is already the server's full map (a superset of what we just
                    // received) and its analyses are computed. Replacing it with the custom-type
                    // subset would narrow the server's own view for no gain. Deliberately not keyed on
                    // "do we already hold these recipe ids" - seeded recipe ids repeat across worlds
                    // while their contents differ, so that test would wrongly skip a real update when
                    // hopping between two servers.
                    PENDING.clear();
                    return;
                }

                Recipes.postProcessRecipes(RecipeMap.create(List.copyOf(PENDING)));
                PotionsPlus.LOGGER.info("Received {} runtime recipes from server in {} batch(es).",
                        PENDING.size(), packet.batchCount());
                PENDING.clear();
            });
        }
    }
}
