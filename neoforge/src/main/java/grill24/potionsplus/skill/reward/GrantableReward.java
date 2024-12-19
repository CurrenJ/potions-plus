package grill24.potionsplus.skill.reward;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public abstract class GrantableReward<RC extends GrantableRewardConfiguration> {
    public final MapCodec<ConfiguredGrantableReward<RC, GrantableReward<RC>>> configuredCodec;

    public GrantableReward(Codec<RC> configurationCodec) {
        this.configuredCodec = configurationCodec.fieldOf("config").xmap(configuration -> new ConfiguredGrantableReward<>(this, configuration), ConfiguredGrantableReward::config);
    }

    public MapCodec<ConfiguredGrantableReward<RC, GrantableReward<RC>>> configuredCodec() {
        return this.configuredCodec;
    }

    public abstract Component getDescription(RC config);
    public abstract void grant(Holder<ConfiguredGrantableReward<?, ?>> holder, RC config, ServerPlayer player);
}
