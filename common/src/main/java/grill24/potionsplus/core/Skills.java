package grill24.potionsplus.core;

import grill24.potionsplus.skill.Skill;
import grill24.potionsplus.skill.SkillConfiguration;
import net.minecraft.core.Holder;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public class Skills {
    public static Holder<Skill<SkillConfiguration>> GENERIC;

    @SuppressWarnings("unchecked")
    public static void init(BiFunction<String, Supplier<Skill<?>>, Holder<Skill<?>>> register) {
        GENERIC = (Holder<Skill<SkillConfiguration>>) (Holder<?>) register.apply("generic", () -> new Skill<>(SkillConfiguration.CODEC));
    }
}
