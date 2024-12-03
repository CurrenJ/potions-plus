package grill24.potionsplus.skill.ability;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public abstract class PlayerAbility<E, AC extends PlayerAbilityConfiguration> {
    public final MapCodec<ConfiguredPlayerAbility<AC, PlayerAbility<E, AC>>> configuredCodec;

    public PlayerAbility(Codec<AC> configurationCodec) {
        this.configuredCodec = configurationCodec.fieldOf("config").xmap(configuration -> new ConfiguredPlayerAbility<>(this, configuration), ConfiguredPlayerAbility::config);
    }

    public MapCodec<ConfiguredPlayerAbility<AC, PlayerAbility<E, AC>>> configuredCodec() {
        return this.configuredCodec;
    }

    public Component getDescription(AC config) {
        return Component.translatable(config.getData().translationKey());
    }

    public AbilityInstance createInstance(ServerPlayer player, Holder<ConfiguredPlayerAbility<?, ?>> ability) {
        return new AbilityInstance(player, ability, true);
    }

    public abstract void enable(ServerPlayer player, AC config, E evaluationData);
    public abstract void disable(ServerPlayer player, AC config, E evaluationData);

    public abstract void onAbilityGranted(ServerPlayer player, AC config);
    public abstract void onAbilityRevoked(ServerPlayer player, AC config);
}
