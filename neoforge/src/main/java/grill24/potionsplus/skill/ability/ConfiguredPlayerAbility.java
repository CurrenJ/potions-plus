package grill24.potionsplus.skill.ability;

import com.mojang.serialization.Codec;
import grill24.potionsplus.core.PotionsPlusRegistries;
import grill24.potionsplus.skill.ability.instance.AbilityInstanceSerializable;
import grill24.potionsplus.utility.HolderCodecs;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public record ConfiguredPlayerAbility<AC extends PlayerAbilityConfiguration, A extends PlayerAbility<AC>>(A ability, AC config) {
    public static final Codec<ConfiguredPlayerAbility<?, ?>> DIRECT_CODEC = PotionsPlusRegistries.PLAYER_ABILITY
            .byNameCodec()
            .dispatch(configured -> configured.ability, PlayerAbility::configuredCodec);

    public static final HolderCodecs<ConfiguredPlayerAbility<?, ?>> HOLDER_CODECS = new HolderCodecs<>(PotionsPlusRegistries.CONFIGURED_PLAYER_ABILITY, DIRECT_CODEC);

    @Override
    public String toString() {
        return "Configured: " + this.ability + ": " + this.config;
    }

    public Component getDescription() {
        return ability.getDescription(config);
    }

    public void onAbilityGranted(ServerPlayer player, AbilityInstanceSerializable<?, ?> abilityInstance) {
        ability.onAbilityGranted(player, config, abilityInstance);
    }

    public void onAbilityRevoked(ServerPlayer player) {
        ability.onAbilityRevoked(player, config);
    }

    public void tryEnable(ServerPlayer player, AbilityInstanceSerializable<?, ?> abilityInstance) {
        ability.enable(player, config, abilityInstance);
    }

    public void tryDisable(ServerPlayer player, AbilityInstanceSerializable<?, ?> abilityInstance) {
        ability.disable(player, config, abilityInstance);
    }

    public void onInstanceChanged(ServerPlayer player, AbilityInstanceSerializable<?, ?> abilityInstance) {
        ability.onInstanceChanged(player, config, abilityInstance);
    }

    public void toggle(ServerPlayer player, AbilityInstanceSerializable<?, ?> abilityInstance) {
        if (abilityInstance.data().isEnabled()) {
            tryDisable(player, abilityInstance);
        } else {
            tryEnable(player, abilityInstance);
        }
    }
}
