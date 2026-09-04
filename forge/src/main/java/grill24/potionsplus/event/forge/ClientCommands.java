package grill24.potionsplus.event.forge;

import com.mojang.brigadier.arguments.StringArgumentType;
import grill24.potionsplus.client.integration.jei.JeiPotionsPlusPlugin;
import grill24.potionsplus.utility.ModInfo;
import grill24.potionsplus.core.PotionsPlus;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Forge equivalent of NeoForge's {@code event.neoforge.ClientCommands}
 * ({@code RegisterClientCommandsEvent}). Forge 52.1.2's event carries the exact same
 * {@code CommandDispatcher<CommandSourceStack>} shape as NeoForge's (javap-confirmed against
 * {@code forge-1.21.1-52.1.2-universal-srg.jar}), so this is a byte-identical port apart from the
 * event-class package and {@code @Mod.EventBusSubscriber} annotation shape (matching
 * {@code core.forge.Renderers}'s dist-gated mod-bus pattern). {@code JeiPotionsPlusPlugin} - this
 * class's only real dependency - is {@code common/} and JEI is wired on all three loaders (Phase
 * 11), so the blocker Phase 7 originally recorded here ("Decision 3 'JEI on all three' is Phase 11
 * scope") is now cleared.
 */
@Mod.EventBusSubscriber(modid = ModInfo.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientCommands {
    @SubscribeEvent
    public static void registerCommands(RegisterClientCommandsEvent event) {
        if (!PotionsPlus.Debug.DEBUG) {
            return;
        }

        event.getDispatcher().register(
                Commands.literal("potionsplus")
                        .then(Commands.literal("reveal")
                                .requires(source -> source.hasPermission(2))
                                .executes(context -> {
                                    PotionsPlus.LOGGER.info("Reloading PotionsPlus data");
                                    Player player = Minecraft.getInstance().player;
                                    if (player == null) {
                                        return 0;
                                    }
                                    if (player.hasPermissions(2)) {
                                        PotionsPlus.Debug.shouldRevealAllRecipes = !PotionsPlus.Debug.shouldRevealAllRecipes;
                                        context.getSource().sendSuccess(() -> Component.literal(PotionsPlus.Debug.shouldRevealAllRecipes ? "true" : "false"), true);
                                        JeiPotionsPlusPlugin.scheduleUpdateJeiHiddenBrewingCauldronRecipes();
                                    }
                                    return 1;
                                }))
                        .then(Commands.literal("dumpResource")
                                .requires((source) -> source.hasPermission(2))
                                .then(Commands.argument("longId", ResourceLocationArgument.id())
                                        .executes(context -> {
                                            Player player = Minecraft.getInstance().player;
                                            ResourceManager rm = Minecraft.getInstance().getResourceManager();
                                            ResourceLocation longId = ResourceLocationArgument.getId(context, "longId");
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
    }
}
