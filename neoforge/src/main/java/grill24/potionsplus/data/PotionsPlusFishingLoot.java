package grill24.potionsplus.data;

import grill24.potionsplus.core.Items;
import grill24.potionsplus.core.LootTables;
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

import java.util.function.BiConsumer;

public class PotionsPlusFishingLoot implements LootTableSubProvider {
    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> consumer) {
        // Fishing
        consumer.accept(
                LootTables.FISHING,
                LootTable.lootTable()
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(ConstantValue.exactly(1))
//                                            .add(LootItem.lootTableItem(Items.COD).setWeight(10))
//                                            .add(LootItem.lootTableItem(Items.SALMON).setWeight(10))
//                                            .add(LootItem.lootTableItem(Items.PUFFERFISH).setWeight(10))
//                                            .add(LootItem.lootTableItem(Items.TROPICAL_FISH).setWeight(4).when(
//                                                    IsInBiomeTagCondition.isInBiomeTag(Tags.Biomes.IS_HOT_OVERWORLD)))
                                        .add(LootItem.lootTableItem(grill24.potionsplus.core.Items.NORTHERN_PIKE.value()).setWeight(1).when(
                                                IsInBiomeCondition.isInBiome(Biomes.BEACH, Biomes.FOREST, Biomes.PLAINS, Biomes.SNOWY_PLAINS)))
                                        .add(LootItem.lootTableItem(grill24.potionsplus.core.Items.PARROTFISH.value()).setWeight(1).when(
                                                IsInBiomeCondition.isInBiome(Biomes.DESERT, Biomes.WINDSWEPT_SAVANNA, Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU, Biomes.LUSH_CAVES)))
                                        .add(LootItem.lootTableItem(grill24.potionsplus.core.Items.RAINFORDIA.value()).setWeight(1).when(
                                                IsInBiomeCondition.isInBiome(Biomes.SAVANNA, Biomes.BAMBOO_JUNGLE, Biomes.PLAINS)))
                                        .add(LootItem.lootTableItem(grill24.potionsplus.core.Items.GARDEN_EEL.value()).setWeight(1).when(
                                                IsInBiomeCondition.isInBiome(Biomes.FLOWER_FOREST, Biomes.SUNFLOWER_PLAINS, Biomes.CHERRY_GROVE, Biomes.LUSH_CAVES)))
                                        .add(LootItem.lootTableItem(grill24.potionsplus.core.Items.LONGNOSE_GAR.value()).setWeight(1).when(
                                                IsInBiomeCondition.isInBiome(Biomes.TAIGA, Biomes.ICE_SPIKES, Biomes.SNOWY_TAIGA, Biomes.FROZEN_RIVER)))
                                        .add(LootItem.lootTableItem(grill24.potionsplus.core.Items.SHRIMP.value()).setWeight(1).when(
                                                IsInBiomeCondition.isInBiome(Biomes.MANGROVE_SWAMP, Biomes.SWAMP, Biomes.GROVE)))
                                        .add(LootItem.lootTableItem(grill24.potionsplus.core.Items.MOORISH_IDOL.value()).setWeight(1).when(
                                                IsInBiomeCondition.isInBiome(Biomes.JUNGLE, Biomes.BAMBOO_JUNGLE, Biomes.SPARSE_JUNGLE, Biomes.BIRCH_FOREST, Biomes.PLAINS)))
                                        .add(LootItem.lootTableItem(grill24.potionsplus.core.Items.MOLTEN_MOORISH_IDOL.value()).when(
                                                IsInBiomeCondition.isInBiome(grill24.potionsplus.core.Biomes.VOLCANIC_CAVE_KEY)))
                                        .add(LootItem.lootTableItem(grill24.potionsplus.core.Items.OCEAN_SUNFISH.value()).setWeight(1).when(
                                                IsInBiomeTagCondition.isInBiomeTag(BiomeTags.IS_DEEP_OCEAN)))
                                        .add(LootItem.lootTableItem(grill24.potionsplus.core.Items.PORTUGUESE_MAN_O_WAR.value()).setWeight(1).when(
                                                IsInBiomeCondition.isInBiome(Biomes.WARM_OCEAN, Biomes.JAGGED_PEAKS, Biomes.STONY_PEAKS)))
                                        .add(LootItem.lootTableItem(grill24.potionsplus.core.Items.BLUEGILL.value()).setWeight(1).when(
                                                IsInBiomeCondition.isInBiome(Biomes.FLOWER_FOREST, Biomes.LUSH_CAVES, Biomes.SNOWY_PLAINS, Biomes.SNOWY_BEACH)))
                                        .add(LootItem.lootTableItem(grill24.potionsplus.core.Items.NEON_TETRA.value()).setWeight(1).when(
                                                IsInBiomeCondition.isInBiome(Biomes.FLOWER_FOREST, Biomes.SNOWY_SLOPES, Biomes.BADLANDS)))
                                        .add(LootItem.lootTableItem(grill24.potionsplus.core.Items.GIANT_MANTA_RAY.value()).setWeight(1).when(
                                                IsInBiomeCondition.isInBiome(Biomes.ERODED_BADLANDS, Biomes.WOODED_BADLANDS, Biomes.BIRCH_FOREST)))
                                        .add(LootItem.lootTableItem(grill24.potionsplus.core.Items.FROZEN_GIANT_MANTA_RAY.value()).setWeight(1).when(
                                                IsInBiomeCondition.isInBiome(grill24.potionsplus.core.Biomes.ICE_CAVE_KEY, Biomes.FROZEN_PEAKS, Biomes.FROZEN_RIVER)))
                                        .add(LootItem.lootTableItem(grill24.potionsplus.core.Items.LIZARDFISH.value()).setWeight(1).when(
                                                IsInBiomeCondition.isInBiome(Biomes.FOREST, Biomes.DARK_FOREST, Biomes.MUSHROOM_FIELDS, Biomes.LUSH_CAVES, Biomes.SUNFLOWER_PLAINS)))
                        )
        );
    }
}
