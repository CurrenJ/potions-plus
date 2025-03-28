package grill24.potionsplus.core;

import com.google.common.collect.ImmutableList;
import grill24.potionsplus.blockentity.AbyssalTroveBlockEntity;
import grill24.potionsplus.blockentity.SanguineAltarBlockEntity;
import grill24.potionsplus.core.seededrecipe.SeededPotionRecipes;
import grill24.potionsplus.persistence.SavedData;
import grill24.potionsplus.recipe.abyssaltroverecipe.SanguineAltarRecipe;
import grill24.potionsplus.recipe.brewingcauldronrecipe.BrewingCauldronRecipe;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RecipesUpdatedEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;

import java.util.List;
import java.util.Map;

@EventBusSubscriber(modid = ModInfo.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class ServerLifecycleListeners {
    public static boolean serverStarted = false;

    @SubscribeEvent
    public static void onServerStarted(final ServerStartedEvent event) {
        final MinecraftServer server = event.getServer();
        PotionsPlus.SERVER = server;

        // Setup saved data
        initializeSavedData(server);

        // Store world seed - used for generating runtime recipes. Important to do this before any recipes are generated or loot tables are initialized.
        PotionsPlus.worldSeed = server.getWorldData().worldGenOptions().seed();

        // Inject runtime recipes
        injectRuntimeRecipes(server);
        postProcessRecipes(server.getRecipeManager());

        serverStarted = true;
    }

    @SubscribeEvent
    public static void onServerAboutToStart(final ServerAboutToStartEvent event) {
        // Inject biomes
        injectBiomes(event.getServer());
    }

    @SubscribeEvent
    public static void onRecipesSynced(final RecipesUpdatedEvent event) {
        postProcessRecipes(event.getRecipeManager());
    }

    public static void postProcessRecipes(RecipeManager recipeManager) {
        List<RecipeHolder<SanguineAltarRecipe>> sanguineAltarRecipes = recipeManager.getAllRecipesFor(Recipes.SANGUINE_ALTAR_RECIPE.get());
        Recipes.SANGUINE_ALTAR_ANALYSIS.compute(sanguineAltarRecipes);
        SanguineAltarBlockEntity.computeRecipeMap(Recipes.SANGUINE_ALTAR_ANALYSIS.getRecipes());

        List<RecipeHolder<BrewingCauldronRecipe>> brewingCauldronRecipes = recipeManager.getAllRecipesFor(Recipes.BREWING_CAULDRON_RECIPE.get());
        Recipes.DURATION_UPGRADE_ANALYSIS.compute(brewingCauldronRecipes.stream().filter(recipeHolder -> recipeHolder.value().isDurationUpgrade()).toList());
        Recipes.AMPLIFICATION_UPGRADE_ANALYSIS.compute(brewingCauldronRecipes.stream().filter(recipeHolder -> recipeHolder.value().isAmplifierUpgrade()).toList());
        Recipes.ALL_SEEDED_POTION_RECIPES_ANALYSIS.compute(brewingCauldronRecipes.stream().filter(recipeHolder -> recipeHolder.value().isSeededRuntimeRecipe()).toList());
        Recipes.ALL_BCR_RECIPES_ANALYSIS.compute(brewingCauldronRecipes);

        AbyssalTroveBlockEntity.computeAbyssalTroveIngredients();
    }

    private static void injectRuntimeRecipes(MinecraftServer server) {
        int numInjected = Recipes.injectRuntimeRecipes(server);
        PotionsPlus.LOGGER.info("Injected {} runtime recipes", numInjected);
    }

    private static void initializeSavedData(MinecraftServer server) {
        ServerLevel level = server.overworld();
        DimensionDataStorage dataStorage = level.getDataStorage();
        SavedData.instance = dataStorage.computeIfAbsent(SavedData.factory(level), SavedData.FILE_NAME);
    }

    private static void injectBiomes(MinecraftServer server) {
        RegistryAccess registryAccess = server.registryAccess();
        Registry<LevelStem> levelStemRegistry = registryAccess.registryOrThrow(Registries.LEVEL_STEM);

        for (Map.Entry<ResourceKey<LevelStem>, LevelStem> entry : levelStemRegistry.entrySet())
        {
            LevelStem stem = entry.getValue();
            if(!(stem.generator().getBiomeSource() instanceof MultiNoiseBiomeSource biomeSource))
            {
                continue;
            }
            Registry<Biome> biomeRegistry = registryAccess.registryOrThrow(Registries.BIOME);
            ImmutableList.Builder<Holder<Biome>> builder = ImmutableList.builder();

            // Add all biomes here:
//            addBiome(builder, biomeRegistry, Biomes.ICE_CAVE_KEY);
//            addBiome(builder, biomeRegistry, Biomes.VOLCANIC_CAVE_KEY);
//            addBiome(builder, biomeRegistry, Biomes.ARID_CAVE_KEY);
//
//            if(!biomeSource.possibleBiomes.get().contains(biomeRegistry.getHolderOrThrow(Biomes.ICE_CAVE_KEY)))
//            {
//                // Biome Source
//                ImmutableList.Builder<Holder<Biome>> biomeSourceBuilder = ImmutableList.builder();
//                biomeSourceBuilder.addAll(biomeSource.possibleBiomes.get());
//                biomeSourceBuilder.addAll(builder.build());
//
//                ImmutableList<Holder<Biome>> biomeList = builder.build().stream().distinct().collect(ImmutableList.toImmutableList());
//
//                biomeSource.possibleBiomes = () -> new ObjectLinkedOpenHashSet<>(biomeList);
//
//                // Parameter List
//                Climate.ParameterList parameterList = biomeSource.parameters.orThrow();
//                ImmutableList.Builder<Pair<Climate.ParameterPoint, Holder<Biome>>> parameterListBuilder = ImmutableList.builder();
//                parameterListBuilder.add(
//                        Pair.of(
//                                Climate.parameters(
//                                        Climate.Parameter.span(0F, 1F),
//                                        Climate.Parameter.span(0F, 1F),
//                                        Climate.Parameter.span(0F, 1F),
//                                        Climate.Parameter.span(0F, 1F),
//                                        Climate.Parameter.span(0F, 1F),
//                                        Climate.Parameter.span(0F, 1F),
//                                        0F),
//                                biomeRegistry.getHolderOrThrow(Biomes.ICE_CAVE_KEY)
//                        )
//                );
//                ImmutableList<Pair<Climate.ParameterPoint, Holder<Biome>>> uniqueValues = parameterListBuilder.build();
//            }
        }
    }

    private static void addBiome(ImmutableList.Builder<Holder<Biome>> builder, Registry<Biome> registry, ResourceKey<Biome> biome) {
        registry.getHolder(biome).ifPresent(builder::add);
    }
}
