package grill24.potionsplus.skill.ability;

import net.minecraft.network.chat.Component;

public interface IAdjustableStrengthAbility<AC extends PlayerAbilityConfiguration> {
    Component getDescription(AC config, float strength);
}
