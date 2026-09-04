package grill24.potionsplus.core.fabric;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import java.util.function.Supplier;

import static grill24.potionsplus.utility.Utility.ppId;

/**
 * Immediate-registration helper for Fabric. Fabric registers eagerly (there is no DeferredRegister),
 * so {@link Registry#registerForHolder} is called with the value resolved up front. The {@code <R, T
 * extends R>} signature lets a hub register a subtype (e.g. {@code BlockEntityType<X>} into the
 * wildcard {@code BlockEntityType<?>} registry) while still returning the concrete {@code Holder<T>}.
 */
public final class FabricRegistration {
    private FabricRegistration() {
    }

    @SuppressWarnings("unchecked")
    public static <R, T extends R> Holder<T> register(Registry<R> registry, String name, Supplier<T> supplier) {
        ResourceKey<R> key = ResourceKey.create(registry.key(), ppId(name));
        // registerForHolder is typed to the registry element type R; the cast bridges back to the
        // concrete subtype T the caller registered.
        return (Holder<T>) (Holder<?>) Registry.registerForHolder(registry, key, supplier.get());
    }
}
