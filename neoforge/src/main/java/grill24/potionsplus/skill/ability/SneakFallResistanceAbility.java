package grill24.potionsplus.skill.ability;

import grill24.potionsplus.core.AbilityInstanceTypes;
import grill24.potionsplus.core.ConfiguredPlayerAbilities;
import grill24.potionsplus.core.PlayerAbilities;
import grill24.potionsplus.core.Translations;
import grill24.potionsplus.skill.SkillsData;
import grill24.potionsplus.skill.ability.instance.AbilityInstanceSerializable;
import grill24.potionsplus.skill.ability.instance.AdjustableStrengthAbilityInstanceData;
import grill24.potionsplus.skill.ability.ConfiguredPlayerAbility;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

@EventBusSubscriber(modid = ModInfo.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class SneakFallResistanceAbility extends SimplePlayerAbility implements ITriggerablePlayerAbility<LivingDamageEvent.Pre, CustomPacketPayload> {
    public SneakFallResistanceAbility() {
        super(Set.of(AbilityInstanceTypes.ADJUSTABLE_STRENGTH.value()));
    }

    @Override
    public Optional<List<List<Component>>> getLongDescription(AbilityInstanceSerializable<?, ?> instance, PlayerAbilityConfiguration config, Object... params) {
        if (instance.data() instanceof AdjustableStrengthAbilityInstanceData data) {
            String reductionPercentage = String.valueOf((int)(getFallDamageReduction(data.getAbilityStrength()) * 100));
            return super.getLongDescription(instance, config, reductionPercentage);
        }

        return super.getLongDescription(instance, config, params);
    }

    @SubscribeEvent
    public static void onLivingDamage(final LivingDamageEvent.Pre event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            // Only trigger for fall damage while sneaking
            if (serverPlayer.isShiftKeyDown() && event.getSource().is(DamageTypes.FALL)) {
                PlayerAbilities.SNEAK_FALL_RESISTANCE.value().doForAbility(serverPlayer, ConfiguredPlayerAbilities.SNEAK_FALL_RESISTANCE.getKey(), instance -> {
                    if (instance.data() instanceof AdjustableStrengthAbilityInstanceData adjustableStrengthAbilityInstanceData) {
                        final float strength = adjustableStrengthAbilityInstanceData.getAbilityStrength();

                        // Reduce fall damage while sneaking
                        float reduction = getFallDamageReduction(strength);
                        float newDamage = event.getNewDamage() * (1.0f - reduction);
                        event.setNewDamage(Math.max(0, newDamage));
                    }
                }, event);
            }
        }
    }

    public boolean doForAbility(Player player, ResourceKey<ConfiguredPlayerAbility<?, ?>> configuredAbilityResourceKey, Consumer<AbilityInstanceSerializable<?, ?>> consumer, LivingDamageEvent.Pre eventData) {
        SkillsData skillsData = SkillsData.getPlayerData(player);

        Optional<AbilityInstanceSerializable<?, ?>> instanceOpt = skillsData.getAbilityInstance(player.registryAccess(), configuredAbilityResourceKey);
        if (instanceOpt.isPresent()) {
            AbilityInstanceSerializable<?, ?> instance = instanceOpt.get();
            if (instance.data().isEnabled()) {
                consumer.accept(instance);
                return true;
            }
        }

        return false;
    }

    // ----- ITriggerablePlayerAbility -----

    @Override
    public Optional<CustomPacketPayload> onTriggeredFromServer(Player player, AbilityInstanceSerializable<?, ?> instance, LivingDamageEvent.Pre event) {
        if (instance.data() instanceof AdjustableStrengthAbilityInstanceData adjustableStrengthAbilityInstanceData) {
            final float strength = adjustableStrengthAbilityInstanceData.getAbilityStrength();

            // Reduce fall damage while sneaking
            float reduction = getFallDamageReduction(strength);
            float newDamage = event.getNewDamage() * (1.0f - reduction);
            event.setNewDamage(Math.max(0, newDamage));
        }

        return Optional.empty();
    }

    @Override
    public Optional<CustomPacketPayload> onTriggeredFromClient(Player player, AbilityInstanceSerializable<?, ?> instance, LivingDamageEvent.Pre event) {
        return Optional.empty();
    }

    private static float getFallDamageReduction(float strength) {
        // Base 25% reduction, up to 75% at max strength
        return Math.min(0.75f, 0.25f + (strength - 1) * 0.1f);
    }
}