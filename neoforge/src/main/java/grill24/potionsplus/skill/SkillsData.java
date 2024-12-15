package grill24.potionsplus.skill;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import grill24.potionsplus.core.DataAttachments;
import grill24.potionsplus.core.PotionsPlusRegistries;
import grill24.potionsplus.skill.ability.AbilityInstance;
import grill24.potionsplus.skill.ability.ConfiguredPlayerAbility;
import grill24.potionsplus.skill.ability.PlayerAbility;
import grill24.potionsplus.skill.ability.PlayerAbilityConfiguration;
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

import javax.swing.text.html.Option;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

public record SkillsData(Map<ResourceKey<ConfiguredSkill<?, ?>>, SkillInstance<?, ?>> skillData, PointEarningHistory pointEarningHistory, Map<ResourceKey<PlayerAbility<?, ?>>, List<AbilityInstance>> activeAbilities) {
    public static final Codec<SkillsData> CODEC = RecordCodecBuilder.create(codecBuilder -> codecBuilder.group(
            Codec.unboundedMap(HolderCodecs.resourceKey(PotionsPlusRegistries.CONFIGURED_SKILL), SkillInstance.CODEC).optionalFieldOf("skillInstances", new HashMap<>()).forGetter(instance -> instance.skillData),
            PointEarningHistory.CODEC.optionalFieldOf("pointEarningHistory", new PointEarningHistory(1000)).forGetter(instance -> instance.pointEarningHistory),
            Codec.unboundedMap(HolderCodecs.resourceKey(PotionsPlusRegistries.PLAYER_ABILITY_REGISTRY_KEY), AbilityInstance.CODEC.listOf()).optionalFieldOf("activeAbilities", new HashMap<>()).forGetter(instance -> instance.activeAbilities)
    ).apply(codecBuilder, SkillsData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, SkillsData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(Object2ObjectOpenHashMap::new, HolderCodecs.resourceKeyStream(PotionsPlusRegistries.CONFIGURED_SKILL), SkillInstance.STREAM_CODEC),
            (instance) -> instance.skillData,
            PointEarningHistory.STREAM_CODEC,
            (instance) -> instance.pointEarningHistory,
            ByteBufCodecs.map(Object2ObjectOpenHashMap::new, HolderCodecs.resourceKeyStream(PotionsPlusRegistries.PLAYER_ABILITY_REGISTRY_KEY), AbilityInstance.STREAM_CODEC.apply(ByteBufCodecs.list())),
            (instance) -> instance.activeAbilities,
            SkillsData::new
    );

    public SkillsData() {
        this(new HashMap<>(), new PointEarningHistory(1000), new HashMap<>());
    }

    public SkillsData(Map<ResourceKey<ConfiguredSkill<?, ?>>, SkillInstance<?, ?>> skillData, PointEarningHistory pointEarningHistory, Map<ResourceKey<PlayerAbility<?, ?>>, List<AbilityInstance>> activeAbilities) {
        this.skillData = new HashMap<>(skillData);
        this.activeAbilities = new HashMap<>();
        activeAbilities.forEach((key, value) -> this.activeAbilities.put(key, new ArrayList<>(value)));        // Deep copy, make sure lists are mutable
        this.pointEarningHistory = pointEarningHistory;
    }

    public void clear(ServerPlayer player) {
        skillData.clear();

        for (List<AbilityInstance> configuredPlayerAbilities : activeAbilities.values()) {
            for (AbilityInstance configuredPlayerAbility : configuredPlayerAbilities) {
                configuredPlayerAbility.tryDisable(player);
            }
        }
        activeAbilities.clear();

        pointEarningHistory.clear();
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

        // For each skill, check if this source is a point source for it. If so, calculate earned points and add them.
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
                        // Add points to skill instance
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

    // ----- Helper Methods Active Abilities -----

    public static <AC extends PlayerAbilityConfiguration, A extends PlayerAbility<?, AC>> Optional<ConfiguredPlayerAbility<AC, A>> getConfiguredAbility(RegistryAccess registryAccess, ResourceLocation configuredAbilityId) {
        return Optional.ofNullable((ConfiguredPlayerAbility<AC, A>) registryAccess.registryOrThrow(PotionsPlusRegistries.CONFIGURED_PLAYER_ABILITY).get(configuredAbilityId));
    }

    public Optional<AbilityInstance> getAbilityInstance(RegistryAccess registryAccess, ResourceLocation configuredAbilityId) {
        Optional<ConfiguredPlayerAbility<PlayerAbilityConfiguration, PlayerAbility<?, PlayerAbilityConfiguration>>> configuredPlayerAbility = getConfiguredAbility(registryAccess, configuredAbilityId);
        if (configuredPlayerAbility.isPresent()) {
            Registry<PlayerAbility<?, ?>> abilityLookup = registryAccess.registryOrThrow(PotionsPlusRegistries.PLAYER_ABILITY_REGISTRY_KEY);
            PlayerAbility<?, ?> ability = configuredPlayerAbility.get().ability();
            ResourceKey<PlayerAbility<?, ?>> abilityKey = abilityLookup.getResourceKey(ability).get();

            if (this.activeAbilities.containsKey(abilityKey)) {
                return this.activeAbilities.get(abilityKey)
                        .stream()
                        .filter(abilityInstance -> abilityInstance.getHolder().getKey().location().equals(configuredAbilityId))
                        .findFirst();
            }
        }

        return Optional.empty();
    }


    public void activateAbilities(ServerPlayer player, HolderSet<ConfiguredPlayerAbility<?, ?>> configuredAbilities) {
        Registry<PlayerAbility<?, ?>> abilityLookup = player.registryAccess().registryOrThrow(PotionsPlusRegistries.PLAYER_ABILITY_REGISTRY_KEY);

        configuredAbilities.forEach(configuredAbility -> {
            activeAbilities
                    .computeIfAbsent(abilityLookup.getResourceKey(configuredAbility.value().ability()).get(), (key) -> new ArrayList<>())
                    .add(configuredAbility.value().ability().createInstance(player, configuredAbility));

            if (configuredAbility.value().config().getData().enabledByDefault()) {
                configuredAbility.value().onAbilityGranted(player);
            }
        });
    }

    public void deactivateAbilities(ServerPlayer player, HolderSet<ConfiguredPlayerAbility<?, ?>> configuredAbilities) {
        Registry<PlayerAbility<?, ?>> abilityLookup = player.registryAccess().registryOrThrow(PotionsPlusRegistries.PLAYER_ABILITY_REGISTRY_KEY);

        configuredAbilities.forEach(configuredAbility -> {
            activeAbilities
                    .computeIfPresent(abilityLookup.getResourceKey(configuredAbility.value().ability()).get(), (key, value) -> {
                        value.remove(configuredAbility);
                        return value;
                    });

            configuredAbility.value().onAbilityRevoked(player);
        });
    }

    public static void tickPointEarningHistory(ServerTickEvent event) {
        final float frequencyHertz = 0.2F; // 1 every 5 seconds
        if (ServerTickHandler.ticksInGame % (20 / frequencyHertz) != 0) return;

        event.getServer().getPlayerList().getPlayers().forEach(
                player -> SkillsData.updatePlayerData(player, data -> data.pointEarningHistory().popOldestEntry()));
    }
}
