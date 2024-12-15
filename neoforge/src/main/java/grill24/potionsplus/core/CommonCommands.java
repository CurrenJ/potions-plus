package grill24.potionsplus.core;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.serialization.JsonOps;
import grill24.potionsplus.core.potion.Potions;
import grill24.potionsplus.persistence.PlayerBrewingKnowledge;
import grill24.potionsplus.persistence.SavedData;
import grill24.potionsplus.skill.*;
import grill24.potionsplus.skill.ability.AbilityInstance;
import grill24.potionsplus.skill.ability.PlayerAbility;
import grill24.potionsplus.skill.reward.SkillLevelUpRewardsConfiguration;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.GameType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;


import java.util.*;
import java.util.function.Consumer;

@EventBusSubscriber(modid = ModInfo.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class CommonCommands {
    public static int expiryTime = 6000;

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        if(!PotionsPlus.Debug.DEBUG) return;

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
                            if(context.getSource().getEntity() instanceof ServerPlayer player) {
                                player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 6000, 0, false, false, false));
                                player.setGameMode(GameType.SPECTATOR);
                            }

                            return 1;
                        })
                )
                .then(Commands.literal("potionHand")
                    .requires((source) -> source.hasPermission(2))
                    .executes(context -> {
                        if(context.getSource().getEntity() instanceof ServerPlayer player) {
                            player.getMainHandItem().set(DataComponents.POTION_CONTENTS, new PotionContents(Potions.FLYING_TIME_POTIONS.potion));
                        }

                        return 1;
                    })
                )
                .then(Commands.literal("skill")
                        .executes(context -> {
                            if (context.getSource().getPlayer() == null) {
                                return 0;
                            }

                            SkillsData.CODEC.encodeStart(JsonOps.INSTANCE, context.getSource().getPlayer().getData(DataAttachments.SKILL_PLAYER_DATA))
                                .ifSuccess((jsonElement) ->
                                        context.getSource().sendSuccess(() ->
                                                Component.literal(jsonElement.toString()), true))
                                .ifError((jsonElement) ->
                                        context.getSource().sendFailure(Component.literal(jsonElement.toString())));


                            return 1;
                        })
                        .then(Commands.literal("clear")
                                .requires((source) -> source.hasPermission(2))
                                .executes(context -> {
                                    if (context.getSource().getPlayer() == null) {
                                        return 0;
                                    }

                                    SkillsData.updatePlayerData(context.getSource().getPlayer(), (skillsData -> skillsData.clear(context.getSource().getPlayer())));

                                    return 1;
                                })
                        )
                        .then(Commands.literal("ability")
                                .then(Commands.literal("byId")
                                        .then(Commands.argument("abilityId", new ConfiguredPlayerAbilityArgument(event.getBuildContext()))
                                                .then(Commands.literal("toggle")
                                                        .executes(context -> {
                                                            if (context.getSource().getPlayer() == null) {
                                                                return 0;
                                                            }

                                                            ResourceLocation abilityId = ConfiguredPlayerAbilityArgument.getHolder(context, "abilityId").getKey().location();
                                                            SkillsData.updatePlayerData(context.getSource().getPlayer(), (skillsData -> {
                                                                skillsData.getAbilityInstance(context.getSource().registryAccess(), abilityId).ifPresent(abilityInstance -> {
                                                                    abilityInstance.toggle(context.getSource().getPlayer());
                                                                    context.getSource().sendSuccess(() -> abilityInstance.getDescription(true), true);
                                                                });
                                                            }));

                                                            return 1;
                                                        })
                                                )
                                                .executes(context -> {
                                                    if (context.getSource().getPlayer() == null) {
                                                        return 0;
                                                    }

                                                    ResourceLocation abilityId = ConfiguredPlayerAbilityArgument.getHolder(context, "abilityId").getKey().location();
                                                    SkillsData.updatePlayerData(context.getSource().getPlayer(), (skillsData -> {
                                                        MutableComponent component = Component.empty();

                                                        SkillsData.updatePlayerData(context.getSource().getPlayer(), (skillsData1 -> {
                                                            skillsData1.getAbilityInstance(context.getSource().registryAccess(), abilityId).ifPresentOrElse(abilityInstance -> {
                                                                component.append(abilityInstance.getDescription(true));
                                                                context.getSource().sendSuccess(() -> component, true);
                                                            }, () -> context.getSource().sendFailure(Component.literal("No unlocked ability found.")));
                                                        }));
                                                    }));

                                                    return 1;
                                                })
                                        )
                                )
                                .executes(context -> {
                                    if (context.getSource().getPlayer() == null) {
                                        return 0;
                                    }

                                    SkillsData.updatePlayerData(context.getSource().getPlayer(), (skillsData -> {
                                        MutableComponent component = Component.empty();
                                        boolean hasAbilities = false;

                                        for (Map.Entry<ResourceKey<PlayerAbility<?, ?>>, List<AbilityInstance>> entry : skillsData.activeAbilities().entrySet()) {
                                            for (AbilityInstance abilityInstance : entry.getValue()) {
                                                Component abilityComponent = abilityInstance.getDescription();
                                                if (hasAbilities) {
                                                    component.append(Component.literal(", ").withStyle(abilityComponent.getStyle()));
                                                }
                                                component.append(abilityComponent);

                                                hasAbilities = true;
                                            }
                                        }

                                        if (hasAbilities) {
                                            context.getSource().sendSuccess(() -> component, true);
                                        } else {
                                            context.getSource().sendFailure(Component.literal("No abilities found."));
                                        }
                                    }));

                                    return 1;
                                })
                        )
                        .then(Commands.literal("byId")
                            .then(Commands.argument("skillId", new ConfiguredSkillArgument(event.getBuildContext()))
                                    .executes(context -> {
                                        tryConsumeSkillInstance(context, skillInstance -> {
                                            SkillInstance.CODEC.encodeStart(JsonOps.INSTANCE, skillInstance)
                                                    .ifSuccess((jsonElement) ->
                                                            context.getSource().sendSuccess(() ->
                                                                    Component.literal(jsonElement.toString()), true));
                                        });

                                        return 1;
                                    })
                                    .requires((source) -> source.hasPermission(2))
                                    .then(Commands.literal("add")
                                            .then(Commands.argument("points", IntegerArgumentType.integer())
                                                    .executes(context -> {
                                                        if (context.getSource().getPlayer() == null) {
                                                            return 0;
                                                        }

                                                        int points = IntegerArgumentType.getInteger(context, "points");
                                                        tryConsumeSkillInstance(context, skillInstance -> {
                                                            skillInstance.addPoints(context.getSource().getPlayer(), points);
                                                            SavedData.instance.setDirty();
                                                        });

                                                        return 1;
                                                    })
                                            )
                                    )
                                    .requires((source) -> source.hasPermission(2))
                                    .then(Commands.literal("set")
                                            .then(Commands.argument("points", IntegerArgumentType.integer())
                                                    .executes(context -> {
                                                        int points = IntegerArgumentType.getInteger(context, "points");
                                                        tryConsumeSkillInstance(context, skillInstance -> {
                                                            skillInstance.setPoints(points);
                                                            SavedData.instance.setDirty();
                                                        });

                                                        return 1;
                                                    })
                                            )
                                    )
                                    .then(Commands.literal("clear")
                                            .executes(context -> {
                                                tryConsumeSkillInstance(context, skillInstance -> {
                                                    skillInstance.clear();
                                                    SavedData.instance.setDirty();
                                                });

                                                return 1;
                                            })
                                    )
                                    .then(Commands.literal("rewards")
                                            .then(Commands.literal("level")
                                                    .then(Commands.argument("level", IntegerArgumentType.integer())
                                                            .executes(context -> {
                                                                tryConsumeSkillInstance(context, skillInstance -> {
                                                                    context.getSource().sendSuccess(() -> skillInstance.getRewardDescription(context.getSource().getPlayer().registryAccess(), IntegerArgumentType.getInteger(context, "level")), true);
                                                                });


                                                                return 1;
                                                            }))
                                            )
                                            .executes(context -> {
                                                tryConsumeSkillInstance(context, skillInstance -> {
                                                    SkillLevelUpRewardsConfiguration rewardsConfiguration = skillInstance.getConfiguredSkill(context.getSource().registryAccess()).config().getData().rewardsConfiguration();
                                                    // Sorted by level
                                                    TreeMap<Integer, Component> rewardsMap = new TreeMap<>();
                                                    rewardsConfiguration.rewardsDataMap.forEach((level, rewardsData) -> {
                                                        int levelInt = Integer.parseInt(level);
                                                        rewardsMap.put(levelInt, skillInstance.getRewardDescription(context.getSource().getPlayer().registryAccess(), levelInt));
                                                    });

                                                    rewardsMap.forEach((level, component) -> {
                                                        context.getSource().sendSuccess(() -> component, true);
                                                    });
                                                });

                                                return 1;
                                            })
                                    )
                                    .then(Commands.literal("progress")
                                            .executes(context -> {
                                                tryConsumeSkillInstance(context, skillInstance -> {
                                                    context.getSource().sendSuccess(() -> skillInstance.getProgressToNextLevel(context.getSource().getPlayer().registryAccess()), true);
                                                });

                                                return 1;
                                            })
                                    )
                                    .then(Commands.literal("penalty")
                                            .executes(context -> {
                                                SkillsData.updatePlayerData(context.getSource().getPlayer(),
                                                        skillsData -> skillsData.getOrCreate(context.getSource().registryAccess(), ConfiguredSkillArgument.getSkill(context, "skillId"))
                                                                .ifPresent(skillInstance -> {
                                                                    MutableComponent component = Component.empty();
                                                                    component.append(skillInstance.getConfiguredSkill(context.getSource().registryAccess()).getChatHeader());
                                                                    float penalty = skillInstance.applyGrindingPenalty(skillsData.pointEarningHistory(), 0.75F, 1);
                                                                    component.append(Component.literal("-" + String.format("%.2f", (1 - penalty) * 100) + "% ").withStyle(ChatFormatting.RED));
                                                                    component.append(Component.translatable(Translations.TOOLTIP_POTIONSPLUS_SKILL_POINTS_EARNED).withStyle(ChatFormatting.RED));

                                                                    context.getSource().sendSuccess(() -> component, true);
                                                                })
                                                );

                                                return 1;
                                            })
                                    )
                            )
                        )
                )
        );
    }

    private static void tryConsumeSkillInstance(CommandContext<CommandSourceStack> context, Consumer<SkillInstance<?, ?>> consumer) {
        if (context.getSource().getEntity() instanceof ServerPlayer player) {
            ConfiguredSkill<?, ?> configuredSkill = ConfiguredSkillArgument.getSkill(context, "skillId");
            SkillsData.updatePlayerData(player, skillsData -> skillsData.getOrCreate(player.registryAccess(), configuredSkill).ifPresent(consumer));
        }
    }
}
