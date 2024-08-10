package grill24.potionsplus.core;

import grill24.potionsplus.utility.ModInfo;
import grill24.potionsplus.worldgen.IceCave;
import grill24.potionsplus.worldgen.VolcanicCave;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class Biomes {
    public static final DeferredRegister<Biome> BIOMES = DeferredRegister.create(ForgeRegistries.BIOMES, ModInfo.MOD_ID);

    public static final RegistryObject<Biome> ICE_CAVE = BIOMES.register("ice_cave", IceCave::iceCave);
    public static final RegistryObject<Biome> VOLCANIC_CAVE = BIOMES.register("volcanic_cave", VolcanicCave::volcanicCave);
}
