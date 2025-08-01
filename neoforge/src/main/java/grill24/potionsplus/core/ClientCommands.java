package grill24.potionsplus.core;

import grill24.potionsplus.client.integration.jei.JeiPotionsPlusPlugin;
import grill24.potionsplus.debug.Debug;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = ModInfo.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class ClientCommands {
    @SubscribeEvent
    public static void registerCommands(RegisterClientCommandsEvent event) {
        if (!Debug.DEBUG) {
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
                                        Debug.shouldRevealAllRecipes = !Debug.shouldRevealAllRecipes;
                                        context.getSource().sendSuccess(() -> Component.literal(Debug.shouldRevealAllRecipes ? "true" : "false"), true);

                                        try {
                                            JeiPotionsPlusPlugin.scheduleUpdateJeiHiddenBrewingCauldronRecipes();
                                        } catch (NoClassDefFoundError error) {
                                            PotionsPlus.LOGGER.warn("JEI is not loaded, cannot update hidden brewing cauldron recipes", error);
                                        }
                                    }
                                    return 1;
                                }))
        );
    }
}
