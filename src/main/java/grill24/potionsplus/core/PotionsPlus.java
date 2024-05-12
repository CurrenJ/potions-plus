package grill24.potionsplus.core;

import com.mojang.logging.LogUtils;
import grill24.potionsplus.utility.ModInfo;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ModInfo.MOD_ID)
public class PotionsPlus {
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public PotionsPlus() {
        Blocks.BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        Blocks.BLOCK_ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
        Items.ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        Particles.PARTICLE_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());

        Recipes.RECIPE_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
        Recipes.RECIPE_SERIALIZERS.register(FMLJavaModLoadingContext.get().getModEventBus());

        MobEffects.EFFECTS.register(FMLJavaModLoadingContext.get().getModEventBus());
        Potions.POTIONS.register(FMLJavaModLoadingContext.get().getModEventBus());

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }
}
