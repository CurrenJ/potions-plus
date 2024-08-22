package grill24.potionsplus.worldgen;

import grill24.potionsplus.core.Features;
import grill24.potionsplus.utility.ModInfo;
import grill24.potionsplus.worldgen.feature.PotionsPlusVegetationPatchConfiguration;
import grill24.potionsplus.worldgen.feature.PotionsPlusVegetationPatchFeature;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class MiscFeatures {
    public static final ResourceKey<PlacedFeature> POTIONS_PLUS_VEGETATION_PATCH_KEY = ResourceKey.create(Registry.PLACED_FEATURE_REGISTRY, new ResourceLocation(ModInfo.MOD_ID, "no_update_vegetation_patch"));
    public static Feature<PotionsPlusVegetationPatchConfiguration> POTIONS_PLUS_VEGETATION_PATCH = Features.register(POTIONS_PLUS_VEGETATION_PATCH_KEY.location().getPath(), new PotionsPlusVegetationPatchFeature(PotionsPlusVegetationPatchConfiguration.CODEC));
}
