package grill24.potionsplus.skill.ability;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import grill24.potionsplus.core.PotionsPlus;
import grill24.potionsplus.network.ClientboundSyncPlayerSkillData;
import grill24.potionsplus.skill.SkillsData;
import grill24.potionsplus.skill.ability.instance.AbilityInstanceType;
import grill24.potionsplus.skill.ability.instance.AbilityInstanceSerializable;
import grill24.potionsplus.utility.Utility;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class PlayerAbility<AC extends PlayerAbilityConfiguration> {
    public final MapCodec<ConfiguredPlayerAbility<AC, PlayerAbility<AC>>> configuredCodec;
    private final Set<AbilityInstanceType<?>> allowedInstanceTypes;

    public PlayerAbility(Codec<AC> configurationCodec, Set<AbilityInstanceType<?>> allowedInstanceTypes) {
        this.configuredCodec = configurationCodec.fieldOf("config").xmap(configuration -> new ConfiguredPlayerAbility<>(this, configuration), ConfiguredPlayerAbility::config);
        this.allowedInstanceTypes = allowedInstanceTypes;
    }

    public MapCodec<ConfiguredPlayerAbility<AC, PlayerAbility<AC>>> configuredCodec() {
        return this.configuredCodec;
    }

    public Component getDescription(AC config, Object... params) {
        return Component.translatable(config.getData().translationKey(), params);
    }

    public Optional<List<List<Component>>> getLongDescription(AbilityInstanceSerializable<?, ?> instance, AC config, Object... params) {
        List<List<Component>> components = new ArrayList<>();
        List<Component> split = Utility.splitOnLinebreaks(Component.translatable(config.getData().longTranslationKey(), params));
        for (Component component : split) {
            components.add(Collections.singletonList(component));
        }

        return config.getData().longTranslationKey().isEmpty() ? Optional.empty() : Optional.of(components);
    }

    public abstract AbilityInstanceSerializable<? ,?> createInstance(ServerPlayer player, Holder<ConfiguredPlayerAbility<?, ?>> ability);

    private boolean isAllowedInstanceType(AbilityInstanceSerializable<?, ?> instance) {
        boolean valid = instance != null && allowedInstanceTypes.contains(instance.type());
        if (!valid) {
            PotionsPlus.LOGGER.warn("Attempted to enable an instance of type {} for ability {}, which is not allowed. Ability will not be enabled.", instance.type(), this);
        }
        return valid;
    }

    public void enable(ServerPlayer player, AC config, AbilityInstanceSerializable<?, ?> abilityInstance) {
        if (isAllowedInstanceType(abilityInstance)) {
            abilityInstance.data().setEnabled(true);
            onEnable(player, config, abilityInstance);

            PacketDistributor.sendToPlayer(player, new ClientboundSyncPlayerSkillData(SkillsData.getPlayerData(player)));
        } else {
            SkillsData.updatePlayerData(player, data -> data.resetAbilityInstance(player, abilityInstance.data().getHolder()));
        }
    }

    public void disable(ServerPlayer player, AC config, @Nullable AbilityInstanceSerializable<?, ?> abilityInstance) {
        onDisable(player, config);
        if (abilityInstance != null) {
            abilityInstance.data().setEnabled(false);
        }

        PacketDistributor.sendToPlayer(player, new ClientboundSyncPlayerSkillData(SkillsData.getPlayerData(player)));
    }

    protected abstract void onEnable(ServerPlayer player, AC config, AbilityInstanceSerializable<?, ?> abilityInstance);
    protected abstract void onDisable(ServerPlayer player, AC config);

    public void onInstanceChanged(ServerPlayer player, AC config, AbilityInstanceSerializable<?, ?> abilityInstance) {
        if (abilityInstance.data().isEnabled()) {
            abilityInstance.tryEnable(player);
        } else {
            abilityInstance.tryDisable(player);
        }
    }

    public abstract void onAbilityGranted(ServerPlayer player, AC config, AbilityInstanceSerializable<?, ?> abilityInstance);
    public abstract void onAbilityRevoked(ServerPlayer player, AC config);
}
