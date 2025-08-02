package grill24.potionsplus.worldgen;

import com.mojang.datafixers.util.Pair;
import grill24.potionsplus.core.Biomes;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import terrablender.api.*;

import java.util.function.Consumer;

import static grill24.potionsplus.utility.Utility.ppId;

@EventBusSubscriber(modid = ModInfo.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class OverworldBiomesRegion extends Region {
    public static final ResourceLocation ID = ppId("overworld_biomes");

    private final Climate.Parameter FULL_RANGE = Climate.Parameter.span(-1.0F, 1.0F);
    private final Climate.Parameter[] temperatures = new Climate.Parameter[]{Climate.Parameter.span(-1.0F, -0.45F), Climate.Parameter.span(-0.45F, -0.15F), Climate.Parameter.span(-0.15F, 0.2F), Climate.Parameter.span(0.2F, 0.55F), Climate.Parameter.span(0.55F, 1.0F)};
    private final Climate.Parameter[] humidities = new Climate.Parameter[]{Climate.Parameter.span(-1.0F, -0.35F), Climate.Parameter.span(-0.35F, -0.1F), Climate.Parameter.span(-0.1F, 0.1F), Climate.Parameter.span(0.1F, 0.3F), Climate.Parameter.span(0.3F, 1.0F)};
    private final Climate.Parameter[] erosions = new Climate.Parameter[]{Climate.Parameter.span(-1.0F, -0.78F), Climate.Parameter.span(-0.78F, -0.375F), Climate.Parameter.span(-0.375F, -0.2225F), Climate.Parameter.span(-0.2225F, 0.05F), Climate.Parameter.span(0.05F, 0.45F), Climate.Parameter.span(0.45F, 0.55F), Climate.Parameter.span(0.55F, 1.0F)};
    private final Climate.Parameter FROZEN_RANGE = temperatures[0];


    public OverworldBiomesRegion(int weight) {
        super(ID, RegionType.OVERWORLD, weight);
    }

    @Override
    public void addBiomes(Registry<Biome> registry, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> consumer) {
        addUndergroundBiome(consumer, FROZEN_RANGE, FULL_RANGE, FULL_RANGE, FULL_RANGE, FULL_RANGE, 0.0F, Biomes.ICE_CAVE_KEY);
        addUndergroundBiome(consumer, temperatures[4], FULL_RANGE, FULL_RANGE, FULL_RANGE, FULL_RANGE, 0.0F, Biomes.VOLCANIC_CAVE_KEY);
        addUndergroundBiome(consumer, Climate.Parameter.span(0.2F, 1.0F), humidities[0], FULL_RANGE, FULL_RANGE, FULL_RANGE, 0.0F, Biomes.ARID_CAVE_KEY);
        addUndergroundBiome(consumer, Climate.Parameter.span(-0.15F, 0.55F), Climate.Parameter.span(0.1F, 1.0F), FULL_RANGE, FULL_RANGE, FULL_RANGE, 0.0F, Biomes.WOODED_CAVE_KEY);
    }

    protected void addUndergroundBiome(Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper, Climate.Parameter temperature, Climate.Parameter humidity, Climate.Parameter continentalness, Climate.Parameter erosion, Climate.Parameter weirdness, float offset, ResourceKey<Biome> biome) {
        VanillaParameterOverlayBuilder overlayBuilder = new VanillaParameterOverlayBuilder();

        new ParameterUtils.ParameterPointListBuilder()
                .temperature(temperature)
                .humidity(humidity)
                .continentalness(continentalness)
                .erosion(erosion)
                .depth(ParameterUtils.Depth.UNDERGROUND.parameter())
                .weirdness(weirdness)
                .offset(offset)
                .build().forEach(point -> overlayBuilder.add(point, biome));

        overlayBuilder.build().forEach(mapper);
    }

    @SubscribeEvent
    public static void onModSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() ->
        {
            Regions.register(new OverworldBiomesRegion(4));
        });
    }
}
