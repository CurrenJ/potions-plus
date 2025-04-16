package grill24.potionsplus.core;

import com.mojang.brigadier.arguments.StringArgumentType;
import grill24.potionsplus.block.PotionsPlusOreBlock;
import grill24.potionsplus.client.integration.jei.JeiPotionsPlusPlugin;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Stream;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = ModInfo.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
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
