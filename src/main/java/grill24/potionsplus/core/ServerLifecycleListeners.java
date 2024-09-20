package grill24.potionsplus.core;

import grill24.potionsplus.blockentity.SanguineAltarBlockEntity;
import grill24.potionsplus.persistence.SavedData;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ServerLifecycleListeners {
    public static boolean serverStarted = false;

    @SubscribeEvent
    public static void onServerStarted(final ServerStartedEvent event) {
        final MinecraftServer server = event.getServer();

        // Setup saved data
        initializeSavedData(server);

        // Store world seed - used for generating runtime recipes. Important to do this before any recipes are generated or loot tables are initialized.
        PotionsPlus.worldSeed = server.getWorldData().worldGenOptions().seed();

        // Inject runtime recipes
        injectRuntimeRecipes(server);

        // Inject biomes
//        initializeBiomes(server);

        serverStarted = true;
    }

    @SubscribeEvent
    public static void onRecipesSynced(final RecipesUpdatedEvent event) {
        Recipes.seededPotionRecipes.computeUniqueIngredientsList(event.getRecipeManager().getAllRecipesFor(Recipes.BREWING_CAULDRON_RECIPE.get()));
        SanguineAltarBlockEntity.setRecipes(event.getRecipeManager().getAllRecipesFor(Recipes.SANGUINE_ALTAR_RECIPE.get()));
    }

    private static void injectRuntimeRecipes(MinecraftServer server) {
        int numInjected = Recipes.injectRuntimeRecipes(server);
        PotionsPlus.LOGGER.info("Injected {} runtime recipes", numInjected);
    }

    private static void initializeSavedData(MinecraftServer server) {
        ServerLevel level = server.overworld();
        DimensionDataStorage dataStorage = level.getDataStorage();
        SavedData.instance = dataStorage.computeIfAbsent(SavedData::load, SavedData::create, SavedData.FILE_NAME);
    }

    private static void initializeBiomes(MinecraftServer server) {
        for (Map.Entry<ResourceKey<LevelStem>, LevelStem> entry : server.registryAccess().registryOrThrow(Registries.LEVEL_STEM).entrySet()) {
            LevelStem stem = entry.getValue();
            ChunkGenerator chunkGenerator = stem.generator();
            MultiNoiseBiomeSource biomeSource = (MultiNoiseBiomeSource)chunkGenerator.getBiomeSource();

            Climate.ParameterList parameters = biomeSource.parameters.left().get();
        }
    }
}
