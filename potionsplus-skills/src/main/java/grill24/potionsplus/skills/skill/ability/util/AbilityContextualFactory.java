package grill24.potionsplus.skill.ability.util;

import grill24.potionsplus.skill.ability.ConfiguredPlayerAbility;

@FunctionalInterface
public interface AbilityContextualFactory<R> extends IContextualFactory<ConfiguredPlayerAbility<?, ?>, R> {
}
