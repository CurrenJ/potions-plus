package grill24.potionsplus.skill.ability;

import grill24.potionsplus.core.AbilityInstanceTypes;
import grill24.potionsplus.core.ConfiguredPlayerAbilities;
import grill24.potionsplus.core.PlayerAbilities;
import grill24.potionsplus.core.PotionsPlusRegistries;
import grill24.potionsplus.skill.ConfiguredSkill;
import grill24.potionsplus.skill.ability.instance.AbilityInstanceSerializable;
import grill24.potionsplus.skill.ability.instance.AbilityInstanceType;
import grill24.potionsplus.skill.ability.instance.SimpleAbilityInstanceData;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;

import java.util.Set;

import static grill24.potionsplus.utility.Utility.ppId;

public class SimplePlayerAbility extends PlayerAbility<PlayerAbilityConfiguration> {
    public SimplePlayerAbility() {
        super(PlayerAbilityConfiguration.CODEC, Set.of(AbilityInstanceTypes.SIMPLE_TOGGLEABLE.value()));
    }

    public SimplePlayerAbility(Set<AbilityInstanceType<?>> instanceTypes) {
        super(PlayerAbilityConfiguration.CODEC, instanceTypes);
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
    public void onAbilityGranted(ServerPlayer player, PlayerAbilityConfiguration config, AbilityInstanceSerializable<?, ?> instance) {}

    @Override
    public void onAbilityRevoked(ServerPlayer player, PlayerAbilityConfiguration config) {}

    public static class Builder implements ConfiguredPlayerAbilities.IAbilityBuilder {
        protected final ResourceKey<ConfiguredPlayerAbility<?, ?>> key;
        protected String translationKey;

        protected ResourceKey<ConfiguredSkill<?, ?>> parentSkillKey;

        public Builder(String key) {
            this.key = ResourceKey.create(PotionsPlusRegistries.CONFIGURED_PLAYER_ABILITY, ppId(key));
        }

        public Builder translationKey(String translationKey) {
            this.translationKey = translationKey;
            return this;
        }

        public Builder parentSkill(ResourceKey<ConfiguredSkill<?, ?>> parentSkillKey) {
            this.parentSkillKey = parentSkillKey;
            return this;
        }

        @Override
        public void generate(BootstrapContext<ConfiguredPlayerAbility<?, ?>> context) {
            if (parentSkillKey == null) {
                throw new IllegalStateException("Parent skill key must be set");
            }

            HolderGetter<ConfiguredSkill<?, ?>> skillLookup = context.lookup(PotionsPlusRegistries.CONFIGURED_SKILL);

            context.register(key,
                    new ConfiguredPlayerAbility<>(PlayerAbilities.SIMPLE.value(),
                            new PlayerAbilityConfiguration(
                                    new PlayerAbilityConfiguration.PlayerAbilityConfigurationData(translationKey, true, skillLookup.getOrThrow(parentSkillKey))
                            )
                    )
            );
        }

        @Override
        public ResourceKey<ConfiguredPlayerAbility<?, ?>> getKey() {
            return key;
        }
    }
}
