package grill24.potionsplus.core.forge;

import com.mojang.serialization.MapCodec;
import grill24.potionsplus.core.forge.util.ForgeHolder;
import grill24.potionsplus.function.GaussianDistributionGenerator;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraftforge.registries.DeferredRegister;

public class NumberProviders {
    public static final DeferredRegister<MapCodec<? extends NumberProvider>> NUMBER_PROVIDERS =
            DeferredRegister.create(Registries.LOOT_NUMBER_PROVIDER_TYPE, ModInfo.MOD_ID);

    public static final ForgeHolder<MapCodec<GaussianDistributionGenerator>> GAUSSIAN_DISTRIBUTION =
            ForgeHolder.of(NUMBER_PROVIDERS.register("gaussian_distribution", () -> GaussianDistributionGenerator.MAP_CODEC));
}
