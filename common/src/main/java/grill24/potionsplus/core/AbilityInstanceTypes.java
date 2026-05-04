package grill24.potionsplus.core;

import grill24.potionsplus.skill.ability.instance.AdjustableStrength;
import grill24.potionsplus.skill.ability.instance.Cooldown;
import grill24.potionsplus.skill.ability.instance.DoubleJump;
import grill24.potionsplus.skill.ability.instance.SimpleToggleable;
import net.minecraft.core.Holder;

public class AbilityInstanceTypes {
    public static Holder<SimpleToggleable> SIMPLE_TOGGLEABLE;
    public static Holder<AdjustableStrength> ADJUSTABLE_STRENGTH;
    public static Holder<DoubleJump> DOUBLE_JUMP;
    public static Holder<Cooldown> COOLDOWN;
}
