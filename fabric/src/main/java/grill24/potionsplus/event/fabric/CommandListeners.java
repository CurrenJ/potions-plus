package grill24.potionsplus.event.fabric;

import grill24.potionsplus.command.PpCommands;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

/**
 * Fabric equivalent of NeoForge's {@code event/neoforge/NeoCommandEvents} (Phase 7 "Commands /
 * input" bucket). {@code potionHand} is NOT registered here - it needs
 * {@code PotionsRegistrar.FLYING_TIME_POTIONS}, still NeoForge-only pending Phase 5's
 * runtime-recipe remainder. See docs/multi-loader-expansion.md.
 */
public final class CommandListeners {
    private CommandListeners() {
    }

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, ctx, selection) -> PpCommands.register(dispatcher));
    }
}
