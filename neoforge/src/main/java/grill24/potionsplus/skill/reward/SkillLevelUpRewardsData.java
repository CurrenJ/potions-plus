package grill24.potionsplus.skill.reward;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import grill24.potionsplus.core.PotionsPlus;
import grill24.potionsplus.core.PotionsPlusRegistries;
import grill24.potionsplus.skill.ability.ConfiguredPlayerAbility;
import net.minecraft.core.*;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public record SkillLevelUpRewardsData(String translationKey, List<Holder<ConfiguredGrantableReward<?, ?>>> rewards) {
    public static final Codec<SkillLevelUpRewardsData> CODEC = RecordCodecBuilder.create(codecBuilder -> codecBuilder.group(
        Codec.STRING.optionalFieldOf("translationKey", "").forGetter(SkillLevelUpRewardsData::translationKey),
        ConfiguredGrantableReward.CODEC.listOf().optionalFieldOf("advancementRewards", List.of()).forGetter(SkillLevelUpRewardsData::rewards)
    ).apply(codecBuilder, SkillLevelUpRewardsData::new));

    public static HolderSet<ConfiguredPlayerAbility<?, ?>> tryBuildValidAbilityList(HolderGetter<ConfiguredPlayerAbility<?, ?>> lookup, Collection<ResourceKey<ConfiguredPlayerAbility<?, ?>>> keys) {
        List<Holder<ConfiguredPlayerAbility<?, ?>>> validAbilities = new ArrayList<>();
        for (ResourceKey<ConfiguredPlayerAbility<?, ?>> key : keys) {
            Optional<Holder.Reference<ConfiguredPlayerAbility<?, ?>>> optional = lookup.get(key);
            optional.ifPresent(validAbilities::add);
        }

        return HolderSet.direct(validAbilities);
    }

        @SafeVarargs
    public static HolderSet<ConfiguredPlayerAbility<?, ?>> tryBuildValidAbilityList(HolderGetter<ConfiguredPlayerAbility<?, ?>> lookup, ResourceKey<ConfiguredPlayerAbility<?, ?>>... keys) {
        return tryBuildValidAbilityList(lookup, List.of(keys));
    }

    public void grant(ServerPlayer player) {
        for (Holder<ConfiguredGrantableReward<?, ?>> reward : rewards) {
            reward.value().grant(reward, player);
        }
    }

    public Component getDescription() {
        MutableComponent component = Component.empty();
        boolean hasText = false;

        if (!translationKey.isEmpty()) {
            component.append(Component.translatable(translationKey));
            hasText = true;
        }

        for (Holder<ConfiguredGrantableReward<?, ?>> reward : rewards) {
            if (reward.value().getDescription() != null) {
                if (hasText) {
                    component.append(Component.literal(", "));
                }
                component.append(reward.value().getDescription());
                hasText = true;
            }
        }

        return component;
    }

    public static class Builder {
        private String translationKey = "";

        private final List<Holder<ConfiguredGrantableReward<?, ?>>> rewards = new ArrayList<>();

        public static Builder create() {
            return new Builder();
        }

        public Builder translationKey(String translationKey) {
            this.translationKey = translationKey;
            return this;
        }

        public Builder addReward(HolderGetter<ConfiguredGrantableReward<?, ?>> lookup, ResourceKey<ConfiguredGrantableReward<?, ?>> reward) {
            Optional<Holder.Reference<ConfiguredGrantableReward<?, ?>>> optional = lookup.get(reward);
            if (optional.isPresent()) {
                this.rewards.add(optional.get());
            } else {
                PotionsPlus.LOGGER.error("Failed to add reward {} to skill level up rewards data", reward.location());
            }
            return this;
        }

        public Builder clear() {
            rewards.clear();
            translationKey = "";
            return this;
        }

        public SkillLevelUpRewardsData build() {
            return new SkillLevelUpRewardsData(translationKey, new ArrayList<>(rewards));
        }
    }
}
