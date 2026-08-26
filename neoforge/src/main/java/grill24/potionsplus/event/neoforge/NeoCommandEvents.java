package grill24.potionsplus.event.neoforge;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import grill24.potionsplus.core.potion.Potions;
import grill24.potionsplus.debug.Debug;
import grill24.potionsplus.item.GeneticCropItem;
import grill24.potionsplus.persistence.PlayerBrewingKnowledge;
import grill24.potionsplus.persistence.SavedData;
import grill24.potionsplus.utility.*;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.GameType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.util.*;

/**
 * NeoForge-specific command registration and handlers.
 * Commands extracted from the old CommonCommands.java.
 */
@EventBusSubscriber(modid = ModInfo.MOD_ID)
public class NeoCommandEvents {
    public static int expiryTime = 6000;

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        if (!Debug.DEBUG) return;

        event.getDispatcher().register(Commands.literal("potionsplus")
                .then(Commands.literal("savedData")
                        .requires((source) -> source.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
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
                .then(Commands.literal("quickItemExpiry")
                        .requires((source) -> source.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
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
                        .requires((source) -> source.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
                        .executes(context -> {
                            if (context.getSource().getEntity() instanceof ServerPlayer player) {
                                player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 6000, 0, false, false, false));
                                player.setGameMode(GameType.SPECTATOR);
                            }
                            return 1;
                        })
                )
                .then(Commands.literal("potionHand")
                        .requires((source) -> source.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
                        .executes(context -> {
                            if (context.getSource().getEntity() instanceof ServerPlayer player) {
                                player.getMainHandItem().set(net.minecraft.core.component.DataComponents.POTION_CONTENTS, new PotionContents(Potions.FLYING_TIME_POTIONS.potion));
                            }
                            return 1;
                        })
                )
                .then(Commands.literal("genetics")
                        .then(Commands.literal("set")
                                .then(Commands.literal("color")
                                        .then(Commands.argument("value", IntegerArgumentType.integer())
                                                .requires((source) -> source.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
                                                .executes(context -> {
                                                    if (context.getSource().getEntity() instanceof ServerPlayer player) {
                                                        int value = IntegerArgumentType.getInteger(context, "value");
                                                        if (player.getMainHandItem().getItem() instanceof GeneticCropItem cropItem) {
                                                            cropItem.setColorChromosomeValue(player.getMainHandItem(), value);
                                                            context.getSource().sendSuccess(() -> Component.literal("Set genetic data on main hand item."), true);
                                                        }
                                                    }
                                                    return 1;
                                                })
                                        )
                                )
                                .then(Commands.literal("weight")
                                        .then(Commands.argument("value", IntegerArgumentType.integer())
                                                .requires((source) -> source.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
                                                .executes(context -> {
                                                    if (context.getSource().getEntity() instanceof ServerPlayer player) {
                                                        int value = IntegerArgumentType.getInteger(context, "value");
                                                        if (player.getMainHandItem().getItem() instanceof GeneticCropItem cropItem) {
                                                            cropItem.setWeightChromosomeValue(player.getMainHandItem(), value);
                                                            context.getSource().sendSuccess(() -> Component.literal("Set genetic data on main hand item."), true);
                                                        }
                                                    }
                                                    return 1;
                                                })
                                        )
                                )
                                .then(Commands.argument("chromosomeIndex", IntegerArgumentType.integer())
                                        .then(Commands.argument("value", IntegerArgumentType.integer())
                                                .executes(context -> {
                                                    if (context.getSource().getEntity() instanceof ServerPlayer player) {
                                                        ItemStack stack = player.getMainHandItem();
                                                        if (stack.getItem() instanceof GeneticCropItem cropItem) {
                                                            int chromosomeIndex = IntegerArgumentType.getInteger(context, "chromosomeIndex");
                                                            int value = IntegerArgumentType.getInteger(context, "value");
                                                            ItemStack result = cropItem.setChromosomeValue(stack, chromosomeIndex, value);
                                                            player.setItemInHand(player.getUsedItemHand(), result);
                                                            context.getSource().sendSuccess(() -> Component.literal("Randomized genetic data on main hand item."), true);
                                                        }
                                                    }
                                                    return 1;
                                                })
                                        )
                                )
                        )
                        .then(Commands.literal("createOffspring")
                                .requires((source) -> source.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
                                .executes(context -> createOffspring(context, 1))
                                .then(Commands.argument("repeat", IntegerArgumentType.integer())
                                        .executes(context -> createOffspring(context, IntegerArgumentType.getInteger(context, "repeat")))
                                )
                        )
                )
        );
    }

    private static int createOffspring(CommandContext<CommandSourceStack> context, int repeat) {
        if (context.getSource().getEntity() instanceof ServerPlayer player) {
            for (int i = 0; i < repeat; i++) {
                ItemStack mainHandItem = player.getMainHandItem();
                ItemStack offHandItem = player.getOffhandItem();
                if (mainHandItem.isEmpty() || offHandItem.isEmpty()) {
                    context.getSource().sendFailure(Component.literal("You must have items in both hands to create offspring."));
                    return 0;
                }
                grill24.potionsplus.utility.Genotype mainHandGenotype = mainHandItem.get(grill24.potionsplus.core.DataComponents.GENETIC_DATA);
                grill24.potionsplus.utility.Genotype offHandGenotype = offHandItem.get(grill24.potionsplus.core.DataComponents.GENETIC_DATA);
                if (mainHandGenotype == null || offHandGenotype == null) {
                    context.getSource().sendFailure(Component.literal("Both items must have genetic data to create offspring."));
                    return 0;
                }
                ItemStack offspring = new ItemStack(mainHandItem.getItem());
                grill24.potionsplus.utility.Genotype offspringGenotype = grill24.potionsplus.utility.Genotype.crossover(mainHandGenotype, offHandGenotype);
                offspringGenotype = grill24.potionsplus.utility.Genotype.tryUniformMutate(offspringGenotype, 0.01F);
                offspring.set(grill24.potionsplus.core.DataComponents.GENETIC_DATA, offspringGenotype);
                if (offspring.getItem() instanceof GeneticCropItem geneticCropItem) {
                    offspring = geneticCropItem.onGeneticDataChanged(offspring);
                }
                grill24.potionsplus.utility.InvUtil.giveOrDropItem(player, offspring);
            }
            context.getSource().sendSuccess(() -> Component.literal("Created offspring item."), true);
        }
        return 1;
    }
}
