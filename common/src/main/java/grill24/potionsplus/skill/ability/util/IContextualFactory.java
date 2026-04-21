package grill24.potionsplus.skill.ability.util;

import net.minecraft.data.worldgen.BootstrapContext;

public interface IContextualFactory<I, R> {
    R create(BootstrapContext<I> context);
}
