package grill24.potionsplus.skill.reward;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import grill24.potionsplus.core.ConfiguredGrantableRewards;
import grill24.potionsplus.core.GrantableRewards;
import grill24.potionsplus.core.PotionsPlusRegistries;
import grill24.potionsplus.network.ClientboundDisplayTossupAnimationPacket;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static grill24.potionsplus.utility.Utility.ppId;

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
    public Optional<Component> getDescription(AdvancementRewardConfiguration config) {
        return config.translationKey.isBlank() ? Optional.empty() : Optional.of(Component.translatable(config.translationKey));
    }

    @Override
    public void grant(Holder<ConfiguredGrantableReward<?, ?>> holder, AdvancementRewardConfiguration config, ServerPlayer player) {
        config.rewards.grant(player);
    }

    public static class AdvancementRewardBuilder implements ConfiguredGrantableRewards.IRewardBuilder {
        private String translationKey;
        private final AdvancementRewards rewards;
        private final ResourceKey<ConfiguredGrantableReward<?, ?>> key;

        public AdvancementRewardBuilder(String name, AdvancementRewards rewards) {
            this.rewards = rewards;
            this.key = ResourceKey.create(PotionsPlusRegistries.CONFIGURED_GRANTABLE_REWARD, ppId(name));
            this.translationKey = "";
        }

        public ResourceKey<ConfiguredGrantableReward<?, ?>> getKey() {
            return key;
        }

        public AdvancementRewardBuilder translation(String translationKey) {
            this.translationKey = translationKey;
            return this;
        }

        @Override
        public void generate(BootstrapContext<ConfiguredGrantableReward<?, ?>> context) {
            context.register(key, new ConfiguredGrantableReward<>(
                    GrantableRewards.ADVANCEMENT.value(),
                    new AdvancementRewardConfiguration(translationKey, rewards)
            ));
        }
    }
}
