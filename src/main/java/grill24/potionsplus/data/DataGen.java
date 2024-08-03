package grill24.potionsplus.data;

import grill24.potionsplus.core.PotionsPlus;
import grill24.potionsplus.core.Sounds;
import grill24.potionsplus.utility.ModInfo;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

@Mod.EventBusSubscriber(modid = ModInfo.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGen {
    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        PotionsPlus.LOGGER.info("Generating data for Potions Plus");

        if (event.includeServer()) {
            generator.addProvider(new BlockStateProvider(generator, ModInfo.MOD_ID, existingFileHelper));
            generator.addProvider(new RecipeProvider(generator, existingFileHelper));
            generator.addProvider(new LootTableProvider(generator));
        }

        if (event.includeClient()) {
            generator.addProvider(new LangProvider(generator, ModInfo.MOD_ID, "GENERATED_en_us"));
        }

        BlockTagsProvider blockTagsProvider = new BlockTagProvider(generator, ModInfo.MOD_ID, existingFileHelper);
        ItemTagProvider itemTagProvider = new ItemTagProvider(generator, blockTagsProvider, ModInfo.MOD_ID, existingFileHelper);
        Sounds soundsProvider = new Sounds(generator, ModInfo.MOD_ID, existingFileHelper);
        generator.addProvider(blockTagsProvider);
        generator.addProvider(itemTagProvider);
        generator.addProvider(soundsProvider);
    }
}
