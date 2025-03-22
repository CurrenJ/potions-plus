package grill24.potionsplus.skill;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import grill24.potionsplus.core.DataAttachments;
import grill24.potionsplus.core.PotionsPlus;
import grill24.potionsplus.core.PotionsPlusRegistries;
import grill24.potionsplus.core.Skills;
import grill24.potionsplus.skill.ability.ConfiguredPlayerAbility;
import grill24.potionsplus.skill.ability.PlayerAbility;
import grill24.potionsplus.skill.ability.PlayerAbilityConfiguration;
import grill24.potionsplus.skill.ability.instance.AbilityInstanceSerializable;
import grill24.potionsplus.skill.reward.*;
import grill24.potionsplus.skill.source.ConfiguredSkillPointSource;
import grill24.potionsplus.skill.source.SkillPointSource;
import grill24.potionsplus.skill.source.SkillPointSourceConfiguration;
import grill24.potionsplus.utility.HolderCodecs;
import grill24.potionsplus.utility.ServerTickHandler;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public record SkillsData(Map<ResourceKey<ConfiguredSkill<?, ?>>, SkillInstance<?, ?>> skillData, PointEarningHistory pointEarningHistory, Map<ResourceKey<PlayerAbility<?>>, List<AbilityInstanceSerializable<?, ?>>> unlockedAbilities, List<ResourceKey<ConfiguredGrantableReward<?, ?>>> pendingChoices) {
    public static final Codec<SkillsData> CODEC = RecordCodecBuilder.create(codecBuilder -> codecBuilder.group(
            Codec.unboundedMap(HolderCodecs.resourceKey(PotionsPlusRegistries.CONFIGURED_SKILL), SkillInstance.CODEC).optionalFieldOf("skillInstances", new HashMap<>()).forGetter(instance -> instance.skillData),
            PointEarningHistory.CODEC.optionalFieldOf("pointEarningHistory", new PointEarningHistory(1000)).forGetter(instance -> instance.pointEarningHistory),
            Codec.unboundedMap(HolderCodecs.resourceKey(PotionsPlusRegistries.PLAYER_ABILITY_REGISTRY_KEY), AbilityInstanceSerializable.DIRECT_CODEC.listOf()).optionalFieldOf("activeAbilities", new HashMap<>()).forGetter(instance -> instance.unlockedAbilities),
            ResourceKey.codec(PotionsPlusRegistries.CONFIGURED_GRANTABLE_REWARD).listOf().optionalFieldOf("pendingChoices", new ArrayList<>()).forGetter(instance -> instance.pendingChoices)
    ).apply(codecBuilder, SkillsData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, SkillsData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(Object2ObjectOpenHashMap::new, HolderCodecs.resourceKeyStream(PotionsPlusRegistries.CONFIGURED_SKILL), SkillInstance.STREAM_CODEC),
            (instance) -> instance.skillData,
            PointEarningHistory.STREAM_CODEC,
            (instance) -> instance.pointEarningHistory,
            ByteBufCodecs.map(Object2ObjectOpenHashMap::new, HolderCodecs.resourceKeyStream(PotionsPlusRegistries.PLAYER_ABILITY_REGISTRY_KEY), AbilityInstanceSerializable.STREAM_CODEC.apply(ByteBufCodecs.list())),
            (instance) -> instance.unlockedAbilities,
            ResourceKey.streamCodec(PotionsPlusRegistries.CONFIGURED_GRANTABLE_REWARD).apply(ByteBufCodecs.list()),
            (instance) -> instance.pendingChoices,
            SkillsData::new
    );

    public SkillsData() {
        this(new HashMap<>(), new PointEarningHistory(1000), new HashMap<>(), new ArrayList<>());
    }

    public SkillsData(Map<ResourceKey<ConfiguredSkill<?, ?>>, SkillInstance<?, ?>> skillData, PointEarningHistory pointEarningHistory, Map<ResourceKey<PlayerAbility<?>>, List<AbilityInstanceSerializable<?, ?>>> unlockedAbilities, List<ResourceKey<ConfiguredGrantableReward<?, ?>>> pendingChoices) {
        this.skillData = new HashMap<>(skillData);
        this.unlockedAbilities = new HashMap<>();
        unlockedAbilities.forEach((key, value) -> this.unlockedAbilities.put(key, new ArrayList<>(value)));        // Deep copy, make sure lists are mutable
        this.pointEarningHistory = pointEarningHistory;
        this.pendingChoices = new ArrayList<>(pendingChoices);
    }

    public void clear(ServerPlayer player) {
        skillData.clear();

        for (List<AbilityInstanceSerializable<?, ?>> abilityInstances : unlockedAbilities.values()) {
            for (AbilityInstanceSerializable<?, ?> instance : abilityInstances) {
                instance.tryEnable(player);
            }
        }
        unlockedAbilities.clear();

        pointEarningHistory.clear();

        pendingChoices.clear();
    }

    // ----- Helper Methods Skill Instances -----

    @Deprecated
    public static void applyToSkillsMatchingPredicate(Player player, Predicate<Holder.Reference<ConfiguredSkill<?, ?>>> predicate, BiConsumer<Player, SkillInstance<?, ?>> skillInstanceConsumer) {
        SkillsData skillsData = player.getData(DataAttachments.SKILL_PLAYER_DATA);
        Registry<ConfiguredSkill<?, ?>> configuredSkillsLookup = player.registryAccess().registryOrThrow(PotionsPlusRegistries.CONFIGURED_SKILL);

        for (Iterator<Holder.Reference<ConfiguredSkill<?, ?>>> it = configuredSkillsLookup.holders().iterator(); it.hasNext(); ) {
            Holder.Reference<ConfiguredSkill<?, ?>> configuredSkill = it.next();

            if (predicate.test(configuredSkill)) {
                ResourceKey<ConfiguredSkill<?, ?>> configuredSkillResourceKey = configuredSkill.getKey();
                Optional<SkillInstance<?, ?>> skillInstance = skillsData.getOrCreate(player.registryAccess(), configuredSkillResourceKey);
                if (skillInstance.isPresent()) {
                    skillInstance.ifPresent(instance -> skillInstanceConsumer.accept(player, instance));
                }
            }
        }

        player.setData(DataAttachments.SKILL_PLAYER_DATA, skillsData);
    }

    public static <E, C extends SkillPointSourceConfiguration> void triggerSkillPointSource(Player player, SkillPointSource<E, C> source, E evaluationData) {
        SkillsData skillsData = player.getData(DataAttachments.SKILL_PLAYER_DATA);
        Registry<ConfiguredSkill<?, ?>> configuredSkillsLookup = player.registryAccess().registryOrThrow(PotionsPlusRegistries.CONFIGURED_SKILL);

        // For each reward, check if this source is a point source for it. If so, calculate earned points and add them.
        for (Iterator<Holder.Reference<ConfiguredSkill<?, ?>>> it = configuredSkillsLookup.holders().iterator(); it.hasNext(); ) {
            Holder.Reference<ConfiguredSkill<?, ?>> configuredSkill = it.next();
            ResourceKey<ConfiguredSkill<?, ?>> configuredSkillResourceKey = configuredSkill.getKey();

            if (configuredSkillResourceKey != null) {
                // Calculate earned points from sources
                float pointsToAdd = 0;
                for (Holder<ConfiguredSkillPointSource<?, ?>> configuredSkillPointSourceHolder : configuredSkill.value().config().getData().skillPointSources()) {
                    if (configuredSkillPointSourceHolder.value().source().getId().equals(source.getId())) {
                        C configuration = (C) configuredSkillPointSourceHolder.value().config();
                        pointsToAdd += source.evaluateSkillPointsToAdd(configuration, evaluationData);
                    }
                }

                // Add points
                if (pointsToAdd > 0) {
                    Optional<SkillInstance<?, ?>> skillInstance = skillsData.getOrCreate(player.registryAccess(), configuredSkillResourceKey);
                    if (skillInstance.isPresent()) {
                        // Add points to reward instance
                        skillInstance.get().addPointsWithGrindingPenalty(player, pointsToAdd, skillsData.pointEarningHistory());

                        // Add points to point earning history
                        skillsData.pointEarningHistory().addPoints(configuredSkillResourceKey, pointsToAdd);
                    }
                }
            }
        }

        player.setData(DataAttachments.SKILL_PLAYER_DATA, skillsData);
    }

    public static void updatePlayerData(Player player, Consumer<SkillsData> consumer) {
        SkillsData skillsData = player.getData(DataAttachments.SKILL_PLAYER_DATA);
        consumer.accept(skillsData);
        player.setData(DataAttachments.SKILL_PLAYER_DATA, skillsData);
    }

    public static SkillsData getPlayerData(Player player) {
        return player.getData(DataAttachments.SKILL_PLAYER_DATA);
    }

    public Optional<SkillInstance<?, ?>> getOrCreate(RegistryAccess registryAccess, ResourceLocation resourceLocation) {
        Optional<Holder.Reference<ConfiguredSkill<?, ?>>> configuredSkillReferenceOptional = registryAccess.registryOrThrow(PotionsPlusRegistries.CONFIGURED_SKILL).getHolder(resourceLocation);
        return configuredSkillReferenceOptional.map(configuredSkill -> skillData.computeIfAbsent(configuredSkill.getKey(), SkillInstance::new));
    }

    public Optional<SkillInstance<?, ?>> getOrCreate(RegistryAccess registryAccess, ResourceKey<ConfiguredSkill<?, ?>> configuredSkillKey) {
        return getOrCreate(registryAccess, configuredSkillKey.location());
    }

    public Optional<SkillInstance<?, ?>> getOrCreate(RegistryAccess registryAccess, ConfiguredSkill<?, ?> configuredSkill) {
        Optional<ResourceKey<ConfiguredSkill<?, ?>>> configuredSkillResourceKeyOptional = registryAccess.registryOrThrow(PotionsPlusRegistries.CONFIGURED_SKILL).getResourceKey(configuredSkill);
        if (configuredSkillResourceKeyOptional.isPresent()) {
            return this.getOrCreate(registryAccess, configuredSkillResourceKeyOptional.get());
        }

        return Optional.empty();
    }

    // ----- Helper Methods Unlocked Abilities -----

    public static <AC extends PlayerAbilityConfiguration, A extends PlayerAbility<AC>> Optional<ConfiguredPlayerAbility<AC, A>> getConfiguredAbility(RegistryAccess registryAccess, ResourceLocation configuredAbilityId) {
        return Optional.ofNullable((ConfiguredPlayerAbility<AC, A>) registryAccess.registryOrThrow(PotionsPlusRegistries.CONFIGURED_PLAYER_ABILITY).get(configuredAbilityId));
    }

    public Optional<AbilityInstanceSerializable<?, ?>> getAbilityInstance(RegistryAccess registryAccess, ResourceLocation configuredAbilityId) {
        Optional<ConfiguredPlayerAbility<PlayerAbilityConfiguration, PlayerAbility<PlayerAbilityConfiguration>>> configuredPlayerAbility = getConfiguredAbility(registryAccess, configuredAbilityId);
        if (configuredPlayerAbility.isPresent()) {
            Registry<PlayerAbility<?>> abilityLookup = registryAccess.registryOrThrow(PotionsPlusRegistries.PLAYER_ABILITY_REGISTRY_KEY);
            PlayerAbility<?> ability = configuredPlayerAbility.get().ability();
            ResourceKey<PlayerAbility<?>> abilityKey = abilityLookup.getResourceKey(ability).get();

            if (this.unlockedAbilities.containsKey(abilityKey)) {
                return this.unlockedAbilities.get(abilityKey)
                        .stream()
                        .filter(abilityInstance -> abilityInstance.data().getHolder().getKey().location().equals(configuredAbilityId))
                        .findFirst();
            }
        }

        return Optional.empty();
    }

    public Optional<AbilityInstanceSerializable<?, ?>> getAbilityInstance(RegistryAccess registryAccess, ResourceKey<ConfiguredPlayerAbility<?, ?>> configuredAbilityId) {
        if (configuredAbilityId == null) {
            return Optional.empty();
        }
        return getAbilityInstance(registryAccess, configuredAbilityId.location());
    }


    public void unlockAbilities(ServerPlayer player, HolderSet<ConfiguredPlayerAbility<?, ?>> configuredAbilities) {
        Registry<PlayerAbility<?>> abilityLookup = player.registryAccess().registryOrThrow(PotionsPlusRegistries.PLAYER_ABILITY_REGISTRY_KEY);

        configuredAbilities.forEach(configuredAbility -> {
            AbilityInstanceSerializable<?, ?> abilityInstance = configuredAbility.value().ability().createInstance(player, configuredAbility);
            ResourceKey<PlayerAbility<?>> abilityKey = abilityLookup.getResourceKey(configuredAbility.value().ability()).get();
            List<AbilityInstanceSerializable<?, ?>> abilityInstances = unlockedAbilities
                    .computeIfAbsent(abilityKey, (key) -> new ArrayList<>());
            Set<ResourceKey<ConfiguredPlayerAbility<?, ?>>> unlockedAbilityKeys = abilityInstances.stream()
                    .map(instance -> instance.data().getHolder().getKey())
                    .collect(Collectors.toSet());
            if (!unlockedAbilityKeys.contains(abilityInstance.data().getHolder().getKey())) {
                abilityInstances.add(abilityInstance);

                if (configuredAbility.value().config().getData().enabledByDefault()) {
                    configuredAbility.value().onAbilityGranted(player, abilityInstance);
                }
            }
        });
    }

    public void resetAbilityInstance(ServerPlayer player, Holder<ConfiguredPlayerAbility<?, ?>> configuredAbility) {
        Registry<PlayerAbility<?>> abilityLookup = player.registryAccess().registryOrThrow(PotionsPlusRegistries.PLAYER_ABILITY_REGISTRY_KEY);
        ResourceKey<PlayerAbility<?>> abilityKey = abilityLookup.getResourceKey(configuredAbility.value().ability()).get();

        unlockedAbilities
                .computeIfPresent(abilityKey, (key, value) -> {
                    value.removeIf(abilityInstance -> abilityInstance.data().getHolder().getKey() != null && abilityInstance.data().getHolder().getKey().equals(configuredAbility.getKey()));
                    return value;
                });
        unlockAbility(player, configuredAbility.getKey());

        PotionsPlus.LOGGER.warn("Reset ability instance for player: " + player.getName().getString() + " with ability: " + configuredAbility.getKey());
    }

    public void unlockAbility(ServerPlayer player, ResourceKey<ConfiguredPlayerAbility<?, ?>> configuredPlayerAbility) {
        Registry<ConfiguredPlayerAbility<?, ?>> abilityLookup = player.registryAccess().registryOrThrow(PotionsPlusRegistries.CONFIGURED_PLAYER_ABILITY);
        if (abilityLookup.containsKey(configuredPlayerAbility)) {
            unlockAbilities(player, HolderSet.direct(abilityLookup.getHolderOrThrow(configuredPlayerAbility)));
        } else {
            PotionsPlus.LOGGER.error("Failed to activate type: " + configuredPlayerAbility);
        }
    }

    @SafeVarargs
    public final void removeUnlockedAbilities(ServerPlayer player, ResourceKey<ConfiguredPlayerAbility<?, ?>>... configuredAbilityKeys) {
        for (ResourceKey<ConfiguredPlayerAbility<?, ?>> configuredAbilityKey : configuredAbilityKeys) {
            removeUnlockedAbility(player, configuredAbilityKey);
        }
    }

    public void removeUnlockedAbility(ServerPlayer player, ResourceKey<ConfiguredPlayerAbility<?, ?>> configuredAbilityKey) {
        Registry<PlayerAbility<?>> abilityLookup = player.registryAccess().registryOrThrow(PotionsPlusRegistries.PLAYER_ABILITY_REGISTRY_KEY);
        Registry<ConfiguredPlayerAbility<?, ?>> configuredAbilityLookup = player.registryAccess().registryOrThrow(PotionsPlusRegistries.CONFIGURED_PLAYER_ABILITY);

        Holder<ConfiguredPlayerAbility<?, ?>> configuredAbility = configuredAbilityLookup.getHolderOrThrow(configuredAbilityKey);
        ResourceKey<PlayerAbility<?>> abilityKey = abilityLookup.getResourceKey(configuredAbility.value().ability()).get();

        unlockedAbilities
                .computeIfPresent(abilityKey, (key, value) -> {
                    value.removeIf(abilityInstance -> abilityInstance.data().getHolder().getKey() != null && abilityInstance.data().getHolder().getKey().equals(configuredAbilityKey));
                    return value;
                });

        configuredAbility.value().onAbilityRevoked(player);
    }

    public void clearAndReunlockAbilities(ServerPlayer player) {
        // Clear and revoke all unlocked abilities
        List<AbilityInstanceSerializable<?, ?>> allUnlockedAbilities = this.unlockedAbilities.values().stream().flatMap(List::stream).toList();
        for (AbilityInstanceSerializable<?, ?> instance : allUnlockedAbilities) {
            removeUnlockedAbilities(player, instance.data().getHolder().getKey());
        }

        // Re-unlock all abilities
        this.skillData.forEach(
                (key, skillInstance) -> {
                   ConfiguredSkill<?, ?> skill = skillInstance.getConfiguredSkill(player.registryAccess());
                   SkillLevelUpRewardsConfiguration skillLevelUpRewardsConfiguration = skill.config().getData().rewardsConfiguration();
                   int currentLevel = skillInstance.getLevel(player.registryAccess());
                   for (int i = 0; i <= currentLevel; i++) {
                       if (skillLevelUpRewardsConfiguration.hasRewardForLevel(i)) {
                            Optional<SkillLevelUpRewardsData> rewards = skillLevelUpRewardsConfiguration.tryGetRewardForLevel(i);
                            for (Holder<ConfiguredGrantableReward<?, ?>> reward : rewards.get().rewards()) {
                                // Re-grant all rewards that have to do with abilities. We don't want to regrant rewards that are not abilities, like loot rewards.
                                GrantableReward<?> grantableReward = reward.value().reward();
                                if (grantableReward instanceof AbilityReward) {
                                    reward.value().grant(reward, player);
                                } else if (grantableReward instanceof IncreaseAbilityStrengthReward) {
                                    reward.value().grant(reward, player);
                                }
                            }
                       }
                   }
                });
    }

    public static void tickPointEarningHistory(ServerTickEvent event) {
        final float frequencyHertz = 0.2F; // 1 every 5 seconds
        if (ServerTickHandler.ticksInGame % (20 / frequencyHertz) != 0) return;

        event.getServer().getPlayerList().getPlayers().forEach(
                player -> SkillsData.updatePlayerData(player, data -> data.pointEarningHistory().popOldestEntry()));
    }

    public void addPendingChoice(ResourceKey<ConfiguredGrantableReward<?, ?>> choice) {
        this.pendingChoices.add(choice);
    }

    public void removePendingChoice(ResourceKey<ConfiguredGrantableReward<?, ?>> choice) {
        this.pendingChoices.remove(choice);
    }

    public boolean hasPendingChoice(ResourceKey<ConfiguredGrantableReward<?, ?>> choice) {
        return this.pendingChoices.stream().anyMatch(pendingChoice -> pendingChoice.equals(choice));
    }
}
