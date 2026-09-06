package grill24.potionsplus.core.forge;

import grill24.potionsplus.core.forge.util.ForgeHolder;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import net.minecraftforge.registries.DeferredRegister;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public class NumberProviders {
    public static final DeferredRegister<LootNumberProviderType> NUMBER_PROVIDERS = DeferredRegister.create(Registries.LOOT_NUMBER_PROVIDER_TYPE, ModInfo.MOD_ID);

    static {
        grill24.potionsplus.core.NumberProviders.init(registrar(NUMBER_PROVIDERS));
    }

    public static void init() {
        // No-op: forces class loading so the static initializer (above) runs and fills NUMBER_PROVIDERS.
    }

    private static <T> BiFunction<String, Supplier<T>, Holder<T>> registrar(DeferredRegister<T> register) {
        return (name, supplier) -> ForgeHolder.of(register.register(name, supplier));
    }
}
