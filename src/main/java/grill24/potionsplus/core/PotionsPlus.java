package grill24.potionsplus.core;

import com.mojang.logging.LogUtils;
import grill24.potionsplus.core.potion.MobEffects;
import grill24.potionsplus.core.potion.Potions;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ModInfo.MOD_ID)
public class PotionsPlus {
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public static long worldSeed = -1;

    public PotionsPlus() {
        Blocks.BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        Blocks.BLOCK_ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
        Items.ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        Particles.PARTICLE_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());

        Recipes.RECIPE_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
        Recipes.RECIPE_SERIALIZERS.register(FMLJavaModLoadingContext.get().getModEventBus());

        MobEffects.EFFECTS.register(FMLJavaModLoadingContext.get().getModEventBus());
        Potions.POTIONS.register(FMLJavaModLoadingContext.get().getModEventBus());

        Sounds.SOUNDS.register(FMLJavaModLoadingContext.get().getModEventBus());

        LootModifiers.LOOT_MODIFIERS.register(FMLJavaModLoadingContext.get().getModEventBus());

        Features.CONFIGURED_CARVERS.register(FMLJavaModLoadingContext.get().getModEventBus());
        Features.FEATURES.register(FMLJavaModLoadingContext.get().getModEventBus());
        Biomes.BIOMES.register(FMLJavaModLoadingContext.get().getModEventBus());


        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    public static class Debug {
        public static final boolean DEBUG = true;

        public static final boolean DEBUG_POTION_INGREDIENTS_GENERATION = false;
        public static final boolean DEBUG_POTION_RECIPE_GENERATION = true;
    }
}
