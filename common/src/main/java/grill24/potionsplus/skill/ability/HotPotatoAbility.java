package grill24.potionsplus.skill.ability;

import grill24.potionsplus.core.AbilityInstanceTypes;
import grill24.potionsplus.core.ConfiguredPlayerAbilities;
import grill24.potionsplus.core.PlayerAbilities;
import grill24.potionsplus.core.Translations;
import grill24.potionsplus.skill.ability.instance.AbilityInstanceSerializable;
import grill24.potionsplus.skill.ability.instance.AdjustableStrengthAbilityInstanceData;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class HotPotatoAbility extends CooldownTriggerableAbility<HotPotatoAbility.FireDamageData, CustomPacketPayload> {
    public record FireDamageData(DamageSource source, Runnable onAbsorb) {
    }
    public HotPotatoAbility() {
        super(Set.of(AbilityInstanceTypes.COOLDOWN.value()));
    }

    @Override
    protected int getCooldownDurationForAbility(AbilityInstanceSerializable<?, ?> instance) {
        float strength = instance.data() instanceof AdjustableStrengthAbilityInstanceData adjustable ? adjustable.getAbilityStrength() : 0F;
        return (int) Math.max(0, 6000 - (strength - 1) * 600);
    }

    @Override
    protected Component getCooldownOverComponent(AbilityInstanceSerializable<?, ?> instance) {
        return Component.translatable(Translations.COOLDOWN_POTIONSPLUS_ABILITY_HOT_POTATO).withStyle(ChatFormatting.GRAY);
    }

    @Override
    public Optional<List<List<Component>>> getLongDescription(AbilityInstanceSerializable<?, ?> instance, PlayerAbilityConfiguration config, Object... params) {
        if (instance.data() instanceof AdjustableStrengthAbilityInstanceData data) {
            String activationPercentage = String.valueOf(getDurationSeconds(data.getAbilityStrength()));
            return super.getLongDescription(instance, config, activationPercentage);
        }

        return super.getLongDescription(instance, config, params);
    }

    // ----- ITriggerablePlayerAbility -----

    // Add a static map to track last alert time per player
    private static final Map<Player, Long> lastFailureAlertTime = new ConcurrentHashMap<>();
    private static final long FAILURE_ALERT_COOLDOWN_MS = 10000; // 2 seconds

    @Override
    public Optional<CustomPacketPayload> onTriggeredFromServer(Player player, AbilityInstanceSerializable<?, ?> instance, FireDamageData event) {
        if (instance.data() instanceof AdjustableStrengthAbilityInstanceData adjustableStrengthAbilityInstanceData) {
            final float strength = adjustableStrengthAbilityInstanceData.getAbilityStrength();

            int poisonousPotatoSlot = player.getInventory().findSlotMatchingItem(new ItemStack(Items.POISONOUS_POTATO));
            if (poisonousPotatoSlot != -1) {
                // Success: consume poisonous potato and grant fire resistance
                event.onAbsorb().run();
                player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, getDurationTicks(strength), 0, false, true));
                player.getInventory().getItem(poisonousPotatoSlot).shrink(1);
                return Optional.empty(); // Success case
            } else {
                // Failure: no poisonous potato available, send chat alert with cooldown
                long now = System.currentTimeMillis();
                Long lastAlert = lastFailureAlertTime.get(player);
                if (lastAlert == null || now - lastAlert > FAILURE_ALERT_COOLDOWN_MS) {
                    if (player instanceof ServerPlayer serverPlayer) {
                        serverPlayer.sendSystemMessage(Component.translatable(Translations.CHAT_POTIONSPLUS_HOT_POTATO_NO_POTATO_WARNING).withStyle(ChatFormatting.RED));
                    }
                    lastFailureAlertTime.put(player, now);
                }
                // Don't prevent the damage - ability fails
                // Return null to indicate failure - this will prevent cooldown from being triggered
                return null;
            }
        }

        return Optional.empty();
    }

    @Override
    public Optional<CustomPacketPayload> onTriggeredFromClient(Player player, AbilityInstanceSerializable<?, ?> instance, FireDamageData event) {
        return Optional.empty();
    }

    public int getDurationTicks(float strength) {
        return (int) (strength * 100);
    }

    public int getDurationSeconds(float strength) {
        return getDurationTicks(strength) / 20;
    }
}
