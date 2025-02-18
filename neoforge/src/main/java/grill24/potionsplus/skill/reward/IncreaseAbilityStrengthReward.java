package grill24.potionsplus.skill.reward;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import grill24.potionsplus.core.PotionsPlus;
import grill24.potionsplus.core.PotionsPlusRegistries;
import grill24.potionsplus.skill.SkillsData;
import grill24.potionsplus.skill.ability.ConfiguredPlayerAbility;
import grill24.potionsplus.skill.ability.instance.AbilityInstanceSerializable;
import grill24.potionsplus.skill.ability.instance.AdjustableStrengthAbilityInstanceData;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

public class IncreaseAbilityStrengthReward extends GrantableReward<IncreaseAbilityStrengthReward.IncreaseAbilityStrengthRewardConfiguration> {
    public static class IncreaseAbilityStrengthRewardConfiguration extends GrantableRewardConfiguration {
        public static final Codec<IncreaseAbilityStrengthRewardConfiguration> CODEC = RecordCodecBuilder.create(
            codecBuilder -> codecBuilder.group(
                ResourceKey.codec(PotionsPlusRegistries.CONFIGURED_PLAYER_ABILITY).fieldOf("abilityKey").forGetter(instance -> instance.abilityKey),
                Codec.FLOAT.fieldOf("strengthIncrease").forGetter(instance -> instance.strengthIncrease)
            ).apply(codecBuilder, IncreaseAbilityStrengthRewardConfiguration::new)
        );

        public final ResourceKey<ConfiguredPlayerAbility<?, ?>> abilityKey;
        public final float strengthIncrease;

        public IncreaseAbilityStrengthRewardConfiguration(ResourceKey<ConfiguredPlayerAbility<?, ?>> abilityKey, float strengthIncrease) {
            this.abilityKey = abilityKey;
            this.strengthIncrease = strengthIncrease;
        }
    }

    public IncreaseAbilityStrengthReward() {
        super(IncreaseAbilityStrengthRewardConfiguration.CODEC);
    }

    @Override
    public Optional<Component> getDescription(IncreaseAbilityStrengthRewardConfiguration config) {
        return Optional.empty();
    }

    @Override
    public void grant(Holder<ConfiguredGrantableReward<?, ?>> holder, IncreaseAbilityStrengthRewardConfiguration config, ServerPlayer player) {
        RegistryAccess registryAccess = player.registryAccess();
        HolderLookup.RegistryLookup<ConfiguredPlayerAbility<?, ?>> configuredAbilityLookup = registryAccess.lookupOrThrow(PotionsPlusRegistries.CONFIGURED_PLAYER_ABILITY);
        Optional<Holder.Reference<ConfiguredPlayerAbility<?, ?>>> ability = configuredAbilityLookup.get(config.abilityKey);

        if (ability.isPresent()) {
            SkillsData skillsData = SkillsData.getPlayerData(player);

            Holder<ConfiguredPlayerAbility<?, ?>> configuredAbility = ability.get();
            skillsData.activateAbility(player, configuredAbility.getKey());

            Optional<AbilityInstanceSerializable<?, ?>> abilityInstance = skillsData.getAbilityInstance(registryAccess, configuredAbility.getKey());
            if (abilityInstance.isPresent()) {
                if (abilityInstance.get().data() instanceof AdjustableStrengthAbilityInstanceData adjustableStrengthData) {
                    adjustableStrengthData.increaseMaxAbilityStrength(config.strengthIncrease);
                    return;
                } else {
                    PotionsPlus.LOGGER.warn("Attempted to increase max ability strength for player {} with ability key {}, but the ability instance is not adjustable strength.", player.getName(), config.abilityKey);
                    return;
                }
            }
        }

        PotionsPlus.LOGGER.warn("Failed to grant increase to max ability strength for player {} with ability key {}.", player.getName(), config.abilityKey);
    }
}
