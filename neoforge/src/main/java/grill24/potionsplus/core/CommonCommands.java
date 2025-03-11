package grill24.potionsplus.core;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.serialization.JsonOps;
import grill24.potionsplus.block.SkillJournalsBlock;
import grill24.potionsplus.core.potion.Potions;
import grill24.potionsplus.misc.FishingGamePlayerAttachment;
import grill24.potionsplus.network.*;
import grill24.potionsplus.persistence.PlayerBrewingKnowledge;
import grill24.potionsplus.persistence.SavedData;
import grill24.potionsplus.render.animation.keyframe.Interpolation;
import grill24.potionsplus.render.animation.keyframe.*;
import grill24.potionsplus.skill.*;
import grill24.potionsplus.skill.ability.PlayerAbility;
import grill24.potionsplus.skill.ability.instance.AbilityInstanceSerializable;
import grill24.potionsplus.skill.reward.SkillLevelUpRewardsConfiguration;
import grill24.potionsplus.utility.DelayedServerEvents;
import grill24.potionsplus.utility.InvUtil;
import grill24.potionsplus.utility.ModInfo;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.joml.Vector3f;
import org.joml.Vector4f;


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
                .then(Commands.literal("wheel")
                        .requires((source) -> source.hasPermission(2))
                        .executes(context -> {
                            if(context.getSource().getEntity() instanceof ServerPlayer player) {
                                List<ItemStack> itemStacks = player.getInventory().items.stream().filter(itemStack -> !itemStack.isEmpty()).toList();
                                int winnerIndex = player.getRandom().nextInt(itemStacks.size());

                                PacketDistributor.sendToPlayer(player, new ClientboundDisplayWheelAnimationPacket(itemStacks, winnerIndex));
                                DelayedServerEvents.queueDelayedEvent(() -> InvUtil.giveOrDropItem(player, itemStacks.get(winnerIndex).copy()), 190);
                            }

                            return 1;
                        })
                )
                .then(Commands.literal("restoreAbilities")
                        .executes(context -> {
                            if (context.getSource().getPlayer() == null) {
                                return 0;
                            }

                            SkillsData.updatePlayerData(context.getSource().getPlayer(), (skillsData -> {
                                skillsData.clearAndReunlockAbilities(context.getSource().getPlayer());
                                PacketDistributor.sendToPlayer(context.getSource().getPlayer(), new ClientboundSyncPlayerSkillData(SkillsData.getPlayerData(context.getSource().getPlayer())));
                            }));
                            return 1;
                        })
                )
                .then(Commands.literal("fishing")
                        .requires((source) -> source.hasPermission(2))
                        .executes(context -> {
                            if(context.getSource().getEntity() instanceof ServerPlayer player) {
                                // Use offhand item as reward
                                ItemStack reward = player.getOffhandItem().copy();
                                // Else use fishing loot table
                                if (reward.isEmpty()) {
                                    LootParams lootParams = new LootParams.Builder(player.serverLevel()).withParameter(LootContextParams.ORIGIN, player.position()).withParameter(LootContextParams.TOOL, player.getMainHandItem()).create(LootContextParamSets.FISHING);
                                    ObjectArrayList<ItemStack> samples = context.getSource().getServer().reloadableRegistries().getLootTable(BuiltInLootTables.FISHING_FISH).getRandomItems(lootParams);
                                    if (!samples.isEmpty()) {
                                        reward = samples.getFirst();
                                    }
                                }

                                if (!reward.isEmpty()) {
                                    PacketDistributor.sendToPlayer(player, ClientboundStartFishingMinigamePacket.create(
                                            player,
                                            new FishingGamePlayerAttachment(reward, new ItemStack(grill24.potionsplus.core.Items.GENERIC_ICON, 23 + player.getRandom().nextInt(4)))
                                    ));
                                }
                            }

                            return 1;
                        })
                )
                .then(Commands.literal("tossup")
                        .requires((source) -> source.hasPermission(2))
                        .executes(context -> {
                            if(context.getSource().getEntity() instanceof ServerPlayer player) {
                                List<ItemStack> itemStacks = new ArrayList<>();
                                for (int i = 0; i < 100; i++) {
                                    if (i % 2 == 0) {
                                        itemStacks.add(new ItemStack(Items.EMERALD));
                                    } else {
                                        itemStacks.add(new ItemStack(Items.DIAMOND));
                                    }
                                }
                                PacketDistributor.sendToPlayer(player, new ClientboundDisplayTossupAnimationPacket(itemStacks, 1, 1F));
                            }

                            return 1;
                        })
                )
                .then(Commands.literal("skillsMenu")
                        .requires((source) -> source.hasPermission(2))
                        .executes(context -> {
                            if(context.getSource().getEntity() instanceof ServerPlayer serverPlayer) {
                                //
                                SkillJournalsBlock.openSkillsMenu(serverPlayer);
                            }

                            return 1;
                        })
                )
                .then(Commands.literal("animation")
                        .then(Commands.argument("id", new SpatialAnimationDataArgument(event.getBuildContext()))
                                .then(Commands.argument("property", StringArgumentType.word()).suggests(SpatialAnimationData.SUGGEST_SPATIAL_ANIMATION_PROPERTIES)
                                        .executes(context -> {
                                            if (context.getSource().getEntity() instanceof ServerPlayer player) {
                                                String propertyString = StringArgumentType.getString(context, "property");
                                                SpatialAnimationData.Property property = SpatialAnimationData.Property.valueOf(propertyString.toUpperCase());
                                                SpatialAnimationData spatialAnimationData = SpatialAnimationDataArgument.get(context, "id");
                                                AnimationCurve<?> curve = spatialAnimationData.get(property);
                                                curve.printInChat(player);
                                            }
                                            return 1;
                                        })
                                        .then(Commands.literal("keyframe")
                                        .then(Commands.literal("remove")
                                                .then(Commands.argument("time", FloatArgumentType.floatArg())
                                                .executes(context -> {
                                                    if (context.getSource().getEntity() instanceof ServerPlayer player) {
                                                        String propertyString = StringArgumentType.getString(context, "property");
                                                        SpatialAnimationData.Property property = SpatialAnimationData.Property.valueOf(propertyString.toUpperCase());
                                                        SpatialAnimationData spatialAnimationData = SpatialAnimationDataArgument.get(context, "id");
                                                        ResourceLocation id = SpatialAnimationDataArgument.getId(context, "id");
                                                        AnimationCurve<?> curve = spatialAnimationData.get(property);
                                                        float time = FloatArgumentType.getFloat(context, "time");

                                                        curve.removeKeyframe(time);
                                                        curve.printInChat(player);
                                                        PacketDistributor.sendToPlayer(player, new ClientboundSyncSpatialAnimationDataPacket(id, spatialAnimationData));
                                                    }
                                                    return 1;
                                                })
                                        ))
                                        .then(Commands.literal("add")
                                                .then(Commands.argument("time", FloatArgumentType.floatArg())
                                                        .then(Commands.argument("interpolation", StringArgumentType.word()).suggests(Interpolation.INTERPOLATION_COMMAND_SUGGESTIONS)
                                                                .then(Commands.argument("x", FloatArgumentType.floatArg())
                                                                        .executes(context -> {
                                                                            if (context.getSource().getEntity() instanceof ServerPlayer player) {
                                                                                String propertyString = StringArgumentType.getString(context, "property");
                                                                                SpatialAnimationData.Property property = SpatialAnimationData.Property.valueOf(propertyString.toUpperCase());
                                                                                SpatialAnimationData spatialAnimationData = SpatialAnimationDataArgument.get(context, "id");
                                                                                ResourceLocation id = SpatialAnimationDataArgument.getId(context, "id");
                                                                                AnimationCurve<?> curve = spatialAnimationData.get(property);

                                                                                String interpolationString = StringArgumentType.getString(context, "interpolation");
                                                                                Interpolation.Mode interpolation = Interpolation.Mode.valueOf(interpolationString.toUpperCase());
                                                                                float time = FloatArgumentType.getFloat(context, "time");
                                                                                float x = FloatArgumentType.getFloat(context, "x");

                                                                                if (curve instanceof FloatAnimationCurve floatAnimationCurve) {
                                                                                    AnimationCurve.Keyframe<Float> keyframe = AnimationCurve.Keyframe.<Float>builder()
                                                                                            .time(time)
                                                                                            .value(x)
                                                                                            .interp(interpolation)
                                                                                            .build();
                                                                                    floatAnimationCurve.addKeyframe(keyframe);
                                                                                    curve.printInChat(player);
                                                                                    PacketDistributor.sendToPlayer(player, new ClientboundSyncSpatialAnimationDataPacket(id, spatialAnimationData));
                                                                                } else {
                                                                                    context.getSource().sendFailure(Component.literal("Property is not a FloatAnimationCurve."));
                                                                                }
                                                                            }
                                                                            return 1;
                                                                        })
                                                                        .then(Commands.argument("y", FloatArgumentType.floatArg())
                                                                                .then(Commands.argument("z", FloatArgumentType.floatArg())
                                                                                        .executes(context -> {
                                                                                            if (context.getSource().getEntity() instanceof ServerPlayer player) {
                                                                                                String propertyString = StringArgumentType.getString(context, "property");
                                                                                                SpatialAnimationData.Property property = SpatialAnimationData.Property.valueOf(propertyString.toUpperCase());
                                                                                                SpatialAnimationData spatialAnimationData = SpatialAnimationDataArgument.get(context, "id");
                                                                                                ResourceLocation id = SpatialAnimationDataArgument.getId(context, "id");
                                                                                                AnimationCurve<?> curve = spatialAnimationData.get(property);

                                                                                                String interpolationString = StringArgumentType.getString(context, "interpolation");
                                                                                                Interpolation.Mode interpolation = Interpolation.Mode.valueOf(interpolationString.toUpperCase());
                                                                                                float time = FloatArgumentType.getFloat(context, "time");
                                                                                                float x = FloatArgumentType.getFloat(context, "x");
                                                                                                float y = FloatArgumentType.getFloat(context, "y");
                                                                                                float z = FloatArgumentType.getFloat(context, "z");

                                                                                                if (curve instanceof Vector3fAnimationCurve vector3fAnimationCurve) {
                                                                                                    AnimationCurve.Keyframe<Vector3f> keyframe = AnimationCurve.Keyframe.<Vector3f>builder()
                                                                                                            .time(time)
                                                                                                            .value(new Vector3f(x, y, z))
                                                                                                            .interp(interpolation)
                                                                                                            .build();
                                                                                                    vector3fAnimationCurve.addKeyframe(keyframe);
                                                                                                    curve.printInChat(player);
                                                                                                    PacketDistributor.sendToPlayer(player, new ClientboundSyncSpatialAnimationDataPacket(id, spatialAnimationData));
                                                                                                } else {
                                                                                                    context.getSource().sendFailure(Component.literal("Property is not a Vector3fAnimationCurve."));
                                                                                                }
                                                                                            }
                                                                                            return 1;
                                                                                        })
                                                                                        .then(Commands.argument("w", FloatArgumentType.floatArg())
                                                                                                .executes(context -> {
                                                                                                    if (context.getSource().getEntity() instanceof ServerPlayer player) {
                                                                                                        String propertyString = StringArgumentType.getString(context, "property");
                                                                                                        SpatialAnimationData.Property property = SpatialAnimationData.Property.valueOf(propertyString.toUpperCase());
                                                                                                        SpatialAnimationData spatialAnimationData = SpatialAnimationDataArgument.get(context, "id");
                                                                                                        ResourceLocation id = SpatialAnimationDataArgument.getId(context, "id");
                                                                                                        AnimationCurve<?> curve = spatialAnimationData.get(property);

                                                                                                        String interpolationString = StringArgumentType.getString(context, "interpolation");
                                                                                                        Interpolation.Mode interpolation = Interpolation.Mode.valueOf(interpolationString.toUpperCase());
                                                                                                        float time = FloatArgumentType.getFloat(context, "time");
                                                                                                        float x = FloatArgumentType.getFloat(context, "x");
                                                                                                        float y = FloatArgumentType.getFloat(context, "y");
                                                                                                        float z = FloatArgumentType.getFloat(context, "z");
                                                                                                        float w = FloatArgumentType.getFloat(context, "w");

                                                                                                        if (curve instanceof Vector4fAnimationCurve vector4fAnimationCurve) {
                                                                                                            AnimationCurve.Keyframe<Vector4f> keyframe = AnimationCurve.Keyframe.<Vector4f>builder()
                                                                                                                    .time(time)
                                                                                                                    .value(new Vector4f(x, y, z, w))
                                                                                                                    .interp(interpolation)
                                                                                                                    .build();
                                                                                                            vector4fAnimationCurve.addKeyframe(keyframe);
                                                                                                            curve.printInChat(player);
                                                                                                            PacketDistributor.sendToPlayer(player, new ClientboundSyncSpatialAnimationDataPacket(id, spatialAnimationData));
                                                                                                        } else {
                                                                                                            context.getSource().sendFailure(Component.literal("Property is not a Vector4fAnimationCurve."));
                                                                                                        }
                                                                                                    }
                                                                                                    return 1;
                                                                                                })
                                                                                        )
                                                                                    )
                                                                            )
                                                                    )
                                                        )))
                                                            )
                                                    )
                                            )
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

                                    SkillsData.updatePlayerData(context.getSource().getPlayer(), (skillsData -> {
                                        skillsData.clear(context.getSource().getPlayer());
                                        PacketDistributor.sendToPlayer(context.getSource().getPlayer(), new ClientboundSyncPlayerSkillData(SkillsData.getPlayerData(context.getSource().getPlayer())));
                                    }));
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

                                                            if (context.getSource().getPlayer() instanceof ServerPlayer player) {
                                                                ResourceLocation abilityId = ConfiguredPlayerAbilityArgument.getHolder(context, "abilityId").getKey().location();
                                                                SkillsData.updatePlayerData(context.getSource().getPlayer(), (skillsData -> {
                                                                    skillsData.getAbilityInstance(context.getSource().registryAccess(), abilityId).ifPresent(abilityInstance -> {
                                                                        abilityInstance.toggle(player);
                                                                        context.getSource().sendSuccess(() -> abilityInstance.data().getDescription(true), true);
                                                                    });
                                                                }));
                                                            }


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
                                                                component.append(abilityInstance.data().getDescription(true));
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

                                        for (Map.Entry<ResourceKey<PlayerAbility<?>>, List<AbilityInstanceSerializable<?, ?>>> entry : skillsData.unlockedAbilities().entrySet()) {
                                            for (AbilityInstanceSerializable<?, ?> abilityInstance : entry.getValue()) {
                                                Component abilityComponent = abilityInstance.data().getDescription();
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
                                                    RegistryAccess registryAccess = context.getSource().registryAccess();
                                                    SkillLevelUpRewardsConfiguration rewardsConfiguration = skillInstance.getConfiguredSkill(context.getSource().registryAccess()).config().getData().rewardsConfiguration();
                                                    // Sorted by level
                                                    TreeMap<Integer, Component> rewardsMap = new TreeMap<>();
                                                    rewardsConfiguration.rewardsDataMap.forEach((level, rewardsData) -> {
                                                        int levelInt = Integer.parseInt(level);
                                                        if (levelInt < skillInstance.getLevel(registryAccess) + 10) {
                                                            rewardsMap.put(levelInt, skillInstance.getRewardDescription(context.getSource().getPlayer().registryAccess(), levelInt));
                                                        }
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
                                                    context.getSource().sendSuccess(() -> skillInstance.getProgressToNextLevel(context.getSource().getPlayer().registryAccess(), true, 10), true);
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
