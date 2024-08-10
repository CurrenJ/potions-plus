package grill24.potionsplus.core;

import grill24.potionsplus.utility.ModInfo;
import grill24.potionsplus.worldgen.LunarBerryBushPatch;
import grill24.potionsplus.worldgen.OreGen;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.util.valueproviders.ConstantFloat;
import net.minecraft.util.valueproviders.TrapezoidFloat;
import net.minecraft.util.valueproviders.UniformFloat;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.carver.CanyonCarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CarverDebugSettings;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.*;
import net.minecraft.world.level.levelgen.heightproviders.UniformHeight;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = ModInfo.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class Features {
    public static DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Registry.FEATURE_REGISTRY, ModInfo.MOD_ID);
    public static DeferredRegister<ConfiguredWorldCarver<?>> CONFIGURED_CARVERS = DeferredRegister.create(Registry.CONFIGURED_CARVER_REGISTRY, ModInfo.MOD_ID);

    public static final RegistryObject<ConfiguredWorldCarver<CanyonCarverConfiguration>> SMALL_CANYON = CONFIGURED_CARVERS.register(
            "small_canyon",
            () -> {
                return WorldCarver.CANYON.configured(
                        new CanyonCarverConfiguration(
                                0.2F, // probability
                                UniformHeight.of(VerticalAnchor.aboveBottom(10), VerticalAnchor.absolute(48)), // y
                                ConstantFloat.of(1.0F), // yScale
                                VerticalAnchor.aboveBottom(8), // lavaLevel
                                CarverDebugSettings.of(false, Blocks.WARPED_BUTTON.defaultBlockState()), // debugSettings
                                UniformFloat.of(-0.125F, 0.125F), // verticalRotation
                                new CanyonCarverConfiguration.CanyonShapeConfiguration(
                                        UniformFloat.of(0.25F, 0.5F), // distanceFactor
                                        TrapezoidFloat.of(0.0F, 6.0F, 2.0F), // thickness
                                        3, // widthSmoothness
                                        UniformFloat.of(0.25F, 0.5F), // horizontalRadiusFactor
                                        1.0F, // verticalRadiusDefaultFactor
                                        0.0F // verticalRadiusCenterFactor
                                )
                        )
                );
            }
    );

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
