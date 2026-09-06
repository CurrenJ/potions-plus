package grill24.potionsplus.utility.registration.item;

import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

import java.util.function.Supplier;

public interface SizeProvider extends Supplier<NumberProvider> {
    SizeProvider modify(SizeProvider sizeProvider);
    float getMean();
    float getVariance();
}
