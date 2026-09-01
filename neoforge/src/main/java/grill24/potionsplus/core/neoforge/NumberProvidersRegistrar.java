package grill24.potionsplus.core.neoforge;

import grill24.potionsplus.core.NumberProviders;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import net.neoforged.neoforge.registries.DeferredRegister;

public class NumberProvidersRegistrar {
    public static final DeferredRegister<LootNumberProviderType> NUMBER_PROVIDERS = DeferredRegister.create(Registries.LOOT_NUMBER_PROVIDER_TYPE, ModInfo.MOD_ID);

    static {
        NumberProviders.init(NUMBER_PROVIDERS::register);
    }
}
