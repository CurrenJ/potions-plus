package grill24.potionsplus.skill.ability;

import grill24.potionsplus.core.AbilityInstanceTypes;
import grill24.potionsplus.core.ConfiguredPlayerAbilities;
import grill24.potionsplus.core.PlayerAbilities;
import grill24.potionsplus.core.PotionsPlusRegistries;
import grill24.potionsplus.network.ClientboundTriggerStunShotPacket;
import grill24.potionsplus.skill.ConfiguredSkill;
import grill24.potionsplus.skill.ability.instance.AbilityInstanceSerializable;
import grill24.potionsplus.skill.ability.instance.AdjustableStrengthAbilityInstanceData;
import grill24.potionsplus.skill.ability.instance.CooldownAbilityInstanceData;
import grill24.potionsplus.utility.ModInfo;
import grill24.potionsplus.utility.Utility;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.CriticalHitEvent;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@EventBusSubscriber(modid = ModInfo.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class StunShotAbility extends CooldownTriggerableAbility<CriticalHitEvent, CustomPacketPayload> {
    @Override
    public AbilityInstanceSerializable<?, ?> createInstance(ServerPlayer player, Holder<ConfiguredPlayerAbility<?, ?>> ability) {
        return new AbilityInstanceSerializable<>(
                AbilityInstanceTypes.COOLDOWN.value(),
                new CooldownAbilityInstanceData(ability, true, 0, 0, 0));
    }

    @Override
    protected int getCooldownDurationForAbility(AbilityInstanceSerializable<?, ?> instance) {
        return 0;
    }

    @Override
    protected Component getCooldownOverComponent(AbilityInstanceSerializable<?, ?> instance) {
        return Component.empty();
    }

    @SubscribeEvent
    public static void onCriticalHit(final CriticalHitEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            PlayerAbilities.STUN_SHOT.value().triggerFromServer(serverPlayer, ConfiguredPlayerAbilities.STUN_SHOT.getKey(), event);
        }
    }

    // ----- ITriggerablePlayerAbility -----

    @Override
    public Optional<CustomPacketPayload> onTriggeredFromServer(Player player, AbilityInstanceSerializable<?, ?> instance, CriticalHitEvent eventData) {
        if (instance.data() instanceof AdjustableStrengthAbilityInstanceData adjustableStrengthAbilityInstanceData) {
            final float strength = adjustableStrengthAbilityInstanceData.getAbilityStrength();
            float chanceToActivate = 0.15F + strength * 0.15F;
            if (player.getRandom().nextFloat() < chanceToActivate) {
                final int duration = getDuration(strength);
                List<Entity> entities = doStunShot(eventData.getTarget(), strength);
                return Optional.of(new ClientboundTriggerStunShotPacket(Math.max(10, duration - 10), eventData.getTarget().blockPosition(), entities.stream().map(Entity::getId).toList()));
            }
        }

        return Optional.empty();
    }

    @Override
    public Optional<CustomPacketPayload> onTriggeredFromClient(Player player, AbilityInstanceSerializable<?, ?> instance, CriticalHitEvent eventData) {
        return Optional.empty();
    }

    // ----- Stun Shot Behaviour -----

    private static List<Entity> doStunShot(Entity target, float strength) {
        final float radius = 1.5F * strength;
        final int duration = getDuration(strength);

        List<Entity> nearbyEntities = Utility.getEntitiesToChainOffensiveAbilityTo(target, 16, radius);
        nearbyEntities.addFirst(target);

        for (Entity entity : nearbyEntities) {
            if(entity instanceof LivingEntity livingEntity) {
                livingEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, duration, 4));
            }
        }

        return nearbyEntities;
    }

    private static int getDuration(float strength) {
        return (int) (30 * strength);
    }

    public static class Builder extends SimplePlayerAbility.Builder {
        public Builder(String key) {
            super(key);
        }


        @Override
        public void generate(BootstrapContext<ConfiguredPlayerAbility<?, ?>> context) {
            if (parentSkillKey == null) {
                throw new IllegalStateException("Parent skill key must be set");
            }

            HolderGetter<ConfiguredSkill<?, ?>> skillLookup = context.lookup(PotionsPlusRegistries.CONFIGURED_SKILL);

            context.register(key,
                    new ConfiguredPlayerAbility<>(PlayerAbilities.STUN_SHOT.value(),
                            new PlayerAbilityConfiguration(
                                    new PlayerAbilityConfiguration.PlayerAbilityConfigurationData(translationKey, true, skillLookup.getOrThrow(parentSkillKey))
                            )
                    )
            );
        }
    }
}
