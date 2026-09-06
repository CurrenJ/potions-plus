package grill24.potionsplus.core.neoforge;

import grill24.potionsplus.utility.ModInfo;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.neoforged.neoforge.registries.DeferredRegister;

public class Features {
    public static DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Registries.FEATURE, ModInfo.MOD_ID);

    public static<C extends FeatureConfiguration, F extends Feature<C>> F register(String name, F feature)
    {
        FEATURES.register(name, () -> feature);
        return feature;
    }
}
