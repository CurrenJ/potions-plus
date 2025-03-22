package grill24.potionsplus.skill.ability;

import grill24.potionsplus.core.PlayerAbilities;
import grill24.potionsplus.core.PotionsPlusRegistries;
import grill24.potionsplus.core.Translations;
import grill24.potionsplus.skill.ConfiguredSkill;
import grill24.potionsplus.skill.ability.instance.AbilityInstanceSerializable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;

import java.util.Optional;

public class SavedByTheBounceAbility extends CooldownTriggerableAbility<LivingFallEvent, CustomPacketPayload> {
    @Override
    public int getCooldownDurationForAbility(AbilityInstanceSerializable<?, ?> instance) {
        return 3600;
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
