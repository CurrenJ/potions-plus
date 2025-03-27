package grill24.potionsplus.skill.ability;

import grill24.potionsplus.core.Translations;
import grill24.potionsplus.skill.ability.instance.AbilityInstanceSerializable;
import grill24.potionsplus.skill.ability.instance.AdjustableStrengthAbilityInstanceData;
import grill24.potionsplus.utility.Utility;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SavedByTheBounceAbility extends CooldownTriggerableAbility<LivingFallEvent, CustomPacketPayload> {
    @Override
    public int getCooldownDurationForAbility(AbilityInstanceSerializable<?, ?> instance) {
        float strength = 0F;
        if (instance.data() instanceof AdjustableStrengthAbilityInstanceData adjustableStrengthAbilityInstanceData) {
            strength = adjustableStrengthAbilityInstanceData.getAbilityStrength();
        }
        return Math.max(600, 3600 - (int) ((strength-1) * 300));
    }

    @Override
    protected Component getCooldownOverComponent(AbilityInstanceSerializable<?, ?> instance) {
        return Component.translatable(Translations.COOLDOWN_POTIONSPLUS_ABILITY_SAVED_BY_THE_BOUNCE).withStyle(ChatFormatting.GRAY);
    }

    @Override
    public Optional<CustomPacketPayload> onTriggeredFromServer(Player player, AbilityInstanceSerializable<?, ?> instance, LivingFallEvent eventData) {
        return Optional.empty();
    }

    @Override
    public Optional<CustomPacketPayload> onTriggeredFromClient(Player player, AbilityInstanceSerializable<?, ?> instance, LivingFallEvent eventData) {
        return Optional.empty();
    }
}
