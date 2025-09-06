package grill24.potionsplus.skill.ability.util;

import net.minecraft.core.HolderGetter;

@FunctionalInterface
public interface RegistryAccessibleFactory<R, T> {
    R create(HolderGetter<T> registryAccess);
}
