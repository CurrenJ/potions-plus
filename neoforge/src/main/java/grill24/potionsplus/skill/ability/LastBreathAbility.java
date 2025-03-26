package grill24.potionsplus.skill.ability;

import grill24.potionsplus.core.AbilityInstanceTypes;
import grill24.potionsplus.core.ConfiguredPlayerAbilities;
import grill24.potionsplus.core.PlayerAbilities;
import grill24.potionsplus.core.Translations;
import grill24.potionsplus.network.ClientboundTriggerChainLightningPacket;
import grill24.potionsplus.skill.ability.instance.AbilityInstanceSerializable;
import grill24.potionsplus.skill.ability.instance.AdjustableStrengthAbilityInstanceData;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDrownEvent;

import java.util.Optional;
import java.util.Set;

@EventBusSubscriber(modid = ModInfo.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class LastBreathAbility extends CooldownTriggerableAbility<LivingDrownEvent, ClientboundTriggerChainLightningPacket> {
    public LastBreathAbility() {
        super(Set.of(AbilityInstanceTypes.COOLDOWN.value()));
    }

    @Override
    protected int getCooldownDurationForAbility(AbilityInstanceSerializable<?, ?> instance) {
        float strength = instance.data() instanceof AdjustableStrengthAbilityInstanceData adjustable ? adjustable.getAbilityStrength() : 0F;
        return (int) Math.max(0, 3600 - strength * 600);
    }

    @Override
    protected Component getCooldownOverComponent(AbilityInstanceSerializable<?, ?> instance) {
        return Component.translatable(Translations.COOLDOWN_POTIONSPLUS_ABILITY_LAST_BREATH).withStyle(ChatFormatting.GRAY);
    }

    @SubscribeEvent
    public static void onLivingDrown(final LivingDrownEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            PlayerAbilities.LAST_BREATH.value().triggerFromServer(serverPlayer, ConfiguredPlayerAbilities.LAST_BREATH.getKey(), event);
        }
    }

    // ----- ITriggerablePlayerAbility -----

    @Override
    public Optional<ClientboundTriggerChainLightningPacket> onTriggeredFromServer(Player player, AbilityInstanceSerializable<?, ?> instance, LivingDrownEvent event) {
        if (instance.data() instanceof AdjustableStrengthAbilityInstanceData adjustableStrengthAbilityInstanceData) {
            final float strength = adjustableStrengthAbilityInstanceData.getAbilityStrength();

            player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, (int) (strength * 100), 0, false, true));
        }

        return Optional.empty();
    }

    @Override
    public Optional<ClientboundTriggerChainLightningPacket> onTriggeredFromClient(Player player, AbilityInstanceSerializable<?, ?> instance, LivingDrownEvent event) {
        return Optional.empty();
    }
}
