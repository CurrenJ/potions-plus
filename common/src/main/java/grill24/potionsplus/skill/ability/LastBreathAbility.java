package grill24.potionsplus.skill.ability;

import grill24.potionsplus.core.AbilityInstanceTypes;
import grill24.potionsplus.core.Translations;
import grill24.potionsplus.network.ClientboundTriggerChainLightningPacket;
import grill24.potionsplus.skill.ability.instance.AbilityInstanceSerializable;
import grill24.potionsplus.skill.ability.instance.AdjustableStrengthAbilityInstanceData;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class LastBreathAbility extends CooldownTriggerableAbility<LastBreathAbility.DrownData, ClientboundTriggerChainLightningPacket> {
    public record DrownData() {
    }
    public LastBreathAbility() {
        super(Set.of(AbilityInstanceTypes.COOLDOWN.value()));
    }

    @Override
    protected int getCooldownDurationForAbility(AbilityInstanceSerializable<?, ?> instance) {
        float strength = instance.data() instanceof AdjustableStrengthAbilityInstanceData adjustable ? adjustable.getAbilityStrength() : 0F;
        return (int) Math.max(0, 3600 - (strength - 1) * 600);
    }

    @Override
    protected Component getCooldownOverComponent(AbilityInstanceSerializable<?, ?> instance) {
        return Component.translatable(Translations.COOLDOWN_POTIONSPLUS_ABILITY_LAST_BREATH).withStyle(ChatFormatting.GRAY);
    }

    @Override
    public Optional<List<List<Component>>> getLongDescription(AbilityInstanceSerializable<?, ?> instance, PlayerAbilityConfiguration config, Object... params) {
        if (instance.data() instanceof AdjustableStrengthAbilityInstanceData data) {
            String durationText = String.valueOf(getDurationSeconds(data.getAbilityStrength()));
            return super.getLongDescription(instance, config, durationText);
        }

        return super.getLongDescription(instance, config, params);
    }

    // ----- ITriggerablePlayerAbility -----

    @Override
    public Optional<ClientboundTriggerChainLightningPacket> onTriggeredFromServer(Player player, AbilityInstanceSerializable<?, ?> instance, DrownData event) {
        if (instance.data() instanceof AdjustableStrengthAbilityInstanceData adjustableStrengthAbilityInstanceData) {
            final float strength = adjustableStrengthAbilityInstanceData.getAbilityStrength();

            player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, getDurationTicks(strength), 0, false, true));
        }

        return Optional.empty();
    }

    @Override
    public Optional<ClientboundTriggerChainLightningPacket> onTriggeredFromClient(Player player, AbilityInstanceSerializable<?, ?> instance, DrownData event) {
        return Optional.empty();
    }

    public int getDurationTicks(float strength) {
        return (int) (strength * 100);
    }

    public int getDurationSeconds(float strength) {
        return getDurationTicks(strength) / 20;
    }
}
