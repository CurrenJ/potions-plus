package grill24.potionsplus.skill.ability;

import com.mojang.serialization.Codec;
import grill24.potionsplus.core.AbilityInstanceTypes;
import grill24.potionsplus.core.PotionsPlus;
import grill24.potionsplus.skill.ability.instance.AbilityInstanceSerializable;
import grill24.potionsplus.skill.ability.instance.AdjustableStrengthAbilityInstanceData;
import grill24.potionsplus.utility.Utility;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.List;
import java.util.Set;

public class PermanentAttributeModifiersAbility<AC extends AttributeModifiersAbilityConfiguration> extends PlayerAbility<AC> {
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

            AttributeInstance attributeInstance = player.getAttribute(config.getAttributeHolder());
            if (attributeInstance == null) {
                PotionsPlus.LOGGER.error("Expected attribute instance but was null: " + config.getAttributeHolder());
            } else {
                attributeInstance.addOrUpdateTransientModifier(modifierWithStrength);
            }
        }
    }

    @Override
    protected void onDisable(ServerPlayer player, AC config) {
        for (AttributeModifier modifier : config.getModifiers()) {
            AttributeInstance attributeInstance = player.getAttribute(config.getAttributeHolder());
            if (attributeInstance == null) {
                PotionsPlus.LOGGER.error("Expected attribute instance but was null: " + config.getAttributeHolder());
            } else {
                attributeInstance.removeModifier(modifier);
            }
        }
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
    public Component getDescription(AC config, Object... params) {
        if (params.length == 1 && params[0] instanceof Float strength) {
            return Component.translatable(config.getData().translationKey(), params);
        } else {
            return super.getDescription(config, params);
        }
    }

    @Override
    public AbilityInstanceSerializable<?, ?> createInstance(ServerPlayer player, Holder<ConfiguredPlayerAbility<?, ?>> ability) {
        return new AbilityInstanceSerializable<>(
                AbilityInstanceTypes.ADJUSTABLE_STRENGTH.value(),
                new AdjustableStrengthAbilityInstanceData(ability, true));
    }

    public static class Builder<A extends PermanentAttributeModifiersAbility<AttributeModifiersAbilityConfiguration>>
            extends SimplePlayerAbility.AbstractBuilder<AttributeModifiersAbilityConfiguration, A, Builder<A>> {
        Holder<Attribute> attribute;
        AttributeModifier.Operation operation;

        public Builder(String key) {
            super(key);
        }

        public Builder<A> attribute(Holder<Attribute> attribute) {
            this.attribute = attribute;
            return this;
        }

        public Builder<A> operation(AttributeModifier.Operation operation) {
            this.operation = operation;
            return this;
        }

        @Override
        public boolean validate(BootstrapContext<ConfiguredPlayerAbility<?, ?>> context) {
            if (attribute == null) {
                throw new IllegalStateException("Attribute must be set. |" + key);
            }

            if (operation == null) {
                throw new IllegalStateException("Operation must be set. |" + key);
            }

            return true;
        }

        @Override
        protected AttributeModifiersAbilityConfiguration buildConfig(BootstrapContext<ConfiguredPlayerAbility<?, ?>> context) {
            return new AttributeModifiersAbilityConfiguration(
                    buildBaseConfigurationData(context),
                    attribute,
                    List.of(new AttributeModifier(Utility.modifierId(key), 0, operation))
            );
        }

        @Override
        public Builder<A> self() {
            return this;
        }
    }
}
