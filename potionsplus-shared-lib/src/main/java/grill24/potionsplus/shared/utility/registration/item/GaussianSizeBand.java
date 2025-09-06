package grill24.potionsplus.utility.registration.item;

import grill24.potionsplus.function.GaussianDistributionGenerator;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

public record GaussianSizeBand(float n, float p) implements SizeProvider {
    public static final GaussianSizeBand SMALL = new GaussianSizeBand(30F, 15F);
    public static final GaussianSizeBand MEDIUM = new GaussianSizeBand(50F, 15F);
    public static final GaussianSizeBand LARGE = new GaussianSizeBand(80F, 15F);
    public static final GaussianSizeBand EXTRA_LARGE = new GaussianSizeBand(150F, 15F);

    @Override
    public GaussianSizeBand modify(SizeProvider provider) {
        return new GaussianSizeBand(this.n + provider.getMean(), this.p + provider.getVariance());
    }

    @Override
    public NumberProvider get() {
        return GaussianDistributionGenerator.gaussian(this.n, this.p);
    }

    @Override
    public float getMean() {
        return this.n;
    }

    @Override
    public float getVariance() {
        return this.p;
    }
}

