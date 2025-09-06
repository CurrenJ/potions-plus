package grill24.potionsplus.utility.registration;

import net.minecraft.core.Holder;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public interface IRegisterer<T> {
    void register(BiFunction<String, Supplier<T>, Holder<T>> register);
}
