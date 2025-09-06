package grill24.potionsplus.skill.reward;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;
import java.util.Optional;

public abstract class GrantableReward<RC extends GrantableRewardConfiguration> {
    public final MapCodec<ConfiguredGrantableReward<RC, GrantableReward<RC>>> configuredCodec;

    public GrantableReward(Codec<RC> configurationCodec) {
        this.configuredCodec = configurationCodec.fieldOf("config").xmap(configuration -> new ConfiguredGrantableReward<>(this, configuration), ConfiguredGrantableReward::config);
    }

    public MapCodec<ConfiguredGrantableReward<RC, GrantableReward<RC>>> configuredCodec() {
        return this.configuredCodec;
    }

    // Used for level rewards description. Should be un-styled, plain text.
    public abstract Optional<Component> getDescription(RegistryAccess registryAccess, RC config);

    // Used for item tooltips (e.g. choice reward item descriptions)
    public List<List<Component>> getMultiLineRichDescription(RegistryAccess registryAccess, RC config) {
        Optional<Component> description = getDescription(registryAccess, config);
        return description.map(component -> List.of(List.of(component))).orElseGet(List::of);

    }

    public abstract void grant(ResourceKey<ConfiguredGrantableReward<?, ?>> holder, RC config, ServerPlayer player);
}