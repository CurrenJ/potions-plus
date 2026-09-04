package grill24.potionsplus.event.fabric;

import grill24.potionsplus.client.integration.jei.JeiPotionsPlusPlugin;
import grill24.potionsplus.core.PotionsPlus;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Fabric equivalent of NeoForge's {@code event.neoforge.ClientCommands}
 * ({@code RegisterClientCommandsEvent}). {@code JeiPotionsPlusPlugin} - this class's only real
 * dependency - is {@code common/} and JEI is wired on all three loaders (Phase 11), clearing the
 * blocker Phase 7 originally recorded here. Unlike NeoForge/Forge, {@code fabric-command-api-v2}'s
 * client command tree runs over {@link FabricClientCommandSource} (javap-confirmed shape:
 * {@code sendFeedback}/{@code sendError}/{@code getPlayer}/{@code getWorld}, no permission-check
 * accessor at all - client commands aren't gated by op level the way {@code CommandSourceStack} is),
 * not {@code CommandSourceStack}, so this isn't a byte-identical port: the {@code .requires(source
 * -> source.hasPermission(2))} guards are dropped (no equivalent exists client-side; harmless for a
 * debug-only command tree already gated behind {@code PotionsPlus.Debug.DEBUG}) and Brigadier nodes
 * are built via {@link ClientCommandManager#literal}/{@code argument} instead of
 * {@code net.minecraft.commands.Commands}.
 */
public final class ClientCommands {
    private ClientCommands() {
    }

    public static void registerClient() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            if (!PotionsPlus.Debug.DEBUG) {
                return;
            }

            dispatcher.register(
                    ClientCommandManager.literal("potionsplus")
                            .then(ClientCommandManager.literal("reveal")
                                    .executes(context -> {
                                        PotionsPlus.LOGGER.info("Reloading PotionsPlus data");
                                        FabricClientCommandSource source = context.getSource();
                                        PotionsPlus.Debug.shouldRevealAllRecipes = !PotionsPlus.Debug.shouldRevealAllRecipes;
                                        source.sendFeedback(Component.literal(PotionsPlus.Debug.shouldRevealAllRecipes ? "true" : "false"));
                                        JeiPotionsPlusPlugin.scheduleUpdateJeiHiddenBrewingCauldronRecipes();
                                        return 1;
                                    }))
                            .then(ClientCommandManager.literal("dumpResource")
                                    .then(ClientCommandManager.argument("longId", ResourceLocationArgument.id())
                                            .executes(context -> {
                                                LocalPlayer player = context.getSource().getPlayer();
                                                ResourceManager rm = Minecraft.getInstance().getResourceManager();
                                                ResourceLocation longId = context.getArgument("longId", ResourceLocation.class);
                                                if (player == null) {
                                                    return 0;
                                                }

                                                Optional<Resource> resource = rm.getResource(longId);
                                                if (resource.isPresent()) {
                                                    player.sendSystemMessage(Component.literal("Resource found: " + longId));
                                                    try {
                                                        Stream<String> rawDump = resource.get().openAsReader().lines();
                                                        Optional<String> noFormatting = rawDump.reduce(String::concat);
                                                        for (String line : (Iterable<String>) rawDump::iterator) {
                                                            player.sendSystemMessage(Component.literal(line));
                                                        }

                                                        if (noFormatting.isPresent()) {
                                                            PotionsPlus.LOGGER.info(noFormatting.get());
                                                        } else {
                                                            PotionsPlus.LOGGER.info("No resource text contents found.");
                                                        }
                                                    } catch (IOException e) {
                                                        throw new RuntimeException(e);
                                                    }
                                                    return 1;
                                                } else {
                                                    player.sendSystemMessage(Component.literal("Resource not found: " + longId));
                                                    return 0;
                                                }
                                            }))
                            )
            );
        });
    }
}
