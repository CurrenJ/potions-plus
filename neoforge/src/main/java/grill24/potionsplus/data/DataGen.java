package grill24.potionsplus.data;

import grill24.potionsplus.core.Biomes;
import grill24.potionsplus.core.PotionsPlus;
import grill24.potionsplus.core.Sounds;
import grill24.potionsplus.utility.ModInfo;
import grill24.potionsplus.worldgen.ConfiguredFeatures;
import grill24.potionsplus.worldgen.Placements;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = ModInfo.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class DataGen {
    private static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.CONFIGURED_FEATURE, ConfiguredFeatures::bootstrap)
            .add(Registries.PLACED_FEATURE, Placements::bootstrap)
            .add(Registries.BIOME, Biomes::bootstrap);

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = event.getGenerator().getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        PotionsPlus.LOGGER.info("Generating data for Potions Plus");


        generator.addProvider(event.includeServer(), new BlockStateProvider(output, existingFileHelper));
        generator.addProvider(event.includeServer(), new RecipeProvider(output, lookupProvider));
        generator.addProvider(event.includeServer(), new LootTableProvider(output, lookupProvider));
        generator.addProvider(event.includeServer(), new DatapackBuiltinEntriesProvider(output, lookupProvider, BUILDER, Set.of(ModInfo.MOD_ID)));
        generator.addProvider(event.includeServer(), new BiomeTagProvider(output, lookupProvider, existingFileHelper));
        generator.addProvider(event.includeServer(), new AdvancementProvider(output, lookupProvider, existingFileHelper, null));

        if (event.includeClient()) {
//            generator.addProvider(true, new LangProvider(output, ModInfo.MOD_ID, "en_us"));
        }

        BlockTagsProvider blockTagsProvider = new BlockTagProvider(output, lookupProvider, existingFileHelper);
        ItemTagProvider itemTagProvider = new ItemTagProvider(output, lookupProvider, blockTagsProvider.contentsGetter(), existingFileHelper);
        Sounds soundsProvider = new Sounds(output, ModInfo.MOD_ID, existingFileHelper);
        generator.addProvider(true, blockTagsProvider);
        generator.addProvider(true, itemTagProvider);
        generator.addProvider(true, soundsProvider);
    }
}
