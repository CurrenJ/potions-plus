package grill24.potionsplus.skill.ability;

import grill24.potionsplus.core.AbilityInstanceTypes;
import grill24.potionsplus.core.ConfiguredPlayerAbilities;
import grill24.potionsplus.core.Particles;
import grill24.potionsplus.core.PlayerAbilities;
import grill24.potionsplus.network.ClientboundTriggerChainLightningPacket;
import grill24.potionsplus.skill.ability.instance.AbilityInstanceSerializable;
import grill24.potionsplus.skill.ability.instance.AdjustableStrengthAbilityInstanceData;
import grill24.potionsplus.skill.ability.instance.CooldownAbilityInstanceData;
import grill24.potionsplus.utility.ModInfo;
import grill24.potionsplus.utility.Utility;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.CriticalHitEvent;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@EventBusSubscriber(modid = ModInfo.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class ChainLightningAbility extends CooldownTriggerableAbility<CriticalHitEvent, ClientboundTriggerChainLightningPacket> {
    public ChainLightningAbility() {
        super(Set.of(AbilityInstanceTypes.COOLDOWN.value()));
    }

    @Override
    public AbilityInstanceSerializable<?, ?> createInstance(ServerPlayer player, Holder<ConfiguredPlayerAbility<?, ?>> ability) {
        return new AbilityInstanceSerializable<>(
                AbilityInstanceTypes.COOLDOWN.value(),
                new CooldownAbilityInstanceData(ability, true, 0, 2, 0, 0));
    }

    @Override
    protected int getCooldownDurationForAbility(AbilityInstanceSerializable<?, ?> instance) {
        return 0;
    }

    @Override
    protected Component getCooldownOverComponent(AbilityInstanceSerializable<?, ?> instance) {
        return Component.empty();
    }

    @Override
    public Optional<List<List<Component>>> getLongDescription(AbilityInstanceSerializable<?, ?> instance, PlayerAbilityConfiguration config, Object... params) {
        if (instance.data() instanceof AdjustableStrengthAbilityInstanceData data) {
            float activationChance = getActivationChance(data.getAbilityStrength());
            String activationPercentage = String.format("%d%%", (int) (activationChance * 100));
            return super.getLongDescription(instance, config, activationPercentage, getEntityLimit(data.getAbilityStrength()));
        }

        return super.getLongDescription(instance, config, params);
    }

    @SubscribeEvent
    public static void onCriticalHit(final CriticalHitEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            PlayerAbilities.CHAIN_LIGHTNING.value().triggerFromServer(serverPlayer, ConfiguredPlayerAbilities.CHAIN_LIGHTNING.getKey(), event);
        }
    }

    // ----- ITriggerablePlayerAbility -----


    @Override
    public Optional<ClientboundTriggerChainLightningPacket> onTriggeredFromServer(Player player, AbilityInstanceSerializable<?, ?> instance, CriticalHitEvent event) {
        if (instance.data() instanceof AdjustableStrengthAbilityInstanceData adjustableStrengthAbilityInstanceData) {
            final float strength = adjustableStrengthAbilityInstanceData.getAbilityStrength();
            float chanceToActivate = getActivationChance(strength);
            if (player.getRandom().nextFloat() < chanceToActivate) {
                List<Entity> affected = doChainLightning(event.getTarget(), strength);

                return Optional.of(new ClientboundTriggerChainLightningPacket(
                        player.blockPosition(),
                        affected.stream().map(Entity::getId).toList()
                ));
            }
        }

        return Optional.empty();
    }

    @Override
    public Optional<ClientboundTriggerChainLightningPacket> onTriggeredFromClient(Player player, AbilityInstanceSerializable<?, ?> instance, CriticalHitEvent event) {
        return Optional.empty();
    }

    // ----- Chain Lightning Behaviour ----

    /**
     * Perform the chain lightning ability.
     *
     * @param target   The entity that was hit by the critical hit
     * @param strength The strength of the ability. Strength = max number of mobs to affect, damage dealt to each mob,
     *                 radius of the ability, and chance to activate.
     */
    private static List<Entity> doChainLightning(Entity target, float strength) {
        final int radius = (int) (strength * 2F);
        List<Entity> entities = Utility.getEntitiesToChainOffensiveAbilityTo(target, (int) strength, radius);

        for (Entity entity : entities) {
            entity.hurt(entity.damageSources().lightningBolt(), (int) strength * 1F);
        }

        return entities;
    }

    /**
     * Spawn a line of particles between two entities.
     *
     * @param start                   The entity to start the line from
     * @param end                     The entity to end the line at
     * @param beamRadius              The radius of the beam
     * @param particleCountMultiplier The multiplier for the number of particles to spawn
     */
    public static void spawnLineOfParticlesBetweenEntities(Entity start, Entity end, double beamRadius, double particleCountMultiplier) {
        if (!start.isAddedToLevel()) {
            return;
        }
        Level level = start.level();

        double x1 = start.getX();
        double y1 = start.getY() + start.getBbHeight() / 2;
        double z1 = start.getZ();
        double x2 = end.getX();
        double y2 = end.getY() + end.getBbHeight() / 2;
        double z2 = end.getZ();
        double dx = x2 - x1;
        double dy = y2 - y1;
        double dz = z2 - z1;
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        final double distancePerParticle = 0.1;
        double particles = Math.min(16, distance) / distancePerParticle;
        dx /= distance;
        dy /= distance;
        dz /= distance;
        for (int i = 0; i < particles; i++) {
            for (int j = 0; j < level.getRandom().nextInt((int) (8 * particleCountMultiplier)); j++) {
                double randOffsetX = level.getRandom().nextDouble() * beamRadius;
                double randOffsetY = level.getRandom().nextDouble() * beamRadius;
                double randOffsetZ = level.getRandom().nextDouble() * beamRadius;
                level.addParticle(getRandomParticleType(level.getRandom()),
                        x1 + dx * i * distancePerParticle + randOffsetX,
                        y1 + dy * i * distancePerParticle + randOffsetY,
                        z1 + dz * i * distancePerParticle + randOffsetZ,
                        0, 0, 0);
            }
        }
    }

    /**
     * Get a random particle type for the chain lightning ability.
     *
     * @param randomSource The random source to use
     * @return A random particle type of either {@link Particles#LIGHTNING_BOLT} or {@link Particles#LIGHTNING_BOLT_SMALL}
     */
    private static ParticleOptions getRandomParticleType(RandomSource randomSource) {
        return randomSource.nextInt(6) == 0 ? Particles.LIGHTNING_BOLT.value() : Particles.LIGHTNING_BOLT_SMALL.value();
    }

    private static float getActivationChance(float strength) {
        return strength * 0.15F;
    }

    private static int getEntityLimit(float strength) {
        return (int) strength;
    }
}
