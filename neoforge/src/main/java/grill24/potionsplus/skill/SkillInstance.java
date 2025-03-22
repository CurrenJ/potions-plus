package grill24.potionsplus.skill;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import grill24.potionsplus.core.PotionsPlusRegistries;
import grill24.potionsplus.core.Translations;
import grill24.potionsplus.network.ClientboundDisplayAlert;
import grill24.potionsplus.network.ClientboundDisplayAlertWithParameter;
import grill24.potionsplus.network.ClientboundSyncPlayerSkillData;
import grill24.potionsplus.skill.reward.ConfiguredGrantableReward;
import grill24.potionsplus.skill.reward.GrantableReward;
import grill24.potionsplus.skill.reward.SkillLevelUpRewardsConfiguration;
import grill24.potionsplus.skill.reward.SkillLevelUpRewardsData;
import io.netty.buffer.ByteBuf;
import net.minecraft.ChatFormatting;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.*;

public class SkillInstance<SC extends SkillConfiguration, S extends Skill<SC>> {
    public static final Codec<SkillInstance<?, ?>> CODEC = RecordCodecBuilder.create(codecBuilder -> codecBuilder.group(
            ResourceKey.codec(PotionsPlusRegistries.CONFIGURED_SKILL).fieldOf("configuredSkill").forGetter(SkillInstance::getConfiguredSkillKey),
            Codec.FLOAT.fieldOf("points").forGetter(SkillInstance::getPoints)
            ).apply(codecBuilder, SkillInstance::new));
    public static final StreamCodec<ByteBuf, SkillInstance<?, ?>> STREAM_CODEC = StreamCodec.composite(
            ResourceKey.streamCodec(PotionsPlusRegistries.CONFIGURED_SKILL),
            SkillInstance::getConfiguredSkillKey,
            ByteBufCodecs.FLOAT,
            SkillInstance::getPoints,
            SkillInstance::new);
    public static final StreamCodec<ByteBuf, List<SkillInstance<?, ?>>> LIST_STREAM_CODEC = STREAM_CODEC.apply(
            ByteBufCodecs.list()
    );

    private final ResourceKey<ConfiguredSkill<?, ?>> configuredSkillId;

    private float points = 0;

    public SkillInstance(ResourceKey<ConfiguredSkill<?, ?>> configuredSkill, float points) {
        this.configuredSkillId = configuredSkill;
        this.points = points;
    }

    public SkillInstance(ResourceKey<ConfiguredSkill<?, ?>> configuredSkillId) {
        this(configuredSkillId, 0);
    }

    public void clear() {
        points = 0;
    }

    public ResourceKey<ConfiguredSkill<?,?>> getConfiguredSkillKey() {
        return configuredSkillId;
    }

    public boolean isConfiguredSkill(ResourceLocation configuredSkillId) {
        return getConfiguredSkillKey().equals(configuredSkillId);
    }

    public ConfiguredSkill<SC, S> getConfiguredSkill(RegistryAccess registryAccess) {
        return (ConfiguredSkill<SC, S>) registryAccess.registryOrThrow(PotionsPlusRegistries.CONFIGURED_SKILL).get(getConfiguredSkillKey());
    }

    public void addPointsWithGrindingPenalty(Player player, float points, PointEarningHistory pointEarningHistory) {
        addPoints(player, applyGrindingPenalty(pointEarningHistory, 0.75f, points));
    }

    public float applyGrindingPenalty(PointEarningHistory pointEarningHistory, float basePenalty, float pointsIn) {
        PointEarningHistory.Window recentHistory = pointEarningHistory.getWindowLast1000();

        // Calculate penalty based on normalized entropy
        float entropyPenalty = (1 - recentHistory.getNormalizedEntropy());
        float frequencyPenalty = recentHistory.getFrequency(getConfiguredSkillKey());
        return (1 - (basePenalty * entropyPenalty * frequencyPenalty)) * pointsIn;
    }

    public void addPoints(Player player, float points) {
        if(points == 0)
            return;

        RegistryAccess registryAccess = player.registryAccess();
        float pointsBefore = this.points;
        int levelBefore = Math.min(getLevel(registryAccess), getConfiguredSkill(registryAccess).getLevelMax());

        ConfiguredSkill<SC, S> configuredSkill = getConfiguredSkill(registryAccess);
        this.points += points;
        this.points = Math.min(this.points, configuredSkill.getPointsMax());

        int levelAfter = Math.min(getLevel(registryAccess), configuredSkill.getLevelMax());

        if (player instanceof ServerPlayer serverPlayer) {
            if (levelBefore != levelAfter && levelAfter == getConfiguredSkill(registryAccess).getLevelMax()) {
                // Send alert
                PacketDistributor.sendToPlayer(serverPlayer, new ClientboundDisplayAlertWithParameter(Translations.TOOLTIP_POTIONSPLUS_SKILL_MAX_LEVEL, true, Integer.toString(levelAfter)));
            } else if(pointsBefore != this.points && this.points == configuredSkill.getPointsMax()) {
                // Send alert
                PacketDistributor.sendToPlayer(serverPlayer, new ClientboundDisplayAlertWithParameter(Translations.TOOLTIP_POTIONSPLUS_SKILL_MAX_POINTS, true, Integer.toString(levelAfter)));
            }


            if(levelAfter > levelBefore) {
                for (int i = levelBefore + 1; i <= levelAfter; i++) {
                    // Grant rewards
                    SkillLevelUpRewardsConfiguration rewardsConfiguration = configuredSkill.config().getData().rewardsConfiguration();
                    rewardsConfiguration.tryGetRewardForLevel(i).ifPresent(reward -> reward.grant(serverPlayer));

                    // Send chat message with reward description
                    if (getConfiguredSkill(registryAccess).config().getData().rewardsConfiguration().hasRewardForLevel(i)) {
                        serverPlayer.sendSystemMessage(getRewardDescription(registryAccess, i));
                    }
                }

                // Build alert
                MutableComponent levelUpAlert = Component.empty();
                levelUpAlert.append(getConfiguredSkill(registryAccess).getChatHeader());
                levelUpAlert.append(Component.translatable(Translations.TOOLTIP_POTIONSPLUS_SKILL_LEVEL_UP, levelAfter).withStyle(ChatFormatting.WHITE));

                PacketDistributor.sendToPlayer(serverPlayer,
                        new ClientboundDisplayAlert(levelUpAlert), // Send alert
                        new ClientboundSyncPlayerSkillData(SkillsData.getPlayerData(player)) // Sync skills data
                );

            }
        }
    }

