package grill24.potionsplus.data;

import grill24.potionsplus.core.Biomes;
import grill24.potionsplus.core.PotionsPlus;
import grill24.potionsplus.core.Sounds;
import grill24.potionsplus.utility.ModInfo;
import grill24.potionsplus.worldgen.ConfiguredFeatures;
import grill24.potionsplus.worldgen.Placements;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.JsonCodecProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

@Mod.EventBusSubscriber(modid = ModInfo.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
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

        PotionsPlus.LOGGER.info("Generating data for Potions Plus");


        if (event.includeServer()) {
            generator.addProvider(true, new BlockStateProvider(output, existingFileHelper));
            generator.addProvider(true, new RecipeProvider(output));
            generator.addProvider(true, new LootTableProvider(output));
            generator.addProvider(true, new DatapackBuiltinEntriesProvider(output, event.getLookupProvider(), BUILDER, Set.of(ModInfo.MOD_ID)));
            generator.addProvider(true, new BiomeTagProvider(output, event.getLookupProvider(), existingFileHelper));
        }

        if (event.includeClient()) {
//            generator.addProvider(true, new LangProvider(output, ModInfo.MOD_ID, "en_us"));
        }

        BlockTagsProvider blockTagsProvider = new BlockTagProvider(output, event.getLookupProvider(), existingFileHelper);
        ItemTagProvider itemTagProvider = new ItemTagProvider(output, event.getLookupProvider(), blockTagsProvider.contentsGetter(), existingFileHelper);
        Sounds soundsProvider = new Sounds(output, ModInfo.MOD_ID, existingFileHelper);
        generator.addProvider(true, blockTagsProvider);
        generator.addProvider(true, itemTagProvider);
        generator.addProvider(true, soundsProvider);
    }
}
