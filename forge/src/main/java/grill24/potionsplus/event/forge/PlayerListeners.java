package grill24.potionsplus.event.forge;

import grill24.potionsplus.behaviour.ClotheslineBehaviour;
import grill24.potionsplus.behaviour.MossBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

/**
 * Forge equivalent of NeoForge's {@code event.neoforge.PlayerListeners#onItemPickedUp}/
 * {@code #onPlayerJoin}/{@code #on(RightClickBlock)} ({@code ItemEntityPickupEvent.Post}/
 * {@code EntityJoinLevelEvent}/{@code PlayerInteractEvent.RightClickBlock}). All three events exist
 * on Forge 52.1.2 (javap-confirmed against {@code forge-1.21.1-52.1.2-universal-srg.jar}:
 * {@code EntityItemPickupEvent(Player, ItemEntity)}, {@code EntityJoinLevelEvent(Entity, Level[, boolean])},
 * and {@code PlayerInteractEvent.RightClickBlock} - the same event class NeoForge's own listener
 * uses, both being pre-split forks of the same Forge event surface), so all three are ported.
 * {@code EntityItemPickupEvent} fires *before* the pickup (the {@code ItemEntity}'s stack is still
 * whole), unlike NeoForge's {@code .Post} variant; harmless here since the shared
 * {@link grill24.potionsplus.event.PlayerListeners#onItemPickedUp} body immediately reduces to a
 * single-count copy for identity purposes only. Plain {@link MinecraftForge#EVENT_BUS} explicit
 * listeners, matching this module's established style (see {@code event.forge.AdvancementListeners}/
 * {@code TickListeners}).
 */
public final class PlayerListeners {
    private PlayerListeners() {
    }

    public static void register() {
        MinecraftForge.EVENT_BUS.addListener((EntityItemPickupEvent event) -> {
            if (event.getEntity() instanceof ServerPlayer serverPlayer) {
                grill24.potionsplus.event.PlayerListeners.onItemPickedUp(serverPlayer, event.getItem().getItem());
            }
        });

        MinecraftForge.EVENT_BUS.addListener((EntityJoinLevelEvent event) -> {
            if (event.getEntity() instanceof ServerPlayer serverPlayer) {
                grill24.potionsplus.event.PlayerListeners.onPlayerJoin(serverPlayer);
            }
        });

        MinecraftForge.EVENT_BUS.addListener((PlayerInteractEvent.RightClickBlock event) -> {
            BlockPos pos = event.getPos();
            if (MossBehaviour.doMossInteractions(event.getLevel(), pos, event.getItemStack(), event.getEntity(), event.getHand())) {
                event.setCanceled(true);
                return;
            }
            if (ClotheslineBehaviour.doClotheslineInteractions(event.getLevel(), pos, event.getItemStack(), event.getEntity(), event.getHand())) {
                event.setCanceled(true);
            }
        });
    }
}
