package grill24.potionsplus.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import grill24.potionsplus.core.PotionsPlus;
import grill24.potionsplus.persistence.PlayerBrewingKnowledge;
import grill24.potionsplus.persistence.SavedData;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.GameType;

import java.util.Map;
import java.util.UUID;

/**
 * Loader-agnostic debug command tree (Phase 7 "Commands / input" bucket), registered by each
 * platform's command-registration hook, only ever wired up when {@link PotionsPlus.Debug#DEBUG}
 * is true. Mirrors 26.1.2's {@code common/command/PpCommands.java}.
 *
 * <p>26.1.2's {@code potionHand} subcommand is NOT ported here - it needs
 * {@code Potions.FLYING_TIME_POTIONS}, which on this branch is still
 * {@code neoforge/core/neoforge/potion/PotionsRegistrar.FLYING_TIME_POTIONS} pending Phase 5's
 * runtime-recipe/seeded-potion-generation remainder (same blocker already recorded against
 * {@code AdvancementListeners} and {@code CommonCommands} in the plan doc). NeoForge keeps that one
 * subcommand registered separately from {@link grill24.potionsplus.core.neoforge.NeoCommandEvents}
 * via a second {@code dispatcher.register(...)} call under the same {@code potionsplus} literal -
 * Brigadier merges children of same-named literal nodes registered separately, so this doesn't
 * require touching the tree here.</p>
 */
public final class PpCommands {
    public static int expiryTime = 6000;

    private PpCommands() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        if (!PotionsPlus.Debug.DEBUG) return;

        dispatcher.register(Commands.literal("potionsplus")
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
        );
    }
}
