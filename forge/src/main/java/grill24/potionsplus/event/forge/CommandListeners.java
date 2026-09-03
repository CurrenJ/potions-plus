package grill24.potionsplus.event.forge;

import grill24.potionsplus.command.PpCommands;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;

/**
 * Forge equivalent of NeoForge's {@code event/neoforge/NeoCommandEvents} (Phase 7 "Commands /
 * input" bucket). {@code RegisterCommandsEvent} lives on the game bus on Forge, same as NeoForge's
 * {@code EventBusSubscriber.Bus.GAME} - registered via {@link MinecraftForge#EVENT_BUS}, matching
 * this module's {@code EffectListeners}/{@code TickListeners} explicit-registration style.
 * {@code potionHand} is NOT registered here - it needs
 * {@code PotionsRegistrar.FLYING_TIME_POTIONS}, still NeoForge-only pending Phase 5's
 * runtime-recipe remainder. See docs/multi-loader-expansion.md.
 */
public final class CommandListeners {
    private CommandListeners() {
    }

    public static void register() {
        MinecraftForge.EVENT_BUS.addListener((RegisterCommandsEvent event) -> PpCommands.register(event.getDispatcher()));
    }
}
