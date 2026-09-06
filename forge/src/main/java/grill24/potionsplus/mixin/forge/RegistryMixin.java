package grill24.potionsplus.mixin.forge;

import com.mojang.serialization.DataResult;
import grill24.potionsplus.core.forge.util.ForgeHolder;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Lets a deferred {@link ForgeHolder} be serialized as the registry entry it stands for.
 *
 * <p>{@code Registry#safeCastToReference} - the encode half of {@code holderByNameCodec()}, and so of
 * every codec that writes a {@code Holder<T>} (potion contents on an item stack, this mod's saved
 * data, its custom recipes) - is a bare {@code instanceof Holder.Reference} check. Forge's
 * {@code DeferredRegister} hands back a {@code RegistryObject}, which {@link ForgeHolder} adapts to
 * {@code Holder} but which is not a {@code Holder.Reference}, so every mod potion or effect held that
 * way failed to encode with "Unregistered holder in ResourceKey[minecraft:root / minecraft:potion]".
 * That surfaced as an exception saving the level at shutdown, and would equally have hit any chest
 * holding one of this mod's potions.
 *
 * <p>NeoForge's {@code DeferredHolder} is itself a {@code Holder.Reference} subclass, so vanilla's
 * {@code instanceof} passes naturally there - which is why the NeoForge module needs nothing here.
 * Forge ships no equivalent hook, so this mixin does the same unwrapping for our own wrapper only,
 * leaving every other holder to vanilla's check.
 */
@Mixin(Registry.class)
public interface RegistryMixin<T> {

    @Inject(method = "safeCastToReference", at = @At("HEAD"), cancellable = true)
    private void potions_plus$unwrapDeferredHolder(Holder<T> holder, CallbackInfoReturnable<DataResult<Holder.Reference<T>>> cir) {
        if (!(holder instanceof ForgeHolder<T> forgeHolder)) {
            return;
        }

        // Empty before the RegisterEvent for this registry has fired; leaving the callback alone then
        // yields vanilla's error, which is the correct outcome for a genuinely unregistered holder.
        forgeHolder.resolveReference().ifPresent(reference -> cir.setReturnValue(DataResult.success(reference)));
    }
}
