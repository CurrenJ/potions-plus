package grill24.potionsplus.event.neoforge;

import grill24.potionsplus.alchemy.PotionData;
import grill24.potionsplus.command.PpCommands;
import grill24.potionsplus.core.neoforge.potion.PotionsRegistrar;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.alchemy.PotionContents;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

/**
 * NeoForge command-registration hook; the shared command tree lives in the loader-agnostic
 * {@link PpCommands} so Fabric/Forge share the same definitions (Phase 7 "Commands / input"
 * bucket). {@code potionHand} stays here, registered as a second {@code dispatcher.register(...)}
 * call under the same {@code potionsplus} literal - Brigadier merges children of same-named
 * literal nodes registered separately - because it needs {@link PotionsRegistrar#FLYING_TIME_POTIONS},
 * still NeoForge-only pending Phase 5's runtime-recipe remainder. See docs/multi-loader-expansion.md.
 */
@EventBusSubscriber(modid = ModInfo.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class NeoCommandEvents {
    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        PpCommands.register(event.getDispatcher());

        event.getDispatcher().register(Commands.literal("potionsplus")
                .then(Commands.literal("potionHand")
                        .requires((source) -> source.hasPermission(2))
                        .executes(context -> {
                            if (context.getSource().getEntity() instanceof ServerPlayer player) {
                                PotionData.write(player.getMainHandItem(), new PotionContents(PotionsRegistrar.FLYING_TIME_POTIONS.potion));
                            }

                            return 1;
                        })
                )
        );
    }
}
