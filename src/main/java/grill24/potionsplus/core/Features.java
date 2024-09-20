package grill24.potionsplus.core;

import grill24.potionsplus.utility.ModInfo;
import grill24.potionsplus.worldgen.*;
import grill24.potionsplus.worldgen.feature.*;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.*;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Set;

@Mod.EventBusSubscriber(modid = ModInfo.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Features {
    public static DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Registries.FEATURE, ModInfo.MOD_ID);

    public static final ResourceKey<PlacedFeature> POTIONS_PLUS_VEGETATION_PATCH_KEY = ResourceKey.create(Registries.PLACED_FEATURE, new ResourceLocation(ModInfo.MOD_ID, "no_update_vegetation_patch"));
    public static Feature<PotionsPlusVegetationPatchConfiguration> POTIONS_PLUS_VEGETATION_PATCH = Features.register(POTIONS_PLUS_VEGETATION_PATCH_KEY.location().getPath(), new PotionsPlusVegetationPatchFeature(PotionsPlusVegetationPatchConfiguration.CODEC));

    // ----- Volcanic Cave -----
    public static Feature<?> FISSURE = Features.register(ConfiguredFeatures.FISSURE_KEY.location().getPath(), new VolcanicFissureFeature(NoneFeatureConfiguration.CODEC));
    public static Feature<?> LAVA_GEYSER = Features.register(ConfiguredFeatures.LAVA_GEYSER_KEY.location().getPath(), new LavaGeyserFeature(NoneFeatureConfiguration.CODEC));

    // ----- Ice Cave -----
    public static final ResourceKey<PlacedFeature> ICICLE_KEY = ResourceKey.create(Registries.PLACED_FEATURE, new ResourceLocation(ModInfo.MOD_ID, "icicle"));
    public static Feature<PointedDripstoneConfiguration> ICICLE = Features.register(ICICLE_KEY.location().getPath(), new IcicleFeature(PointedDripstoneConfiguration.CODEC));

    public static final ResourceKey<PlacedFeature> AQUIFER_FREEZE_KEY = ResourceKey.create(Registries.PLACED_FEATURE, new ResourceLocation(ModInfo.MOD_ID, "aquifer_freeze"));
    public static Feature<NoneFeatureConfiguration> AQUIFER_FREEZE = Features.register(AQUIFER_FREEZE_KEY.location().getPath(), new AquiferFreezeFeature(NoneFeatureConfiguration.CODEC));

    public static final ResourceKey<PlacedFeature> CAMPFIRE_HUDDLE_KEY = ResourceKey.create(Registries.PLACED_FEATURE, new ResourceLocation(ModInfo.MOD_ID, "campfire_huddle"));
    public static Feature<NoneFeatureConfiguration> CAMPFIRE_HUDDLE = Features.register(CAMPFIRE_HUDDLE_KEY.location().getPath(), new CampfireHuddleFeature(NoneFeatureConfiguration.CODEC));

    public static final ResourceKey<PlacedFeature> GIANT_SNOWFLAKE_KEY = ResourceKey.create(Registries.PLACED_FEATURE, new ResourceLocation(ModInfo.MOD_ID, "giant_snowflake"));
    public static Feature<NoneFeatureConfiguration> GIANT_SNOWFLAKE = Features.register(GIANT_SNOWFLAKE_KEY.location().getPath(), new GiantSnowflakeFeature(NoneFeatureConfiguration.CODEC));

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event)
    {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();

    }

    public static<C extends FeatureConfiguration, F extends Feature<C>> F register(String name, F feature)
    {
        ForgeRegistries.FEATURES.register(name, feature);
        return feature;
    }
}
