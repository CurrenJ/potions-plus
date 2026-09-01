package grill24.potionsplus.utility;

import java.util.function.Supplier;

/**
 * Loader-agnostic replacement for {@code net.neoforged.neoforge.common.util.Lazy}, used by
 * common code that used to depend on the NeoForge-only class.
 */
public final class Lazy<T> implements Supplier<T> {
    private final Supplier<T> supplier;
    private volatile T value;
    private volatile boolean resolved;

    private Lazy(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    public static <T> Lazy<T> of(Supplier<T> supplier) {
        return new Lazy<>(supplier);
    }

    @Override
    public T get() {
        if (!resolved) {
            synchronized (this) {
                if (!resolved) {
                    value = supplier.get();
                    resolved = true;
                }
            }
        }
        return value;
    }
}
