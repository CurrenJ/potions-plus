package grill24.potionsplus.skill.ability;

import grill24.potionsplus.core.AbilityInstanceTypes;
import grill24.potionsplus.core.ConfiguredPlayerAbilities;
import grill24.potionsplus.core.PlayerAbilities;
import grill24.potionsplus.core.Translations;
import grill24.potionsplus.skill.ability.instance.AbilityInstanceSerializable;
import grill24.potionsplus.skill.ability.instance.AdjustableStrengthAbilityInstanceData;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@EventBusSubscriber(modid = ModInfo.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class SneakInvisibilityAbility extends CooldownTriggerableAbility<LivingDamageEvent.Pre, CustomPacketPayload> {
    public SneakInvisibilityAbility() {
        super(Set.of(AbilityInstanceTypes.COOLDOWN.value()));
    }

    @Override
    protected int getCooldownDurationForAbility(AbilityInstanceSerializable<?, ?> instance) {
        float strength = instance.data() instanceof AdjustableStrengthAbilityInstanceData adjustable ? adjustable.getAbilityStrength() : 1F;
        // Base cooldown of 30 seconds (600 ticks), reduced by strength
        return (int) Math.max(300, 600 - (strength - 1) * 100);
    }

    @Override
    protected Component getCooldownOverComponent(AbilityInstanceSerializable<?, ?> instance) {
        return Component.translatable(Translations.COOLDOWN_POTIONSPLUS_ABILITY_SNEAK_INVISIBILITY).withStyle(ChatFormatting.GRAY);
    }

    @Override
    public Optional<List<List<Component>>> getLongDescription(AbilityInstanceSerializable<?, ?> instance, PlayerAbilityConfiguration config, Object... params) {
        if (instance.data() instanceof AdjustableStrengthAbilityInstanceData data) {
            String durationSeconds = String.valueOf(getDurationSeconds(data.getAbilityStrength()));
            String cooldownSeconds = String.valueOf(getCooldownDurationForAbility(instance) / 20);
            return super.getLongDescription(instance, config, durationSeconds, cooldownSeconds);
        }

        return super.getLongDescription(instance, config, params);
    }

    @SubscribeEvent
    public static void onLivingDamage(final LivingDamageEvent.Pre event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            // Only trigger if player is sneaking
            if (serverPlayer.isShiftKeyDown()) {
                PlayerAbilities.SNEAK_INVISIBILITY.value().triggerFromServer(serverPlayer, ConfiguredPlayerAbilities.SNEAK_INVISIBILITY.getKey(), event);
            }
        }
    }

    // ----- ITriggerablePlayerAbility -----

    @Override
    public Optional<CustomPacketPayload> onTriggeredFromServer(Player player, AbilityInstanceSerializable<?, ?> instance, LivingDamageEvent.Pre event) {
        if (instance.data() instanceof AdjustableStrengthAbilityInstanceData adjustableStrengthAbilityInstanceData) {
            final float strength = adjustableStrengthAbilityInstanceData.getAbilityStrength();

            // Apply invisibility effect
            int duration = getDurationTicks(strength);
            player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, duration, 0, false, true));
        }

        return Optional.empty();
    }

    @Override
    public Optional<CustomPacketPayload> onTriggeredFromClient(Player player, AbilityInstanceSerializable<?, ?> instance, LivingDamageEvent.Pre event) {
        return Optional.empty();
    }

    public int getDurationTicks(float strength) {
        // Base duration of 5 seconds (100 ticks), increased by strength
        return (int) (100 + strength * 40);
    }

    public int getDurationSeconds(float strength) {
        return getDurationTicks(strength) / 20;
    }
}