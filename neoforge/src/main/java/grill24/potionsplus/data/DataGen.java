package grill24.potionsplus.data;

import grill24.potionsplus.core.*;
import grill24.potionsplus.data.loot.GlobalLootModifierProvider;
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
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = ModInfo.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class DataGen {
    private static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            // World Gen
            .add(Registries.CONFIGURED_FEATURE, ConfiguredFeatures::bootstrap)
            .add(Registries.PLACED_FEATURE, Placements::bootstrap)
            .add(Registries.BIOME, Biomes::bootstrap)
            .add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, BiomeModifierProvider::bootstrap)
            // Custom Datapack Registries
            .add(PotionsPlusRegistries.CONFIGURED_SKILL, ConfiguredSkills::generate)
            .add(PotionsPlusRegistries.CONFIGURED_SKILL_POINT_SOURCE, ConfiguredSkillPointSources::generate)
            .add(PotionsPlusRegistries.CONFIGURED_PLAYER_ABILITY, ConfiguredPlayerAbilities::generate)
            .add(PotionsPlusRegistries.CONFIGURED_GRANTABLE_REWARD, ConfiguredGrantableRewards::generate);

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent.Client event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = event.getGenerator().getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        PotionsPlus.LOGGER.info("Generating data for Potions Plus");

        BlockTagsProvider blockTagsProvider = new BlockTagProvider(output, lookupProvider);
        ItemTagProvider itemTagProvider = new ItemTagProvider(output, lookupProvider, blockTagsProvider.contentsGetter());
        Sounds soundsProvider = new Sounds(output, ModInfo.MOD_ID);
        generator.addProvider(true, blockTagsProvider);
        generator.addProvider(true, itemTagProvider);
        generator.addProvider(true, soundsProvider);

        event.createProvider(BlockStateProvider::new);
        event.createProvider(RecipeProvider.Runner::new);
        event.createProvider(LootTableProvider::new);
        generator.addProvider(true, new DatapackBuiltinEntriesProvider(output, lookupProvider, BUILDER, Set.of(ModInfo.MOD_ID)));
        event.createProvider(BiomeTagProvider::new);
        event.createProvider(AdvancementProvider::new);
        event.createProvider(GlobalLootModifierProvider::new);
    }
}
