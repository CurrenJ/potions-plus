package grill24.potionsplus.event.fabric;

import grill24.potionsplus.behaviour.ClotheslineBehaviour;
import grill24.potionsplus.behaviour.MossBehaviour;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;

/**
 * Fabric equivalent of NeoForge's {@code event.neoforge.PlayerListeners} join/right-click-block
 * halves ({@code EntityJoinLevelEvent}, {@code PlayerInteractEvent.RightClickBlock}).
 * {@code onItemPickedUp}'s equivalent lives in {@code mixin.fabric.ItemEntityMixin} instead (no
 * fabric-api event for item pickup - see that class's javadoc).
 *
 * <p>{@link #register()} is called once from {@code PotionsPlusFabric#onInitialize}, which runs on
 * both the dedicated server and the client's internal server thread (Fabric's common entrypoint) -
 * registering here (not separately in a client entrypoint too) avoids double-registering
 * {@link UseBlockCallback#EVENT}, which is a single shared {@code Event<T>} invoked independently by
 * whichever logical side actually processes the block-use packet.
 */
public final class PlayerListeners {
    private PlayerListeners() {
    }

    public static void register() {
        ServerEntityEvents.ENTITY_LOAD.register((entity, level) -> {
            if (entity instanceof ServerPlayer player) {
                grill24.potionsplus.event.PlayerListeners.onPlayerJoin(player);
            }
        });

        // fabric-events-interaction-v0's UseBlockCallback (interact(Player, Level, InteractionHand,
        // BlockHitResult) -> InteractionResult) is the closest equivalent to NeoForge's
        // PlayerInteractEvent.RightClickBlock; javap-confirmed against fabric-events-interaction-v0
        // 0.7.13 (resolved via :fabric:dependencies).
        UseBlockCallback.EVENT.register((player, level, hand, hitResult) -> {
            BlockPos pos = hitResult.getBlockPos();
            ItemStack stack = player.getItemInHand(hand);
            if (MossBehaviour.doMossInteractions(level, pos, stack, player, hand)) {
                return InteractionResult.SUCCESS;
            }
            if (ClotheslineBehaviour.doClotheslineInteractions(level, pos, stack, player, hand)) {
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        });
    }
}
