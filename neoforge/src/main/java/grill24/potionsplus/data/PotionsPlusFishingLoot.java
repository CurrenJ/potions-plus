package grill24.potionsplus.data;

import grill24.potionsplus.core.Items;
import grill24.potionsplus.core.LootTables;
import grill24.potionsplus.function.GaussianDistributionGenerator;
import grill24.potionsplus.function.SetFishSizeFunction;
import grill24.potionsplus.loot.IsInBiomeCondition;
import grill24.potionsplus.loot.IsInBiomeTagCondition;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.neoforged.neoforge.common.Tags;

import java.util.function.BiConsumer;

public class PotionsPlusFishingLoot implements LootTableSubProvider {
    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> consumer) {
        // Fishing
        final int vanillaFishWeight = 40; // Vanilla fish use 120/160 weight total
        final int defaultFishWeight = 1 ; // Each non-rare fish
        final int bonusBiomeWeight = 10; // The bonus chance to catch a non-rare fish if you are in the correct biome
        final int rareFishWeight = 5; // Rare fish have a 8/120 or 8/160 chance to be caught anywhere in their required biome, zero elsewhere

        final int biomeFishWeight = defaultFishWeight + bonusBiomeWeight; // 20/120 or 20/160 chance to be caught in the correct biome

        final NumberProvider smallFishSize = GaussianDistributionGenerator.gaussian(30F, 15F);
        final NumberProvider mediumFishSize = GaussianDistributionGenerator.gaussian(50F, 15F);
        final NumberProvider largeFishSize = GaussianDistributionGenerator.gaussian(80F, 15F);

        consumer.accept(
                LootTables.FISHING,
                LootTable.lootTable()
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(ConstantValue.exactly(1))
                                        // Vanilla fish
                                        .add(LootItem.lootTableItem(net.minecraft.world.item.Items.COD)
                                                .setWeight(vanillaFishWeight)
                                                .apply(new SetFishSizeFunction.Builder(smallFishSize)))
                                        .add(LootItem.lootTableItem(net.minecraft.world.item.Items.SALMON)
                                                .setWeight(vanillaFishWeight)
                                                .apply(new SetFishSizeFunction.Builder(mediumFishSize)))
                                        .add(LootItem.lootTableItem(net.minecraft.world.item.Items.PUFFERFISH)
                                                .setWeight(vanillaFishWeight)
                                                .apply(new SetFishSizeFunction.Builder(smallFishSize)))
                                        .add(LootItem.lootTableItem(net.minecraft.world.item.Items.TROPICAL_FISH)
                                                .setWeight(vanillaFishWeight)
                                                .apply(new SetFishSizeFunction.Builder(mediumFishSize))
                                                .when(IsInBiomeTagCondition.isInBiomeTag(Tags.Biomes.IS_HOT_OVERWORLD)))
                                        // Northern Pike
                                        .add(LootItem.lootTableItem(Items.NORTHERN_PIKE.getItem()) // Anywhere drop rates
                                                .setWeight(defaultFishWeight)
                                                .setQuality(1)
                                                .apply(new SetFishSizeFunction.Builder(largeFishSize)))
                                        .add(LootItem.lootTableItem(Items.NORTHERN_PIKE.getItem()) // Biome specific rates
                                                .setWeight(biomeFishWeight)
                                                .setQuality(1)
                                                .apply(new SetFishSizeFunction.Builder(largeFishSize))
                                                .when(IsInBiomeCondition.isInBiome(Biomes.BEACH, Biomes.FOREST, Biomes.PLAINS, Biomes.SNOWY_PLAINS)))
                                        // Parrotfish
                                        .add(LootItem.lootTableItem(Items.PARROTFISH.getItem())
                                                .setWeight(defaultFishWeight)
                                                .setQuality(1)
                                                .apply(new SetFishSizeFunction.Builder(smallFishSize)))
                                        .add(LootItem.lootTableItem(Items.PARROTFISH.getItem())
                                                .setWeight(biomeFishWeight)
                                                .apply(new SetFishSizeFunction.Builder(smallFishSize))
                                                .when(IsInBiomeCondition.isInBiome(Biomes.DESERT, Biomes.WINDSWEPT_SAVANNA, Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU, Biomes.LUSH_CAVES)))
                                        // Rainfordia
                                        .add(LootItem.lootTableItem(Items.RAINFORDIA.getItem())
                                                .setWeight(defaultFishWeight)
                                                .setQuality(1)
                                                .apply(new SetFishSizeFunction.Builder(smallFishSize)))
                                        .add(LootItem.lootTableItem(Items.RAINFORDIA.getItem())
                                                .setWeight(biomeFishWeight)
                                                .setQuality(1)
                                                .apply(new SetFishSizeFunction.Builder(smallFishSize))
                                                .when(IsInBiomeCondition.isInBiome(Biomes.SAVANNA, Biomes.BAMBOO_JUNGLE, Biomes.PLAINS)))
                                        // Garden Eel
                                        .add(LootItem.lootTableItem(Items.GARDEN_EEL.getItem())
                                                .setWeight(defaultFishWeight)
                                                .setQuality(1)
                                                .apply(new SetFishSizeFunction.Builder(smallFishSize)))
                                        .add(LootItem.lootTableItem(Items.GARDEN_EEL.getItem())
                                                .setWeight(biomeFishWeight)
                                                .setQuality(1)
                                                .apply(new SetFishSizeFunction.Builder(smallFishSize))
                                                .when(IsInBiomeCondition.isInBiome(Biomes.FLOWER_FOREST, Biomes.SUNFLOWER_PLAINS, Biomes.CHERRY_GROVE, Biomes.LUSH_CAVES)))
                                        // Royal Garden Eel
                                        .add(LootItem.lootTableItem(Items.ROYAL_GARDEN_EEL.getItem())
                                                .setWeight(rareFishWeight)
                                                .setQuality(2)
                                                .apply(new SetFishSizeFunction.Builder(smallFishSize))
                                                .when(IsInBiomeCondition.isInBiome(Biomes.FLOWER_FOREST, Biomes.SUNFLOWER_PLAINS, Biomes.CHERRY_GROVE, Biomes.LUSH_CAVES)))
                                        // Longnose Gar
                                        .add(LootItem.lootTableItem(Items.LONGNOSE_GAR.getItem())
                                                .setWeight(defaultFishWeight)
                                                .setQuality(1)
                                                .apply(new SetFishSizeFunction.Builder(largeFishSize)))
                                        .add(LootItem.lootTableItem(Items.LONGNOSE_GAR.getItem())
                                                .setWeight(biomeFishWeight)
                                                .setQuality(1)
                                                .apply(new SetFishSizeFunction.Builder(largeFishSize))
                                                .when(IsInBiomeCondition.isInBiome(Biomes.TAIGA, Biomes.ICE_SPIKES, Biomes.SNOWY_TAIGA, Biomes.FROZEN_RIVER)))
                                        // Shrimp
                                        .add(LootItem.lootTableItem(Items.SHRIMP.getItem())
                                                .setWeight(defaultFishWeight)
                                                .setQuality(1)
                                                .apply(new SetFishSizeFunction.Builder(smallFishSize)))
                                        .add(LootItem.lootTableItem(Items.SHRIMP.getItem())
                                                .setWeight(biomeFishWeight)
                                                .setQuality(1)
                                                .apply(new SetFishSizeFunction.Builder(smallFishSize))
                                                .when(IsInBiomeCondition.isInBiome(Biomes.MANGROVE_SWAMP, Biomes.SWAMP, Biomes.GROVE)))
                                        // Fried Shrimp
                                        .add(LootItem.lootTableItem(Items.FRIED_SHRIMP.getItem())
                                                .setWeight(rareFishWeight)
                                                .setQuality(2)
                                                .apply(new SetFishSizeFunction.Builder(smallFishSize))
                                                .when(IsInBiomeCondition.isInBiome(Biomes.MUSHROOM_FIELDS, grill24.potionsplus.core.Biomes.ARID_CAVE_KEY, Biomes.MANGROVE_SWAMP)))
                                        // Moorish Idol
                                        .add(LootItem.lootTableItem(Items.MOORISH_IDOL.getItem())
                                                .setWeight(defaultFishWeight)
                                                .setQuality(1)
                                                .apply(new SetFishSizeFunction.Builder(mediumFishSize)))
                                        .add(LootItem.lootTableItem(Items.MOORISH_IDOL.getItem())
                                                .setWeight(biomeFishWeight)
                                                .setQuality(1)
                                                .apply(new SetFishSizeFunction.Builder(mediumFishSize))
                                                .when(IsInBiomeCondition.isInBiome(Biomes.JUNGLE, Biomes.BAMBOO_JUNGLE, Biomes.SPARSE_JUNGLE, Biomes.BIRCH_FOREST, Biomes.PLAINS)))
                                        // Molten Moorish Idol
                                        .add(LootItem.lootTableItem(Items.MOLTEN_MOORISH_IDOL.getItem())
                                                .setWeight(rareFishWeight)
                                                .setQuality(2)
                                                .apply(new SetFishSizeFunction.Builder(mediumFishSize))
                                                .when(IsInBiomeCondition.isInBiome(grill24.potionsplus.core.Biomes.VOLCANIC_CAVE_KEY)))
                                        // Ocean Sunfish
                                        .add(LootItem.lootTableItem(Items.OCEAN_SUNFISH.getItem())
                                                .setWeight(defaultFishWeight)
                                                .setQuality(1)
                                                .apply(new SetFishSizeFunction.Builder(largeFishSize)))
                                        .add(LootItem.lootTableItem(Items.OCEAN_SUNFISH.getItem())
                                                .setWeight(biomeFishWeight)
                                                .setQuality(1)
                                                .apply(new SetFishSizeFunction.Builder(largeFishSize))
                                                .when(IsInBiomeTagCondition.isInBiomeTag(BiomeTags.IS_DEEP_OCEAN)))
                                        // Portuguese Man O' War
                                        .add(LootItem.lootTableItem(Items.PORTUGUESE_MAN_O_WAR.getItem())
                                                .setWeight(defaultFishWeight)
                                                .setQuality(1)
                                                .apply(new SetFishSizeFunction.Builder(largeFishSize)))
                                        .add(LootItem.lootTableItem(Items.PORTUGUESE_MAN_O_WAR.getItem())
                                                .setWeight(biomeFishWeight)
                                                .setQuality(1)
                                                .apply(new SetFishSizeFunction.Builder(largeFishSize))
                                                .when(IsInBiomeCondition.isInBiome(Biomes.WARM_OCEAN, Biomes.JAGGED_PEAKS, Biomes.STONY_PEAKS)))
                                        // Bluegill
                                        .add(LootItem.lootTableItem(Items.BLUEGILL.getItem())
                                                .setWeight(defaultFishWeight)
                                                .setQuality(1)
                                                .apply(new SetFishSizeFunction.Builder(mediumFishSize)))
                                        .add(LootItem.lootTableItem(Items.BLUEGILL.getItem())
                                                .setWeight(biomeFishWeight)
                                                .setQuality(1)
                                                .apply(new SetFishSizeFunction.Builder(mediumFishSize))
                                                .when(IsInBiomeCondition.isInBiome(Biomes.FLOWER_FOREST, Biomes.LUSH_CAVES, Biomes.SNOWY_PLAINS, Biomes.SNOWY_BEACH)))
                                        // Neon Tetra
                                        .add(LootItem.lootTableItem(Items.NEON_TETRA.getItem())
                                                .setWeight(defaultFishWeight)
                                                .setQuality(1)
                                                .apply(new SetFishSizeFunction.Builder(smallFishSize)))
                                        .add(LootItem.lootTableItem(Items.NEON_TETRA.getItem())
                                                .setWeight(biomeFishWeight)
                                                .setQuality(1)
                                                .apply(new SetFishSizeFunction.Builder(smallFishSize))
                                                .when(IsInBiomeCondition.isInBiome(Biomes.FLOWER_FOREST, Biomes.SNOWY_SLOPES, Biomes.BADLANDS)))
                                        // Giant Manta Ray
                                        .add(LootItem.lootTableItem(Items.GIANT_MANTA_RAY.getItem())
                                                .setWeight(defaultFishWeight)
                                                .setQuality(1)
                                                .apply(new SetFishSizeFunction.Builder(largeFishSize)))
                                        .add(LootItem.lootTableItem(Items.GIANT_MANTA_RAY.getItem())
                                                .setWeight(biomeFishWeight)
                                                .setQuality(1)
                                                .apply(new SetFishSizeFunction.Builder(largeFishSize))
                                                .when(IsInBiomeCondition.isInBiome(Biomes.ERODED_BADLANDS, Biomes.WOODED_BADLANDS, Biomes.BIRCH_FOREST)))
                                        // Frozen Giant Manta Ray
                                        .add(LootItem.lootTableItem(Items.FROZEN_GIANT_MANTA_RAY.getItem())
                                                .setWeight(rareFishWeight)
                                                .setQuality(2)
                                                .apply(new SetFishSizeFunction.Builder(largeFishSize))
                                                .when(IsInBiomeCondition.isInBiome(grill24.potionsplus.core.Biomes.ICE_CAVE_KEY, Biomes.FROZEN_PEAKS, Biomes.FROZEN_RIVER)))
                                        // Lizardfish
                                        .add(LootItem.lootTableItem(Items.LIZARDFISH.getItem())
                                                .setWeight(defaultFishWeight)
                                                .setQuality(1)
                                                .apply(new SetFishSizeFunction.Builder(mediumFishSize)))
                                        .add(LootItem.lootTableItem(Items.LIZARDFISH.getItem())
                                                .setWeight(biomeFishWeight)
                                                .setQuality(1)
                                                .apply(new SetFishSizeFunction.Builder(mediumFishSize))
                                                .when(IsInBiomeCondition.isInBiome(Biomes.FOREST, Biomes.DARK_FOREST, Biomes.MUSHROOM_FIELDS, Biomes.LUSH_CAVES, Biomes.SUNFLOWER_PLAINS)))
                        )
        );
    }
}
