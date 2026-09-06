package grill24.potionsplus.data.neoforge;

import grill24.potionsplus.core.*;
import grill24.potionsplus.core.neoforge.NeoSounds;
import grill24.potionsplus.data.loot.neoforge.GlobalLootModifierProvider;
import grill24.potionsplus.utility.ModInfo;
import grill24.potionsplus.worldgen.ConfiguredFeatures;
import grill24.potionsplus.worldgen.Placements;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.component.DataComponentInitializers;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = ModInfo.MOD_ID)
public class DataGen {
    private static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            // World Gen
            .add(Registries.CONFIGURED_FEATURE, ConfiguredFeatures::bootstrap)
            .add(Registries.PLACED_FEATURE, Placements::bootstrap);

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent.Client event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = event.getGenerator().getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        PotionsPlus.LOGGER.info("Generating data for Potions Plus");

        // Outside a full game/server boot, ReloadableServerResources never runs, so item Holders are
        // never bound to their default DataComponentMap - constructing an ItemStack in a recipe
        // provider (e.g. PotionContainer.create) throws "Components not bound yet". Bind them ourselves
        // once the registry lookup resolves, the same way RegistryDataCollector does for a real client.
        lookupProvider.thenAccept(registries -> {
            try {
                BuiltInRegistries.DATA_COMPONENT_INITIALIZERS.build(registries)
                        .forEach(DataComponentInitializers.PendingComponents::apply);
            } catch (RuntimeException e) {
                // The datagen tool's registry lookup doesn't have tags fully resolved the way a real
                // game boot does, which can produce a default component value NeoForge's dev-only
                // equals/hashCode validator rejects (e.g. an unresolved HolderSet). The bound values
                // are never seen by players - only used transiently to let this process build
                // ItemStacks - so fall back to binding everything empty rather than failing datagen.
                PotionsPlus.LOGGER.warn("Falling back to empty item components for datagen ({})", e.toString());
                BuiltInRegistries.ITEM.listElements().forEach(holder -> {
                    if (!holder.areComponentsBound()) {
                        holder.bindComponents(DataComponentMap.EMPTY);
                    }
                });
            }
        }).join();

        BlockTagsProvider blockTagsProvider = new BlockTagProvider(output, lookupProvider);
        ItemTagProvider itemTagProvider = new ItemTagProvider(output, lookupProvider);
        NeoSounds soundsProvider = new NeoSounds(output, ModInfo.MOD_ID);
        generator.addProvider(true, blockTagsProvider);
        generator.addProvider(true, itemTagProvider);
        generator.addProvider(true, soundsProvider);
        // Empty structure templates the game tests run inside. Output is committed, so this only needs
        // rerunning if a test needs a differently sized area.
        generator.addProvider(true, new GameTestStructureProvider(output));

        event.createProvider(BlockStateProvider::new);
        event.createProvider(RecipeProvider.Runner::new);
        // TODO: Re-enable after MC 26.1 data component migration
        // event.createProvider(LootTableProvider::new);
        generator.addProvider(true, new DatapackBuiltinEntriesProvider(output, lookupProvider, BUILDER, Set.of(ModInfo.MOD_ID)));
        // event.createProvider(AdvancementProvider::new);
        // event.createProvider(GlobalLootModifierProvider::new);
    }
}