    public float getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getLevel(RegistryAccess registryAccess) {
        return getConfiguredSkill(registryAccess).getLevel(this);
    }

    public float getPartialLevel(RegistryAccess registryAccess) {
        return getConfiguredSkill(registryAccess).getLevelPartial(this);
    }

    public int getPointsMax(RegistryAccess registryAccess) {
        return getConfiguredSkill(registryAccess).getPointsMax();
    }

    public int getLevelMax(RegistryAccess registryAccess) {
        return getConfiguredSkill(registryAccess).getLevelMax();
    }

    public Component getProgressToNextLevel(RegistryAccess registryAccess, boolean fluffText, int numProgressBarChars) {
        MutableComponent component = Component.empty();

        int currentLevel = getLevel(registryAccess);
        int nextLevel = currentLevel + 1;
        if (fluffText) {
            component.append(getConfiguredSkill(registryAccess).getChatHeader());
            component.append(getLevelChatHeader(registryAccess, currentLevel, true));
        }

        if (currentLevel >= getConfiguredSkill(registryAccess).getLevelMax()) {
            component.append(Component.literal(" ")).append(Component.translatable(Translations.TOOLTIP_POTIONSPLUS_SKILL_MAX_LEVEL).withStyle(ChatFormatting.GRAY));
            return component;
        }

        // Progress bar by coloring "|" char based on progress
        float progress = getPartialLevel(registryAccess) - currentLevel;
        int greenBars = (int) (numProgressBarChars * progress);
        int grayBars = numProgressBarChars - greenBars;
        component.append(Component.literal("|".repeat(greenBars)).withStyle(ChatFormatting.GREEN));
        component.append(Component.literal("|".repeat(grayBars)).withStyle(ChatFormatting.GRAY));

        if (fluffText) {
            component.append(Component.literal(" ")).append(getLevelChatHeader(registryAccess, nextLevel, false));
        }

        Optional<SkillLevelUpRewardsData> rewardsData = getConfiguredSkill(registryAccess).config().getData().rewardsConfiguration().tryGetRewardForLevel(nextLevel);
        rewardsData.ifPresent(skillLevelUpRewardsData -> component.append(skillLevelUpRewardsData.getDescription().copy().withStyle(ChatFormatting.GRAY)));

        return component;
    }

    public Component getRewardDescription(RegistryAccess registryAccess, int level) {
        return getRewardDescription(registryAccess, level, true, true, true);
    }

    public Component getRewardDescription(RegistryAccess registryAccess, int level, boolean includeSkillName, boolean includeLevel, boolean includeRewards) {
        MutableComponent component = Component.empty();
        if (includeSkillName) {
            component.append(getConfiguredSkill(registryAccess).getChatHeader());
        }

        int currentLevel = getLevel(registryAccess);
        boolean isUnlocked = currentLevel >= level;
        if (includeLevel) {
            component.append(getLevelChatHeader(registryAccess, level, isUnlocked));
        }

        if (includeRewards) {
            Optional<SkillLevelUpRewardsData> rewardsData = getConfiguredSkill(registryAccess).config().getData().rewardsConfiguration().tryGetRewardForLevel(level);
            rewardsData.ifPresent(skillLevelUpRewardsData -> component.append(skillLevelUpRewardsData.getDescription().copy().withStyle(isUnlocked ? ChatFormatting.GREEN : ChatFormatting.GRAY)));
        }

        return component;
    }

    public boolean hasRewardsForLevel(RegistryAccess registryAccess, int level) {
        return getConfiguredSkill(registryAccess).config().getData().rewardsConfiguration().hasRewardForLevel(level);
    }

    public Component getLevelChatHeader(RegistryAccess registryAccess, int level, boolean isUnlocked) {
        MutableComponent levelComponent = Component.translatable(Translations.TOOLTIP_POTIONSPLUS_SKILL_LEVEL, level).withStyle(isUnlocked ? ChatFormatting.GREEN : ChatFormatting.GRAY);
        return levelComponent;
    }
}
