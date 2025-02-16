package grill24.potionsplus.skill.ability;

import grill24.potionsplus.core.AbilityInstanceTypes;
import grill24.potionsplus.skill.ability.instance.AbilityInstanceSerializable;
import grill24.potionsplus.skill.ability.instance.SimpleAbilityInstanceData;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;

import java.util.Set;

public class SimplePlayerAbility extends PlayerAbility<PlayerAbilityConfiguration>{
    public SimplePlayerAbility() {
        super(PlayerAbilityConfiguration.CODEC, Set.of(AbilityInstanceTypes.SIMPLE_TOGGLEABLE.value()));
    }

    @Override
    public AbilityInstanceSerializable<?, ?> createInstance(ServerPlayer player, Holder<ConfiguredPlayerAbility<?, ?>> ability) {
        return new AbilityInstanceSerializable<>(
                AbilityInstanceTypes.SIMPLE_TOGGLEABLE.value(),
                new SimpleAbilityInstanceData(ability, true));
    }

    @Override
    public void onEnable(ServerPlayer player, PlayerAbilityConfiguration config, AbilityInstanceSerializable<?, ?> instance) {}

    @Override
    public void onDisable(ServerPlayer player, PlayerAbilityConfiguration config) {}

    @Override
    public void onInstanceChanged(ServerPlayer player, PlayerAbilityConfiguration config, AbilityInstanceSerializable<?, ?> abilityInstance) {
        abilityInstance.tryEnable(player);
    }

    @Override
    public void onAbilityGranted(ServerPlayer player, PlayerAbilityConfiguration config, AbilityInstanceSerializable<?, ?> instance) {}

    @Override
    public void onAbilityRevoked(ServerPlayer player, PlayerAbilityConfiguration config) {}
}
