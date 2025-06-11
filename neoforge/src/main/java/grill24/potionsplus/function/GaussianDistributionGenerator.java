package grill24.potionsplus.function;

import com.google.common.collect.Sets;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import grill24.potionsplus.utility.Utility;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

import java.util.Set;

/**
 * A number provider which generates a random number based on a Gaussian distribution.
 */
public record GaussianDistributionGenerator(NumberProvider mean, NumberProvider stdDev) implements NumberProvider {
    public static final MapCodec<GaussianDistributionGenerator> CODEC = RecordCodecBuilder.mapCodec(
        codecBuilder -> codecBuilder.group(
                    NumberProviders.CODEC.fieldOf("mean").forGetter(GaussianDistributionGenerator::mean),
                    NumberProviders.CODEC.fieldOf("stdDev").forGetter(GaussianDistributionGenerator::stdDev)
                )
                .apply(codecBuilder, GaussianDistributionGenerator::new)
    );

    @Override
    public LootNumberProviderType getType() {
        return grill24.potionsplus.core.NumberProviders.GAUSSIAN_DISTRIBUTION.value();
    }

    @Override
    public int getInt(LootContext lootContext) {
        return (int) this.getFloat(lootContext);
    }

    @Override
    public float getFloat(LootContext lootContext) {
        return (float) Utility.nextGaussian(
            this.mean.getFloat(lootContext),
            this.stdDev.getFloat(lootContext),
            lootContext.getRandom()
        );
    }

    public static GaussianDistributionGenerator gaussian(float n, float p) {
        return new GaussianDistributionGenerator(ConstantValue.exactly(n), ConstantValue.exactly(p));
    }

    @Override
    public Set<ContextKey<?>> getReferencedContextParams() {
        return Sets.union(this.mean.getReferencedContextParams(), this.stdDev.getReferencedContextParams());
    }
}
