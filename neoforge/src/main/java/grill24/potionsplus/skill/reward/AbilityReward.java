package grill24.potionsplus.skill.reward;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import grill24.potionsplus.core.ConfiguredGrantableRewards;
import grill24.potionsplus.core.GrantableRewards;
import grill24.potionsplus.core.PotionsPlusRegistries;
import grill24.potionsplus.skill.SkillsData;
import grill24.potionsplus.skill.ability.ConfiguredPlayerAbility;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

import static grill24.potionsplus.utility.Utility.ppId;

public class AbilityReward extends GrantableReward<AbilityReward.AbilityRewardConfiguration> {
    public AbilityReward() {
        super(AbilityRewardConfiguration.CODEC);
    }

    public static class AbilityRewardConfiguration extends GrantableRewardConfiguration {
        public static final Codec<AbilityRewardConfiguration> CODEC = RecordCodecBuilder.create(codecBuilder -> codecBuilder.group(
                ConfiguredPlayerAbility.HOLDER_CODECS.holderCodec().fieldOf("configuredPlayerAbility").forGetter(instance -> instance.ability)
        ).apply(codecBuilder, AbilityRewardConfiguration::new));

        public Holder<ConfiguredPlayerAbility<?, ?>> ability;

        public AbilityRewardConfiguration(Holder<ConfiguredPlayerAbility<?, ?>> ability) {
            this.ability = ability;
        }
    }

    public static ConfiguredGrantableReward<AbilityRewardConfiguration, AbilityReward> ability(BootstrapContext<ConfiguredGrantableReward<?, ?>> context, ResourceKey<ConfiguredPlayerAbility<?, ?>> ability) {
        HolderGetter<ConfiguredPlayerAbility<?, ?>> lookup = context.lookup(PotionsPlusRegistries.CONFIGURED_PLAYER_ABILITY);
        return new ConfiguredGrantableReward<>(GrantableRewards.ABILITY.value(), new AbilityRewardConfiguration(lookup.getOrThrow(ability)));
    }

    @Override
    public Optional<Component> getDescription(AbilityRewardConfiguration config) {
        return Optional.ofNullable(config.ability.value().getDescription());
    }

    @Override
    public void grant(Holder<ConfiguredGrantableReward<?, ?>> holder, AbilityRewardConfiguration config, ServerPlayer player) {
        SkillsData.updatePlayerData(player, data -> data.activateAbility(player, config.ability.getKey()));
    }

    public static class AbilityRewardBuilder implements ConfiguredGrantableRewards.IRewardBuilder {
        private final ResourceKey<ConfiguredPlayerAbility<?, ?>> abilityKey;
        private final ResourceKey<ConfiguredGrantableReward<?, ?>> abilityRewardKey;

        public AbilityRewardBuilder(ResourceKey<ConfiguredPlayerAbility<?, ?>> abilityKey) {
            this.abilityKey = abilityKey;
            this.abilityRewardKey = registerKey(abilityKey);
        }

        public ResourceKey<ConfiguredGrantableReward<?, ?>> getKey() {
            return abilityRewardKey;
        }

        private static ResourceKey<ConfiguredGrantableReward<?, ?>> registerKey(ResourceKey<ConfiguredPlayerAbility<?, ?>> ability) {
            return ResourceKey.create(PotionsPlusRegistries.CONFIGURED_GRANTABLE_REWARD, ppId(ability.location().getPath()));
        }

        @Override
        public void generate(BootstrapContext<ConfiguredGrantableReward<?, ?>> context) {
            context.register(this.abilityRewardKey, ability(context, abilityKey));
        }
    }
}
