package grill24.potionsplus.skill.ability;

import grill24.potionsplus.core.AbilityInstanceTypes;
import grill24.potionsplus.core.PlayerAbilities;
import grill24.potionsplus.core.PotionsPlusRegistries;
import grill24.potionsplus.skill.ConfiguredSkill;
import grill24.potionsplus.skill.ability.instance.AbilityInstanceSerializable;
import grill24.potionsplus.skill.ability.instance.CooldownAbilityInstanceData;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;

import java.util.Optional;

public class SavedByTheBounceAbility extends SimplePlayerAbility implements ITriggerablePlayerAbility<LivingFallEvent, CustomPacketPayload> {
    @Override
    public Optional<CustomPacketPayload> onTrigger(ServerPlayer player, AbilityInstanceSerializable<?, ?> instance, LivingFallEvent eventData) {
        return Optional.empty();
    }

    @Override
    public AbilityInstanceSerializable<?, ?> createInstance(ServerPlayer player, Holder<ConfiguredPlayerAbility<?, ?>> ability) {
        return new AbilityInstanceSerializable<>(
                AbilityInstanceTypes.COOLDOWN.value(),
                new CooldownAbilityInstanceData(ability, true, 0, 0, 6000, 0));
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
                    new ConfiguredPlayerAbility<>(PlayerAbilities.SAVED_BY_THE_BOUNCE.value(),
                            new PlayerAbilityConfiguration(
                                    new PlayerAbilityConfiguration.PlayerAbilityConfigurationData(translationKey, true, skillLookup.getOrThrow(parentSkillKey))
                            )
                    )
            );
        }
    }
}
