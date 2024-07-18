package grill24.potionsplus.core;

import grill24.potionsplus.persistence.PlayerBrewingKnowledge;
import grill24.potionsplus.persistence.SavedData;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = ModInfo.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CommonCommands {

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("potionsplus")
                .then(Commands.literal("savedData")
                        .requires((source) -> source.hasPermission(2))
                        .then(Commands.literal("clear")
                                .executes(context -> {
                                    SavedData.instance.clear();
                                    context.getSource().sendSuccess(new TextComponent("Cleared saved data."), true);
                                    return 1;
                                })
                                .then(Commands.literal("playerData")
                                        .executes(context -> {
                                            SavedData.instance.playerDataMap.clear();
                                            SavedData.instance.setDirty();
                                            context.getSource().sendSuccess(new TextComponent("Cleared player data."), true);
                                            return 1;
                                        })
                                )
                                .then(Commands.literal("seededPotionRecipes")
                                        .executes(context -> {
                                            SavedData.instance.seededPotionRecipes.clear();
                                            SavedData.instance.setDirty();
                                            context.getSource().sendSuccess(new TextComponent("Cleared seeded potion recipes."), true);
                                            return 1;
                                        })
                                )
                        )
                        .then(Commands.literal("info")
                                .executes(context -> {
                                    SavedData savedData = SavedData.instance;
                                    context.getSource().sendSuccess(new TextComponent("Player data entries: " + savedData.playerDataMap.size()), true);
                                    context.getSource().sendSuccess(new TextComponent("Seeded potion recipes: " + savedData.seededPotionRecipes.size()), true);
                                    return 1;
                                })
                                .then(Commands.literal("playerData")
                                        .executes(context -> {
                                            SavedData savedData = SavedData.instance;
                                            context.getSource().sendSuccess(new TextComponent("Player data entries: " + savedData.playerDataMap.size()), true);
                                            return 1;
                                        })
                                        .then(Commands.literal("verbose")
                                                .executes(context -> {
                                                    SavedData savedData = SavedData.instance;
                                                    for (Map.Entry<UUID, PlayerBrewingKnowledge> entry : savedData.playerDataMap.entrySet()) {
                                                        context.getSource().sendSuccess(new TextComponent(entry.getKey().toString()), true);
                                                    }
                                                    return 1;
                                                })
                                        )
                                )
                                .then(Commands.literal("bcRecipes")
                                        .executes(context -> {
                                            SavedData savedData = SavedData.instance;
                                            context.getSource().sendSuccess(new TextComponent("Seeded potion recipes: " + savedData.seededPotionRecipes.size()), true);
                                            return 1;
                                        })
                                        .then(Commands.literal("verbose")
                                                .executes(context -> {
                                                    SavedData savedData = SavedData.instance;
                                                    for (int i = 0; i < savedData.seededPotionRecipes.size(); i++) {
                                                        context.getSource().sendSuccess(new TextComponent((i + 1) + ". " + savedData.seededPotionRecipes.get(i)), true);
                                                    }
                                                    return 1;
                                                })
                                        )
                                )
                        )
                )
        );
    }
}
