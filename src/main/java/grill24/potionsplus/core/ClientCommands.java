package grill24.potionsplus.core;

import grill24.potionsplus.client.integration.jei.JeiPotionsPlusPlugin;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ModInfo.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientCommands {
    public static boolean shouldRevealAllRecipes = false;

    @SubscribeEvent
    public static void registerCommands(RegisterClientCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("potionsplus")
                .then(Commands.literal("reveal")
                        .executes(context -> {
                            PotionsPlus.LOGGER.info("Reloading PotionsPlus data");
                            Player player = Minecraft.getInstance().player;
                            if (player == null) {
                                return 0;
                            }
                            if (player.hasPermissions(2)) {
                                shouldRevealAllRecipes = !shouldRevealAllRecipes;
                                context.getSource().sendSuccess(() -> Component.literal(shouldRevealAllRecipes ? "true" : "false"), true);
                                JeiPotionsPlusPlugin.scheduleUpdateJeiHiddenBrewingCauldronRecipes();
                            }
                            return 1;
                        }))
        );
    }
}
