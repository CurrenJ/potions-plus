package grill24.potionsplus.event.forge;

import grill24.potionsplus.utility.ClientItemStacksTooltip;
import grill24.potionsplus.utility.ItemStacksTooltip;
import grill24.potionsplus.utility.ModInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Forge equivalent of NeoForge's {@code event.neoforge.ClientTooltipComponentFactoriesListeners}
 * ({@code RegisterClientTooltipComponentFactoriesEvent}). Forge 52.1.2 has the same event
 * (javap-confirmed against {@code forge-1.21.1-52.1.2-universal-srg.jar}:
 * {@code RegisterClientTooltipComponentFactoriesEvent#register(Class<T>, Function<? super T, ?
 * extends ClientTooltipComponent>)}, identical shape to NeoForge's) - Phase 7's original text was
 * right that this registration is trivial; the real (former) blocker was
 * {@link ClientItemStacksTooltip} itself, not the registration point - see that class's javadoc.
 * {@code @Mod.EventBusSubscriber(bus = MOD, value = Dist.CLIENT)} is FML's own dist-gated
 * discovery, matching {@code core.forge.Renderers}'s established pattern for mod-bus client events
 * (this event implements {@code IModBusEvent}, confirmed via javap) - not
 * {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS}, which only carries game-bus events.
 */
@Mod.EventBusSubscriber(modid = ModInfo.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientTooltipComponentFactoriesListeners {
    private ClientTooltipComponentFactoriesListeners() {
    }

    @SubscribeEvent
    public static void on(final RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(ItemStacksTooltip.class, (itemStacksTooltip) -> new ClientItemStacksTooltip(itemStacksTooltip.items(), itemStacksTooltip.hideUnknownPotionIngredients(), itemStacksTooltip.renderItemDecorations()));
    }
}
