package grill24.potionsplus.core;

import grill24.potionsplus.client.integration.jei.JeiPotionsPlusPlugin;
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
        event.getDispatcher().register(Commands.literal("potionsplus")
                .then(Commands.literal("reveal")
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
        );
    }
}
