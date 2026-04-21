package grill24.potionsplus.core;

import com.mojang.serialization.MapCodec;
import grill24.potionsplus.function.GaussianDistributionGenerator;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.neoforged.neoforge.registries.DeferredRegister;

public class NumberProviders {
    public static final DeferredRegister<MapCodec<? extends NumberProvider>> NUMBER_PROVIDERS =
            DeferredRegister.create(Registries.LOOT_NUMBER_PROVIDER_TYPE, ModInfo.MOD_ID);

    public static final Holder<MapCodec<? extends NumberProvider>> GAUSSIAN_DISTRIBUTION =
            NUMBER_PROVIDERS.register("gaussian_distribution", () -> GaussianDistributionGenerator.MAP_CODEC);
}
