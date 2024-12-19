package grill24.potionsplus.skill.reward;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import grill24.potionsplus.core.GrantableRewards;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;

public class AdvancementReward extends GrantableReward<AdvancementReward.AdvancementRewardConfiguration> {
    public AdvancementReward() {
        super(AdvancementRewardConfiguration.CODEC);
    }

    public static class AdvancementRewardConfiguration extends GrantableRewardConfiguration {
        public static final Codec<AdvancementRewardConfiguration> CODEC = RecordCodecBuilder.create(codecBuilder -> codecBuilder.group(
                Codec.STRING.fieldOf("translationKey").forGetter(instance -> instance.translationKey),
                AdvancementRewards.CODEC.fieldOf("rewards").forGetter(instance -> instance.rewards)
        ).apply(codecBuilder, AdvancementRewardConfiguration::new));

        public String translationKey;
        public AdvancementRewards rewards;

        public AdvancementRewardConfiguration(AdvancementRewards rewards) {
            this("", rewards);
        }

        public AdvancementRewardConfiguration(String translationKey, AdvancementRewards rewards) {
            this.rewards = rewards;
            this.translationKey = translationKey;
        }
    }

    public static ConfiguredGrantableReward<AdvancementRewardConfiguration, AdvancementReward> advancementRewards(AdvancementRewards rewards) {
        return new ConfiguredGrantableReward<>(GrantableRewards.ADVANCEMENT.value(), new AdvancementRewardConfiguration(rewards));
    }

    @Override
    public Component getDescription(AdvancementRewardConfiguration config) {
        return config.translationKey.isBlank() ? null : Component.translatable(config.translationKey);
    }

    @Override
    public void grant(Holder<ConfiguredGrantableReward<?, ?>> holder, AdvancementRewardConfiguration config, ServerPlayer player) {
        config.rewards.grant(player);
    }
}
