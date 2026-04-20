package grill24.potionsplus.core;

import grill24.potionsplus.skill.source.BreakBlockSource;
import grill24.potionsplus.skill.source.IncrementStatSource;
import grill24.potionsplus.skill.source.KillEntitySource;
import grill24.potionsplus.skill.source.SkillPointSource;
import grill24.potionsplus.utility.ModInfo;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class SkillPointSources {
    public static final DeferredRegister<SkillPointSource<?, ?>> SKILL_POINT_SOURCES = DeferredRegister.create(PotionsPlusRegistries.SKILL_POINT_SOURCE_REGISTRY_KEY, ModInfo.MOD_ID);

    public static final DeferredHolder<SkillPointSource<?, ?>, BreakBlockSource> BREAK_BLOCK = register("break_block", BreakBlockSource::new);
    public static final DeferredHolder<SkillPointSource<?, ?>, IncrementStatSource> INCREMENT_STAT = register("increment_stat", IncrementStatSource::new);
    public static final DeferredHolder<SkillPointSource<?, ?>, KillEntitySource> KILL_ENTITY = register("kill_entity", KillEntitySource::new);

    private static <S extends SkillPointSource<?, ?>> DeferredHolder<SkillPointSource<?, ?>, S> register(String name, Supplier<S> supplier) {
        return SKILL_POINT_SOURCES.register(name, supplier);
    }
}
