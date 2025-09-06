package grill24.potionsplus.skill.reward;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import grill24.potionsplus.core.ConfiguredGrantableRewards;
import grill24.potionsplus.core.GrantableRewards;
import grill24.potionsplus.core.PotionsPlus;
import grill24.potionsplus.core.PotionsPlusRegistries;
import grill24.potionsplus.skill.SkillsData;
import grill24.potionsplus.skill.ability.ConfiguredPlayerAbility;
import grill24.potionsplus.skill.ability.instance.AbilityInstanceSerializable;
import grill24.potionsplus.skill.ability.instance.AdjustableStrengthAbilityInstanceData;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

import static grill24.potionsplus.utility.Utility.ppId;

public class IncreaseAbilityStrengthReward extends GrantableReward<IncreaseAbilityStrengthReward.IncreaseAbilityStrengthRewardConfiguration> {
    public static class IncreaseAbilityStrengthRewardConfiguration extends GrantableRewardConfiguration {
        public static final Codec<IncreaseAbilityStrengthRewardConfiguration> CODEC = RecordCodecBuilder.create(
                codecBuilder -> codecBuilder.group(
                        ConfiguredPlayerAbility.HOLDER_CODECS.holderCodec().fieldOf("ability").forGetter(instance -> instance.ability),
                        Codec.FLOAT.fieldOf("strengthIncrease").forGetter(instance -> instance.strengthIncrease),
                        Codec.STRING.optionalFieldOf("translationKey", "").forGetter(instance -> instance.translationKey)
                ).apply(codecBuilder, IncreaseAbilityStrengthRewardConfiguration::new)
        );

        public final Holder<ConfiguredPlayerAbility<?, ?>> ability;
        public final float strengthIncrease;
        public final String translationKey;

        public IncreaseAbilityStrengthRewardConfiguration(Holder<ConfiguredPlayerAbility<?, ?>> ability, float strengthIncrease, String translationKey) {
            this.ability = ability;
            this.strengthIncrease = strengthIncrease;
            this.translationKey = translationKey;
        }

        public IncreaseAbilityStrengthRewardConfiguration(Holder<ConfiguredPlayerAbility<?, ?>> ability, float strengthIncrease) {
            this(ability, strengthIncrease, "");
        }
    }

    public IncreaseAbilityStrengthReward() {
        super(IncreaseAbilityStrengthRewardConfiguration.CODEC);
    }

    @Override
    public Optional<Component> getDescription(RegistryAccess registryAccess, IncreaseAbilityStrengthRewardConfiguration config) {
        if (!config.translationKey.isEmpty()) {
            // Use the translation key if it is set
            return Optional.of(Component.translatable(config.translationKey));
        } else {
            // Otherwise, use the ability description
            ConfiguredPlayerAbility<?, ?> ability = config.ability.value();
            return Optional.of(ability.getDescription(config.strengthIncrease));
        }
    }

    @Override
    public void grant(ResourceKey<ConfiguredGrantableReward<?, ?>> holder, IncreaseAbilityStrengthRewardConfiguration config, ServerPlayer player) {
        RegistryAccess registryAccess = player.registryAccess();

        SkillsData skillsData = SkillsData.getPlayerData(player);
        skillsData.unlockAbility(player, config.ability.getKey());

        Optional<AbilityInstanceSerializable<?, ?>> abilityInstance = skillsData.getAbilityInstance(registryAccess, config.ability.getKey());
        if (abilityInstance.isPresent()) {
            if (abilityInstance.get().data() instanceof AdjustableStrengthAbilityInstanceData adjustableStrengthData) {
                adjustableStrengthData.increaseMaxAbilityStrength(config.strengthIncrease);
                adjustableStrengthData.setAbilityStrength(adjustableStrengthData.getAbilityStrength() + config.strengthIncrease);
                abilityInstance.get().onInstanceChanged(player);
            } else {
                PotionsPlus.LOGGER.warn("Attempted to increase max ability strength for player {} with ability key {}, but the ability instance is not adjustable strength.", player.getName(), config.ability);
            }
            return;
        }

        PotionsPlus.LOGGER.warn("Failed to grant increase to max ability strength for player {} with ability key {}.", player.getName(), config.ability);
    }

    public static class Builder implements ConfiguredGrantableRewards.IRewardBuilder {
        private final ResourceKey<ConfiguredGrantableReward<?, ?>> key;

        private ResourceKey<ConfiguredPlayerAbility<?, ?>> ability;
        private float strengthIncrease;
        private String translationKey;

        public Builder(String name) {
            this.strengthIncrease = 0;

            this.key = ResourceKey.create(PotionsPlusRegistries.CONFIGURED_GRANTABLE_REWARD, ppId(name));
            this.translationKey = "";
        }

        public Builder strength(float strengthIncrease) {
            this.strengthIncrease = strengthIncrease;
            return this;
        }

        public Builder ability(ResourceKey<ConfiguredPlayerAbility<?, ?>> ability) {
            this.ability = ability;
            return this;
        }

        public Builder translationKey(String translationKey) {
            this.translationKey = translationKey;
            return this;
        }

        public ResourceKey<ConfiguredGrantableReward<?, ?>> getKey() {
            return key;
        }

        @Override
        public void generate(BootstrapContext<ConfiguredGrantableReward<?, ?>> context) {
            if (this.ability == null) {
                throw new IllegalArgumentException("Ability must be set");
            }

            if (this.strengthIncrease == 0) {
                throw new IllegalArgumentException("Strength increase must be set");
            }

            Optional<Holder.Reference<ConfiguredPlayerAbility<?, ?>>> ability = context.lookup(PotionsPlusRegistries.CONFIGURED_PLAYER_ABILITY).get(this.ability);
            if (ability.isEmpty()) {
                throw new IllegalArgumentException("Ability not found: " + this.ability);
            }

            context.register(key, new ConfiguredGrantableReward<>(
                    GrantableRewards.INCREASE_ABILITY_STRENGTH.value(),
                    new IncreaseAbilityStrengthRewardConfiguration(ability.get(), this.strengthIncrease, this.translationKey)
            ));
        }
    }
}
