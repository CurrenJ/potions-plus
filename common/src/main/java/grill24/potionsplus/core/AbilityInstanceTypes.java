package grill24.potionsplus.core;

import grill24.potionsplus.skill.ability.instance.AbilityInstanceType;
import grill24.potionsplus.skill.ability.instance.AdjustableStrength;
import grill24.potionsplus.skill.ability.instance.Cooldown;
import grill24.potionsplus.skill.ability.instance.DoubleJump;
import grill24.potionsplus.skill.ability.instance.SimpleToggleable;
import net.minecraft.core.Holder;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public class AbilityInstanceTypes {
    public static Holder<SimpleToggleable> SIMPLE_TOGGLEABLE;
    public static Holder<AdjustableStrength> ADJUSTABLE_STRENGTH;
    public static Holder<DoubleJump> DOUBLE_JUMP;
    public static Holder<Cooldown> COOLDOWN;

    @SuppressWarnings("unchecked")
    public static void init(BiFunction<String, Supplier<AbilityInstanceType<?>>, Holder<AbilityInstanceType<?>>> register) {
        SIMPLE_TOGGLEABLE = (Holder<SimpleToggleable>) (Holder<?>) register.apply("simple_toggleable", SimpleToggleable::new);
        ADJUSTABLE_STRENGTH = (Holder<AdjustableStrength>) (Holder<?>) register.apply("adjustable_strength", AdjustableStrength::new);
        DOUBLE_JUMP = (Holder<DoubleJump>) (Holder<?>) register.apply("double_jump", DoubleJump::new);
        COOLDOWN = (Holder<Cooldown>) (Holder<?>) register.apply("cooldown", Cooldown::new);
    }
}
