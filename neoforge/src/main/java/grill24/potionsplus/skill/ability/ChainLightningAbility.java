package grill24.potionsplus.skill.ability;

import grill24.potionsplus.core.*;
import grill24.potionsplus.network.ClientboundTriggerChainLightningPacket;
import grill24.potionsplus.skill.ConfiguredSkill;
import grill24.potionsplus.skill.ability.instance.AbilityInstanceSerializable;
import grill24.potionsplus.skill.ability.instance.AdjustableStrengthAbilityInstanceData;
import grill24.potionsplus.skill.ability.instance.ChainLightningAbilityInstanceData;
import grill24.potionsplus.utility.ModInfo;
import grill24.potionsplus.utility.Utility;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.CriticalHitEvent;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@EventBusSubscriber(modid = ModInfo.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class ChainLightningAbility extends SimplePlayerAbility implements ITriggerablePlayerAbility<CriticalHitEvent, ClientboundTriggerChainLightningPacket> {
    public ChainLightningAbility() {
        super(Set.of(AbilityInstanceTypes.CHAIN_LIGHTNING.value()));
    }

    @Override
    public AbilityInstanceSerializable<?, ?> createInstance(ServerPlayer player, Holder<ConfiguredPlayerAbility<?, ?>> ability) {
        return new AbilityInstanceSerializable<>(
                AbilityInstanceTypes.CHAIN_LIGHTNING.value(),
                new ChainLightningAbilityInstanceData(ability, true));
    }


    // ----- ITriggerablePlayerAbility -----

    @SubscribeEvent
    public static void onCriticalHit(final CriticalHitEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            DeferredHolder<PlayerAbility<?>, ChainLightningAbility> ability = PlayerAbilities.CHAIN_LIGHTNING;
            ability.value().trigger(serverPlayer, PlayerAbilities.CHAIN_LIGHTNING.getKey(), event);
        }
    }

    @Override
    public Optional<ClientboundTriggerChainLightningPacket> onTrigger(ServerPlayer player, AbilityInstanceSerializable<?, ?> instance, CriticalHitEvent event) {
        if (instance.data() instanceof AdjustableStrengthAbilityInstanceData adjustableStrengthAbilityInstanceData) {
            final float strength = adjustableStrengthAbilityInstanceData.getAbilityStrength();
            float chanceToActivate = strength * 0.15F;
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

    // ----- Chain Lightning Behaviour -----

    /**
     * Perform the chain lightning ability.
     * @param target The entity that was hit by the critical hit
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
     * @param start The entity to start the line from
     * @param end The entity to end the line at
     * @param beamRadius The radius of the beam
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
     * @param randomSource The random source to use
     * @return A random particle type of either {@link Particles#LIGHTNING_BOLT} or {@link Particles#LIGHTNING_BOLT_SMALL}
     */
    private static ParticleOptions getRandomParticleType(RandomSource randomSource) {
        return randomSource.nextInt(6) == 0 ? Particles.LIGHTNING_BOLT.value() : Particles.LIGHTNING_BOLT_SMALL.value();
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
                    new ConfiguredPlayerAbility<>(PlayerAbilities.CHAIN_LIGHTNING.value(),
                            new PlayerAbilityConfiguration(
                                    new PlayerAbilityConfiguration.PlayerAbilityConfigurationData(translationKey, true, skillLookup.getOrThrow(parentSkillKey))
                            )
                    )
            );
        }
    }
}
