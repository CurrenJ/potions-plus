package grill24.potionsplus.mixin;

import com.mojang.datafixers.util.Pair;
import grill24.potionsplus.core.Biomes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.OverworldBiomeBuilder;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(OverworldBiomeBuilder.class)
public abstract class OverworldBiomeBuilderMixin {
    @Shadow protected abstract void addUndergroundBiome(Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> p_187201_, Climate.Parameter p_187202_, Climate.Parameter p_187203_, Climate.Parameter p_187204_, Climate.Parameter p_187205_, Climate.Parameter p_187206_, float p_187207_, ResourceKey<Biome> p_187208_);

    @Shadow @Final private Climate.Parameter FULL_RANGE;

    @Shadow @Final private Climate.Parameter[] temperatures;

    @Shadow @Final private Climate.Parameter FROZEN_RANGE;

    @Shadow @Final private Climate.Parameter UNFROZEN_RANGE;

    @Shadow @Final private Climate.Parameter[] humidities;

    @Inject(method = "addUndergroundBiomes(Ljava/util/function/Consumer;)V", at = @At("HEAD"))
    private void addUndergroundBiomes(Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> consumer, CallbackInfo ci) {
       // consumer, temperature, humidity, continentalness, erosion, depth, weirdness, biome
        this.addUndergroundBiome(consumer, this.FROZEN_RANGE, this.FULL_RANGE, this.FULL_RANGE, this.FULL_RANGE, this.FULL_RANGE, 0.0F, Biomes.ICE_CAVE.getKey());
        this.addUndergroundBiome(consumer, this.temperatures[4], this.FULL_RANGE, this.FULL_RANGE, this.FULL_RANGE, this.FULL_RANGE, 0.0F, Biomes.VOLCANIC_CAVE.getKey());
        this.addUndergroundBiome(consumer, Climate.Parameter.span(0.2F, 1.0F), this.humidities[0], this.FULL_RANGE, this.FULL_RANGE, this.FULL_RANGE, 0.0F, Biomes.ARID_CAVE.getKey());
    }
}
