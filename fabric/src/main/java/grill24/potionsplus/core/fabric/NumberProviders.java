package grill24.potionsplus.core.fabric;

import com.mojang.serialization.MapCodec;
import grill24.potionsplus.function.GaussianDistributionGenerator;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;

public class NumberProviders {
    public static final Holder<MapCodec<GaussianDistributionGenerator>> GAUSSIAN_DISTRIBUTION =
            FabricRegistration.register(BuiltInRegistries.LOOT_NUMBER_PROVIDER_TYPE, "gaussian_distribution", () -> GaussianDistributionGenerator.MAP_CODEC);

    public static void init() {
    }
}
