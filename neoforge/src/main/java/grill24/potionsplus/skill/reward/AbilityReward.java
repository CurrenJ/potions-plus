package grill24.potionsplus.skill.reward;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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
    public Component getDescription(AbilityRewardConfiguration config) {
        return config.ability.value().getDescription();
    }

    @Override
    public void grant(Holder<ConfiguredGrantableReward<?, ?>> holder, AbilityRewardConfiguration config, ServerPlayer player) {
        SkillsData.updatePlayerData(player, data -> data.activateAbility(player, config.ability.getKey()));
    }
}
