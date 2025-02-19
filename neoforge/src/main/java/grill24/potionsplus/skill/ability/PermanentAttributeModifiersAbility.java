package grill24.potionsplus.skill.ability;

import com.mojang.serialization.Codec;
import grill24.potionsplus.core.AbilityInstanceTypes;
import grill24.potionsplus.core.ConfiguredPlayerAbilities;
import grill24.potionsplus.core.PotionsPlusRegistries;
import grill24.potionsplus.skill.ConfiguredSkill;
import grill24.potionsplus.skill.ability.instance.AbilityInstanceSerializable;
import grill24.potionsplus.skill.ability.instance.AdjustableStrengthAbilityInstanceData;
import grill24.potionsplus.utility.Utility;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.List;
import java.util.Set;

import static grill24.potionsplus.utility.Utility.ppId;

public class PermanentAttributeModifiersAbility<AC extends AttributeModifiersAbilityConfiguration> extends PlayerAbility<AC> implements IAdjustableStrengthAbility<AC> {
    public PermanentAttributeModifiersAbility(Codec<AC> configurationCodec) {
        super(configurationCodec, Set.of(AbilityInstanceTypes.ADJUSTABLE_STRENGTH.value()));
    }

    @Override
    protected void onEnable(ServerPlayer player, AC config, AbilityInstanceSerializable<?, ?> abilityInstance) {
        if (abilityInstance.data() instanceof AdjustableStrengthAbilityInstanceData adjustableStrengthData) {
            enable(player, config, adjustableStrengthData.getAbilityStrength());
        } else {
            enable(player, config, 1F);
        }
    }

    private void enable(ServerPlayer player, AC config, float strength) {
        for (AttributeModifier modifier : config.getModifiers()) {
            AttributeModifier modifierWithStrength = new AttributeModifier(modifier.id(), strength, modifier.operation());
            player.getAttribute(config.getAttributeHolder()).addOrUpdateTransientModifier(modifierWithStrength);
        }
    }

    @Override
    protected void onDisable(ServerPlayer player, AC config) {
        for (AttributeModifier modifier : config.getModifiers()) {
            player.getAttribute(config.getAttributeHolder()).removeModifier(modifier);
        }
    }

    @Override
    public void onInstanceChanged(ServerPlayer player, AC config, AbilityInstanceSerializable<?, ?> abilityInstance) {
        abilityInstance.tryEnable(player);
    }

    @Override
    public void onAbilityGranted(ServerPlayer player, AC config, AbilityInstanceSerializable<?, ?> abilityInstance) {
        if (abilityInstance.data().getConfiguredAbility().config().getData().enabledByDefault()) {
            abilityInstance.tryEnable(player);
        }
    }

    @Override
    public void onAbilityRevoked(ServerPlayer player, AC config) {
        disable(player, config, null);
    }

    @Override
    public Component getDescription(AC config) {
        return getDescriptionWithStrength(config, 1);
    }

    private Component getDescriptionWithStrength(AttributeModifiersAbilityConfiguration config, float strength) {
        if (config.getModifiers().size() == 1) {
            AttributeModifier.Operation operation = config.getModifiers().get(0).operation();

            String param = "";
            // Format float to 2 decimal places
            String amountStr = String.format("%.2f", strength);
            switch (operation) {
                case ADD_VALUE -> param = "+" + amountStr;
                case ADD_MULTIPLIED_BASE, ADD_MULTIPLIED_TOTAL -> param = "x" + (amountStr + 1);
            }

            return Component.translatable(config.getData().translationKey(), param);
        } else {
            return Component.translatable(config.getData().translationKey());
        }
    }

    @Override
    public Component getDescription(AC config, float strength) {
        return getDescriptionWithStrength(config, strength);
    }

    @Override
    public AbilityInstanceSerializable<?, ?> createInstance(ServerPlayer player, Holder<ConfiguredPlayerAbility<?, ?>> ability) {
        return new AbilityInstanceSerializable<>(
                AbilityInstanceTypes.ADJUSTABLE_STRENGTH.value(),
                new AdjustableStrengthAbilityInstanceData(ability, true));
    }

    public static class Builder<A extends PermanentAttributeModifiersAbility<AttributeModifiersAbilityConfiguration>> implements ConfiguredPlayerAbilities.IAbilityBuilder {
        ResourceKey<ConfiguredPlayerAbility<?, ?>> key;
        String translationKey;

        ResourceKey<ConfiguredSkill<?, ?>> parentSkillKey;
        A ability;
        Holder<Attribute> attribute;
        AttributeModifier.Operation operation;
        boolean enabledByDefault;

        public Builder(String name) {
            this.key = ResourceKey.create(PotionsPlusRegistries.CONFIGURED_PLAYER_ABILITY, ppId(name));
        }

        public Builder<A> translationKey(String translationKey) {
            this.translationKey = translationKey;
            return this;
        }

        public Builder<A> parentSkill(ResourceKey<ConfiguredSkill<?, ?>> parentSkillKey) {
            this.parentSkillKey = parentSkillKey;
            return this;
        }

        public Builder<A> ability(A ability) {
            this.ability = ability;
            return this;
        }

        public Builder<A> attribute(Holder<Attribute> attribute) {
            this.attribute = attribute;
            return this;
        }

        public Builder<A> operation(AttributeModifier.Operation operation) {
            this.operation = operation;
            return this;
        }

        public Builder<A> enabledByDefault(boolean enabledByDefault) {
            this.enabledByDefault = enabledByDefault;
            return this;
        }

        @Override
        public void generate(BootstrapContext<ConfiguredPlayerAbility<?, ?>> context) {
            if (ability == null) {
                throw new IllegalStateException("Ability must be set");
            }

            if (attribute == null) {
                throw new IllegalStateException("Attribute must be set");
            }

            if (operation == null) {
                throw new IllegalStateException("Operation must be set");
            }

            if (parentSkillKey == null) {
                throw new IllegalStateException("Parent skill must be set");
            }

            HolderGetter<ConfiguredSkill<?, ?>> skillLookup = context.lookup(PotionsPlusRegistries.CONFIGURED_SKILL);

            context.register(key,
                    new ConfiguredPlayerAbility<>(ability,
                            new AttributeModifiersAbilityConfiguration(
                                    new PlayerAbilityConfiguration.PlayerAbilityConfigurationData(translationKey, enabledByDefault, skillLookup.getOrThrow(parentSkillKey)),
                                    attribute,
                                    List.of(new AttributeModifier(Utility.modifierId(key), 0, operation))
                            )));
        }

        @Override
        public ResourceKey<ConfiguredPlayerAbility<?, ?>> getKey() {
            return key;
        }
    }
}
