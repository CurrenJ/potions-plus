package grill24.potionsplus.skill.ability;

import com.mojang.serialization.Codec;
import grill24.potionsplus.core.AbilityInstanceTypes;
import grill24.potionsplus.skill.ability.instance.AbilityInstanceSerializable;
import grill24.potionsplus.skill.ability.instance.AdjustableStrengthAbilityInstanceData;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class PermanentAttributeModifiersAbility<E, AC extends AttributeModifiersAbilityConfiguration> extends PlayerAbility<E, AC> implements IAdjustableStrengthAbility<AC>{
    public PermanentAttributeModifiersAbility(Codec<AC> configurationCodec) {
        super(configurationCodec);
    }

    @Override
    public void enable(ServerPlayer player, AC config, @Nullable E evaluationData) {
        enable(player, config, evaluationData, 1);
    }

    public void enable(ServerPlayer player, AC config, @Nullable E evaluationData, float strength) {
        for (AttributeModifier modifier : config.getModifiers()) {
            AttributeModifier modifierStrengthScaled = new AttributeModifier(modifier.id(), modifier.amount() * strength, modifier.operation());
            player.getAttribute(config.getAttributeHolder()).addOrUpdateTransientModifier(modifierStrengthScaled);
        }
    }

    public void disable(ServerPlayer player, AC config, @Nullable  E evaluationData) {
        for (AttributeModifier modifier : config.getModifiers()) {
            player.getAttribute(config.getAttributeHolder()).removeModifier(modifier);
        }
    }

    @Override
    public void  onAbilityGranted(ServerPlayer player, AC config) {
        enable(player, config, null);
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
            double amount = config.getModifiers().get(0).amount() * strength;
            AttributeModifier.Operation operation = config.getModifiers().get(0).operation();

            String param = "";
            // Format float to 2 decimal places
            String amountStr = String.format("%.2f", amount);
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
    public void onAbilityStrengthChanged(ServerPlayer player, AC config, float strength) {
        disable(player, config, null);
        enable(player, config, null, strength);
    }

    @Override
    public void onAbilityGranted(ServerPlayer player, AC config, float strength) {
        enable(player, config, null, strength);
    }

    @Override
    public AbilityInstanceSerializable<?, ?> createInstance(ServerPlayer player, Holder<ConfiguredPlayerAbility<?, ?>> ability) {
        return new AbilityInstanceSerializable<>(
                AbilityInstanceTypes.ADJUSTABLE_STRENGTH.value(),
                new AdjustableStrengthAbilityInstanceData(player, ability, true, 1F));
    }
}
