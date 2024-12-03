package grill24.potionsplus.skill.reward;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import grill24.potionsplus.skill.SkillsData;
import grill24.potionsplus.skill.ability.ConfiguredPlayerAbility;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public record SkillLevelUpRewardsData(String translationKey, List<AdvancementRewards> advancementRewards, HolderSet<ConfiguredPlayerAbility<?, ?>> abilities, List<AnimatedItemRewardData> animatedItemRewards) implements IGrantableReward{
    public static final Codec<SkillLevelUpRewardsData> CODEC = RecordCodecBuilder.create(codecBuilder -> codecBuilder.group(
        Codec.STRING.optionalFieldOf("translationKey", "").forGetter(SkillLevelUpRewardsData::translationKey),
        AdvancementRewards.CODEC.listOf().optionalFieldOf("advancementRewards", List.of()).forGetter(SkillLevelUpRewardsData::advancementRewards),
        ConfiguredPlayerAbility.HOLDER_CODECS.holderSetCodec().optionalFieldOf("abilities", HolderSet.empty()).forGetter(SkillLevelUpRewardsData::abilities),
        AnimatedItemRewardData.CODEC.listOf().optionalFieldOf("animatedItemRewards", List.of()).forGetter(SkillLevelUpRewardsData::animatedItemRewards)
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
        // Grant advancement rewards
        advancementRewards.forEach(reward -> reward.grant(player));

        // Grant abilities
        SkillsData.updatePlayerData(player, (skillsData -> skillsData.activateAbilities(player, abilities)));

        // Grant animated item rewards
        animatedItemRewards.forEach(reward -> reward.grant(player));
    }

    public Component getDescription() {
        MutableComponent component = Component.empty();
        boolean hasText = false;

        if (!translationKey.isEmpty()) {
            component.append(Component.translatable(translationKey));
            hasText = true;
        }

        for (Holder<ConfiguredPlayerAbility<?, ?>> ability : abilities) {
            if (hasText) {
                component.append(Component.literal(", "));
            }
            component.append(ability.value().getDescription());
            hasText = true;
        }

        return component;
    }

    public static class Builder {
        private String translationKey = "";
        private final List<AdvancementRewards> advancementRewards = new ArrayList<>();
        private final List<ResourceKey<ConfiguredPlayerAbility<?, ?>>> abilities = new ArrayList<>();
        private final List<AnimatedItemRewardData> animatedItemRewards = new ArrayList<>();

        public static Builder create() {
            return new Builder();
        }

        public Builder translationKey(String translationKey) {
            this.translationKey = translationKey;
            return this;
        }

        public Builder addAdvancementRewards(AdvancementRewards advancementRewards) {
            this.advancementRewards.add(advancementRewards);
            return this;
        }

        public Builder addAbility(ResourceKey<ConfiguredPlayerAbility<?, ?>> ability) {
            this.abilities.add(ability);
            return this;
        }

        public Builder addAnimatedItemReward(AnimatedItemRewardData animatedItemReward) {
            this.animatedItemRewards.add(animatedItemReward);
            return this;
        }

        public SkillLevelUpRewardsData build(HolderGetter<ConfiguredPlayerAbility<?, ?>> lookup) {
            return new SkillLevelUpRewardsData(translationKey, advancementRewards, tryBuildValidAbilityList(lookup, abilities.toArray(new ResourceKey[0])), animatedItemRewards);
        }
    }
}
