package grill24.potionsplus.core;

import grill24.potionsplus.skill.source.BreakBlockSource;
import grill24.potionsplus.skill.source.IncrementStatSource;
import grill24.potionsplus.skill.source.KillEntitySource;
import grill24.potionsplus.skill.source.SkillPointSource;
import net.minecraft.core.Holder;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public class SkillPointSources {
    public static Holder<BreakBlockSource> BREAK_BLOCK;
    public static Holder<IncrementStatSource> INCREMENT_STAT;
    public static Holder<KillEntitySource> KILL_ENTITY;

    @SuppressWarnings("unchecked")
    public static void init(BiFunction<String, Supplier<SkillPointSource<?, ?>>, Holder<SkillPointSource<?, ?>>> register) {
        BREAK_BLOCK = (Holder<BreakBlockSource>) (Holder<?>) register.apply("break_block", BreakBlockSource::new);
        INCREMENT_STAT = (Holder<IncrementStatSource>) (Holder<?>) register.apply("increment_stat", IncrementStatSource::new);
        KILL_ENTITY = (Holder<KillEntitySource>) (Holder<?>) register.apply("kill_entity", KillEntitySource::new);
    }
}
