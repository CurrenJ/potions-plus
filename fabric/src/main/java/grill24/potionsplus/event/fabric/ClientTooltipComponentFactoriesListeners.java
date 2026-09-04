package grill24.potionsplus.event.fabric;

import grill24.potionsplus.utility.ClientItemStacksTooltip;
import grill24.potionsplus.utility.ItemStacksTooltip;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;

/**
 * Fabric equivalent of NeoForge's {@code event.neoforge.ClientTooltipComponentFactoriesListeners}
 * ({@code RegisterClientTooltipComponentFactoriesEvent}). {@code fabric-rendering-v1} 5.1.0's
 * {@link TooltipComponentCallback} (javap-confirmed: {@code getComponent(TooltipComponent) ->
 * ClientTooltipComponent}, single-return, invoked once per registered factory until one returns
 * non-null - same "first match wins" shape as NeoForge's/Forge's class-keyed registries) is the
 * fabric-api equivalent Phase 7's original text predicted; the real (former) blocker was
 * {@link ClientItemStacksTooltip} itself, not the registration point - see that class's javadoc.
 */
public final class ClientTooltipComponentFactoriesListeners {
    private ClientTooltipComponentFactoriesListeners() {
    }

    public static void registerClient() {
        TooltipComponentCallback.EVENT.register(component -> {
            if (component instanceof ItemStacksTooltip itemStacksTooltip) {
                return new ClientItemStacksTooltip(itemStacksTooltip.items(), itemStacksTooltip.hideUnknownPotionIngredients(), itemStacksTooltip.renderItemDecorations());
            }
            return null;
        });
    }
}
