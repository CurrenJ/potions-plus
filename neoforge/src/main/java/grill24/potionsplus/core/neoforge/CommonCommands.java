package grill24.potionsplus.core.neoforge;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import grill24.potionsplus.alchemy.PotionData;
import grill24.potionsplus.core.neoforge.potion.PotionsRegistrar;
import grill24.potionsplus.persistence.PlayerBrewingKnowledge;
import grill24.potionsplus.persistence.SavedData;
import grill24.potionsplus.utility.ModInfo;
import grill24.potionsplus.core.PotionsPlus;
import net.minecraft.commands.Commands;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.GameType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.util.*;

@EventBusSubscriber(modid = ModInfo.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class CommonCommands {
    public static int expiryTime = 6000;

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        if (!PotionsPlus.Debug.DEBUG) return;

        event.getDispatcher().register(Commands.literal("potionsplus")
                .then(Commands.literal("savedData")
                        .requires((source) -> source.hasPermission(2))
                        .then(Commands.literal("clear")
                                .executes(context -> {
                                    SavedData.instance.clear();
                                    context.getSource().sendSuccess(() -> Component.literal("Cleared saved data."), true);
                                    return 1;
                                })
                                .then(Commands.literal("playerData")
                                        .executes(context -> {
                                            SavedData.instance.playerDataMap.clear();
                                            SavedData.instance.setDirty();
                                            context.getSource().sendSuccess(() -> Component.literal("Cleared player data."), true);
                                            return 1;
                                        })
                                )
                                .then(Commands.literal("seededPotionRecipes")
                                        .executes(context -> {
                                            SavedData.instance.seededPotionRecipes.clear();
                                            SavedData.instance.setDirty();
                                            context.getSource().sendSuccess(() -> Component.literal("Cleared seeded potion recipes."), true);
                                            return 1;
                                        })
                                )
                        )
                        .then(Commands.literal("info")
                                .executes(context -> {
                                    SavedData savedData = SavedData.instance;
                                    context.getSource().sendSuccess(() -> Component.literal("Player data entries: " + savedData.playerDataMap.size()), true);
                                    context.getSource().sendSuccess(() -> Component.literal("Seeded potion recipes: " + savedData.seededPotionRecipes.size()), true);
                                    context.getSource().sendSuccess(() -> Component.literal("Item entity expiry time: " + expiryTime + " ticks"), true);
                                    return 1;
                                })
                                .then(Commands.literal("playerData")
                                        .executes(context -> {
                                            SavedData savedData = SavedData.instance;
                                            context.getSource().sendSuccess(() -> Component.literal("Player data entries: " + savedData.playerDataMap.size()), true);
                                            return 1;
                                        })
                                        .then(Commands.literal("verbose")
                                                .executes(context -> {
                                                    SavedData savedData = SavedData.instance;
                                                    for (Map.Entry<UUID, PlayerBrewingKnowledge> entry : savedData.playerDataMap.entrySet()) {
                                                        context.getSource().sendSuccess(() -> Component.literal(entry.getKey().toString()), true);
                                                    }
                                                    return 1;
                                                })
                                        )
                                )
                                .then(Commands.literal("bcRecipes")
                                        .executes(context -> {
                                            SavedData savedData = SavedData.instance;
                                            context.getSource().sendSuccess(() -> Component.literal("Seeded potion recipes: " + savedData.seededPotionRecipes.size()), true);
                                            return 1;
                                        })
                                        .then(Commands.literal("verbose")
                                                .executes(context -> {
                                                    SavedData savedData = SavedData.instance;
                                                    for (int i = 0; i < savedData.seededPotionRecipes.size(); i++) {
                                                        int finalI = i;
                                                        context.getSource().sendSuccess(() -> Component.literal((finalI + 1) + ". " + savedData.seededPotionRecipes.get(finalI)), true);
                                                    }
                                                    return 1;
                                                })
                                        )
                                )
                        )
                )
                // Takes in integer argument
                .then(Commands.literal("quickItemExpiry")
                        .requires((source) -> source.hasPermission(2))
                        .then(Commands.argument("expiryTime", IntegerArgumentType.integer(-1))
                                .executes(context -> {
                                    expiryTime = IntegerArgumentType.getInteger(context, "expiryTime");
                                    String seconds = String.format("%.2f", expiryTime / 20f);
                                    context.getSource().sendSuccess(() -> Component.literal("Set item entity expiry time to " + expiryTime + " ticks. (" + seconds + " seconds)"), true);
                                    return 1;
                                })
                        )
                )
                .then(Commands.literal("caveDiver")
                        .requires((source) -> source.hasPermission(2))
                        .executes(context -> {
                            if (context.getSource().getEntity() instanceof ServerPlayer player) {
                                player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 6000, 0, false, false, false));
                                player.setGameMode(GameType.SPECTATOR);
                            }

                            return 1;
                        })
                )
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
