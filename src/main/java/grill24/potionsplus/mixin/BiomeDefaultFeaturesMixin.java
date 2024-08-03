package grill24.potionsplus.mixin;

import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BiomeDefaultFeatures.class)
public abstract class BiomeDefaultFeaturesMixin {
    @Inject(method = "addCommonBerryBushes", at = @At("HEAD"), cancellable = true)
    private static void addCommonBerryBushes(BiomeGenerationSettings.Builder builder, CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "addRareBerryBushes", at = @At("HEAD"), cancellable = true)
    private static void addRareBerryBushes(BiomeGenerationSettings.Builder builder, CallbackInfo ci) {
        ci.cancel();
    }
}
