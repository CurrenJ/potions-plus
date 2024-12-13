package grill24.potionsplus.core;

import grill24.potionsplus.utility.ModInfo;
import grill24.potionsplus.worldgen.*;
import grill24.potionsplus.worldgen.feature.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.*;
import net.minecraft.world.level.levelgen.placement.*;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import static grill24.potionsplus.utility.Utility.ppId;

@EventBusSubscriber(modid = ModInfo.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class Features {
    public static DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Registries.FEATURE, ModInfo.MOD_ID);

    public static final ResourceKey<PlacedFeature> POTIONS_PLUS_VEGETATION_PATCH_KEY = ResourceKey.create(Registries.PLACED_FEATURE, ppId("no_update_vegetation_patch"));
    public static Feature<PotionsPlusVegetationPatchConfiguration> POTIONS_PLUS_VEGETATION_PATCH = Features.register(POTIONS_PLUS_VEGETATION_PATCH_KEY.location().getPath(), new PotionsPlusVegetationPatchFeature(PotionsPlusVegetationPatchConfiguration.CODEC));

    // ----- Volcanic Cave -----
    public static Feature<?> FISSURE = Features.register(ConfiguredFeatures.FISSURE_KEY.location().getPath(), new VolcanicFissureFeature(NoneFeatureConfiguration.CODEC));
    public static Feature<?> LAVA_GEYSER = Features.register(ConfiguredFeatures.LAVA_GEYSER_KEY.location().getPath(), new LavaGeyserFeature(NoneFeatureConfiguration.CODEC));

    public static Feature<PointedDripstoneConfiguration> ICICLE = Features.register("icicle", new IcicleFeature(PointedDripstoneConfiguration.CODEC));
    public static Feature<NoneFeatureConfiguration> AQUIFER_FREEZE = Features.register("aquifer_freeze", new AquiferFreezeFeature(NoneFeatureConfiguration.CODEC));
    public static Feature<NoneFeatureConfiguration> CAMPFIRE_HUDDLE = Features.register("campfire_huddle", new CampfireHuddleFeature(NoneFeatureConfiguration.CODEC));
    public static Feature<NoneFeatureConfiguration> GIANT_SNOWFLAKE = Features.register("giant_snowflake", new GiantSnowflakeFeature(NoneFeatureConfiguration.CODEC));

    // ----- Arid Cave -----
    public static Feature<NoneFeatureConfiguration> ARID_CAVE_SUSPICIOUS_SAND = Features.register("arid_cave_suspicious_sand", new SuspiciousSandFeature(NoneFeatureConfiguration.CODEC));

    // ----- Misc. -----
    public static final Feature<VersatilePlantBlockFeatureConfiguration> VERSATILE_PLANT = Features.register("versatile_plant", new VersatilePlantBlockFeature());

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event)
    {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();

    }

    public static<C extends FeatureConfiguration, F extends Feature<C>> F register(String name, F feature)
    {
        FEATURES.register(name, () -> feature);
        return feature;
    }
}
