package grill24.potionsplus.event.neoforge;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.serialization.JsonOps;
import grill24.potionsplus.block.SkillJournalsBlock;
import grill24.potionsplus.core.neoforge.DataAttachmentsImpl;
import grill24.potionsplus.core.potion.Potions;
import grill24.potionsplus.debug.Debug;
import grill24.potionsplus.item.GeneticCropItem;
import grill24.potionsplus.network.ClientboundDisplayTossupAnimationPacket;
import grill24.potionsplus.network.ClientboundDisplayWheelAnimationPacket;
import grill24.potionsplus.network.ClientboundSyncPlayerSkillData;
import grill24.potionsplus.network.ClientboundSyncSpatialAnimationDataPacket;
import grill24.potionsplus.persistence.PlayerBrewingKnowledge;
import grill24.potionsplus.persistence.SavedData;
import grill24.potionsplus.render.animation.keyframe.*;
import grill24.potionsplus.skill.*;
import grill24.potionsplus.skill.ability.PlayerAbility;
import grill24.potionsplus.skill.ability.instance.AbilityInstanceSerializable;
import grill24.potionsplus.skill.ability.instance.CooldownAbilityInstanceData;
import grill24.potionsplus.skill.reward.SkillLevelUpRewardsConfiguration;
import grill24.potionsplus.utility.*;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.GameType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.*;
import java.util.function.Consumer;

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
                .then(Commands.literal("wheel")
                        .requires((source) -> source.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
                        .executes(context -> {
                            if (context.getSource().getEntity() instanceof ServerPlayer player) {
                                List<ItemStack> itemStacks = player.getInventory().getNonEquipmentItems().stream().filter(itemStack -> !itemStack.isEmpty()).toList();
                                int winnerIndex = player.getRandom().nextInt(itemStacks.size());
                                PacketDistributor.sendToPlayer(player, new ClientboundDisplayWheelAnimationPacket(itemStacks, winnerIndex));
                                DelayedEvents.queueDelayedEvent(() -> InvUtil.giveOrDropItem(player, itemStacks.get(winnerIndex).copy()), 190);
                            }
                            return 1;
                        })
                )
                .then(Commands.literal("restoreAbilities")
                        .executes(context -> {
                            if (context.getSource().getPlayer() == null) return 0;
                            SkillsData.updatePlayerData(context.getSource().getPlayer(), (skillsData -> {
                                skillsData.clearAndReunlockAbilities(context.getSource().getPlayer());
                                PacketDistributor.sendToPlayer(context.getSource().getPlayer(), new ClientboundSyncPlayerSkillData(SkillsData.getPlayerData(context.getSource().getPlayer())));
                            }));
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
                .then(Commands.literal("tossup")
                        .requires((source) -> source.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
                        .executes(context -> {
                            if (context.getSource().getEntity() instanceof ServerPlayer player) {
                                List<ItemStack> itemStacks = new ArrayList<>();
                                for (int i = 0; i < 100; i++) {
                                    itemStacks.add(new ItemStack(i % 2 == 0 ? Items.EMERALD : Items.DIAMOND));
                                }
                                PacketDistributor.sendToPlayer(player, new ClientboundDisplayTossupAnimationPacket(itemStacks, 1, 1F));
                            }
                            return 1;
                        })
                )
                .then(Commands.literal("skillsMenu")
                        .requires((source) -> source.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
                        .executes(context -> {
                            if (context.getSource().getEntity() instanceof ServerPlayer serverPlayer) {
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
                                                                        Identifier id = SpatialAnimationDataArgument.getId(context, "id");
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
                                                                                        Identifier id = SpatialAnimationDataArgument.getId(context, "id");
                                                                                        AnimationCurve<?> curve = spatialAnimationData.get(property);
                                                                                        String interpolationString = StringArgumentType.getString(context, "interpolation");
                                                                                        Interpolation.Mode interpolation = Interpolation.Mode.valueOf(interpolationString.toUpperCase());
                                                                                        float time = FloatArgumentType.getFloat(context, "time");
                                                                                        float x = FloatArgumentType.getFloat(context, "x");
                                                                                        if (curve instanceof FloatAnimationCurve floatAnimationCurve) {
                                                                                            AnimationCurve.Keyframe<Float> keyframe = AnimationCurve.Keyframe.<Float>builder()
                                                                                                    .time(time).value(x).interp(interpolation).build();
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
                                                                                                        Identifier id = SpatialAnimationDataArgument.getId(context, "id");
                                                                                                        AnimationCurve<?> curve = spatialAnimationData.get(property);
                                                                                                        String interpolationString = StringArgumentType.getString(context, "interpolation");
                                                                                                        Interpolation.Mode interpolation = Interpolation.Mode.valueOf(interpolationString.toUpperCase());
                                                                                                        float time = FloatArgumentType.getFloat(context, "time");
                                                                                                        float x = FloatArgumentType.getFloat(context, "x");
                                                                                                        float y = FloatArgumentType.getFloat(context, "y");
                                                                                                        float z = FloatArgumentType.getFloat(context, "z");
                                                                                                        if (curve instanceof Vector3fAnimationCurve vector3fAnimationCurve) {
                                                                                                            AnimationCurve.Keyframe<Vector3f> keyframe = AnimationCurve.Keyframe.<Vector3f>builder()
                                                                                                                    .time(time).value(new Vector3f(x, y, z)).interp(interpolation).build();
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
                                                                                                                Identifier id = SpatialAnimationDataArgument.getId(context, "id");
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
                                                                                                                            .time(time).value(new Vector4f(x, y, z, w)).interp(interpolation).build();
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
                                                                ))
                                                )
                                        )
                                )
                        )
                )
                .then(Commands.literal("skill")
                        .executes(context -> {
                            if (context.getSource().getPlayer() == null) return 0;
                            SkillsData.CODEC.encodeStart(JsonOps.INSTANCE, context.getSource().getPlayer().getData(grill24.potionsplus.core.neoforge.DataAttachmentsImpl.SKILL_PLAYER_DATA))
                                    .ifSuccess((jsonElement) -> context.getSource().sendSuccess(() -> Component.literal(jsonElement.toString()), true))
                                    .ifError((jsonElement) -> context.getSource().sendFailure(Component.literal(jsonElement.toString())));
                            return 1;
                        })
                        .then(Commands.literal("clear")
                                .requires((source) -> source.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
                                .executes(context -> {
                                    if (context.getSource().getPlayer() == null) return 0;
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
                                                            if (context.getSource().getPlayer() == null) return 0;
                                                            if (context.getSource().getPlayer() instanceof ServerPlayer player) {
                                                                Identifier abilityId = ConfiguredPlayerAbilityArgument.getHolder(context, "abilityId").getKey().identifier();
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
                                                .then(Commands.literal("skipCooldown")
                                                        .executes(context -> {
                                                            if (context.getSource().getPlayer() == null) return 0;
                                                            if (context.getSource().getPlayer() instanceof ServerPlayer player) {
                                                                Identifier abilityId = ConfiguredPlayerAbilityArgument.getHolder(context, "abilityId").getKey().identifier();
                                                                SkillsData.updatePlayerData(context.getSource().getPlayer(), (skillsData -> {
                                                                    skillsData.getAbilityInstance(context.getSource().registryAccess(), abilityId).ifPresent(abilityInstance -> {
                                                                        if (abilityInstance.data() instanceof CooldownAbilityInstanceData data) {
                                                                            data.setLastTriggeredTick(player.level().getGameTime() - 60000);
                                                                            context.getSource().sendSuccess(() -> Component.literal("Ability cooldown skipped."), true);
                                                                        }
                                                                    });
                                                                }));
                                                            }
                                                            return 1;
                                                        })
                                                )
                                                .executes(context -> {
                                                    if (context.getSource().getPlayer() == null) return 0;
                                                    Identifier abilityId = ConfiguredPlayerAbilityArgument.getHolder(context, "abilityId").getKey().identifier();
                                                    SkillsData.updatePlayerData(context.getSource().getPlayer(), (skillsData1 -> {
                                                        skillsData1.getAbilityInstance(context.getSource().registryAccess(), abilityId).ifPresentOrElse(
                                                                abilityInstance -> context.getSource().sendSuccess(() -> abilityInstance.data().getDescription(true), true),
                                                                () -> context.getSource().sendFailure(Component.literal("No unlocked ability found.")));
                                                    }));
                                                    return 1;
                                                })
                                        )
                                )
                                .executes(context -> {
                                    if (context.getSource().getPlayer() == null) return 0;
                                    SkillsData.updatePlayerData(context.getSource().getPlayer(), (skillsData -> {
                                        MutableComponent component = Component.empty();
                                        boolean hasAbilities = false;
                                        for (Map.Entry<ResourceKey<PlayerAbility<?>>, List<AbilityInstanceSerializable<?, ?>>> entry : skillsData.unlockedAbilities().entrySet()) {
                                            for (AbilityInstanceSerializable<?, ?> abilityInstance : entry.getValue()) {
                                                Component abilityComponent = abilityInstance.data().getDescription();
                                                if (hasAbilities) component.append(Component.literal(", ").withStyle(abilityComponent.getStyle()));
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
                                                        .ifSuccess((jsonElement) -> context.getSource().sendSuccess(() -> Component.literal(jsonElement.toString()), true));
                                            });
                                            return 1;
                                        })
                                        .requires((source) -> source.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
                                        .then(Commands.literal("add")
                                                .then(Commands.argument("points", IntegerArgumentType.integer())
                                                        .executes(context -> {
                                                            if (context.getSource().getPlayer() == null) return 0;
                                                            int points = IntegerArgumentType.getInteger(context, "points");
                                                            tryConsumeSkillInstance(context, skillInstance -> {
                                                                skillInstance.addPoints(context.getSource().getPlayer(), points);
                                                                SavedData.instance.setDirty();
                                                            });
                                                            return 1;
                                                        })
                                                )
                                        )
                                        .requires((source) -> source.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
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
                                                        TreeMap<Integer, Component> rewardsMap = new TreeMap<>();
                                                        rewardsConfiguration.rewardsDataMap.forEach((level, rewardsData) -> {
                                                            int levelInt = Integer.parseInt(level);
                                                            if (levelInt < skillInstance.getLevel(registryAccess) + 10) {
                                                                rewardsMap.put(levelInt, skillInstance.getRewardDescription(context.getSource().getPlayer().registryAccess(), levelInt));
                                                            }
                                                        });
                                                        rewardsMap.forEach((level, component) -> context.getSource().sendSuccess(() -> component, true));
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
                                                                        component.append(Component.translatable(grill24.potionsplus.core.Translations.TOOLTIP_POTIONSPLUS_SKILL_POINTS_EARNED).withStyle(ChatFormatting.RED));
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

    private static void tryConsumeSkillInstance(CommandContext<CommandSourceStack> context, Consumer<SkillInstance<?, ?>> consumer) {
        if (context.getSource().getEntity() instanceof ServerPlayer player) {
            ConfiguredSkill<?, ?> configuredSkill = ConfiguredSkillArgument.getSkill(context, "skillId");
            SkillsData.updatePlayerData(player, skillsData -> skillsData.getOrCreate(player.registryAccess(), configuredSkill).ifPresent(consumer));
        }
    }
}
