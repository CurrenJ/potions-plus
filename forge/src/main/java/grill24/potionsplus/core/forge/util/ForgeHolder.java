package grill24.potionsplus.core.forge.util;

import com.mojang.datafixers.util.Either;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraftforge.registries.RegistryObject;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Adapts Forge's {@link RegistryObject} to the vanilla {@link Holder} interface the common
 * registration layer expects. Forge's {@code DeferredRegister.register} returns a
 * {@code RegistryObject} which implements {@link Supplier} but NOT {@link Holder}; this mirrors
 * NeoForge {@code DeferredHolder} semantics so every common {@code init(...)} call can be
 * satisfied with a {@link Holder} without touching common code.
 *
 * <p>{@link #getKey()}/{@link #getId()} are available immediately (the registry name is known at
 * registration time); {@link #value()} and the holder-backed methods resolve lazily once the
 * RegisterEvent fires and the delegate's holder is filled in.
 */
public final class ForgeHolder<T> implements Holder<T>, Supplier<T> {
    private final RegistryObject<T> delegate;

    private ForgeHolder(RegistryObject<T> delegate) {
        this.delegate = delegate;
    }

    public static <T> ForgeHolder<T> of(RegistryObject<T> delegate) {
        return new ForgeHolder<>(delegate);
    }

    @Override
    public T get() {
        return delegate.get();
    }

    @Override
    public T value() {
        return delegate.get();
    }

    public ResourceKey<T> getKey() {
        return delegate.getKey();
    }

    public Identifier getId() {
        return delegate.getId();
    }

    /**
     * The real registry holder this stands for, once the RegisterEvent for its registry has fired and
     * only if the registry actually produced a {@link Holder.Reference}. Used by
     * {@code grill24.potionsplus.mixin.forge.RegistryMixin} to make this wrapper serializable - see
     * that class for why Forge needs it and NeoForge does not.
     */
    @SuppressWarnings("unchecked")
    public Optional<Holder.Reference<T>> resolveReference() {
        return delegate.getHolder()
                .filter(holder -> holder instanceof Holder.Reference<?>)
                .map(holder -> (Holder.Reference<T>) holder);
    }

    @Override
    public boolean isBound() {
        return delegate.getHolder().map(Holder::isBound).orElse(false);
    }

    @Override
    public boolean areComponentsBound() {
        return delegate.getHolder().map(Holder::areComponentsBound).orElse(false);
    }

    @Override
    public boolean is(Identifier id) {
        return id.equals(delegate.getId());
    }

    @Override
    public boolean is(ResourceKey<T> key) {
        return key.equals(getKey());
    }

    @Override
    public boolean is(Predicate<ResourceKey<T>> predicate) {
        return getKey() != null && predicate.test(getKey());
    }

    @Override
    public boolean is(TagKey<T> tag) {
        return delegate.getHolder().map(h -> h.is(tag)).orElse(false);
    }

    @Override
    public boolean is(Holder<T> holder) {
        return getKey() != null && holder.unwrapKey().map(getKey()::equals).orElse(false);
    }

    @Override
    public Stream<TagKey<T>> tags() {
        return delegate.getHolder().map(Holder::tags).orElse(Stream.empty());
    }

    @Override
    public DataComponentMap components() {
        return delegate.getHolder().map(Holder::components).orElse(DataComponentMap.EMPTY);
    }

    @Override
    public Either<ResourceKey<T>, T> unwrap() {
        return Either.left(getKey());
    }

    @Override
    public Optional<ResourceKey<T>> unwrapKey() {
        return Optional.ofNullable(getKey());
    }

    @Override
    public Kind kind() {
        return Kind.REFERENCE;
    }

    @Override
    public boolean canSerializeIn(HolderOwner<T> owner) {
        return delegate.getHolder().map(h -> h.canSerializeIn(owner)).orElse(false);
    }

    /**
     * Compares by resource key against any {@link Holder}, not just other {@link ForgeHolder}s.
     *
     * <p>{@code Holder.Reference} (what {@code BuiltInRegistries.*.listElements()} actually hands
     * back) declares no {@code equals}/{@code hashCode} override of its own - registries hand out one
     * singleton instance per key, so plain identity equality is correct for vanilla-to-vanilla
     * comparisons. A {@link ForgeHolder} is a second, distinct object standing in for that same key,
     * so a class-restricted equals (the previous implementation) could never match the real reference:
     * {@code someList.contains(MobEffects.MAGNETIC)} silently returned false against a list built from
     * the registry directly, e.g. {@code EffectRegistry.passiveEligible}. Matching by key here mirrors
     * {@link #is(Holder)}'s existing semantics, at the cost of one-sided symmetry - the vanilla
     * reference's own identity-based equals still won't recognise this wrapper - which is an accepted,
     * unavoidable tradeoff without patching vanilla (see {@code RegistryMixin} for the same tradeoff
     * made for serialization).
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Holder<?> other)) return false;
        return getKey() != null && other.unwrapKey().map(getKey()::equals).orElse(false);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKey());
    }
}
