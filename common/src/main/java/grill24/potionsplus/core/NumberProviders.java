package grill24.potionsplus.core;

import grill24.potionsplus.function.GaussianDistributionGenerator;
import net.minecraft.core.Holder;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public class NumberProviders {
    public static Holder<LootNumberProviderType> GAUSSIAN_DISTRIBUTION;

    public static void init(BiFunction<String, Supplier<LootNumberProviderType>, Holder<LootNumberProviderType>> register) {
        GAUSSIAN_DISTRIBUTION = register.apply("gaussian_distribution", () -> new LootNumberProviderType(GaussianDistributionGenerator.CODEC));
    }
}
