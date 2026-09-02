package grill24.potionsplus.core.fabric;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public class NumberProviders {

    public static void init() {
        grill24.potionsplus.core.NumberProviders.init(registrar(BuiltInRegistries.LOOT_NUMBER_PROVIDER_TYPE));
    }

    private static <T> BiFunction<String, Supplier<T>, Holder<T>> registrar(Registry<T> registry) {
        return (name, supplier) -> FabricRegistration.register(registry, name, supplier);
    }
}
