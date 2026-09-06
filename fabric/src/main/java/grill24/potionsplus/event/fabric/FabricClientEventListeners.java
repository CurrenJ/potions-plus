package grill24.potionsplus.event.fabric;

import com.mojang.datafixers.util.Pair;
import grill24.potionsplus.alchemy.EffectComparison;
import grill24.potionsplus.event.AnimatedItemTooltipEvent;
import grill24.potionsplus.event.ItemListenersGame;
import grill24.potionsplus.item.WeightDataComponent;
import grill24.potionsplus.item.tooltip.BrewingTooltips;
import grill24.potionsplus.item.tooltip.PotionEffectTooltips;
import grill24.potionsplus.utility.ClientItemStacksTooltip;
import grill24.potionsplus.utility.ClientTickHandler;
import grill24.potionsplus.utility.DelayedEvents;
import grill24.potionsplus.utility.ItemStacksTooltip;
import grill24.potionsplus.utility.TickHandler;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.rendering.v1.ClientTooltipComponentCallback;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Fabric equivalents of the NeoForge client-side event listeners, registered as fabric-api
 * callbacks from {@code PotionsPlusFabricClient.onInitializeClient()}.
 */
public final class FabricClientEventListeners {

    private FabricClientEventListeners() {
    }

    public static void register() {
        registerTicks();
        registerTooltipComponentFactory();
        registerTooltip();
    }

    // ----- NeoDelayedEvents.onClientTickEnd + ClientGameListeners -----

    private static void registerTicks() {
        ClientTickEvents.END_CLIENT_TICK.register(minecraft -> {
            DelayedEvents.tick(TickHandler.ticks());
            ClientTickHandler.clientTickEnd();
        });

        // RenderFrameEvent.Post equivalent. Only fires while a level is being rendered (not at the
        // main menu) - acceptable since ClientTickHandler is only consumed for in-game tooltip timing.
        LevelRenderEvents.START_MAIN.register(context ->
                ClientTickHandler.renderTick(Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true)));
    }

    // ----- ClientTooltipComponentFactoriesListeners -----

    private static void registerTooltipComponentFactory() {
        ClientTooltipComponentCallback.EVENT.register(data ->
                data instanceof ItemStacksTooltip tooltip
                        ? new ClientItemStacksTooltip(tooltip.items(), tooltip.hideUnknownPotionIngredients(), tooltip.renderItemDecorations())
                        : null);
    }

    // ----- NeoItemListeners.onToolTipEvent -----

    private static float animationStartTimestamp = 0;
    private static float lastTooltipTimestamp = 0;
    private static ItemStack lastItemStack = ItemStack.EMPTY;

    private static void registerTooltip() {
        ItemTooltipCallback.EVENT.register((stack, context, flags, tooltipLines) -> {
            List<List<Component>> tooltipMessages = getTooltipMessages(stack);
            animateTooltipMessages(tooltipMessages, tooltipLines, stack);
        });
    }

    private static List<List<Component>> getTooltipMessages(ItemStack itemStack) {
        List<AnimatedItemTooltipEvent.TooltipLines> tooltipMessages = new ArrayList<>();
        Player player = Minecraft.getInstance().player;
        if (player != null) {
            AnimatedItemTooltipEvent.Add addEvent = new AnimatedItemTooltipEvent.Add(player, itemStack, tooltipMessages);
            WeightDataComponent.onTooltip(addEvent);
            BrewingTooltips.onBrewingTooltip(addEvent);
            PotionEffectTooltips.onPotionEffectTooltip(addEvent);
        }

        return AnimatedItemTooltipEvent.getPriorityOrderTooltipLines(tooltipMessages);
    }

    private static void animateTooltipMessages(List<List<Component>> tooltipMessages, List<Component> tooltipLines, ItemStack itemStack) {
        if (!EffectComparison.matches(lastItemStack, itemStack, EffectComparison.MatchCriteria.EXACT_MATCH) || ClientTickHandler.total() - lastTooltipTimestamp > 10.0F) {
            animationStartTimestamp = ClientTickHandler.total();
        }

        for (int i = 0; i < tooltipMessages.size(); i++) {
            List<Component> tooltipMessage = tooltipMessages.get(i);

            int delayTicks = i * 2;
            Pair<MutableComponent, Integer> animatedComponent = ItemListenersGame.animateComponentText(tooltipMessage, ItemListenersGame.durationUpgradeTextAnimationDurationTicks, delayTicks, animationStartTimestamp);
            if (animatedComponent.getSecond() > 0 || i < 2) {
                tooltipLines.add(animatedComponent.getFirst());
            }
        }

        lastTooltipTimestamp = ClientTickHandler.total();
        lastItemStack = itemStack;
    }
}
