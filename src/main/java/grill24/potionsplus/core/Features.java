package grill24.potionsplus.core;

import grill24.potionsplus.utility.ModInfo;
import grill24.potionsplus.worldgen.LunarBerryBushPatch;
import grill24.potionsplus.worldgen.OreGen;
import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.*;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;

@Mod.EventBusSubscriber(modid = ModInfo.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class Features {
    public static DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Registry.FEATURE_REGISTRY, ModInfo.MOD_ID);

    @SubscribeEvent
    public static void onBiomeLoadingEvent(BiomeLoadingEvent event) {
        LunarBerryBushPatch.addLunarBerryBushes(event);
        OreGen.addDenseDiamondOre(event.getGeneration());
    }

    public static <C extends FeatureConfiguration, F extends Feature<C>> F register(String key, F value)
    {
        FEATURES.register(key, () -> value);
        return value;
    }
}
