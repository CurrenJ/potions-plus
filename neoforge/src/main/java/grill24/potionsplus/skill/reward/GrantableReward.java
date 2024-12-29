package grill24.potionsplus.skill.reward;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public abstract class GrantableReward<RC extends GrantableRewardConfiguration> {
    public final MapCodec<ConfiguredGrantableReward<RC, GrantableReward<RC>>> configuredCodec;

    public GrantableReward(Codec<RC> configurationCodec) {
        this.configuredCodec = configurationCodec.fieldOf("config").xmap(configuration -> new ConfiguredGrantableReward<>(this, configuration), ConfiguredGrantableReward::config);
    }

    public MapCodec<ConfiguredGrantableReward<RC, GrantableReward<RC>>> configuredCodec() {
        return this.configuredCodec;
    }

    // Used for level rewards description. Should be un-styled, plain text.
    public abstract Component getDescription(RC config);
    // Used for item tooltips (e.g. choice reward item descriptions)
    public List<List<Component>> getMultiLineRichDescription(RC config) { return List.of(List.of(getDescription(config))); }
    public abstract void grant(Holder<ConfiguredGrantableReward<?, ?>> holder, RC config, ServerPlayer player);
}