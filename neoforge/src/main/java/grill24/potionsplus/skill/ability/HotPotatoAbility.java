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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@EventBusSubscriber(modid = ModInfo.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class HotPotatoAbility extends CooldownTriggerableAbility<LivingDamageEvent.Pre, CustomPacketPayload> {
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

    @SubscribeEvent
    public static void onLivingDamage(final LivingDamageEvent.Pre event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            PlayerAbilities.HOT_POTATO.value().triggerFromServer(serverPlayer, ConfiguredPlayerAbilities.HOT_POTATO.getKey(), event);
        }
    }

    // ----- ITriggerablePlayerAbility -----

    @Override
    public Optional<CustomPacketPayload> onTriggeredFromServer(Player player, AbilityInstanceSerializable<?, ?> instance, LivingDamageEvent.Pre event) {
        if (instance.data() instanceof AdjustableStrengthAbilityInstanceData adjustableStrengthAbilityInstanceData) {
            final float strength = adjustableStrengthAbilityInstanceData.getAbilityStrength();

            int poisonousPotatoSlot = player.getInventory().findSlotMatchingItem(new ItemStack(Items.POISONOUS_POTATO));
            if (poisonousPotatoSlot != -1) {
                event.setNewDamage(0);
                player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, getDurationTicks(strength), 0, false, true));
                player.getInventory().getItem(poisonousPotatoSlot).shrink(1);
            }
        }

        return Optional.empty();
    }

    @Override
    public Optional<CustomPacketPayload> onTriggeredFromClient(Player player, AbilityInstanceSerializable<?, ?> instance, LivingDamageEvent.Pre event) {
        return Optional.empty();
    }

    public int getDurationTicks(float strength) {
        return (int) (strength * 100);
    }

    public int getDurationSeconds(float strength) {
        return getDurationTicks(strength) / 20;
    }
}
