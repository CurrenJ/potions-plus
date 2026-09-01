package grill24.potionsplus.gametest;

import grill24.potionsplus.core.Recipes;
import grill24.potionsplus.network.ClientboundSyncRuntimeRecipesPacket;
import io.netty.buffer.Unpooled;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeMap;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Coverage for {@link ClientboundSyncRuntimeRecipesPacket}, the packet that carries this mod's
 * custom-{@code RecipeType} recipes to remote clients on Forge and Fabric (NeoForge uses its own
 * {@code sendRecipes}/{@code RecipesReceivedEvent} pair instead - the packet is common code either
 * way, so these tests run on all three loaders).
 *
 * <p>The interesting failure modes are all server-side and reachable without a second connection:
 * dropping recipes while batching, and a recipe whose serializer cannot survive the wire. Whether the
 * client ends up with them is a function of the loader wiring, which only a two-process manual run can
 * confirm - see the loaders' {@code ServerLifecycleListeners}.
 */
public final class RecipeSyncGameTests {

    private RecipeSyncGameTests() {}

    /** Batches partition the synced recipes exactly: no duplicates, no drops, none oversized. */
    public static void runtimeRecipeSyncBatchesCoverEveryRecipe(GameTestHelper helper) {
        RecipeMap recipeMap = Recipes.recipes;
        assertTrue(helper, recipeMap != null, "Recipes.recipes is null - runtime recipes were never injected");

        Set<ResourceKey<Recipe<?>>> expected = syncedRecipeKeys(recipeMap);
        assertTrue(helper, !expected.isEmpty(), "no custom-type recipes exist to sync at all");

        List<ClientboundSyncRuntimeRecipesPacket> batches = ClientboundSyncRuntimeRecipesPacket.createBatches(recipeMap);
        assertTrue(helper, !batches.isEmpty(), "createBatches produced no packets");

        Set<ResourceKey<Recipe<?>>> seen = new HashSet<>();
        for (int i = 0; i < batches.size(); i++) {
            ClientboundSyncRuntimeRecipesPacket batch = batches.get(i);
            assertTrue(helper, batch.batchIndex() == i,
                    "batch " + i + " reports index " + batch.batchIndex());
            assertTrue(helper, batch.batchCount() == batches.size(),
                    "batch " + i + " reports a count of " + batch.batchCount() + ", expected " + batches.size());
            assertTrue(helper, batch.recipes().size() <= ClientboundSyncRuntimeRecipesPacket.BATCH_SIZE,
                    "batch " + i + " carries " + batch.recipes().size() + " recipes, over the "
                            + ClientboundSyncRuntimeRecipesPacket.BATCH_SIZE + " cap");

            for (RecipeHolder<?> holder : batch.recipes()) {
                assertTrue(helper, seen.add(holder.id()), "recipe " + holder.id() + " was batched twice");
            }
        }

        assertTrue(helper, seen.equals(expected),
                "batches carried " + seen.size() + " recipes, expected " + expected.size());

        helper.succeed();
    }

    /**
     * Every synced recipe survives an encode/decode round trip through the packet's stream codec - i.e.
     * each custom recipe serializer's stream codec is wired up - and no batch approaches the 1 MiB
     * custom-payload cap.
     */
    public static void runtimeRecipeSyncRoundTripsThroughTheStreamCodec(GameTestHelper helper) {
        RecipeMap recipeMap = Recipes.recipes;
        assertTrue(helper, recipeMap != null, "Recipes.recipes is null - runtime recipes were never injected");

        List<ClientboundSyncRuntimeRecipesPacket> batches = ClientboundSyncRuntimeRecipesPacket.createBatches(recipeMap);
        List<RecipeHolder<?>> decoded = new ArrayList<>();

        for (ClientboundSyncRuntimeRecipesPacket batch : batches) {
            RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(
                    Unpooled.buffer(), helper.getLevel().registryAccess());
            try {
                ClientboundSyncRuntimeRecipesPacket.STREAM_CODEC.encode(buf, batch);

                int encodedBytes = buf.readableBytes();
                assertTrue(helper, encodedBytes < 1048576,
                        "batch " + batch.batchIndex() + " encodes to " + encodedBytes
                                + " bytes, over the 1 MiB custom-payload cap");

                ClientboundSyncRuntimeRecipesPacket read = ClientboundSyncRuntimeRecipesPacket.STREAM_CODEC.decode(buf);
                assertTrue(helper, buf.readableBytes() == 0,
                        "batch " + batch.batchIndex() + " left " + buf.readableBytes() + " unread bytes");
                assertTrue(helper, read.batchIndex() == batch.batchIndex() && read.batchCount() == batch.batchCount(),
                        "batch header did not round trip");
                decoded.addAll(read.recipes());
            } finally {
                buf.release();
            }
        }

        // What the client would rebuild from the wire has to match what the server holds, type by type.
        RecipeMap rebuilt = RecipeMap.create(decoded);
        for (RecipeType<?> type : syncedTypes()) {
            int before = recipeMap.byType(cast(type)).size();
            int after = rebuilt.byType(cast(type)).size();
            assertTrue(helper, before == after,
                    "recipe type " + type + ": server holds " + before + ", the wire carried " + after);
        }

        helper.succeed();
    }

    // ----- helpers -----

    private static List<RecipeType<?>> syncedTypes() {
        return List.of(
                Recipes.BREWING_CAULDRON_RECIPE.get(),
                Recipes.CLOTHESLINE_RECIPE.get(),
                Recipes.SANGUINE_ALTAR_RECIPE.get());
    }

    private static Set<ResourceKey<Recipe<?>>> syncedRecipeKeys(RecipeMap recipeMap) {
        Set<RecipeType<?>> types = new HashSet<>(syncedTypes());
        Set<ResourceKey<Recipe<?>>> keys = new HashSet<>();
        for (RecipeHolder<?> holder : recipeMap.values()) {
            if (types.contains(holder.value().getType())) {
                keys.add(holder.id());
            }
        }
        return keys;
    }

    @SuppressWarnings("unchecked")
    private static <I extends net.minecraft.world.item.crafting.RecipeInput, T extends Recipe<I>> RecipeType<T> cast(RecipeType<?> type) {
        return (RecipeType<T>) type;
    }

    private static void assertTrue(GameTestHelper helper, boolean condition, String message) {
        if (!condition) {
            throw helper.assertionException(Component.literal(message));
        }
    }
}
