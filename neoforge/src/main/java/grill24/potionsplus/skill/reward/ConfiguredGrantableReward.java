package grill24.potionsplus.skill.reward;

import com.mojang.serialization.Codec;
import grill24.potionsplus.core.PotionsPlusRegistries;
import grill24.potionsplus.utility.HolderCodecs;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.server.level.ServerPlayer;

public record ConfiguredGrantableReward<RC extends GrantableRewardConfiguration, R extends GrantableReward<RC>>(R reward, RC config) {
    public static final Codec<ConfiguredGrantableReward<?, ?>> DIRECT_CODEC = PotionsPlusRegistries.GRANTABLE_REWARD
            .byNameCodec()
            .dispatch(configured -> configured.reward, GrantableReward::configuredCodec);
    public static final Codec<Holder<ConfiguredGrantableReward<?, ?>>> CODEC = RegistryFileCodec.create(PotionsPlusRegistries.CONFIGURED_GRANTABLE_REWARD, DIRECT_CODEC);

    public static final HolderCodecs<ConfiguredGrantableReward<?, ?>> HOLDER_CODECS = new HolderCodecs<>(PotionsPlusRegistries.CONFIGURED_GRANTABLE_REWARD, DIRECT_CODEC);

    @Override
    public String toString() {
        return "Reward: " + this.reward + ": " + this.config;
    }

    public void grant(Holder<ConfiguredGrantableReward<?, ?>> holder, ServerPlayer player) {
        this.reward.grant(holder, this.config, player);
    }

    public Component getDescription() {
        return this.reward.getDescription(config);
    }
}
